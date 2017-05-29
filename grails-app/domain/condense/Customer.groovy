package condense

import java.util.Date;
import condense.ProductOverride.OverrideType;
import java.io.Serializable;

class Customer implements Serializable {
	
	private static final long serialVersionUID = 1
	
	String cspCustomerPrimaryDomain
	String cspCustomerId
	String cspDomain
	String externalId
	PricingSet pricingSet
	SupportPlan supportPlan
	
	/* Automatic timestamping of GORM */
	Date dateCreated
	Date lastUpdated
	
	static hasMany = [
		subscriptions: Subscription
	]
	
    static constraints = {
		cspCustomerPrimaryDomain blank: false, unique: true
		cspCustomerId blank: false, unique: true
		cspDomain()
		externalId()
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
