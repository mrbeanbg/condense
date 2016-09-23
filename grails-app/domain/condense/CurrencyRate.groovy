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
		rate blank: false, size: 30, scale: 15
    }
	
	public String toString() {
		return "${currency} - ${rate}";
	}
	
	int hashCode() {
		return currency.hashCode()
	}
}
