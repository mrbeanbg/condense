package condense

import java.io.Serializable;
import java.util.Date;

class SupportPlan implements Serializable {
	
	private static final long serialVersionUID = 1
	
	String name
	BigDecimal minCharge
	BigDecimal maxCharge
	
	/* Automatic timestamping of GORM */
	Date dateCreated
	Date lastUpdated
	
    static constraints = {
		name blank:false, unique: true
		minCharge nullable:true, scale: 15, max: 99999999999999999999.999999999999999
		maxCharge nullable:true, scale: 15, max: 99999999999999999999.999999999999999
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
		if (id != null && id == supportPlan.id) return true
		if (name != supportPlan.name) return false
		return true
	}
	
	int hashCode() {
		return name.hashCode()
	}
}
