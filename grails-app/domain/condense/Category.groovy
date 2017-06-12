package condense

import java.util.Date;

class Category implements Serializable {
	
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
		Category category = (Category) o
		if (id != null && id == category.id) return true
		if (name != category.name) return false
		return true
	}
	
	int hashCode() {
		return name.hashCode()
	}
}
