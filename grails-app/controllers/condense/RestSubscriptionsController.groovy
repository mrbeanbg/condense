package condense

import java.lang.ClassValue.Identity;

import grails.transaction.Transactional;

class RestSubscriptionsController {
	static responseFormats = ['json']
	
	def index(Integer max) {
		def cspCustomerId = params.restCustomersId
		if (cspCustomerId == null) {
			respond Subscription.list()
			return
		}
		
		def cspCustomer = Customer.find {id == cspCustomerId.toLong()}
		if (cspCustomer == null) {
			render status: 404
			return
		}
		respond Subscription.createCriteria().list {eq("customer.id", cspCustomer.id)}
	}
	
	def show(Subscription subscription) {
		if(subscription == null) {
			render status:404
			return
		}
		def cspCustomerId = params.restCustomersId?.toLong()
		if (cspCustomerId != null) {
			if (subscription.customer.id != cspCustomerId) {
				render status: 404
				return
			}
		}
		
		respond subscription
	}
	
	@Transactional
	def delete (Subscription subscription) {
		if(subscription == null) {
			render status:404
			return
		}
		def cspCustomerId = params.restCustomersId?.toLong()
		if (cspCustomerId != null) {
			if (subscription.customer.id != cspCustomerId) {
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
		
		def cspCustomer = Customer.get(cspCustomerId.toLong())
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
}
