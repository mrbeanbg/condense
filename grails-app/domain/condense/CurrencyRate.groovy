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
		rate blank: false, scale: 15, max: 99999999999999999999.999999999999999
    }
	
	public String toString() {
		return "${currency} - ${rate}";
	}
	
	int hashCode() {
		return currency.hashCode()
	}
}
