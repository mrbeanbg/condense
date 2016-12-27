package condense
import java.text.SimpleDateFormat;
import java.util.Date;

import grails.converters.*
import grails.plugin.springsecurity.annotation.Secured;

@Secured(['permitAll'])
class RestBillingController {

	BillingService billingService
	
    def index() {
		print params 
		def subscription = Subscription.findBySubscriptionId(params.restSubscriptionsId)
		if (subscription == null) {
			render status: 404
			return
		}
		def dateFormat = new SimpleDateFormat("yyyy-MM-dd")
		dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"))
		
		def fromDate = params.fromDate
		def toDate = params.toDate
		
		if (!fromDate || !toDate) {
			render status: 404, text: "fromDate and toDate are required."
			return
		}
		
		try {
			fromDate = dateFormat.parse(fromDate)
			toDate = dateFormat.parse(toDate)
		} catch (Exception) {
			render status: 404, text: "Invalid dates provided. The format must be yyyy-MM-dd"
			return
		}

		def billingPeriodDays = params.billingPeriodDays?.toInteger()
		def simpleBillingPolicy = !!(params.simpleBillingPolicy?.toInteger())
		def currencyName = params.currency?.toUpperCase()
		def currencyRate
		if (currencyName!= null && currencyName != "USD") {
			currencyRate = CurrencyRate.find() {currency == currencyName}
			if (currencyRate == null) {
				render status: 400, text: "Invalid or unsupport currency provided."
				return
			}
		}
		
		def	subscriptionTransactions = billingService.getSubscriptionTransactions(
			subscription, fromDate, toDate, billingPeriodDays, simpleBillingPolicy, currencyRate)
		render subscriptionTransactions as JSON
	}
}
