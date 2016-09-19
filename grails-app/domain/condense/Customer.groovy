package condense

import java.util.Date;

class Customer {
	
	String cspCustomerId
	PricingSet pricingSet
	SupportPlan supportPlan
	
	/* Automatic timestamping of GORM */
	Date dateCreated
	Date lastUpdated
	
	static hasMany = [
		subscriptions: Subscription
	]
	
    static constraints = {
		cspCustomerId blank: false, unique: true
		pricingSet()
		supportPlan nullable: true
    }
	
	public String toString() {
		return "${cspCustomerId}";
	}
	
	boolean equals(o) {
		if (this.is(o)) return true
		if (getClass() != o.class) return false
		Customer customer = (Customer) o
		if (id != null && id == customer.id) return true
		if (cspCustomerId != customer.cspCustomerId) return false
		return true
	}
	
	int hashCode() {
		return cspCustomerId.hashCode()
	}
}
