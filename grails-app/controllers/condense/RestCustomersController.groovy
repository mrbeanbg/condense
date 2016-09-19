package condense


class RestCustomersController {
	static responseFormats = ['json']
	
    def index(Integer max) { 
		respond Customer.list()
	}
	
	def show(Customer customer) {
		if(customer == null) {
			render status:404
		}
//		if(!authService.isUserAuthorizedForStore(params.currentUser, store.id)) {
//			render status: 403
//			return
//		}
		respond customer
	}
}
