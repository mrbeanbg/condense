package condense

import java.util.Date;

class ConfigDb {
	String key
	String value
	
	/* Automatic timestamping of GORM */
	Date dateCreated
	Date lastUpdated

    static constraints = {
		key blank: false, unique: true
		value blank: false
    }
	
	public String toString() {
		return "${key}:${value}";
	}
	
	boolean equals(o) {
		if (this.is(o)) return true
		if (getClass() != o.class) return false
		ConfigDb configDb = (ConfigDb) o
		if (id != null && id == configDb.id) return true
		if (key != configDb.key) return false
		return true
	}
	
	int hashCode() {
		return key.hashCode()
	}
}
