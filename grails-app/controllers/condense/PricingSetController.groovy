package condense



import static org.springframework.http.HttpStatus.*
import grails.converters.JSON
import grails.transaction.Transactional
import org.springframework.dao.DataIntegrityViolationException

@Transactional(readOnly = true)
class PricingSetController {

    static allowedMethods = [save: "POST", update: "PUT", delete: "DELETE"]

    def index(Integer max) {
        params.max = Math.min(max ?: 10, 100)
        respond PricingSet.list(params), model:[pricingSetInstanceCount: PricingSet.count()]
    }

    def manage(PricingSet pricingSetInstance) {
		def allCategoriesCombinations = Product.createCriteria().list {
			projections {
				property "category.id"
				property "subcategory.id"
				groupProperty "id"
				groupProperty "category"
				groupProperty "subcategory"
				groupProperty "region"
			}
			and {
				order "id", "asc"
				order "category", "asc"
				order "subcategory", "asc"
				order "region", "asc"
			}
		}
		def categories = []
		allCategoriesCombinations.each {
			def newCategoryCombination = [categoryId: it[0],
				subcategoryId: it[1],
				categoryRepresentation: (it[1]==null) ? "${it[3]}":"${it[3]} - ${it[4]}"]
			if (!categories.contains(newCategoryCombination)) categories << newCategoryCombination
		}
		
		def pricingBooks = PricingBook.createCriteria().list {
			order "fromDate", "desc"
		}
		
        respond pricingSetInstance, model: [
				categories: categories,
				pricingBooks: pricingBooks
			]
    }

    def create() {
        respond new PricingSet(params)
    }

    @Transactional
    def save(PricingSet pricingSetInstance) {
        if (pricingSetInstance == null) {
            notFound()
            return
        }

        if (pricingSetInstance.hasErrors()) {
            respond pricingSetInstance.errors, view:'create'
            return
        }

        pricingSetInstance.save flush:true

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.created.message', args: [message(code: 'pricingSet.label', default: 'PricingSet'), pricingSetInstance.id])
                redirect action:"manage", id: pricingSetInstance.id
            }
            '*' { respond pricingSetInstance, [status: CREATED] }
        }
    }

    def edit(PricingSet pricingSetInstance) {
        respond pricingSetInstance
    }

    @Transactional
    def update(PricingSet pricingSetInstance) {
        if (pricingSetInstance == null) {
            notFound()
            return
        }

        if (pricingSetInstance.hasErrors()) {
            respond pricingSetInstance.errors, view:'edit'
            return
        }

        pricingSetInstance.save flush:true

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.updated.message', args: [message(code: 'PricingSet.label', default: 'PricingSet'), pricingSetInstance.id])
                redirect action:"manage", id: pricingSetInstance.id
            }
            '*'{ respond pricingSetInstance, [status: OK] }
        }
    }

    @Transactional
    def delete(PricingSet pricingSetInstance) {

        if (pricingSetInstance == null) {
            notFound()
            return
        }

		try {
			pricingSetInstance.delete flush:true
		} catch (DataIntegrityViolationException e) {
			flash.error = message(code: 'pricingset.in.use.cannot.be.deleted',
				default: "The Pricing Set is in use and can not be deleted.")
			redirect action:"index", method:"GET"
			return
		}

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.deleted.message', args: [message(code: 'PricingSet.label', default: 'PricingSet'), pricingSetInstance.id])
                redirect action:"index", method:"GET"
            }
            '*'{ render status: NO_CONTENT }
        }
    }

    protected void notFound() {
        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.not.found.message', args: [message(code: 'pricingSet.label', default: 'PricingSet'), params.id])
                redirect action: "index", method: "GET"
            }
            '*'{ render status: NOT_FOUND }
        }
    }
	
	def ajax_get_products() {
		def currentPricingBook = PricingBook.get(params.currentPricingBookId)
		def currentCategory = Category.get(params.currentCategoryId)
		def currentSubCategory = Subcategory.get(params.currentSubCategoryId)
		
		if (currentPricingBook == null || currentCategory == null) {
			render status:400
			return
		}
		
		if (params.currentSubCategoryId != null && params.currentSubCategoryId != ""
			&& currentSubCategory == null) {
			render status:400
			return
		}
		
		def matchingTiers = TierDefinition.where {
			pricingBook == currentPricingBook
			product.category == currentCategory
			if (currentSubCategory) {
				product.subcategory == currentSubCategory
			}
			order "product.id", "asc"
		}.list()
		
		def matchingProducts = matchingTiers*.product.unique()
		
		respond matchingProducts, model: [
				matchingProducts: matchingProducts
			]
	}
	
	def ajax_get_tiers() {
		def currentPricingSet = PricingSet.get(params.currentPricingSetId)
		def currentPricingBook = PricingBook.get(params.currentPricingBookId)
		def currentProduct = Product.get(params.currentProductId)
		
		if (currentPricingSet == null || currentPricingBook == null || currentProduct == null) {
			render status:400
			return
		}
		
		def calculatedTiers = calculateTiersExpectedPrices(currentPricingBook, currentPricingSet, currentProduct)
		
		respond calculatedTiers['overridesRepresentation'], model: [
			currentProduct: currentProduct,
			overridesRepresentation: calculatedTiers['overridesRepresentation'],
			tierDefinitionsRepresentation: calculatedTiers['tierDefinitionsRepresentation'],
			expectedPricesRepresentation: calculatedTiers['expectedPricesRepresentation'],
			showAction: true
		]
	}
	
	def ajax_add_tier() {
		def currentPricingBook = PricingBook.get(params.currentPricingBookId)
		def currentPricingSet = PricingSet.get(params.currentPricingSetId)
		def currentProduct = Product.get(params.currentProductId)
		
		def existingProductOverride = ProductOverride.where {
			startQuantity == params.startQuantity
			product == currentProduct
			pricingSet == currentPricingSet
		}.list()
		
		if (existingProductOverride != null && !existingProductOverride.empty) {
			render text: '{"error": "Tier with the same Start Quantity is already defined"}', status: 400
			return
		}
		
		def productOverride = new ProductOverride(
				includedQuantity: params.includedQuantity,
				startQuantity: params.startQuantity,
				overrideType: ProductOverride.OverrideType.valueOfName(params.overrideType),
				amount: params.amount,
				pricingSet: currentPricingSet,
				product: currentProduct
			)
		
		if (!productOverride.validate()) {
			productOverride.discard()
			render text: productOverride as JSON, status: 400
			return
		}
		
		productOverride.save flush: true
		
		respondWithTiersCalculated(currentPricingBook, currentPricingSet, currentProduct)
	}
	
	def ajax_delete_tier() {
		def tier = ProductOverride.where {
			id == params.tierId
		}.find()
		
		tier.delete flush:true
		
		def currentPricingBook = PricingBook.get(params.currentPricingBookId)
		def currentPricingSet = tier.pricingSet
		def currentProduct = tier.product
		
		respondWithTiersCalculated(currentPricingBook, currentPricingSet, currentProduct)
	}
	
	private respondWithTiersCalculated(PricingBook currentPricingBook,
		PricingSet currentPricingSet, Product currentProduct) {
		
		def calculatedTiers = calculateTiersExpectedPrices(currentPricingBook, currentPricingSet, currentProduct)
		
		
		respond calculatedTiers['overridesRepresentation'], model: [
			currentProduct: currentProduct,
			overridesRepresentation: calculatedTiers['overridesRepresentation'],
			tierDefinitionsRepresentation: calculatedTiers['tierDefinitionsRepresentation'],
			expectedPricesRepresentation: calculatedTiers['expectedPricesRepresentation'],
			showAction: true
		], view: "_tiers_container"
	}
	
	private calculateTiersExpectedPrices(PricingBook currentPricingBook,
		PricingSet currentPricingSet, Product currentProduct) {
		
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
		print expectedPricesRepresentation

		return [
			overridesRepresentation: overridesRepresentation,
			tierDefinitionsRepresentation: tierDefinitionsRepresentation,
			expectedPricesRepresentation: expectedPricesRepresentation
		]
	}
}
