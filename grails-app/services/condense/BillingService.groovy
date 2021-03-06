package condense

import java.math.RoundingMode;
import java.util.Date;

import org.hibernate.criterion.CriteriaSpecification;

import grails.transaction.Transactional
import groovy.transform.Synchronized;

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
//	def isSimpleBillingPolicy() {
//		return !!(isSimpleBillingPolicy)
//	}
	
	@Synchronized
	def checkBillingPeriodDates(Date fromDate, Date toDate, Integer billingPeriodDays = null) {
		if (fromDate >= toDate) {
			throw new Exception("Invalid billing period from ${fromDate} to ${toDate}")
		}
		def billingDay = fromDate[Calendar.DAY_OF_MONTH]
		if (billingPeriodDays == null && billingDay == 29) {
			throw new Exception ("Invalid billing day ${billingDay}. Can be only an integer between 1 and 28.")
		}
	}
	
	@Synchronized
	def getOriginalTier(PricingBook currentPricingBook, String productGuid, BigDecimal quantity) {
		def theProduct = Product.findByGuid(productGuid)
		print currentPricingBook.id
		print theProduct.id
		print quantity
		
		def matchingTiers = TierDefinition.where {
			(pricingBook == currentPricingBook &&
				product == theProduct &&
				startQuantity <= quantity)
		}.list()
		
        def matchedTier = matchingTiers.max {
			it.startQuantity
		}
		
		return matchedTier
	}
	
	@Synchronized
	def getEffectiveOverride(PricingSet currentPricingSet, String productGuid, BigDecimal quantity) {
		def productOverrides = ProductOverride.where {
			(pricingSet == currentPricingSet &&
				product.guid == productGuid &&
				startQuantity <= quantity)
		}.list()
		def productOverride = productOverrides.max {
			it.startQuantity
		}
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
	@Synchronized
    def getEffectivePrice(PricingBook currentPricingBook, PricingSet pricingSet, String productGuid, BigDecimal quantity) {
		//print "Quantity: ${quantity}"
		def originalTier = getOriginalTier(currentPricingBook, productGuid, quantity)
		if (originalTier == null) {
			throw new Exception("There is no tier defined for ${quantity} units of product ${productGuid} in pricing book ${currentPricingBook}")
		}
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
		} else if (pricingSet.defaultOverride != null) {
			price += price*pricingSet.defaultOverride / 100.0
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
	@Synchronized
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
	@Synchronized
	def getBillingPeriods(Date fromDate, Date toDate, Integer billingPeriodDays = null) {

		// Get the current month's billing date
		def nextBillingDate

		if (billingPeriodDays == null) {
			nextBillingDate = fromDate + getDaysInMonth(fromDate)
		} else {
			nextBillingDate = fromDate + billingPeriodDays
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
	@Synchronized
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
		
		//Assign the last pricing book to the remaining billing periods
		while (currentPeriodIndex < billingPeriods.size()) {
			currentPeriod = billingPeriods[currentPeriodIndex]
			effectivePeriods << ["fromDate": currentPeriod.fromDate, "toDate": currentPeriod.toDate, "pricingBook": effectivePricingBooks[i]]
			currentPeriodIndex++
		}
		
		println "effectivePeriods ${effectivePeriods}"
		return effectivePeriods
	}
	
	@Synchronized
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
	@Synchronized
	def getBillingEffectivePeriods(Date fromDate, Date toDate, Integer billingPeriodDays = null, Boolean isSimpleBillingPolicy = false) {
		println "*** getBillingEffectivePeriods called with fromDate: ${fromDate}, toDate: ${toDate}, billingPeriodDays: ${billingPeriodDays}, isSimpleBillingPolicy: ${isSimpleBillingPolicy}"
		def effectivePricingBooks = getEffectivePricingBooks(fromDate, toDate)
		println "effectivePricingBooks: ${effectivePricingBooks}"
		if (effectivePricingBooks.size() == 0) {
			throw new Exception("No valid effecive pricing book found")
		}
		def billingEffectivePeriods = []
		
		def billingPeriods = getBillingPeriods(fromDate, toDate, billingPeriodDays)
		println "billingPeriods: ${billingPeriods}"
		if (isSimpleBillingPolicy) {
			return getSimpleEffectivePeriods(billingPeriods, effectivePricingBooks)
		}
		
		return getComplexEffectivePeriods(billingPeriods, effectivePricingBooks)
	}
	
	@Synchronized
	def getDaysInMonth(Date date) {
		Calendar calendar = date.toCalendar();
		return calendar.getActualMaximum(Calendar.DATE);		
	}
	
	@Synchronized
	def getProductUsage(Long subscriptionId, Product product, Date fromDate, Date toDate) {		
		def allUsages = UsageRecord.createCriteria().list {
			and {
				eq ("subscription.id", subscriptionId)
				eq ("meteredId", product.guid)
				ge ("startTime", fromDate)
				lt ("startTime", toDate)
			}
		}
		def totalUsage = allUsages*.quantity.sum()
		
		return totalUsage == null ? 0 : totalUsage
	}
	
	@Synchronized
	def getProductUnit(Long subscriptionId, Product product, Date fromDate, Date toDate) {
		def firstUsage = UsageRecord.createCriteria().list {
			and {
				maxResults (1)
				eq ("subscription.id", subscriptionId)
				eq ("meteredId", product.guid)
				ge ("startTime", fromDate)
				lt ("startTime", toDate)
			}
		}
		
		return firstUsage[0].unit
	}
	
	@Synchronized
	def getProductTransactions(PricingSet pricingSet, Long subscriptionId, Product product, List billingPeriods, List effectivePeriods, Integer billingPeriodDays = null) {
		println "getProductTransactions called with PricingSet: ${pricingSet}, subscriptionId: ${subscriptionId}, product: ${product}, billingPeriods: ${billingPeriods}, effectivePeriods: ${effectivePeriods}, billingPeriodDays: ${billingPeriodDays}"
		if (billingPeriods.size() == 0) {
			return []
		}
					
		def transactions = []
		billingPeriods.each {
			def billingPeriodFromDate = it.fromDate
			def billingPeriodToDate = it.toDate
			def billingDays 
			use(groovy.time.TimeCategory) {
				billingDays = (billingPeriodToDate - billingPeriodFromDate).days
			}
			
			def billingEffectivePeriods = effectivePeriods.findAll { 
				it.fromDate >= billingPeriodFromDate && it.toDate <= billingPeriodToDate}
			
			def effectivePeriodsDetails = []
			def subTotal = 0
			def totalUsage = getProductUsage(subscriptionId, product, billingPeriodFromDate, billingPeriodToDate)
			
			billingEffectivePeriods.each {
				def usage = getProductUsage(subscriptionId, product, it.fromDate, it.toDate)
				
				if (usage > 0) {
					def tiersRepresentations = calculateTiersRepresentations(it.pricingBook, pricingSet, product)
					def expectedPricingTiers = tiersRepresentations['expectedPricesRepresentation']
					if (expectedPricingTiers.size() == 0) {
						return []
					}
					println "----------------------"
					println "expectedPricingTiers: " + expectedPricingTiers
					
					def effectiveFromDate = it.fromDate
					def effectiveToDate = it.toDate
					def effectiveDays
					use(groovy.time.TimeCategory) {
						effectiveDays = (effectiveToDate - effectiveFromDate).days
					}
					
					def actualExpectedPricingTiers = expectedPricingTiers
					def maxTierIndex = 0
					def lastIncludedQuantity = expectedPricingTiers[0]['includedQuantity']
					
					if (expectedPricingTiers.size() > 1) {
						def minReverseTierFound = false
						def resultingReversedExpectedTiers = []
						expectedPricingTiers.reverse().each { tier ->
							if (minReverseTierFound == false) {
								if (tier['startQuantity'] == 0) {
									minReverseTierFound = true
									lastIncludedQuantity = tier['includedQuantity']
								} else if (usage > tier['startQuantity'] + tier['includedQuantity']) {
										minReverseTierFound = true
										lastIncludedQuantity = tier['includedQuantity']
								}
							}
							
							if (minReverseTierFound) {
								resultingReversedExpectedTiers << tier
							}
						}
						//actualExpectedPricingTiers = expectedPricingTiers.reverse().takeRight(expectedPricingTiers.size() - minReverseTierIndex).reverse()
						actualExpectedPricingTiers = resultingReversedExpectedTiers.reverse()
					}
					
					println "actualExpectedPricingTiers: " + actualExpectedPricingTiers
					
					def usagePricesAndIncludes = []
					def usageReminder = usage
					println "usage: ${usage}"
					actualExpectedPricingTiers.eachWithIndex { actualExpectedPricingTier, index ->
						println ">>>>>>tier: ${actualExpectedPricingTier}"
						
						def expectedPrice = actualExpectedPricingTier['expectedPrice']
						def includedQuantity = 0
						if (index == 0) {
							includedQuantity = lastIncludedQuantity
						}
							
						def usageForCurrentTier = 0
						if (actualExpectedPricingTier['endQuantity'] == 'Infinity') {
							println "+++++++++INDEX ${index}"
							usageForCurrentTier = usageReminder
						} else {
							println "+++++++++INDEXo ${index}"
							usageForCurrentTier = usageReminder.min(actualExpectedPricingTier['endQuantity'] + includedQuantity)
						}
						
						println "usageForCurrentTier: ${usageForCurrentTier}"

						usageReminder = usageReminder - usageForCurrentTier
						
						println "usageReminder: ${usageReminder}"
						
						
						
						def includedForPeriod = (includedQuantity * effectiveDays * 1.0) / billingDays
						
						println "includedForPeriod: ${includedForPeriod}"
						
						//For better precision
						def includedAmountForPeriod = (includedQuantity * expectedPrice * effectiveDays * 1.0) / billingDays
						
						println "includedAmountForPeriod: ${includedAmountForPeriod}"
						
						usagePricesAndIncludes << ["usage": usageForCurrentTier,
							"price": expectedPrice,
							"included": includedQuantity,
							"includedForPeriod": includedForPeriod,
							"includedAmountForPeriod": includedAmountForPeriod
						]
						
						println "usagePricesAndIncludes: ${usagePricesAndIncludes}"

						subTotal += usageForCurrentTier * expectedPrice - includedAmountForPeriod
						
						println "subTotal: ${subTotal}"
					}
					
					effectivePeriodsDetails << ["fromDate": it.fromDate,
						"toDate": it.toDate,
						"usagePricesAndIncludes": usagePricesAndIncludes
					]
					
				}
			}
			
			println "effectivePeriodsDetails: ${effectivePeriodsDetails}"
			
			transactions << ["effectivePeriods": effectivePeriodsDetails,
				"fromDate": billingPeriodFromDate,
				"toDate": billingPeriodToDate,
				"subTotal": subTotal > 0 ? subTotal : 0,
				"totalUsage": totalUsage]
		}
		
		println "transactions: ${transactions}"
		return transactions
	}
	
	@Synchronized
	def getSubscriptionTransactions(Subscription subscription, Date fromDate, Date toDate, 
			Integer billingPeriodDays = null, Boolean isSimpleBillingPolicy = false, CurrencyRate currencyRate) {
		checkBillingPeriodDates(fromDate, toDate, billingPeriodDays)
		println "===== getSubscriptionUsage ${subscription}"
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
			def effectivePeriods = getBillingEffectivePeriods(fromDate, toDate, billingPeriodDays, isSimpleBillingPolicy)
			
			allUsages.each {
//				if (it.guid != '0f521674-5ebd-4679-bd97-8bc2ac4a9040' && it.guid != '077a07bb-20f8-4bc6-b596-ab7211a1e247') {
//					return
//				}
				
				if (it.totalUsage > 0) {
					def productGuid = it.guid
					
					def product = Product.find {guid == productGuid}
					if (product == null) {
						throw new Exception("Cannot find product ${productGuid}")
					}
					
					def productName = product.name
					def category = product.category.name
					def subcategory = product.subcategory?.name
					def region = product.region.name
					
					
					
					def productsDetails = getProductTransactions(pricingSet, subscription.id,
						product, billingPeriods, effectivePeriods, billingPeriodDays)
					def productUnit = getProductUnit(subscription.id, product, fromDate, toDate)
					
					println groovy.json.JsonOutput.toJson(productsDetails)
					
					def newProductsDetails = []
					productsDetails.each { productDetail ->
						productDetail['effectivePeriods'].each { effectivePeriod ->
							effectivePeriod['usagePricesAndIncludes'].each { usagePricesAndIncludes ->
								def newEffectivePeriodsDetails = []
								newEffectivePeriodsDetails << [
									"fromDate": effectivePeriod.fromDate,
									"toDate": effectivePeriod.toDate,
									"usage": usagePricesAndIncludes['usage'],
									"price": usagePricesAndIncludes['price'],
									"included": usagePricesAndIncludes['included'],
									"includedForPeriod": usagePricesAndIncludes['includedForPeriod']
								]
								
								def subTotal = usagePricesAndIncludes['usage'] * usagePricesAndIncludes['price'] - usagePricesAndIncludes['includedAmountForPeriod']
								newProductsDetails << ["effectivePeriods": newEffectivePeriodsDetails,
									"fromDate": productDetail['fromDate'],
									"toDate": productDetail['toDate'],
									"subTotal": subTotal > 0 ? subTotal : 0,
									"totalUsage": usagePricesAndIncludes['usage']]
							}
						}
					}
					
					println groovy.json.JsonOutput.toJson(newProductsDetails)
					
					subscriptionDetails << ["productGuid": productGuid, 
											"name": productName, 
											"category": category,
											"subcategory": subcategory,
											"region": region,
											"productUnit": productUnit,
											"details" : newProductsDetails]
				}
			}
		}
		
		subscriptionDetails = this.changeTheSubscriptionDetailsRepresentations(subscription, subscriptionDetails, currencyRate)
		
		return subscriptionDetails
	}
	
	@Synchronized
	private changeTheSubscriptionDetailsRepresentations(subscriptionInstance, subscriptionDetails, currencyRateInstance) {
		def currencyRate = 1
		def currencyAbbreviation = "USD"
		
		if (currencyRateInstance != null) {
			currencyRate = currencyRateInstance.rate
			currencyAbbreviation = currencyRateInstance.currency
		}
		
		def billingPeriods = [:]
		
		subscriptionDetails.each { currentProductDetailO ->
			currentProductDetailO.details.each { currentBillingPeriodO ->
				if (!billingPeriods.containsKey(currentBillingPeriodO.fromDate)) {
					billingPeriods.put(currentBillingPeriodO.fromDate, [
							fromDate: currentBillingPeriodO.fromDate,
							toDate: currentBillingPeriodO.toDate,
							products: [],
							billingPeriodSubtotal: 0,
							billintPeriodSupportCharges: 0,
							billingPeriodTotal: 0,
						]
					)
				}
			}
		}
		
		subscriptionDetails.each { currentProductDetailO ->
			currentProductDetailO.details.each { currentBillingPeriodO ->
				def currentBillingPeriod = billingPeriods.get(currentBillingPeriodO.fromDate)

				def effectivePeriods = currentBillingPeriodO.effectivePeriods.collect {
					it.price = it.price * currencyRate
					return it
				}
				currentBillingPeriod.products << [
						productGuid: currentProductDetailO.productGuid,
						name: currentProductDetailO.name,
						category: currentProductDetailO.category,
						subcategory: currentProductDetailO.subcategory,
						region: currentProductDetailO.region,
						unit: currentProductDetailO.productUnit,
						subTotal: new BigDecimal(currentBillingPeriodO.subTotal * currencyRate).setScale(2, RoundingMode.HALF_UP),
						totalUsage: currentBillingPeriodO.totalUsage,
						usgeAndPricingDetails: effectivePeriods
					]
				currentBillingPeriod.billingPeriodSubtotal +=  new BigDecimal(currentBillingPeriodO.subTotal * currencyRate).setScale(2, RoundingMode.HALF_UP)
				currentBillingPeriod.billingPeriodTotal =  currentBillingPeriod.billingPeriodSubtotal
			}
		}
		
		def supportTiers = []
		def minSupportCharge
		def maxSupportCharge
		
		if (subscriptionInstance.customer.supportPlan != null) {
			minSupportCharge = subscriptionInstance.customer.supportPlan.minCharge
			maxSupportCharge = subscriptionInstance.customer.supportPlan.maxCharge
			
			if (minSupportCharge != null) {
				minSupportCharge *= currencyRate
			}
			
			if (maxSupportCharge != null) {
				maxSupportCharge *= currencyRate
			}
			
			supportTiers = SupportTier.where {
				supportPlan == subscriptionInstance.customer.supportPlan
			}.list(sort: "startAmount", order: "desc")
		}
		
		
		billingPeriods.each { billingPeriod ->
			def theSupportCharges = 0
			if (billingPeriod.value.billingPeriodSubtotal > 0) {
				if (supportTiers?.size() > 0) {
					def matchingTier = supportTiers.find {
						it.startAmount * currencyRate <= billingPeriod.value.billingPeriodSubtotal
					}
					
					
					if (matchingTier.tierType == SupportTier.TierType.FIXED) {
						theSupportCharges = matchingTier.rate * currencyRate
					} else {
						theSupportCharges = billingPeriod.value.billingPeriodSubtotal * matchingTier.rate/100
					}
				}
			}
			
			if (minSupportCharge != null && theSupportCharges < minSupportCharge) {
				theSupportCharges = minSupportCharge
			}
			
			if (maxSupportCharge != null && theSupportCharges > maxSupportCharge) {
				theSupportCharges = maxSupportCharge
			}
			
			billingPeriod.value.billintPeriodSupportCharges = new BigDecimal(theSupportCharges).setScale(2, RoundingMode.HALF_UP)
			billingPeriod.value.billingPeriodTotal = new BigDecimal(billingPeriod.value.billingPeriodSubtotal + billingPeriod.value.billintPeriodSupportCharges).setScale(2, RoundingMode.HALF_UP)
		}
		
		return [billingPeriods: billingPeriods.values(), currency: currencyAbbreviation]
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
	@Synchronized
	def getCustomerTransactions(Customer customer, Date fromDate, Date toDate, Integer billingPeriodDays = null) {
		checkBillingPeriodDates(fromDate, toDate, billingPeriodDays)
		def subscriptionDetails = []
		customer.subscriptions.each{
			subscriptionDetails << ["subscription": it.subscriptionId, "details": getSubscriptionTransactions(it, fromDate, toDate, billingPeriodDays)]
		}
		// TODO: return also the grand total and any other requested summary
		return ["subscriptions": subscriptionDetails]
	}
	
	@Synchronized
	def calculateTiersRepresentations(PricingBook currentPricingBook, PricingSet currentPricingSet, Product currentProduct) {
		
		def tierDefinitions = TierDefinition.where {
			pricingBook == currentPricingBook
			product == currentProduct
		}.list(sort: "startQuantity", order: "asc")
		
		def productOverrides = ProductOverride.where {
			pricingSet == currentPricingSet
			product == currentProduct
		}.list(sort: "startQuantity", order: "asc")
		
		def overridesRepresentation = []
		for (def i=0;i<productOverrides.size();i++) {
			def currentEndQuantity = (i<productOverrides.size()-1) ? productOverrides.get(i+1).startQuantity : 'Infinity'
			overridesRepresentation << [
				id: productOverrides.get(i).id,
				includedQuantity: productOverrides.get(i).includedQuantity,
				startQuantity: productOverrides.get(i).startQuantity,
				endQuantity: currentEndQuantity,
				overrideType: productOverrides.get(i).overrideType,
				amount: productOverrides.get(i).amount
				]
		}
		
		def tierDefinitionsRepresentation = []
		for (def i=0;i<tierDefinitions.size();i++) {
			def currentEndQuantity = (i<tierDefinitions.size()-1) ? tierDefinitions.get(i+1).startQuantity : 'Infinity'
			tierDefinitionsRepresentation << [
				includedQuantity: tierDefinitions.get(i).includedQuantity,
				startQuantity: tierDefinitions.get(i).startQuantity,
				endQuantity: currentEndQuantity,
				price: tierDefinitions.get(i).price
				]
		}
		
		def expectedPrices = []
		
		if (!tierDefinitionsRepresentation.empty) {
			if (overridesRepresentation.empty) {
				tierDefinitions.each {
					expectedPrices << [
							includedQuantity: it.includedQuantity,
							startQuantity: it.startQuantity,
							originalPrice: it.price,
							adjustment: "${currentPricingSet.defaultOverride} PERCENT",
							expectedPrice: it.price * (1 + currentPricingSet.defaultOverride/100)
						]
				}
			} else {
				def newStarts = []
				newStarts = (overridesRepresentation*.startQuantity + tierDefinitionsRepresentation*.startQuantity).unique()
				
				newStarts.each { theNewStart ->
					def theOverride = overridesRepresentation.find {
						if (it.endQuantity instanceof String && it.endQuantity == "Infinity") {
							return it.startQuantity <= theNewStart
						}
						return it.startQuantity <= theNewStart && it.endQuantity > theNewStart
					}
					
					def theTier = tierDefinitionsRepresentation.find {
						if (it.endQuantity == "Infinity") {
							return it.startQuantity <= theNewStart
						}
						return it.startQuantity <= theNewStart && it.endQuantity > theNewStart
					}
					
					def adjustment = ""
					def expectedPrice = 0
					if (theOverride.overrideType == ProductOverride.OverrideType.PERCENT) {
						adjustment = "${theOverride.amount} PERCENT"
						expectedPrice = theTier.price * (1 + theOverride.amount/100)
					} else if (theOverride.overrideType == ProductOverride.OverrideType.DELTA) {
						adjustment = "${theOverride.amount} DELTA"
						expectedPrice = theTier.price + theOverride.amount
					} else {
						adjustment = "${theOverride.amount} FIXED"
						expectedPrice = theOverride.amount
					}
					
					expectedPrices << [
						includedQuantity: theOverride.includedQuantity,
						startQuantity: theNewStart,
						originalPrice: theTier.price,
						adjustment: adjustment,
						expectedPrice: expectedPrice
					]
				}
			}
		}
		
		def expectedPricesRepresentation = []
		
		for (def i=0;i<expectedPrices.size();i++) {
			def currentEndQuantity = (i<expectedPrices.size()-1) ? expectedPrices.get(i+1).startQuantity : 'Infinity'
			expectedPricesRepresentation << [
				includedQuantity: expectedPrices.get(i).includedQuantity,
				startQuantity: expectedPrices.get(i).startQuantity,
				endQuantity: currentEndQuantity,
				originalPrice: expectedPrices.get(i).originalPrice,
				adjustment: expectedPrices.get(i).adjustment,
				expectedPrice: expectedPrices.get(i).expectedPrice
				]
		}

		return [
			overridesRepresentation: overridesRepresentation,
			tierDefinitionsRepresentation: tierDefinitionsRepresentation,
			expectedPricesRepresentation: expectedPricesRepresentation
		]
	}

}
