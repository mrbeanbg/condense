package condense
import java.math.BigDecimal;
import java.util.Date;

import grails.converters.*
import grails.plugin.springsecurity.annotation.Secured;

@Secured(['permitAll'])
class RestMonthlyBillingController {

	BillingService billingService
	
    def index() {
		print params
		def subscription = Subscription.findBySubscriptionId(params.restSubscriptionsId)
		if (subscription == null) {
			render status: 404
			return
		}
		def forMonth = Integer.parseInt(params.month)
		def forYear = Integer.parseInt(params.year)
		
		print forMonth
		
		
		if (!forYear || !forMonth) {
			render status: 404, text: "The month and year parameters are required."
			return
		}
		
		if (forMonth > 12 || forMonth < 1) {
			render status: 400, text: "Invalid value provided for the month parameter"
			return
		}
		
		if (forYear < 2016) {
			render status: 400, text: "Invalid value provided for the year parameter"
			return
		}
		
		Calendar c = Calendar.getInstance()
		c.set(forYear, forMonth-1, 1, 0, 0, 0);
		def fromDate = c.getTime()
		c.add(Calendar.MONTH, 1)
		def toDate = c.getTime()
		c.add(Calendar.DATE, 5)
		def safeToObtainDate = c.getTime()
		
		print safeToObtainDate
		print new Date()
		
		if (new Date() < safeToObtainDate) {
			render status: 409, text: "Too early to obtain billing for the requested month"
			return
		}

		def currencyName = params.currency?.toUpperCase()
		def currencyRate
		if (currencyName!= null && currencyName != "USD") {
			currencyRate = CurrencyRate.find() {currency == currencyName}
			if (currencyRate == null) {
				render status: 400, text: "Invalid or unsupport currency provided."
				return
			}
		}
		
		def monthlyBill = MonthlyBill.findBySubscriptionAndYearAndMonth(subscription, forYear, forMonth)
		
		if (monthlyBill == null) {
			//fromDate = fromDate + 100000
			//toDate = toDate + 100000
			def	subscriptionTransactions = billingService.getSubscriptionTransactions(
				subscription, fromDate, toDate, null, true, currencyRate)
			
			if (subscriptionTransactions?.billingPeriods?.size() > 0) {
				print "size:${subscriptionTransactions.billingPeriods?.size()}"
				subscriptionTransactions.billingPeriods
				monthlyBill = new MonthlyBill(
					month: forMonth,
					year: forYear,
					currency: subscriptionTransactions.currency,
					subscription: subscription,
					billSubtotal: subscriptionTransactions.billingPeriods[0].billingPeriodSubtotal,
					billSupportCharges: subscriptionTransactions.billingPeriods[0].billintPeriodSupportCharges,
					billTotal: subscriptionTransactions.billingPeriods[0].billingPeriodTotal 
				)
				
				subscriptionTransactions.billingPeriods[0].products.each {
					monthlyBill.addToMonthlyTransactions(new MonthlyTransaction(
							productName: it.name,
							productResourceId: it.productGuid,
							productCategory: it.category,
							productSubcategory: it.subcategory,
							productRegion: it.region,
							usage: it.usgeAndPricingDetails[0].usage,
							included: it.usgeAndPricingDetails[0].included,
							totalUsage: it.totalUsage,
							price: it.usgeAndPricingDetails[0].price,
							productSubtotal: it.subTotal
						)
					)
				}
				
				if (!monthlyBill.save(flush:true, failOnError:true)) {
					if (monthlyBill.hasErrors()) {
						response.status = 500
						respond monthlyBill.errors
						return
					}
				}
			}
		}
		
		render monthlyBill as JSON
	}
}
