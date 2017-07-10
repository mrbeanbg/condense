package condense

import org.codehaus.groovy.grails.web.sitemesh.GroovyPageLayoutFinder

class CondenseFilters {
	def passwordEncoder
	static final AJAX_LAYOUT = "ajax"
	
	private static final log = org.apache.commons.logging.LogFactory.getLog(this)

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
		
		paramLogger(controller:'*', action:'*') {
			before = {
				log.debug "path ${request.contextPath}"
				log.debug "request params: $params"
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
							print base64DecodedAuthString
							def usernamePasswordSplittedAuth = base64DecodedAuthString.split(":")
							def sentUser = User.findByUsername(usernamePasswordSplittedAuth[0])
							if (passwordEncoder.isPasswordValid(sentUser?.password, usernamePasswordSplittedAuth[1], null)) {
								def userRoleApi = Role.findByAuthority('ROLE_API')
								if (sentUser.authorities.contains(userRoleApi)) {
									params.currentUser = sentUser
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
