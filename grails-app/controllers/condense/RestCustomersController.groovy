package condense

import grails.transaction.Transactional;
import grails.plugin.springsecurity.annotation.Secured;

@Secured(['permitAll'])
class RestCustomersController {
	
	ConfigService configService
	
	static responseFormats = ['json']
	
    def index(Integer max) { 
		respond Customer.list()
	}
	
	def show() {
		print params
		def customerId = (params.format != null) ? "${params.id}.${params.format}" : params.id
		def customer = Customer.findByCspCustomerId(customerId)
		
		if(customer == null) {
			render status:404
		}
		respond customer
	}
	
	@Transactional
	def delete() {
		def customerId = (params.format != null) ? "${params.id}.${params.format}" : params.id
		def customer = Customer.findByCspCustomerId(customerId)
		
		if(customer == null) {
			render status:404
		}
		customer.delete flush:true
		render status:204
	}
	
	@Transactional
	def save() {
		def pricingSetId = request.JSON?.pricingSetId?.toLong()
		if (pricingSetId == null) {
			// TODO: obtain the default values PricingSet
			pricingSetId = configService.getValue("defaultPricingSet")
			if (pricingSetId == null) {
				render status: 400, text: "pricingSetId is required"
				return
			}
		}
		
		def cspDomain = request.JSON?.cspDomain
		if (cspDomain == null) {
			render status: 400, text: "cspDomain is required"
			return
		}
		
		def externalId = request.JSON?.externalId
		if (externalId == null) {
			render status: 400, text: "externalId is required"
			return
		}
		
		def newCspCustomerId = request.JSON?.cspCustomerId
		if (newCspCustomerId == null) {
			render status: 400, text: "cspCustomerId is required"
			return

		} else if (Customer.findByCspCustomerId(newCspCustomerId) != null) {
			render status: 409, text: "CSP customer ${newCspCustomerId} already exists"
			return
		}
		
		def pricingSet = PricingSet.find {id == pricingSetId}
		
		if (pricingSet == null) {
			render status: 400, text: "Invalid pricing set ID"
			return
		}
		
		def supportPlanId = request.JSON?.supportPlanId?.toLong()
		def supportPlan
		if (supportPlanId == null) {
			//TODO: obtain the default Support Plan
			supportPlanId = configService.getValue("defaultSupportPlan")
		}
		if (supportPlanId != null) {
			supportPlan = SupportPlan.find{id == supportPlanId}
			if (supportPlan == null) {
				render status: 400, text: "Invalid support plan ID"
				return
			}
		}
		
		def customer = new Customer(
			cspCustomerId: newCspCustomerId,
			cspDomain: cspDomain,
			externalId: externalId,
			pricingSet: pricingSet,
			supportPlan: supportPlan)
		
		if (!customer.save(flush:true, failOnError:true)) {
			if (customer.hasErrors()) {
				respond customer.errors
				return
			}
		}
		
		respond customer.refresh(), [status: 201]
	}
	
	@Transactional
	def update() {
		Customer customer = Customer.findByCspCustomerId(params.id)
		if(customer == null) {
			render status:404
		}
		
		def cspDomain = request.JSON?.cspDomain
		if (cspDomain != null) {
			customer.cspDomain = cspDomain
		}
		
		def externalId = request.JSON?.externalId
		if (externalId != null) {
			customer.externalId = externalId
		}
		
		def pricingSetId = request.JSON?.pricingSetId?.toLong()
		if (pricingSetId != null) {
			def pricingSet = PricingSet.find{id == pricingSetId}
			if (pricingSet == null) {
				render status: 400, text: "Invalid pricing set ID"
				return
			}
			customer.pricingset = pricingSet
		}
		
		def supportPlanId = request.JSON?.supportPlanId?.toLong()
		if (supportPlanId != null) {
			def supportPlan = SupportPlan.find{id == supportPlanId}
			if (supportPlan == null) {
				render status: 400, text: "Invalid support plan ID"
				return
			}
			customer.supportPlan = supportPlan
		}
		
		if (!customer.save(flush:true, failOnError:true)) {
			if (customer.hasErrors()) {
				respond customer.errors
				return
			}
		}
		
		respond customer.refresh()
	}
}
