package condense



import static org.springframework.http.HttpStatus.*
import grails.transaction.Transactional

@Transactional(readOnly = true)
class PricingBookController {
	
	PricingBookService pricingBookService

    static allowedMethods = [save: "POST", update: "PUT", delete: "DELETE"]

    def index(Integer max) {
        params.max = Math.min(max ?: 10, 100)
        respond PricingBook.list(params), model:[pricingBookInstanceCount: PricingBook.count()]
    }

    def show(PricingBook pricingBookInstance) {
        [pricingBookInstance: pricingBookInstance, tierDefinitions: pricingBookInstance?.tierDefinitions]
    }

    def create() {
        respond new PricingBook(params)
    }

    @Transactional
    def save(PricingBook pricingBookInstance) {
        if (pricingBookInstance == null) {
            notFound()
            return
        }

        if (pricingBookInstance.hasErrors()) {
            respond pricingBookInstance.errors, view:'create'
            return
        }

		def file = request.getFile('file')
		def allLines = file.inputStream.text
		pricingBookInstance = pricingBookService.importPricingBook(pricingBookInstance.fromDate, allLines)
		

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.created.message', args: [message(code: 'pricingBook.label', default: 'PricingBook'), pricingBookInstance.id])
                redirect pricingBookInstance
            }
            '*' { respond pricingBookInstance, [status: CREATED] }
        }
    }

    @Transactional
    def delete(PricingBook pricingBookInstance) {

        if (pricingBookInstance == null) {
            notFound()
            return
        }

		def products = pricingBookInstance.tierDefinitions.each() {
			it.product.discard()
		}
		
        pricingBookInstance.delete flush:true

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.deleted.message', args: [message(code: 'PricingBook.label', default: 'PricingBook'), pricingBookInstance.id])
                redirect action:"index", method:"GET"
            }
            '*'{ render status: NO_CONTENT }
        }
    }

    protected void notFound() {
        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.not.found.message', args: [message(code: 'pricingBook.label', default: 'PricingBook'), params.id])
                redirect action: "index", method: "GET"
            }
            '*'{ render status: NOT_FOUND }
        }
    }
}
