package condense

import java.text.SimpleDateFormat
import java.util.Date;

class PricingBook {
	
	Date fromDate
	
	/* Automatic timestamping of GORM */
	Date dateCreated
	Date lastUpdated
	
	static hasMany = [tierDefinitions: TierDefinition]
	
    static constraints = {
		fromDate blank:false, unique: true
    }
	
	public String toString() {
		def fromDateRepresentation = (fromDate == null) ? "" : new SimpleDateFormat('yyyy-M-d').format(fromDate)
		return "${fromDateRepresentation}";
	}
	
	boolean equals(o) {
		if (this.is(o)) return true
		if (getClass() != o.class) return false
		PricingBook pricingBook = (PricingBook) o
		if (id != null && id == pricingBook.id) return true
		if (fromDate != pricingBook.fromDate) return false
		return true
	}
	
	int hashCode() {
		return fromDate.hashCode()
	}
}
