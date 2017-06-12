package condense

import java.io.Serializable;
import java.util.Date;

class Region implements Serializable {
	
	private static final long serialVersionUID = 1
	
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
		Region region = (Region) o
		if (id != null && id == region.id) return true
		if (name != region.name) return false
		return true
	}
	
	int hashCode() {
		return name.hashCode()
	}
}
