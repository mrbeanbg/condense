package condense

import java.math.BigDecimal;
import java.util.Date;
import java.text.DecimalFormat

import grails.converters.JSON

class UsageCollectorJob {
	def CspService cspService
	
	def concurrent = false
	
    static triggers = {
      cron name: 'myTrigger', cronExpression: "0 0/1 * * * ?"
    }

    def execute() {
        def allSubscriptions = Subscription.list()
		allSubscriptions.each { currentSubscription ->
			if (currentSubscription.usageObtainedUntil != null && 
				currentSubscription.usageObtainedUntil > new Date() - 1) {
				return
			}
			
			def startTime = (currentSubscription.usageObtainedUntil != null) ? currentSubscription.usageObtainedUntil : new Date() - 1
			def endTime = startTime + 1
			
			def startTimeStr = startTime.format("yyyy-MM-dd", TimeZone.getTimeZone("GMT")) + " 00:00:00Z"
			def endTimeStr = endTime.format("yyyy-MM-dd", TimeZone.getTimeZone("GMT")) + " 00:00:00Z"
			
			print startTimeStr
			print endTimeStr
			
			def usages = null
			try {
				usages = cspService.getUsage(currentSubscription.subscriptionId, startTimeStr, endTimeStr)
			} catch (Exception ex) {
				log.error("Unable to obtain usage for ${currentSubscription.subscriptionId}", ex)
			}
			
			print usages as JSON
			
			UsageRecord.withTransaction { status ->
				for (usage in usages) {
					//					Date startTime
					//					Date endTime
					//					BigDecimal quantity
					//					String unit
					//					String meteredId
					//					String category
					//					String subcategory
					//					String name
					//					String region
					def usageRecord = new UsageRecord(meteredId: usage['meter_id'],
						startTime: usage['usage_start_time'],
						endTime: usage['usage_end_time'],
						quantity: new BigDecimal(new DecimalFormat("0.######E0").format(usage['quantity'])),
						unit: usage['unit'],
						category: usage['meter_category'],
						region: usage['meter_region'],
						name: usage['meter_name']
						)
					if (usage.containsKey("category")) {
						usageRecord.category = usage['category']
					}
					usageRecord.subscription = currentSubscription
					
					
					currentSubscription.addToUsageRecords(usageRecord)
					currentSubscription.usageObtainedUntil = endTime
					currentSubscription.save()
				}
			}
		}
    }
}
