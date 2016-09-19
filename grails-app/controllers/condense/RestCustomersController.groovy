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
		
		def pricingSet = PricingSet.find{id == pricingSetId}
		
		if (pricingSet == null) {
			render status: 400, text: "Invalid pricing set ID"
			return
		}
		
		def supportPlanId = request.JSON?.supportPlanId?.toLong()
		def supportPlan
		if (supportPlanId != null) {
			supportPlan = SupportPlan.find{id == pricingSetId}
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
}
