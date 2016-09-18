package condense

import java.util.Date;

class PricingSet {
	
	String name
	Integer defaultOverride
	
	/* Automatic timestamping of GORM */
	Date dateCreated
	Date lastUpdated
	
	
    static constraints = {
		name blank:false, unique:true
		defaultOverride blank:false
    }
	
	static hasMany = [
		customers: Customer,
		productOverrides: ProductOverride
	]
	
	public String toString() {
		return "${name}";
	}
	
	boolean equals(o) {
		if (this.is(o)) return true
		if (getClass() != o.class) return false
		PricingSet pricingSet = (PricingSet) o
		if (id == pricingSet.id) return true
		if (name != pricingSet.name) return false
		return true
	}
	
	int hashCode() {
		return name.hashCode()
	}
	
}
