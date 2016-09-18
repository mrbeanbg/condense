package condense

import java.util.Date;

class Subsciption {
	
	String subscriptionid

	/* Automatic timestamping of GORM */
	Date dateCreated
	Date lastUpdated
	
	static belongsTo = [customer: Customer]
	
	static hasMany = [usageRecors: UsageRecord]
	
    static constraints = {
		subscriptionid blank:false, unique:true
    }
	
	public String toString() {
		return "${subscriptionid}";
	}
	
	boolean equals(o) {
		if (this.is(o)) return true
		if (getClass() != o.class) return false
		Subsciption subsciption = (Subsciption) o
		if (id == subsciption.id) return true
		if (subscriptionid != subsciption.subscriptionid) return false
		return true
	}
	
	int hashCode() {
		return subscriptionid.hashCode()
	}
}
