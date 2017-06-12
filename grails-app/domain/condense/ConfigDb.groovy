package condense

import java.io.Serializable;
import java.util.Date;

class ConfigDb implements Serializable {
	
	private static final long serialVersionUID = 1
	
	String fieldKey
	String fieldVal
	
	/* Automatic timestamping of GORM */
	Date dateCreated
	Date lastUpdated

    static constraints = {
		fieldKey blank: false, unique: true
		fieldVal nullable: true
    }
	
	public String toString() {
		return "${fieldKey}:${fieldVal?:''}";
	}
	
	boolean equals(o) {
		if (this.is(o)) return true
		if (getClass() != o.class) return false
		ConfigDb configDb = (ConfigDb) o
		if (id != null && id == configDb.id) return true
		if (fieldKey != configDb.fieldKey) return false
		return true
	}
	
	int hashCode() {
		return fieldKey.hashCode()
	}
}
