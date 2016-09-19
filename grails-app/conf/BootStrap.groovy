import condense.*
import grails.converters.JSON;

class BootStrap {
	
	PricingBookService pricingBookService

    def init = { servletContext ->
		new Category(name: "Cloud Services").save()
		new Subcategory(name: "API Management").save()
		new Region(name: "AU East").save()
		
		def newPricingSet = new PricingSet(name: "Default Pricing Set", defaultOverride: 20).save()
		def newSupportPlan = new SupportPlan(name: "New Support Plan ABC").save()
		def newCustomer = new Customer(cspCustomerId: "customer-123", pricingSet: newPricingSet, supportPlan: newSupportPlan).save()
		def newSubscription = new Subscription(subscriptionId: "subscription-123", customer: newCustomer).save()
		
		JSON.registerObjectMarshaller(Customer) {
			def map= [:]
			map['id'] = it.id
			map['cspCustomerId'] = it.cspCustomerId
			map['pricingSetId'] = it.pricingSet?.id
			map['supportPlanId'] = it.supportPlan?.id
			map['subscriptions'] = []
			it.subscriptions.each{ subscription ->
				map['subscriptions'] << [
					'id': subscription.id,
					'subscriptionId': subscription.subscriptionId
				]
			}
			return map
		}
    }
    def destroy = {
    }
}
