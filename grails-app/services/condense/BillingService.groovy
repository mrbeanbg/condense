package condense

import java.util.Date;

import grails.transaction.Transactional

@Transactional
class BillingService {

	def getOriginalTier(PricingBook currentPricingBook, String productGuid, BigDecimal quantity) {
		def matchedTier = currentPricingBook.tierDefinitions.findAll({
			it.product.guid == productGuid && it.startQuantity <= quantity
		}).max {
			it.startQuantity
		}
		//print matchedTier
		return matchedTier
	}
	
	def getEffectiveOverride(PricingSet pricingSet, String productGuid, BigDecimal quantity) {
		print pricingSet.productOverrides
		def productOverride = pricingSet.productOverrides.findAll({
			it.product.guid == productGuid && it.startQuantity <= quantity
		}).max {
			it.startQuantity
		}
		//print productOverride
		return productOverride
	}
	
    def getEffectivePrice(PricingBook currentPricingBook, PricingSet pricingSet, String productGuid, BigDecimal quantity) {
		print "Quantity: ${quantity}"
		def originalTier = getOriginalTier(currentPricingBook, productGuid, quantity)
		def price = originalTier.price
		print "Original price: ${price}"
		def override = getEffectiveOverride(pricingSet, productGuid, quantity)

		if (override != null) {
			if (override.overrideType.equals(ProductOverride.OverrideType.PERCENT)) {
				price += price*override.amount / 100.0
			} else if (override.overrideType.equals(ProductOverride.OverrideType.DELTA)) {
				price += override.amount
			} else {
				price = override.amount
			}
		}
		print "Override: ${override}"
		def effectivePrice = ['effectivePrice': price, 'includedQuantity': originalTier.includedQuantity]
		print effectivePrice
		return effectivePrice
    }
	
	def getEffectivePricingBooks(Date fromDate, Date toDate) {
		def inPeriodPricingBooks = PricingBook.findAllByFromDateGreaterThanEqualsAndFromDateLessThan(fromDate, toDate)
		def lastestPricingBookBeforePeriod = PricingBook.findAllByFromDateLessThan(fromDate).max {
			it.fromDate
		}
		
		assert lastestPricingBookBeforePeriod != null
		
		if (!inPeriodPricingBooks.contains(lastestPricingBookBeforePeriod)) {
			inPeriodPricingBooks.add(lastestPricingBookBeforePeriod)
		}
		return inPeriodPricingBooks.sort {it.fromDate}
	}
	
	def getProductUsages(Date fromDate, Date toDate) {
		print "===== getProductUsages from ${fromDate} to ${toDate}"
		return [["product": Product.find {guid == "0d1aa0ed-0a5d-4e42-ab7c-a12f55699b33"}, "usage": 100]]
	}
	
	def getSubscriptionUsage(Subscription subscription, Date fromDate, Date toDate) {
		print "===== getSubscriptionUsage ${subscription}"
		//assumed that the records are collected daily
		def allUsages = UsageRecord.createCriteria().list {
			and {
				eq ("subscription.id", subscription.id)
				le ("startTime", fromDate)
				lt ("startTime", toDate)
			}
		}
		allUsages.each {print it}
	}
	
	def getCustomerTransactions(Customer customer, Date fromDate, Date toDate) {
		def usageTransactions = []
		customer.subscriptions.each{
			usageTransactions << ["subscription": it, "usages": getSubscriptionUsage(it, fromDate, toDate)]
		}
		return usageTransactions
	}

}
