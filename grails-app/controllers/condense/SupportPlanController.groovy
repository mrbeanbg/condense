package condense



import static org.springframework.http.HttpStatus.*
import grails.transaction.Transactional
import org.springframework.dao.DataIntegrityViolationException

@Transactional(readOnly = true)
class SupportPlanController {

    static allowedMethods = [save: "POST", update: "PUT", delete: "DELETE"]

    def index(Integer max) {
        params.max = Math.min(max ?: 10, 100)
        respond SupportPlan.list(params), model:[supportPlanInstanceCount: SupportPlan.count()]
    }

    def show(SupportPlan supportPlanInstance) {
		session['detailRows'] = supportPlanInstance.supportTiers.collect {[startAmount: it.startAmount, tierType: it.tierType, rate: it.rate]}
		session['detailRows'] = precalculateSupportTiers(session['detailRows'])
        respond supportPlanInstance, model: [detailRows: session['detailRows']] 
    }

    def create() {
		session['detailRows'] = []
        respond new SupportPlan(params)
    }
	
	def ajax_add_row() {
		def recordToValidate = new SupportTier(
			supportPlan: new SupportPlan(),
			startAmount: params.int('startAmount'),
			tierType: SupportTier.TierType.valueOfName(params.tierType),
			rate: new BigDecimal(params.double('rate'))
			)
		def startAmount = recordToValidate.startAmount
		def tierType = recordToValidate.tierType
		def rate = recordToValidate.rate
		
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
			session['detailRows'].find { it.startAmount == startAmount }) {
			recordToValidate.errors.reject("Already existing Support Tier Definition", "Already existing Support Tier Definition")
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
		session['detailRows'] << [startAmount: startAmount, tierType: tierType, rate: rate]
		session['detailRows'] = precalculateSupportTiers(session['detailRows'])
		
		respond(
			session['detailRows'],
			model:
			[
				detailRows: session['detailRows'],
				showAction: true
			])
	}
	
	def ajax_delete_row() {
		if (!params.containsKey('startAmount') || params.startAmount == null) {
			render status:400
			return
		}
		
		session['detailRows'].removeAll {
			it.startAmount == params.int('startAmount')
		}
		
		session['detailRows'] = precalculateSupportTiers(session['detailRows'])
		
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
	
	private List precalculateSupportTiers(List tiers) {
		List newTiersList = tiers.sort { it.startAmount }
		for (def i=0;i<newTiersList.size();i++) {
			newTiersList.get(i).endAmount = (i<newTiersList.size()-1) ? newTiersList.get(i+1).startAmount : 'Infinity'
		}
		return newTiersList
	}

    @Transactional
    def save(SupportPlan supportPlanInstance) {
        if (supportPlanInstance == null) {
            notFound()
            return
        }

        if (supportPlanInstance.hasErrors()) {
            respond supportPlanInstance.errors, view:'create'
            return
        }

        supportPlanInstance.save flush:true
		
		session['detailRows'].each {
			new SupportTier(supportPlan: supportPlanInstance,
				startAmount: it.startAmount, tierType: it.tierType, rate: it.rate).save(flush:true)
		}

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.created.message', args: [message(code: 'supportPlan.label', default: 'SupportPlan'), supportPlanInstance.id])
                redirect supportPlanInstance
            }
            '*' { respond supportPlanInstance, [status: CREATED] }
        }
    }

    def edit(SupportPlan supportPlanInstance) {
		session['detailRows'] = supportPlanInstance.supportTiers.collect {[startAmount: it.startAmount, tierType: it.tierType, rate: it.rate]}
		session['detailRows'] = precalculateSupportTiers(session['detailRows'])
        respond supportPlanInstance, model: [detailRows: session['detailRows'], showAction: true]
    }

    @Transactional
    def update(SupportPlan supportPlanInstance) {
        if (supportPlanInstance == null) {
            notFound()
            return
        }

        if (supportPlanInstance.hasErrors()) {
            respond supportPlanInstance.errors, view:'edit'
            return
        }
		
		def oldSupportTiers = supportPlanInstance.supportTiers.collect {it}
		
		oldSupportTiers.each {
			it.delete()
			supportPlanInstance.removeFromSupportTiers(it)
		}
		
		session['detailRows'].each {
			new SupportTier(supportPlan: supportPlanInstance,
				startAmount: it.startAmount, tierType: it.tierType, rate: it.rate).save(flush:true)
		}

        supportPlanInstance.save flush:true

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.updated.message', args: [message(code: 'SupportPlan.label', default: 'SupportPlan'), supportPlanInstance.id])
                redirect supportPlanInstance
            }
            '*'{ respond supportPlanInstance, [status: OK] }
        }
    }

    @Transactional
    def delete(SupportPlan supportPlanInstance) {

        if (supportPlanInstance == null) {
            notFound()
            return
        }

		try {
			supportPlanInstance.delete flush:true
		} catch (DataIntegrityViolationException e) {
			flash.error = message(code: 'supportplan.in.use.cannot.be.deleted',
				default: "The Support Plan is in use and can not be deleted.")
			redirect action:"show", id: supportPlanInstance.id, method:"GET"
			return
		}

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.deleted.message', args: [message(code: 'SupportPlan.label', default: 'SupportPlan'), supportPlanInstance.id])
                redirect action:"index", method:"GET"
            }
            '*'{ render status: NO_CONTENT }
        }
    }

    protected void notFound() {
        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.not.found.message', args: [message(code: 'supportPlan.label', default: 'SupportPlan'), params.id])
                redirect action: "index", method: "GET"
            }
            '*'{ render status: NOT_FOUND }
        }
    }
}
