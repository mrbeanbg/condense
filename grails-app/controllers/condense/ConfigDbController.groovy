package condense



import static org.springframework.http.HttpStatus.*
import grails.transaction.Transactional
import grails.plugin.springsecurity.annotation.Secured;

@Secured(['ROLE_ADMIN'])
@Transactional(readOnly = true)
class ConfigDbController {

    static allowedMethods = [save: "POST", update: "PUT", delete: "DELETE"]

    def edit(ConfigDb configDbInstance) {
		def currentDefaultPricingSet = ConfigDb.findByFieldKey("defaultPricingSet")
		print currentDefaultPricingSet
		def currentdefaultSupportPlan = ConfigDb.findByFieldKey("defaultSupportPlan")
        respond currentDefaultPricingSet, [model:[currentDefaultPricingSet: currentDefaultPricingSet,
			currentdefaultSupportPlan: currentdefaultSupportPlan]]
    }

    @Transactional
    def update() {
		def currentDefaultPricingSetVal = params.currentDefaultPricingSet.fieldVal
		def currentdefaultSupportPlanVal = params.currentdefaultSupportPlan.fieldVal
		
		def currentDefaultPricingSet = ConfigDb.findByFieldKey("defaultPricingSet")
		def currentdefaultSupportPlan = ConfigDb.findByFieldKey("defaultSupportPlan")
		
		currentDefaultPricingSet.fieldVal = currentDefaultPricingSetVal
		currentdefaultSupportPlan.fieldVal = currentdefaultSupportPlanVal

        if (currentDefaultPricingSet.hasErrors()) {
            respond currentDefaultPricingSet.errors, view:'edit'
            return
        }

        currentDefaultPricingSet.save flush:true
		
		
		if (currentdefaultSupportPlan.hasErrors()) {
			respond currentdefaultSupportPlan.errors, view:'edit'
			return
		}

		currentdefaultSupportPlan.save flush:true

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.settings.successfully.updated', default: 'Successfully updated!')
                redirect action: 'edit'
            }
            '*'{ respond configDbInstance, view: 'edit', [status: OK] }
        }
    }

    protected void notFound() {
        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.not.found.message', args: [message(code: 'configDb.label', default: 'ConfigDb'), params.id])
                redirect action: "index", method: "GET"
            }
            '*'{ render status: NOT_FOUND }
        }
    }
}
