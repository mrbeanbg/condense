package condense

import grails.transaction.Transactional;

import java.text.SimpleDateFormat

import org.joda.time.DateTime
import grails.plugin.springsecurity.annotation.Secured;

@Secured(['ROLE_MANAGER', 'ROLE_ADMIN'])
class ProbaController {
	PricingBookService pricingBookService
	BillingService billingService
	PartnerCenterService partnerCenterService

	def index() {
//		def dateFormat = new SimpleDateFormat("yyyy-MM-dd")
//		print billingService.getDaysInMonth(dateFormat.parse("2015-02-01"))
//		print billingService.getDaysInMonth(dateFormat.parse("2016-03-04"))
//		print billingService.getDaysInMonth(dateFormat.parse("2016-04-04"))
		//testPricingBook()
		//testBillingPeriodsChunks()
		//geteffectivePricingPeriods()
		//manualImportUsage()
		get_usage()
	}
	
	@Transactional
	def manualImportUsage() {
		def testProduct = Product.find {guid == "e41c0dc6-9337-49a0-88a8-68df68c0b6b5"}
		print "${testProduct}"
		def foundCustomer = Customer.findByCspCustomerId("customer-123")
		def customerSubscription = foundCustomer.subscriptions[0]
		//print "BEFORE SEARCH ${customerSubscription}"
		UsageRecord.where { id > new Long(-1) }.deleteAll()
		
		def dateFormatUsage = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ")
		dateFormatUsage.setTimeZone(TimeZone.getTimeZone("GMT"))
		def usageDate = dateFormatUsage.parse("2016-05-02T00:00:00+0000")
		new UsageRecord(startTime: usageDate, endTime: usageDate + 1, subscription: customerSubscription, quantity: 10000, meteredId: "f2dee52c-2ead-4685-b743-d2cac3073ded").save()
		new UsageRecord(startTime: usageDate, endTime: usageDate + 1, subscription: customerSubscription, quantity: 20000, meteredId: "f2dee52c-2ead-4685-b743-d2cac3073ded").save()
		new UsageRecord(startTime: usageDate, endTime: usageDate + 1, subscription: customerSubscription, quantity: 500000, meteredId: "e0d5dad9-c5ce-433d-bea3-2b4c699c3d5e").save()
		new UsageRecord(startTime: usageDate+1, endTime: usageDate + 2, subscription: foundCustomer.subscriptions[1], meteredId: "e0d5dad9-c5ce-433d-bea3-2b4c699c3d5e", quantity: 300000).save()
		new UsageRecord(startTime: usageDate+10, endTime: usageDate + 11, subscription: foundCustomer.subscriptions[1], meteredId: "e0d5dad9-c5ce-433d-bea3-2b4c699c3d5e", quantity: 200000).save()
		def dateFormat = new SimpleDateFormat("yyyy-MM-dd")
		dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"))
		//print billingService.getCustomerTransactions(foundCustomer, dateFormat.parse("2016-05-02"), dateFormat.parse("2016-05-16"))
	}
	
	@Transactional
	def geteffectivePricingPeriods() {
		PricingBook.where { id > new Long(-1) }.deleteAll()
		def dateFormat = new SimpleDateFormat("yyyy-MM-dd")
		new PricingBook(fromDate: dateFormat.parse("2015-02-01")).save()
		new PricingBook(fromDate: dateFormat.parse("2015-05-01")).save()
		new PricingBook(fromDate: dateFormat.parse("2015-05-11")).save()
		new PricingBook(fromDate: dateFormat.parse("2015-05-15")).save()
		new PricingBook(fromDate: dateFormat.parse("2015-05-16")).save()
		new PricingBook(fromDate: dateFormat.parse("2015-06-01")).save()
		new PricingBook(fromDate: dateFormat.parse("2015-06-15")).save()
		
		billingService.getBillingEffectivePeriods(dateFormat.parse("2015-02-10"), dateFormat.parse("2015-08-12")).each {print it}
	}
	def testBillingPeriodsChunks() {
		def dateFormat = new SimpleDateFormat("yyyy-MM-dd")
		billingService.getBillingPeriods(dateFormat.parse("2015-02-10"), dateFormat.parse("2015-08-12")).each {print it}
		print "================"
		billingService.getBillingPeriods(dateFormat.parse("2015-02-10"), dateFormat.parse("2015-08-12"), 30).each {print it}
		print "================"
		billingService.getBillingPeriods(dateFormat.parse("2015-02-10"), dateFormat.parse("2015-08-12"), null, 11).each {print it}
		print "================"
		billingService.getBillingPeriods(dateFormat.parse("2015-02-10"), dateFormat.parse("2015-08-12"), 30, 11).each {print it}
		print "================"
		billingService.getBillingPeriods(dateFormat.parse("2015-02-10"), dateFormat.parse("2015-08-12"), null, 5).each {print it}
		print "================"
		billingService.getBillingPeriods(dateFormat.parse("2015-02-10"), dateFormat.parse("2015-08-12"), 30, 5).each {print it}
	}
	
	@Transactional
	def testPricingBook() {
		PricingBook.where { id > new Long(-1) }.deleteAll()
		def dateFormat = new SimpleDateFormat("yyyy-MM-dd")
		new PricingBook(fromDate: dateFormat.parse("2016-05-01")).save()
		new PricingBook(fromDate: dateFormat.parse("2016-05-10")).save()
		new PricingBook(fromDate: dateFormat.parse("2016-05-15")).save()
		new PricingBook(fromDate: dateFormat.parse("2016-05-16")).save()
		new PricingBook(fromDate: dateFormat.parse("2016-06-01")).save()
		new PricingBook(fromDate: dateFormat.parse("2016-06-15")).save()
		
		def foundCustomer = Customer.findByCspCustomerId("customer-123")
		def customerSubscription = foundCustomer.subscriptions[0]
		//print "BEFORE SEARCH ${customerSubscription}"
		UsageRecord.where { id > new Long(-1) }.deleteAll()
		
		def dateFormatUsage = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ")
		def usageDate = dateFormat.parse("2016-05-02T00:00:00+00:00")
		new UsageRecord(startTime: usageDate, endTime: usageDate + 1, subscription: customerSubscription, quantity: 100, meteredId: "product A").save()
		new UsageRecord(startTime: usageDate, endTime: usageDate + 1, subscription: customerSubscription, quantity: 2000, meteredId: "product A").save()
		new UsageRecord(startTime: usageDate, endTime: usageDate + 1, subscription: customerSubscription, quantity: 50000, meteredId: "product B").save()
		new UsageRecord(startTime: usageDate, endTime: usageDate + 1, subscription: foundCustomer.subscriptions[1], quantity: 400000).save()
		
		//billingService.getCustomerTransactions(foundCustomer, dateFormat.parse("2016-05-02"), dateFormat.parse("2016-05-16"))
		//print billingService.getEffectivePricingBooks(dateFormat.parse("2016-05-01"), dateFormat.parse("2016-05-16"))
	}
	
	@Transactional
    def testPricingEffectivePrice() {
		def file = new File("C:\\Users\\Ani\\Downloads\\pricing.csv")
		def allLines = file.getText()
		def today = new Date()
		def pricingBook = PricingBook.find {it.fromDate.equals(today)} 
		if (pricingBook == null) {
			pricingBook = pricingBookService.importPricingBook(today, allLines)
		}
		def usage = 20000000
		def productGuid = "0d1aa0ed-0a5d-4e42-ab7c-a12f55699b33"
		def foundProduct = Product.find {guid == productGuid}
		def pricingSet = PricingSet.findByName("Default Pricing Set")
		print pricingSet

		if (pricingSet.productOverrides.size() == 0) {
			new ProductOverride(
				startQuantity: 0, overrideType: ProductOverride.OverrideType.PERCENT,
				amount: 50, pricingSet: pricingSet, product: foundProduct).save flush:true
			new ProductOverride(
				startQuantity: 500, overrideType: ProductOverride.OverrideType.FIXED,
				amount: 500, pricingSet: pricingSet, product: foundProduct).save flush:true
			new ProductOverride(
				startQuantity: 600, overrideType: ProductOverride.OverrideType.DELTA,
				amount: 1000, pricingSet: pricingSet, product: foundProduct).save flush:true
		}
		
		billingService.getEffectivePrice(pricingBook, pricingSet, productGuid, 50)
		billingService.getEffectivePrice(pricingBook, pricingSet, productGuid, 550)
		billingService.getEffectivePrice(pricingBook, pricingSet, productGuid, 7000)
	}
	
	def get_usage() {
		def subscriptions = Subscription.list()
		def usages = [:]
		for (def i=0; i<subscriptions.size(); i++) {
			usages << ["${subscriptions.get(i).subscriptionId}": partnerCenterService.getUsage(subscriptions.get(i).customer.cspCustomerPrimaryDomain, subscriptions.get(i).subscriptionId, "2017-05-10T00:00:00Z", "2017-05-11T00:00:00Z")]
		}
		
		render usages
	}
	
	def test_time() {
		DateTime dateTime = new DateTime(new Date()).minusDays(1).withTime(0, 0, 0, 0)
		print dateTime
	}
}
