package condense

import java.text.SimpleDateFormat
import org.joda.time.DateTime

class ProbaController {
	PricingBookService pricingBookService
	BillingService billingService
	CspService cspService

	def index() {
//		def dateFormat = new SimpleDateFormat("yyyy-MM-dd")
//		print billingService.getDaysInMonth(dateFormat.parse("2015-02-01"))
//		print billingService.getDaysInMonth(dateFormat.parse("2016-03-04"))
//		print billingService.getDaysInMonth(dateFormat.parse("2016-04-04"))
		//testPricingBook()
		//testBillingPeriodsChunks()
		//geteffectivePricingPeriods()
		manualImportUsage()
	}
	def manualImportUsage() {
		def foundCustomer = Customer.findByCspCustomerId("customer-123")
		def customerSubscription = foundCustomer.subscriptions[0]
		//print "BEFORE SEARCH ${customerSubscription}"
		UsageRecord.where { id > new Long(-1) }.deleteAll()
		
		def dateFormatUsage = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ")
		dateFormatUsage.setTimeZone(TimeZone.getTimeZone("GMT"))
		def usageDate = dateFormat.parse("2016-05-02T00:00:00+00:00")
		new UsageRecord(startTime: usageDate, endTime: usageDate + 1, subscription: customerSubscription, quantity: 1, meteredId: "0d1aa0ed-0a5d-4e42-ab7c-a12f55699b33").save()
		new UsageRecord(startTime: usageDate, endTime: usageDate + 1, subscription: customerSubscription, quantity: 2, meteredId: "0d1aa0ed-0a5d-4e42-ab7c-a12f55699b33").save()
		new UsageRecord(startTime: usageDate, endTime: usageDate + 1, subscription: customerSubscription, quantity: 5, meteredId: "cce2c24b-69a1-44cc-9f8a-963aa0c8f648").save()
		new UsageRecord(startTime: usageDate, endTime: usageDate + 1, subscription: foundCustomer.subscriptions[1], metredId: "cce2c24b-69a1-44cc-9f8a-963aa0c8f648", quantity: 4).save()
		
		billingService.getCustomerTransactions(foundCustomer, dateFormat.parse("2016-05-02"), dateFormat.parse("2016-05-16"))
	}
	
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
		new UsageRecord(startTime: usageDate, endTime: usageDate + 1, subscription: customerSubscription, quantity: 1, meteredId: "product A").save()
		new UsageRecord(startTime: usageDate, endTime: usageDate + 1, subscription: customerSubscription, quantity: 2, meteredId: "product A").save()
		new UsageRecord(startTime: usageDate, endTime: usageDate + 1, subscription: customerSubscription, quantity: 5, meteredId: "product B").save()
		new UsageRecord(startTime: usageDate, endTime: usageDate + 1, subscription: foundCustomer.subscriptions[1], quantity: 4).save()
		
		billingService.getCustomerTransactions(foundCustomer, dateFormat.parse("2016-05-02"), dateFormat.parse("2016-05-16"))
		//print billingService.getEffectivePricingBooks(dateFormat.parse("2016-05-01"), dateFormat.parse("2016-05-16"))
	}
	
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
			usages << ["${subscriptions.get(i).subscriptionId}": cspService.getUsage(subscriptions.get(i).subscriptionId, "2016-09-23 00:00:00Z", "2016-09-24 00:00:00Z")]
		}
		
		render usages
	}
	
	def test_time() {
		DateTime dateTime = new DateTime(new Date()).minusDays(1).withTime(0, 0, 0, 0)
		print dateTime
	}
}
