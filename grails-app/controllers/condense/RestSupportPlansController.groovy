package condense

class RestSupportPlansController {
	static responseFormats = ['json']
	
	def index(Integer max) {
		respond SupportPlan.list()
	}
}
