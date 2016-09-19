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
		price blank: false
    }
	
	public String toString() {
		return "${pricingBook.fromDate} - ${product.name} - SQ:${startQuantity} - ${price}";
	}
	
	boolean equals(o) {
		if (this.is(o)) return true
		if (getClass() != o.class) return false
		TierDefinition tierDefinition = (TierDefinition) o
		if (id == tierDefinition.id) return true
		if (pricingBook != tierDefinition.pricingBook || startQuantity != tierDefinition.startQuantity) return false
		return true
	}
	
	int hashCode() {
		return toString().hashCode()
	}
}
