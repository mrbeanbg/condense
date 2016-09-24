package condense

import java.util.Date;

class Subscription {
	
	String subscriptionId
	Date usageObtainedUntil

	/* Automatic timestamping of GORM */
	Date dateCreated
	Date lastUpdated
	
	static belongsTo = [customer: Customer]
	
	static hasMany = [usageRecords: UsageRecord]
	
    static constraints = {
		subscriptionId blank:false, unique:true
		usageObtainedUntil nullable: true
    }
	
	public String toString() {
		return "${subscriptionId}";
	}
	
	boolean equals(o) {
		if (this.is(o)) return true
		if (getClass() != o.class) return false
		Subscription subsciption = (Subscription) o
		if (id != null && id == subsciption.id) return true
		if (subscriptionId != subsciption.subscriptionId) return false
		return true
	}
	
	int hashCode() {
		return subscriptionId.hashCode()
	}
}
