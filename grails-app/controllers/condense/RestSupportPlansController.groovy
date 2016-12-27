package condense

import grails.plugin.springsecurity.annotation.Secured;

@Secured(['permitAll'])
class RestSupportPlansController {
	static responseFormats = ['json']
	
	def index(Integer max) {
		respond SupportPlan.list()
	}
}
