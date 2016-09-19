package condense

import java.util.Date;

class Subcategory {
	
	String name
	
	/* Automatic timestamping of GORM */
	Date dateCreated
	Date lastUpdated
	
	static hasMany = [
		products: Product
	]
	
	static constraints = {
		name blank:false, unique:true
	}
	
	public String toString() {
		return "${name}";
	}
	
	boolean equals(o) {
		if (this.is(o)) return true
		if (getClass() != o.class) return false
		Subcategory subcategory = (Subcategory) o
		if (id != null && id == subcategory.id) return true
		if (name != subcategory.name) return false
		return true
	}
	
	int hashCode() {
		return name.hashCode()
	}
}
