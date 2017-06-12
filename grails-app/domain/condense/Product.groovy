package condense

import java.io.Serializable;
import java.util.Date;

class Product implements Serializable {
	
	private static final long serialVersionUID = 1
	
	String name
	String guid
	
	/* Automatic timestamping of GORM */
	Date dateCreated
	Date lastUpdated
	
	static hasMany = [
		tierDefinitions: TierDefinition,
		productOverrides: ProductOverride
	]
	
	static belongsTo = [
		category: Category,
		subcategory: Subcategory,
		region: Region
	]
	
	static constraints = {
		name blank: false
		guid blank: false, unique: true
		subcategory nullable: true
	}
	
	public String toString() {
		return "${name} - ${guid}";
	}
	
	boolean equals(o) {
		if (this.is(o)) return true
		if (getClass() != o.class) return false
		Product product = (Product) o
		if (id != null && id == product.id) return true
		if (guid != product.guid) return false
		return true
	}
	
	int hashCode() {
		return guid.hashCode()
	}

}
