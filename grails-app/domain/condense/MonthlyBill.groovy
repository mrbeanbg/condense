package condense

import java.io.Serializable;
import java.util.Date;

class MonthlyBill implements Serializable {
	
	private static final long serialVersionUID = 1
	
	Long month
	Long year
	String currency
	BigDecimal billSubtotal
	BigDecimal billSupportCharges
	BigDecimal billTotal
	
	/* Automatic timestamping of GORM */
	Date dateCreated
	Date lastUpdated
	
	static hasMany = [
		monthlyTransactions: MonthlyTransaction
	]
	
	static belongsTo = [subscription: Subscription]
	
    static constraints = {
		month blank: false
		year blank: false
		currency blank: false
		billSubtotal scale: 2, max: 99999999999999999999.99
		billSupportCharges scale: 2, max: 99999999999999999999.99
		billTotal scale: 2, max: 99999999999999999999.99
    }
}
