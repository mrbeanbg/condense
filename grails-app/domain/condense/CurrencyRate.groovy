package condense

import java.util.Date;

class CurrencyRate {
	
	String currency
	BigDecimal rate
	
	/* Automatic timestamping of GORM */
	Date dateCreated
	Date lastUpdated

    static constraints = {
		currency blank: false, unique: true
		rate blank: false
    }
	
	public String toString() {
		return "${currency} - ${rate}";
	}
	
	int hashCode() {
		return currency.hashCode()
	}
}
