package condense

class CondenseFilters {	def passwordEncoder
	static final AJAX_LAYOUT = "ajax"

	def filters = {
		applyLayoutForAjax(controller:'*', action:'*') {
			before = {
				params.isAjax = false
				if (request.xhr) {
					params.isAjax = true
					request[GroovyPageLayoutFinder.LAYOUT_ATTRIBUTE] = AJAX_LAYOUT
				}
			}
		}
		
		restBasicAuth(uri: '/rest/**') {
			before = {
				def authString = request.getHeader("Authorization")
				
				def authenticated = false
				if (authString != null) {
					def athStringSplitted = authString.split(" ")
					if (athStringSplitted.length == 2) {
						authString = athStringSplitted[1]
						def base64DecodedAuthString = new String(authString.decodeBase64())
						if (base64DecodedAuthString.contains(":")) {
							def usernamePasswordSplittedAuth = base64DecodedAuthString.split(":")
							if (usernamePasswordSplittedAuth.length == 2) {
								if (usernamePasswordSplittedAuth[0] == "admin" && usernamePasswordSplittedAuth[1] == "admin") {
									authenticated = true
								}
							}
						}
					}
				}
				
				if (!authenticated) {
					render status: 401
					return false
				}
			}
		}
	}
}
