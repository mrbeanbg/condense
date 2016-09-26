package condense
import java.text.SimpleDateFormat;
import java.util.Date;

import grails.converters.*

class RestBillingController {

	BillingService billingService
	
    def index() { 
		def data = ["ala": "bala"]
		def subscription = Subscription.get(params.restSubscriptionsId?.toLong())
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
		def	subscriptionTransactions = billingService.getSubscriptionTransactions(subscription, fromDate, toDate, billingPeriodDays)
		render subscriptionTransactions as JSON
	}
}
