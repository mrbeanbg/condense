package condense



import static org.springframework.http.HttpStatus.*
import grails.transaction.Transactional
import grails.plugin.springsecurity.annotation.Secured;

@Secured(['ROLE_MANAGER', 'ROLE_ADMIN'])
@Transactional(readOnly = true)
class ConfigDbController {

    static allowedMethods = [save: "POST", update: "PUT", delete: "DELETE"]

    def index(Integer max) {
        params.max = Math.min(max ?: 10, 100)
        respond ConfigDb.list(params), model:[configDbInstanceCount: ConfigDb.count()]
    }

    def show(ConfigDb configDbInstance) {
        respond configDbInstance
    }

    def create() {
        respond new ConfigDb(params)
    }

    @Transactional
    def save(ConfigDb configDbInstance) {
        if (configDbInstance == null) {
            notFound()
            return
        }

        if (configDbInstance.hasErrors()) {
            respond configDbInstance.errors, view:'create'
            return
        }

        configDbInstance.save flush:true

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.created.message', args: [message(code: 'configDb.label', default: 'ConfigDb'), configDbInstance.id])
                redirect configDbInstance
            }
            '*' { respond configDbInstance, [status: CREATED] }
        }
    }

    def edit(ConfigDb configDbInstance) {
        respond configDbInstance
    }

    @Transactional
    def update(ConfigDb configDbInstance) {
        if (configDbInstance == null) {
            notFound()
            return
        }

        if (configDbInstance.hasErrors()) {
            respond configDbInstance.errors, view:'edit'
            return
        }

        configDbInstance.save flush:true

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.updated.message', args: [message(code: 'ConfigDb.label', default: 'ConfigDb'), configDbInstance.id])
                redirect configDbInstance
            }
            '*'{ respond configDbInstance, [status: OK] }
        }
    }

    @Transactional
    def delete(ConfigDb configDbInstance) {

        if (configDbInstance == null) {
            notFound()
            return
        }

        configDbInstance.delete flush:true

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.deleted.message', args: [message(code: 'ConfigDb.label', default: 'ConfigDb'), configDbInstance.id])
                redirect action:"index", method:"GET"
            }
            '*'{ render status: NO_CONTENT }
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
