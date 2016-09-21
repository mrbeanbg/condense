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
        respond pricingSetInstance
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
}
