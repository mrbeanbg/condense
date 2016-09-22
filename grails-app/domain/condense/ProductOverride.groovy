package condense

import java.util.Date;

import condense.SupportTier.TierType;

class ProductOverride {
	
	Long includedQuantity
	Long startQuantity
	OverrideType overrideType
	BigDecimal amount
	
	/* Automatic timestamping of GORM */
	Date dateCreated
	Date lastUpdated
	
	static belongsTo = [
		pricingSet: PricingSet,
		product: Product
	]
	

    static constraints = {
		includedQuantity nullable: true
		startQuantity blank: false
		overrideType blank: false
		amount blank: false
    }
	
	public String toString() {
		return "${pricingSet.name} - ${product.name} - ${startQuantity} - ${overrideType} - ${amount}";
	}
	
	boolean equals(o) {
		if (this.is(o)) return true
		if (getClass() != o.class) return false
		ProductOverride productOverride = (ProductOverride) o
		if (id != null && id == productOverride.id) return true
		if (pricingSet != productOverride.pricingSet || startQuantity != productOverride.startQuantity) return false
		return true
	}
	
	int hashCode() {
		return toString().hashCode()
	}
	
	enum OverrideType {
		PERCENT,
		DELTA,
		FIXED
		
		static OverrideType valueOfName(String name) {
			values().find { it.name() == name }
		}
	}
	
	
}
