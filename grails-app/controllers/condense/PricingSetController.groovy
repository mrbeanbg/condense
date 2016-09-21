package condense



import static org.springframework.http.HttpStatus.*
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
		
		def matchingTiers = TierDefinition.where {
			pricingBook == currentPricingBook
			product.category == currentCategory
			if (currentSubCategory) {
				product.subcategory == currentSubCategory
			}
			order "id"
		}.list()
		
		respond matchingTiers, model: [
				matchingTiers: matchingTiers
			]
	}
}
