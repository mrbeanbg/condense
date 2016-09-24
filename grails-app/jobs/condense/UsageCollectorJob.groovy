package condense

import java.math.BigDecimal;
import java.util.Date;
import java.text.DateFormat;
import java.text.DecimalFormat
import java.text.SimpleDateFormat

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
			
			def usages = null
			try {
				usages = cspService.getUsage(currentSubscription.subscriptionId, startTimeStr, endTimeStr)
			} catch (Exception ex) {
				log.error("Unable to obtain usage for ${currentSubscription.subscriptionId}", ex)
			}
			
			for (usage in usages) {
				DateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
				def recordStartTime = format.parse(usage['usage_start_time'])
				def recordEndTime = format.parse(usage['usage_end_time'])
				
				new UsageRecord(
					meteredId: usage['meter_id'],
					startTime: recordStartTime,
					endTime: recordEndTime,
					quantity: new BigDecimal(new DecimalFormat("0.######E0").format(usage['quantity'])),
					unit: usage['unit'],
					category: usage['meter_category'],
					subcategory: (usage.containsKey("meter_sub_category") ? usage['meter_sub_category'] : null),
					region: usage['meter_region'],
					name: usage['meter_name'],
					subscription: currentSubscription).save(flush: true)
			}
			
			currentSubscription.usageObtainedUntil = endTime
			currentSubscription.save flush: true
		}
    }
}
