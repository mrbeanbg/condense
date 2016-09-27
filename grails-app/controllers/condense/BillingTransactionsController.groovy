package condense

import org.joda.time.DateTime;

class BillingTransactionsController {
	
	def billingService

    def index(Integer max) {
        params.max = Math.min(max ?: 10, 100)
        respond Subscription.list(params), model:[subscriptionInstanceCount: Subscription.count()]
    }
	
	def show(Subscription subscriptionInstance) {
		def startDate = new DateTime().withDayOfMonth(1).toDate()
		def endDate = new Date()
		
		respond subscriptionInstance,
			model: [startDate: startDate, endDate: endDate]
	}
	
	def obtain_transactions(Subscription subscriptionInstance) {
		if (subscriptionInstance == null) {
			notFound()
			return
		}
		
		def dateFrom = params.dateFrom
		def dateTo = params.dateTo
		
		def currencyRateInstance = (params.currencyRate != null && params.currencyRate != "null") ? CurrencyRate.get(params.currencyRate.toLong()) : null
		
		def subscriptionTransctions
		try {
			subscriptionTransctions = billingService.getSubscriptionTransactions(subscriptionInstance, dateFrom, dateTo, null, false, currencyRateInstance)
		} catch (Exception ex) {
			subscriptionInstance.errors.reject("problem.obtaining.transactions", [subscriptionInstance.subscriptionId, ex.getMessage()] as Object[], "Unable to obtain billing transactions for subscription:{0}. Additional error message: {1}")
		}
		
		respond subscriptionInstance,
			model: [subscriptionTransctions: subscriptionTransctions],
			view: "_transactions_table"
	}
}
