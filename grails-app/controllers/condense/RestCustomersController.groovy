package condense

import grails.transaction.Transactional;


class RestCustomersController {
	static responseFormats = ['json']
	
    def index(Integer max) { 
		respond Customer.list()
	}
	
	def create() {
		
	}
	
	def show(Customer customer) {
		if(customer == null) {
			render status:404
		}
		respond customer
	}
	
	def delete(Customer customer) {
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
			render status: 400, text: "pricingSetId is required"
			return
		}
		
		def newCspCustomerId = request.JSON?.cspCustomerId
		if (newCspCustomerId == null) {
			render status: 400, text: "cspCustomerId is required"
			return

		} else if (Customer.find{cspCustomerId == newCspCustomerId} != null) {
			render status: 409, text: "CSP customer ${newCspCustomerId} already exists"
			return
		}
		
		def pricingSet = PricingSet.find{id == pricingSetId}
		
		if (pricingSet == null) {
			render status: 400, text: "Invalid pricing set ID"
			return
		}
		
		def supportPlanId = request.JSON?.supportPlanId?.toLong()
		def supportPlan
		if (supportPlanId != null) {
			supportPlan = SupportPlan.find{id == supportPlanId}
			if (supportPlan == null) {
				render status: 400, text: "Invalid support plan ID"
				return
			}
		}
		
		def customer = new Customer(
			cspCustomerId: request.JSON?.cspCustomerId,
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
		Customer customer = Customer.get(params.id.toLong())
		if(customer == null) {
			render status:404
		}
		def cspCustomerId = request.JSON?.cspCustomerId?.toLong()
		if (cspCustomerId) {
			customer.cspCustomerId = cspCustomerId
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
