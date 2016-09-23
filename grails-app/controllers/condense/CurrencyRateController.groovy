package condense



import static org.springframework.http.HttpStatus.*
import grails.transaction.Transactional

@Transactional(readOnly = true)
class CurrencyRateController {

    static allowedMethods = [save: "POST", update: "PUT", delete: "DELETE"]

    def index(Integer max) {
        params.max = Math.min(max ?: 10, 100)
        respond CurrencyRate.list(params), model:[currencyRateInstanceCount: CurrencyRate.count()]
    }

    def show(CurrencyRate currencyRateInstance) {
        respond currencyRateInstance
    }

    def create() {
        respond new CurrencyRate(params)
    }

    @Transactional
    def save(CurrencyRate currencyRateInstance) {
        if (currencyRateInstance == null) {
            notFound()
            return
        }

        if (currencyRateInstance.hasErrors()) {
            respond currencyRateInstance.errors, view:'create'
            return
        }

        currencyRateInstance.save flush:true

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.created.message', args: [message(code: 'currencyRate.label', default: 'CurrencyRate'), currencyRateInstance.id])
                redirect currencyRateInstance
            }
            '*' { respond currencyRateInstance, [status: CREATED] }
        }
    }

    def edit(CurrencyRate currencyRateInstance) {
        respond currencyRateInstance
    }

    @Transactional
    def update(CurrencyRate currencyRateInstance) {
        if (currencyRateInstance == null) {
            notFound()
            return
        }

        if (currencyRateInstance.hasErrors()) {
            respond currencyRateInstance.errors, view:'edit'
            return
        }

        currencyRateInstance.save flush:true

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.updated.message', args: [message(code: 'CurrencyRate.label', default: 'CurrencyRate'), currencyRateInstance.id])
                redirect currencyRateInstance
            }
            '*'{ respond currencyRateInstance, [status: OK] }
        }
    }

    @Transactional
    def delete(CurrencyRate currencyRateInstance) {

        if (currencyRateInstance == null) {
            notFound()
            return
        }

        currencyRateInstance.delete flush:true

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.deleted.message', args: [message(code: 'CurrencyRate.label', default: 'CurrencyRate'), currencyRateInstance.id])
                redirect action:"index", method:"GET"
            }
            '*'{ render status: NO_CONTENT }
        }
    }

    protected void notFound() {
        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.not.found.message', args: [message(code: 'currencyRate.label', default: 'CurrencyRate'), params.id])
                redirect action: "index", method: "GET"
            }
            '*'{ render status: NOT_FOUND }
        }
    }
}
