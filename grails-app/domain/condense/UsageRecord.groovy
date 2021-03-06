package condense

import java.io.Serializable;
import java.util.Date;

class UsageRecord implements Serializable {
	
	private static final long serialVersionUID = 1

	Date startTime
	Date endTime
	BigDecimal quantity
	String unit
	String meteredId
	String category
	String subcategory
	String name
	String region
	
	/* Automatic timestamping of GORM */
	Date dateCreated
	Date lastUpdated
	
	static belongsTo = [subscription: Subscription]
	
    static constraints = {
		startTime nullable: true
		endTime nullable: true
		quantity nullable: true, scale: 15, max: 99999999999999999999.999999999999999
		unit nullable: true
		meteredId nullable: true
		category nullable: true
		subcategory nullable: true
		name nullable: true
		region nullable: true 
    }
	
	public String toString() {
		return "${name} - ${quantity} - ${startTime} - ${endTime}";
	}
}
