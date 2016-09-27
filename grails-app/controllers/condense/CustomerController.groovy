package condense



import static org.springframework.http.HttpStatus.*
import grails.transaction.Transactional

@Transactional(readOnly = true)
class CustomerController {

    static allowedMethods = [save: "POST", update: "PUT", delete: "DELETE"]

    def index(Integer max) {
        params.max = Math.min(max ?: 10, 100)
        respond Customer.list(params), model:[customerInstanceCount: Customer.count()]
    }

    def show(Customer customerInstance) {
        respond customerInstance
    }

    def create() {
		session['detailRows'] = []
        respond new Customer(params)
    }
	
	def ajax_add_row() {
		def recordToValidate = new Subscription(customer: new Customer(),
			subscriptionId: params.subscriptionId)
		def subscriptionId = recordToValidate.subscriptionId
		
		if (!recordToValidate.validate()) {
			recordToValidate.discard()
			respond(recordToValidate.errors,
				model:
				[
					recordToValidate: recordToValidate,
					detailRows: session['detailRows'],
					showAction: true
				])
			return
		}
		
		if ((session['detailRows'] != null || !session['detailRows'].empty) &&
			session['detailRows'].find { it.subscriptionId == subscriptionId }) {
			recordToValidate.errors.reject("Already existing subscritption Id", "Already existing subscritption Id")
			respond(recordToValidate.errors,
				model:
				[
					recordToValidate: recordToValidate,
					detailRows: session['detailRows'],
					showAction: true
				])
			return
		}
		
		recordToValidate.discard()
		
		if (session['detailRows'] == null) {
			session['detailRows'] = []
		}
		session['detailRows'] << [subscriptionId: subscriptionId]
		respond(
			session['detailRows'],
			model:
			[
				detailRows: session['detailRows'],
				showAction: true
			])
	}
	
	def ajax_delete_row() {
		if (!params.containsKey('subscriptionId') || params.subscriptionId == null) {
			render status:400
			return
		}
		
		session['detailRows'].removeAll {
			it.subscriptionId == params.subscriptionId
		}
		
		respond(
			session['detailRows'],
			[
				view:'ajax_add_row',
				model:
				[
					detailRows: session['detailRows'],
					showAction: true
				]
			])
	}

    @Transactional
    def save(Customer customerInstance) {
        if (customerInstance == null) {
            notFound()
            return
        }

        if (customerInstance.hasErrors()) {
            respond customerInstance.errors, view:'create'
            return
        }

        customerInstance.save flush:true
		
		session['detailRows'].each {
			new Subscription(customer: customerInstance, subscriptionId: it.subscriptionId).save(flush:true)
		}

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.created.message', args: [message(code: 'customer.label', default: 'Customer'), customerInstance.id])
                redirect customerInstance
            }
            '*' { respond customerInstance, [status: CREATED] }
        }
    }

    def edit(Customer customerInstance) {
		session['detailRows'] = customerInstance.subscriptions
		session['initialSubscriptionsIds'] = customerInstance.subscriptions.collect {it.subscriptionId}
		print session['initialSubscriptionsIds']
		respond(
			customerInstance,
			model:
			[
				detailRows: session['detailRows'],
				showAction: true
			])
    }

    @Transactional
    def update(Customer customerInstance) {
        if (customerInstance == null) {
            notFound()
            return
        }

        if (customerInstance.hasErrors()) {
            respond customerInstance.errors, view:'edit'
            return
        }
		
		// calculate all subscriptionIds that needs to be deleted
		def subscriptionIdsToDelete = []
		session['initialSubscriptionsIds'].each {
			def currentSubscriptionId = it
			if (!session['detailRows'].find {it.subscriptionId == currentSubscriptionId}) {
				subscriptionIdsToDelete << currentSubscriptionId
			}
			//Subscription.delete()
		}
		if (!subscriptionIdsToDelete.empty) {
			subscriptionIdsToDelete.each {
				def subscriptionToRemove = Subscription.findBySubscriptionId(it)
				subscriptionIdsToDelete.each {
					customerInstance.removeFromSubscriptions(subscriptionToRemove)
				}
				subscriptionToRemove.delete(flush:true)
			}
		}
		session['detailRows'].each {
			if (!session['initialSubscriptionsIds'].contains(it.subscriptionId)) {
				new Subscription(customer: customerInstance,
					subscriptionId: it.subscriptionId).save flush:true
			}
		}

        customerInstance.save flush:true

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.updated.message', args: [message(code: 'Customer.label', default: 'Customer'), customerInstance.id])
                redirect customerInstance
            }
            '*'{ respond customerInstance, [status: OK] }
        }
    }

    @Transactional
    def delete(Customer customerInstance) {

        if (customerInstance == null) {
            notFound()
            return
        }

        customerInstance.delete flush:true

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.deleted.message', args: [message(code: 'Customer.label', default: 'Customer'), customerInstance.id])
                redirect action:"index", method:"GET"
            }
            '*'{ render status: NO_CONTENT }
        }
    }

    protected void notFound() {
        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.not.found.message', args: [message(code: 'customer.label', default: 'Customer'), params.id])
                redirect action: "index", method: "GET"
            }
            '*'{ render status: NOT_FOUND }
        }
    }
}
