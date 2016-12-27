package condense

import grails.transaction.Transactional

@Transactional
class ConfigService {

    def getValue(String key) {
		def result = null
		def configDb = ConfigDb.findByKey(key)
		if (configDb != null) {
			result = configDb.value
		}
		result
    }
}
