package condense

import java.util.Date;

import org.hibernate.criterion.CriteriaSpecification;

import grails.transaction.Transactional

@Transactional
class BillingService {
	Boolean isSimpleBillingPolicy
	/**
	 * Check if the billing policy is simple i.e. the last applied prices 
	 * whose effective date is LESS OR EQUALS to the start of the billing period 
	 * will be applied to the whole billing period.
	 * 
	 * Also, when calculating the price for a prorated period the included amount
	 * will not also be prorated if the simple billing policy is used.
	 * 
	 * @return boolean
	 */
	def isSimpleBillingPolicy() {
		return !!(isSimpleBillingPolicy)
	}
	
	def checkBillingPeriodDates(Date fromDate, Date toDate, Integer billingPeriodDays = null) {
		if (fromDate >= toDate) {
			throw new Exception("Invalid billing period from ${fromDate} to ${toDate}")
		}
		def billingDay = fromDate[Calendar.DAY_OF_MONTH]
		if (billingPeriodDays == null && billingDay == 29) {
			throw new Exception ("Invalid billing day ${billingDay}. Can be only an integer between 1 and 28.")
		}
	}
	
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
	
	/**
	 * Find the appropriate effective price and included quantity for a product based on an usage amount
	 * 
	 * @param currentPricingBook the pricing book against which the tier definition will be defined
	 * @param pricingSet the override policy that will be used
	 * @param productGuid the GUID of a product 
	 * @param quantity usage amount
	 * 
	 * @return the price to be charge, 
	 * the amount of included (free) units for the whole billing period
	 */
    def getEffectivePrice(PricingBook currentPricingBook, PricingSet pricingSet, String productGuid, BigDecimal quantity) {
		//print "Quantity: ${quantity}"
		def originalTier = getOriginalTier(currentPricingBook, productGuid, quantity)
		def price = originalTier.price
		//print "Original price: ${price}"
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
		//print "Override: ${override}"
		
		def includedQuantity = override != null ? override.includedQuantity : originalTier.includedQuantity
		def effectivePrice = ['price': price, 'includedQuantity': includedQuantity]
		//print effectivePrice
		return effectivePrice
    }
	
	/**
	 * Get list of pricing book applied from the billing period sorted by the fromDate
	 * @param fromDate the start of the billing period
	 * @param toDate the end of the billing period
	 * @return List of PricingBook
	 */
	def getEffectivePricingBooks(Date fromDate, Date toDate) {
		print PricingBook.list()
		def inPeriodPricingBooks = PricingBook.findAllByFromDateGreaterThanEqualsAndFromDateLessThan(fromDate, toDate).sort {it.fromDate}
		
		if (inPeriodPricingBooks.size() == 0 || inPeriodPricingBooks[0].fromDate != fromDate) {
			// There isn't any  pricing book defined in period [fromDate, toDate). Get the latest pricing book before the billing period.
			def lastestPricingBookBeforePeriod = PricingBook.findAllByFromDateLessThan(fromDate).max {
				it.fromDate
			}
			if (lastestPricingBookBeforePeriod == null) {
				throw new Exception ("Cannot find the first effective pricing book")
			}
			inPeriodPricingBooks.add(0, lastestPricingBookBeforePeriod)
		}

		return inPeriodPricingBooks
	}
	
	/**
	 * Split the period into chunks for which the customers is billed. 
	 * It's considered that fromDate is always the start date of a billing period.
	 * 
	 * @param fromDate the beginning of the period
	 * @param toDate the end of the period
	 * @param billingPeriodDays the number of the days in the billing period. 
	 * If omitted, it's considered that the customer is charged on the same day every month i.e. on the day of fromDate
	 * 
	 * @return billing period chunks
	 */
	def getBillingPeriods(Date fromDate, Date toDate, Integer billingPeriodDays = null) {

		// Get the current month's billing date
		def nextBillingDate

		if (billingPeriodDays == null) {
			nextBillingDate = fromDate + getDaysInMonth(fromDate)
		} else {
			nextBillingDate += billingPeriodDays
		}
		
		def nextFromDate = fromDate
		def billingPeriods = []
		while (nextFromDate < toDate) {
			nextBillingDate = [nextBillingDate, toDate].min()
			billingPeriods << ["fromDate": nextFromDate, "toDate": nextBillingDate]
			nextFromDate = nextBillingDate
			if (nextFromDate < toDate) {
				// Haven't reached toDate yet
				nextFromDate = nextBillingDate
				// Get the next billing date
				if (billingPeriodDays == null) {
					nextBillingDate += getDaysInMonth(nextBillingDate)
				} else {
					nextBillingDate += billingPeriodDays
				}
			}
		}
		return billingPeriods
	}
	
	/**
	 * Get the effective pricing book for each billing period. 
	 * If there are mid-term pricing changes aren't taken in consideration. 
	 * The pricing book which is effective for the beginning of a billing period is applied for the whole term.
	 * 
	 * @param billingPeriods
	 * @param effectivePricingBooks
	 * @return
	 */
	def getSimpleEffectivePeriods(List billingPeriods, List effectivePricingBooks) {
		def effectivePeriods = []
		def currentPeriodIndex = 0
		def currentPeriod = billingPeriods[currentPeriodIndex]
		int i
		for (i = 0; i < effectivePricingBooks.size() - 1; i++) {
			while (currentPeriodIndex < billingPeriods.size() &&
				effectivePricingBooks[i].fromDate <= currentPeriod.fromDate &&
				effectivePricingBooks[i + 1].fromDate > currentPeriod.fromDate) {
				effectivePeriods << ["fromDate": currentPeriod.fromDate, "toDate": currentPeriod.toDate, "pricingBook": effectivePricingBooks[i]]
				currentPeriodIndex++
				currentPeriod = billingPeriods[currentPeriodIndex]
			}
		}
		
		//Assign the last pricing book to the last billing period
		while (currentPeriodIndex < billingPeriods.size()) {
			currentPeriod = billingPeriods[currentPeriodIndex]
			effectivePeriods << ["fromDate": currentPeriod.fromDate, "toDate": currentPeriod.toDate, "pricingBook": effectivePricingBooks[i]]
			currentPeriodIndex++
		}
		return effectivePeriods
	}
	
	def getComplexEffectivePeriods(List billingPeriods, List effectivePricingBooks) {
		if (effectivePricingBooks.size() == 0) {
			return []
		}
		
		def effectivePeriods = []
		def pricingBookIndex = 0
		billingPeriods.eachWithIndex { billingPeriod, index ->
			def currentPricingBook = effectivePricingBooks[pricingBookIndex]
			if (pricingBookIndex == effectivePricingBooks.size() - 1) {
				// Reached the last pricing book i.e. this is the last pricing change and should be applicable the whole billing period
				effectivePeriods << ["fromDate": billingPeriod.fromDate, "toDate": billingPeriod.toDate, "pricingBook": currentPricingBook]
				
			} else {
				def nextPricingBook = effectivePricingBooks[pricingBookIndex + 1]
				def fromDate = billingPeriod.fromDate
				while (pricingBookIndex < (effectivePricingBooks.size() - 1) && nextPricingBook.fromDate < billingPeriod.toDate) {
					effectivePeriods << ["fromDate": fromDate, "toDate": nextPricingBook.fromDate, "pricingBook": currentPricingBook]
					fromDate = nextPricingBook.fromDate
					currentPricingBook = nextPricingBook
					pricingBookIndex++
					nextPricingBook = effectivePricingBooks[pricingBookIndex + 1]
				}
				if (nextPricingBook && nextPricingBook.fromDate < billingPeriod.toDate) {
					// The last pricing book is reached. Split the rest of the billing period in 2
					effectivePeriods << ["fromDate": fromDate, "toDate": nextPricingBook.fromDate, "pricingBook": nextPricingBook]
					effectivePeriods << ["fromDate": nextPricingBook.fromDate, "toDate": billingPeriod.toDate, "pricingBook": nextPricingBook]
				} else {
					effectivePeriods << ["fromDate": fromDate, "toDate": billingPeriod.toDate, "pricingBook": currentPricingBook]
				}
			}
		}
		return effectivePeriods
	}
	
	/**
	 * Get billing periods for which the price should be calculated according to the price list changes
	 * 
	 * @return list of billing periods with relevant effective pricing books 
	 */
	def getBillingEffectivePeriods(Date fromDate, Date toDate, Integer billingPeriodDays = null, Integer billingDay = null) {
		def effectivePricingBooks = getEffectivePricingBooks(fromDate, toDate)
		if (effectivePricingBooks.size() == 0) {
			throw new Exception("No valid effecive pricing book found")
		}
		def billingEffectivePeriods = []
		
		def billingPeriods = getBillingPeriods(fromDate, toDate, billingPeriodDays)
		if (isSimpleBillingPolicy()) {
			return getSimpleEffectivePeriods(billingPeriods, effectivePricingBooks)
		}
		
		return getComplexEffectivePeriods(billingPeriods, effectivePricingBooks)
	}
	
	def getDaysInMonth(Date date) {
		Calendar calendar = date.toCalendar();
		return calendar.getActualMaximum(Calendar.DATE);		
	}
	
	def getProductUsage(Product product, Date fromDate, Date toDate) {
		if (billingPeriodDays == null) {
			billingPeriodDays = getDaysInMonth(fromDate)
		}
		
		assert (toDate - fromDate).days <= billingPeriodDays
		
		def totalUsage = UsageRecord.createCriteria().get {
			and {
				eq ("meteredId", product.guid)
				ge ("startTime", fromDate)
				lt ("startTime", toDate)
			}
			resultTransformer(CriteriaSpecification.ALIAS_TO_ENTITY_MAP)
			projections {
				sum("quantity", "totalUsage")
			}
		}.quantity
		return usage
	}
	
	def getProductTransactions(PricingSet pricingSet, Product product, List billingPeriods, List effectivePeriods, Integer billingPeriodDays = null) {
		if (billingPeriods.size() == 0) {
			return []
		}
		def transactions = []
		billingPeriods.each {
			def billingPeriodFromDate = it.fromDate
			def billingPeriodToDate = it.toDate
			def billingDays = (billingPeriodToDate - billingPeriodFromDate).days
			
			def billingEffectivePeriods = effectivePeriods.findAll { 
				it.fromDate >= billingPeriodFromDate && it.toDate <= billingPeriodToDate}
			
			def effectivePeriodsDetails = []
			def subTotal = 0
			def totalUsage = 0
			billingEffectivePeriods.each {
				def usage = getProductUsage(product, it.fromDate, it.toDate)
				
				def effectivePrice = geteffectivePrice(it.pricingBook, pricingSet, product.guid, usage)
				
				def effectiveDays = (it.fromDate - it.toDate).days
				
				def includedForPeriod = (effectivePrice.includedQuantity *effectiveDays * 1.0) / billingDays
				
				//For better precision
				def includedAmountForPeriod = (effectivePrice.includedQuantity * effectivePrice.price * effectiveDays * 1.0) / billingDays
				
				effectivePeriodsDetails << ["fromDate": it.fromDate, "toDate": it.toDate, 
											"usage": usage, "price": effectivePrice.price, 
											"included": effectivePrice.includedQuantity, "includedForPeriod": includedForPeriod]
				subTotal += usage*effectivePrice.price - includedAmountForPeriod
				totalUsage += usage
			}
			transactions << ["effectivePeriods": effectivePeriodsDetails,
							 "fromDate": billingFromDate,
							 "toDate": billingPeriodToDate,
							 "subTotal": subTotal,
							 "totalUsage": totalUsage]
		}
		
	}
	
	def getSubscriptionTransactions(Subscription subscription, Date fromDate, Date toDate, Integer billingPeriodDays = null) {
		checkBillingPeriodDates(fromDate, toDate, billingPeriodDays)
		print "===== getSubscriptionUsage ${subscription}"
		//assumed that the records are collected daily
		def allUsages = UsageRecord.createCriteria().list {
			and {
				eq ("subscription.id", subscription.id)
				ge ("startTime", fromDate)
				lt ("startTime", toDate)
			}
			resultTransformer(CriteriaSpecification.ALIAS_TO_ENTITY_MAP)
			projections {
				groupProperty("meteredId", "guid")
				sum("quantity", "totalUsage")
			}
		}
		def subscriptionDetails = []
		if (allUsages.size() > 0) {
			def pricingSet = subscription.customer.pricingSet
			
			def billingPeriods = getBillingPeriods(fromDate, toDate, billingPeriodDays)
			def effectivePeriods = getBillingEffectivePeriods(fromDate, toDate, billingPeriodDays)
			
			allUsages.each {
				if (it.totalUsage > 0) {
					def productGuid = it.guid
					def product = Product.find {guid == productGuid}
					def productDetails = getProductTransactions(pricingSet,
						product, billingPeriods, effectivePeriods, billingPeriodDays)
					subscriptionDetails << ["guid": productGuid, "details" : productDetails]
				}
			}
		}
		return subscriptionDetails
	}

	/**
	 * Get a summary of the generated consumptions for a given customer.
	 * 
	 * @param customer
	 * @param fromDate always considered as the beginning of a billing period
	 * @param toDate
	 * @param billingPeriodDays the number of the days in the billing period. 
	 * If omitted it's considered that the customer is charged on the same billing day every month. 
	 * 
	 * @return
	 */
	def getCustomerTransactions(Customer customer, Date fromDate, Date toDate, Integer billingPeriodDays = null) {
		checkBillingPeriodDates(fromDate, toDate, billingPeriodDays)
		def subscriptionDetails = []
		customer.subscriptions.each{
			subscriptionDetails << ["subscription": it.subscriptionId, "details": getSubscriptionTransactions(it, fromDate, toDate, billingPeriodDays, billingDay)]
		}
		// TODO: return also the grand total and any other requested summary
		return ["subscriptions": subscriptionDetails]
	}

}
