package condense

import java.util.Date;

class SupportPlan {
	
	String name
	BigDecimal minCharge
	BigDecimal maxCharge
	
	/* Automatic timestamping of GORM */
	Date dateCreated
	Date lastUpdated
	
    static constraints = {
		name blank:false, unique: true
		minCharge nullable:true, scale: 2
		maxCharge nullable:true, scale: 2
    }
	
	static hasMany = [
		customers: Customer,
		supportTiers: SupportTier
	]
	
	public String toString() {
		return "${name}";
	}
	
	boolean equals(o) {
		if (this.is(o)) return true
		if (getClass() != o.class) return false
		SupportPlan supportPlan = (SupportPlan) o
		if (id == supportPlan.id) return true
		if (name != supportPlan.name) return false
		return true
	}
	
	int hashCode() {
		return name.hashCode()
	}
}
