package condense

import java.util.Date;

class TierDefinition {

	Long startQuantity
	Long includedQuantity
	BigDecimal price
	
	/* Automatic timestamping of GORM */
	Date dateCreated
	Date lastUpdated
	
	static belongsTo = [
		pricingBook: PricingBook,
		product: Product
	]
	
    static constraints = {
		startQuantity blank: false
		includedQuantity nullable: true
		price blank: false, scale: 15, max: 99999999999999999999.999999999999999
    }
	
	public String toString() {
		return "IQ: ${includedQuantity} SQ:${startQuantity} - Price:${price}";
	}
	
	boolean equals(o) {
		if (this.is(o)) return true
		if (getClass() != o.class) return false
		TierDefinition tierDefinition = (TierDefinition) o
		if (id != null && id == tierDefinition.id) return true
		if (pricingBook != tierDefinition.pricingBook || startQuantity != tierDefinition.startQuantity) return false
		return true
	}
}
