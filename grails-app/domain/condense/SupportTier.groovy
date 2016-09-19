package condense

import java.util.Date;

class SupportTier {
	
	Integer startAmount
	TierType tierType
	BigDecimal rate
	
	/* Automatic timestamping of GORM */
	Date dateCreated
	Date lastUpdated
	
	enum TierType {
		PERCENT,
		FIXED
		
		static TierType valueOfName(String name) {
			values().find { it.name() == name }
		}
	}
	
    static constraints = {
		startAmount blank: false
		tierType blank: false
		rate blank: false
    }
	
	static belongsTo = [supportPlan: SupportPlan]
	
	public String toString() {
		return "${supportPlan.name} - ${startAmount}";
	}
	
	boolean equals(o) {
		if (this.is(o)) return true
		if (getClass() != o.class) return false
		SupportTier supportTier = (SupportTier) o
		if (id != null && id == supportTier.id) return true
		if (supportPlan != supportTier.supportPlan || startAmount != supportTier.startAmount) return false
		return true
	}
	
	int hashCode() {
		return toString().hashCode()
	}
}
