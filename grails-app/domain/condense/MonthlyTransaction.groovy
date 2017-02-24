package condense

import java.util.Date;

class MonthlyTransaction {
	String id = UUID.randomUUID().toString()
	String productName
	String productResourceId
	String productCategory
	String productSubcategory
	String productRegion
	BigDecimal productUsage
	Long included
	BigDecimal totalUsage
	BigDecimal price
	BigDecimal productSubtotal
	
	/* Automatic timestamping of GORM */
	Date dateCreated
	Date lastUpdated
	
	static belongsTo = [monthlyBill: MonthlyBill]

    static constraints = {
		productName blank: false
		productResourceId blank: false
		productCategory blank: false
		productSubcategory nullable: true
		productRegion blank: false
		productUsage scale: 15, max: 99999999999999999999.999999999999999
		included()
		totalUsage scale: 15, max: 99999999999999999999.999999999999999
		price scale: 15, max: 99999999999999999999.999999999999999
		productSubtotal()
    }
	
	static mapping = {
		id generator:'assigned'
	}
}
