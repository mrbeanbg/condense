package condense



import static org.springframework.http.HttpStatus.*
import grails.transaction.Transactional

import java.text.DateFormat
import java.text.DecimalFormat
import java.text.SimpleDateFormat

import grails.plugin.springsecurity.annotation.Secured;

@Secured(['ROLE_MANAGER', 'ROLE_ADMIN'])
@Transactional(readOnly = true)
class SubscriptionController {
	
	def cspService
	def partnerCenterService

    static allowedMethods = [save: "POST", update: "PUT", delete: "DELETE"]

    def index(Integer max) {
        params.max = Math.min(max ?: 10, 100)
        respond Subscription.list(params), model:[subscriptionInstanceCount: Subscription.count()]
    }

    def show(Subscription subscriptionInstance) {
		params.max = 50
		
		if (!params.containsKey("sort")) {
			params.sort = "startTime"
			params.order = "desc"
		}
		def usageRecords = UsageRecord.where {
			subscription == subscriptionInstance
		}.list(params)
		
		def usageRecordsCount = UsageRecord.where {
			subscription == subscriptionInstance
		}.count()
		
        respond subscriptionInstance, model: [usageRecords: usageRecords, usageRecordsCount: UsageRecord.count()]
    }
	
	def usages(Subscription subscriptionInstance) {
		params.max = 50
		
		if (!params.containsKey("sort")) {
			params.sort = "startTime"
			params.order = "desc"
		}
		def usageRecords = UsageRecord.where {
			if (params.filterFromDate) {
				startTime >= params.filterFromDate
			}
			if (params.filterToDate) {
				endTime <= params.filterToDate
			}
			subscription == subscriptionInstance
		}.list(params)
		
		def usageRecordsCount = UsageRecord.where {
			if (params.filterFromDate) {
				startTime >= params.filterFromDate
			}
			if (params.filterToDate) {
				endTime <= params.filterToDate
			}
			subscription == subscriptionInstance
		}.count()
		
        respond subscriptionInstance,
			model: [usageRecords: usageRecords, usageRecordsCount: usageRecordsCount],
			view: "_usages_table"
	}

	def obtain_usage(Subscription subscriptionInstance) {
		println "============"
		println "obtain_usage"
		
		if (subscriptionInstance == null) {
			notFound()
			return
		}
		
		def startDate = null
		if (subscriptionInstance.usageObtainedUntil != null) {
			startDate = subscriptionInstance.usageObtainedUntil
		} else {
			startDate = new Date() - 1 
		}
		
		def endDate = startDate + 1
		respond subscriptionInstance, model: [
				startDate: startDate,
				endDate: endDate
			]
	}
	
	@Transactional
	def get_usage(Subscription subscriptionInstance) {
		if (subscriptionInstance == null) {
			notFound()
			return
		}
		
		def startDate = null
		def endDate = null
		
		if (subscriptionInstance.usageObtainedUntil != null) {
			startDate = subscriptionInstance.usageObtainedUntil
		} else {
			startDate = params.startDate
		}
		
		endDate = params.endDate
		
		if (request.method == 'GET') {
			endDate = startDate + 1
			respond subscriptionInstance,
				model: [startDate: startDate, endDate: endDate],
				view: "obtain_usage"
			return
		}
		if (startDate == null || endDate == null) {
			subscriptionInstance.errors.reject("either.one.of.the.dates.is.not.defined.error", "Either one of the two dates is not defined. Pelase define Start and End dates. Thank You!")
			respond subscriptionInstance,
				model: [startDate: startDate, endDate: endDate],
				view: "obtain_usage"
			return
		}
		
		if (startDate >= endDate) {
			subscriptionInstance.errors.reject("please.define.correct.password.error", "Pease define correct period. Thank You!")
			respond subscriptionInstance,
				model: [startDate: startDate, endDate: endDate],
				view: "obtain_usage"
			return
		}
		
		print startDate
		print endDate
		
		def startTimeStr = startDate.format("yyyy-MM-dd") + "T00:00:00Z"
		def endTimeStr = endDate.format("yyyy-MM-dd") + "T00:00:00Z"
		
		print startTimeStr
		print endTimeStr
		
		def usages = []
		try {
			usages = partnerCenterService.getUsage(subscriptionInstance.customer.cspCustomerId, subscriptionInstance.subscriptionId, startTimeStr, endTimeStr)
		} catch (Exception ex) {
			subscriptionInstance.errors.reject("either.one.of.the.dates.is.not.defined.error", [subscriptionInstance.subscriptionId, ex.getMessage()] as Object[], "Unable to obtain usage for subscription:{0}. Additional error message: {1}")
			respond subscriptionInstance,
				model: [startDate: startDate, endDate: endDate],
				view: "obtain_usage"
			return
		}
						
		for (usage in usages) {
			DateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX");
			def recordStartTime = format.parse(usage['usageStartTime'])
			def recordEndTime = format.parse(usage['usageEndTime'])
			
			new UsageRecord(
				meteredId: usage['resource']['id'],
				startTime: recordStartTime,
				endTime: recordEndTime,
				quantity: new BigDecimal(new DecimalFormat("0.######E0").format(usage['quantity'])),
				unit: usage['unit'],
				category: usage['resource']['category'],
				subcategory: (usage['resource'].containsKey("subcategory") ? usage['resource']['subcategory'] : null),
				region: usage['resource']['region'],
				name: usage['resource']['name'],
				subscription: subscriptionInstance).save(flush: true)
		}
		
		subscriptionInstance.usageObtainedUntil = endDate
		subscriptionInstance.save flush: true
		
		redirect(action: "show", id: subscriptionInstance.id)
	}


    protected void notFound() {
        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.not.found.message', args: [message(code: 'subscription.label', default: 'Subscription'), params.id])
                redirect action: "index", method: "GET"
            }
            '*'{ render status: NOT_FOUND }
        }
    }
}
