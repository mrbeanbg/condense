package condense

import java.lang.ClassValue.Identity;

import grails.transaction.Transactional;
import grails.plugin.springsecurity.annotation.Secured;

@Secured(['permitAll'])
class RestSubscriptionsController {
	static responseFormats = ['json']
	
	def index(Integer max) {
		def cspCustomerId = params.restCustomersId
		if (cspCustomerId == null) {
			respond Subscription.list()
			return
		}
		
		def cspCustomer = Customer.findByCspCustomerId(cspCustomerId)
		if (cspCustomer == null) {
			render status: 404
			return
		}
		respond Subscription.createCriteria().list {eq("customer.id", cspCustomer.id)}
	}
	
	def show() {
		def subscription = Subscription.findBySubscriptionId(params.id)
		
		if(subscription == null) {
			render status:404
			return
		}
		def cspCustomerId = params.restCustomersId
		if (cspCustomerId != null) {
			if (subscription.customer.cspCustomerId != cspCustomerId) {
				render status: 404
				return
			}
		}
		
		respond subscription
	}
	
	@Transactional
	def delete () {
		def subscription = Subscription.findBySubscriptionId(params.id)
		
		if(subscription == null) {
			render status:404
			return
		}
		def cspCustomerId = params.restCustomersId
		if (cspCustomerId != null) {
			if (subscription.customer.cspCustomerId != cspCustomerId) {
				render status: 404
				return
			}
		}
		subscription.delete flush:true
		render status: 204
	}
	
	@Transactional
	def save() {
		def newSubscriptionId = request.JSON?.subscriptionId
		
		if (newSubscriptionId == null)  {
			render status: 400, text: "subscriptionId is required"
			return
		}
		
		def cspCustomerId = params.restCustomersId
		if (cspCustomerId == null) {
			cspCustomerId = request.JSON?.customerId
			if (cspCustomerId == null)  {
				render status: 400, text: "customerId is required"
				return
			}
		}
		
		def cspCustomer = Customer.findByCspCustomerId(cspCustomerId)
		if (cspCustomer == null) {
			render status: 404
			return
		}
		
		if (Subscription.find {subscriptionId == newSubscriptionId} != null) {
			render status: 409, text: "Subscription ${newSubscriptionId} already exists"
			return
		}
		
		def subscription = new Subscription(subscriptionId: newSubscriptionId, customer: cspCustomer)
		subscription.save flush:true
		respond subscription.refresh(), [status: 201]
	}
	
	@Transactional
	def update() {
		print params
		Subscription subscription = Subscription.findBySubscriptionId(params.id)
		if(subscription == null) {
			render status:404
		}
		if (request.JSON?.isActive != null) {
			subscription.isActive = request.JSON?.isActive
			
			if (!subscription.save(flush:true, failOnError:true)) {
				if (subscription.hasErrors()) {
					respond subscription.errors
					return
				}
			}
		}
		
		respond subscription.refresh()
	}
}
