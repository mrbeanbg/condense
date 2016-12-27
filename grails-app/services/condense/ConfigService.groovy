package condense

import grails.transaction.Transactional

@Transactional
class ConfigService {

    def getValue(String fieldKey) {
		def result = null
		def configDb = ConfigDb.findByKey(fieldKey)
		if (configDb != null) {
			result = configDb.fieldVal
		}
		result
    }
}
