package condense

import java.text.SimpleDateFormat

class ProbaController {
	PricingBookService pricingBookService
	BillingService billingService

	def index() {
		testPricingBook()
	}
	
	def testPricingBook() {
		PricingBook.where { gt("id", new Long(-1)) }.deleteAll()
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
		UsageRecord.where { gt("id", new Long(-1)) }.deleteAll()
		def usageDate = dateFormat.parse("2016-05-02")
		new UsageRecord(startTime: usageDate, endTime: usageDate + 1, subscription: customerSubscription, quantity: 1).save()
		new UsageRecord(startTime: usageDate, endTime: usageDate + 1, subscription: customerSubscription, quantity: 2).save()
		new UsageRecord(startTime: usageDate, endTime: usageDate + 1, subscription: customerSubscription, quantity: 3).save()
		new UsageRecord(startTime: usageDate, endTime: usageDate + 1, subscription: foundCustomer.subscriptions[1], quantity: 4).save()
		
		billingService.getCustomerTransactions(foundCustomer, dateFormat.parse("2016-05-02"), dateFormat.parse("2016-05-16"))
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
}
