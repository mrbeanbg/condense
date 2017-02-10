import condense.*
import grails.converters.JSON;
import org.codehaus.groovy.grails.web.converters.configuration.*

class BootStrap {
	
	PricingBookService pricingBookService

    def init = { servletContext ->
		TimeZone.setDefault(TimeZone.getTimeZone("UTC"))
		System.setProperty("user.timezone", "UTC")
		
		new Category(name: "Cloud Services").save()
		new Subcategory(name: "API Management").save()
		new Region(name: "AU East").save()
		
		def newPricingSet = new PricingSet(name: "Default Pricing Set", defaultOverride: 20).save()
		def newSupportPlan = new SupportPlan(name: "New Support Plan ABC").save()
		new SupportPlan(name: "New Support Plan 2").save()
		def newCustomer = new Customer(cspCustomerId: "customer-123", pricingSet: newPricingSet, supportPlan: newSupportPlan).save()
		def newSubscription = new Subscription(subscriptionId: "subscription-123", customer: newCustomer).save()
		new Subscription(subscriptionId: "subscription-ABC", customer: newCustomer).save()
		
		def userRoleAdmin = Role.findOrSaveWhere(authority: 'ROLE_ADMIN')
		def userRoleManager = Role.findOrSaveWhere(authority: 'ROLE_MANAGER')
		def userRoleApi = Role.findOrSaveWhere(authority: 'ROLE_API')
		
		def userAdmin
		def userManager
		def userApi
		if (User.count() == 0) {
			userAdmin = new User(username: "admin", password: "1234")
									.save(failOnError: true)

			userManager = new User(username: "manager", password: "1234")
									.save(failOnError: true)
									
			userApi = new User(username: "api", password: "1234")
									.save(failOnError: true)
			
			assert User.count() == 3
			
			if (!userAdmin.authorities.contains(userRoleAdmin)) {
				UserRole.create(userAdmin, userRoleAdmin, true)
			}
			if (!userManager.authorities.contains(userRoleManager)) {
				UserRole.create(userManager, userRoleManager, true)
			}
			if (!userApi.authorities.contains(userRoleApi)) {
				UserRole.create(userApi, userRoleApi, true)
			}
		}
		
		if (ConfigDb.count() == 0) {
			new ConfigDb(fieldKey: "defaultPricingSet", fieldVal: null).save(failOnError: true)
			new ConfigDb(fieldKey: "defaultSupportPlan", fieldVal: null).save(failOnError: true)
		}
		
		JSON.registerObjectMarshaller(Customer) {
			def map= [:]
			map['cspCustomerId'] = it.cspCustomerId
			map['cspDomain'] = it.cspDomain
			map['externalId'] = it.externalId
			map['pricingSetId'] = it.pricingSet?.id
			map['supportPlanId'] = it.supportPlan?.id
			return map
		}
		
		JSON.registerObjectMarshaller(Subscription) {
			def map= [:]
			map['subscriptionId'] = it.subscriptionId
			map['cspCustomerId'] = it.customer.cspCustomerId
			map['isActive'] = 'true' ? it.isActive : 'false' 
			return map
		}
		
		JSON.registerObjectMarshaller(SupportPlan) {
			def map= [:]
			map['id'] = it.id
			map['name'] = it.name
			map['minCharge'] = it.minCharge
			map['maxCharge'] = it.maxCharge
			return map
		}
		
		JSON.registerObjectMarshaller(MonthlyBill) {
			def map= [:]
			map['id'] = it.id
			map['month'] = it.month
			map['year'] = it.year
			map['billSubtotal'] = it.billSubtotal
			map['billSupportCharges'] = it.billSupportCharges
			map['billTotal'] = it.billTotal
			map['monthlyTransactions'] = []
			it.monthlyTransactions.each { monthlyTransaction->
				map['monthlyTransactions'] << [
					id: monthlyTransaction.id,
					productInvoiceName: "${monthlyTransaction.productName} - ${monthlyTransaction.productCategory}${(monthlyTransaction.productSubcategory==null) ? '': '- ' + monthlyTransaction.productSubcategory}",
					"productName": monthlyTransaction.productName,
					"productResourceId": monthlyTransaction.productResourceId,
					"productCategory": monthlyTransaction.productCategory,
					"productSubcategory": monthlyTransaction.productSubcategory,
					"productRegion": monthlyTransaction.productRegion,
					"productUsage": monthlyTransaction.productUsage,
					"included": monthlyTransaction.included,
					"totalUsage": monthlyTransaction.totalUsage,
					"price": monthlyTransaction.price,
					"productSubtotal": monthlyTransaction.productSubtotal
				]
			}
			return map
		}
		
		DefaultConverterConfiguration<JSON> cfg = (DefaultConverterConfiguration<JSON>)ConvertersConfigurationHolder.getConverterConfiguration(JSON)
		ConvertersConfigurationHolder.setDefaultConfiguration(JSON.class, new ChainedConverterConfiguration<JSON>(cfg, cfg.proxyHandler));
    }
    def destroy = {
    }
}
