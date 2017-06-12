package condense

import java.io.Serializable;
import java.util.Date;

import condense.SupportTier.TierType;

class ProductOverride implements Serializable {
	
	private static final long serialVersionUID = 1
	
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
		amount blank: false, scale: 15, max: 99999999999999999999.999999999999999
    }
	
	public String toString() {
		return "IQ:${includedQuantity} SQ:${startQuantity} - ${overrideType} - ${amount}";
	}
	
	boolean equals(o) {
		if (this.is(o)) return true
		if (getClass() != o.class) return false
		ProductOverride productOverride = (ProductOverride) o
		if (id != null && id == productOverride.id) return true
		if (pricingSet != productOverride.pricingSet || startQuantity != productOverride.startQuantity) return false
		return true
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
