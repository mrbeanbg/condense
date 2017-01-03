package condense

import grails.plugins.rest.client.RestBuilder
import grails.transaction.Transactional
import org.codehaus.groovy.grails.commons.GrailsApplication
import org.springframework.util.LinkedMultiValueMap
import org.springframework.util.MultiValueMap

@Transactional
class CspService {
	
	GrailsApplication grailsApplication
	
	def cachedAzureAdToken = [adToken: null, expiresOn: null]
	def cacheresellerSAToken = [resellerSAToken: null, expiresOn: null]
	def resellerId = null

    def getUsage(subscriptionId, startTime, endTime) {
		def subscription = Subscription.findBySubscriptionId(subscriptionId)
		def cspProperties = grailsApplication.config.getProperty('csp')
		if (cspProperties == null || cspProperties.isEmpty()) {
			print "empty"
			throw new Exception("There are no csp section defined in condense-config.properties")
		}
		def tokenizedDomain = subscription.customer.cspDomain.tokenize('.')
		def currentSection = "csp"
		tokenizedDomain.each {
			currentSection = currentSection + ".${it}"
			cspProperties = cspProperties.getProperty(it)
			if (cspProperties == null || cspProperties.isEmpty()) {
				throw new Exception("There are no ${currentSection} section defined in condense-config.properties")
			}
		}
		
		def adAPIEndpoint = cspProperties.getProperty("adAPIEndpoint")
		def defaultDomain = cspProperties.getProperty("defaultDomain")
		
		def appId = cspProperties.getProperty("appId")
		def appKey = cspProperties.getProperty("appKey")
		def tenantId = cspProperties.getProperty("tenantId")
		
		def cspAPIEndpoint = cspProperties.getProperty("cspAPIEndpoint")
		
		def corelationId = UUID.randomUUID().toString()
		def trackingId = UUID.randomUUID().toString()
		
		def adToken = obtainAzureADToken(appId, appKey, adAPIEndpoint, defaultDomain)
		def resellerSAToken = obtainResellerSAToken(cspAPIEndpoint, adToken)
		def resellerId = getResellerID(cspAPIEndpoint, tenantId, resellerSAToken)
		
		RestBuilder rest = new RestBuilder()
		MultiValueMap<String, String> form = new LinkedMultiValueMap<String, String>()
		
		// get the usage
		print "${cspAPIEndpoint}/${resellerId}/usage-records?entitlement_id=${subscriptionId}&reported_start_time=${startTime}&reported_end_time=${endTime}&show_details=True&granularity=daily"
		def resp = rest.get("${cspAPIEndpoint}/${resellerId}/usage-records?entitlement_id=${subscriptionId}&reported_start_time=${startTime}&reported_end_time=${endTime}&show_details=True&granularity=daily") {
			header 'Authorization', "Bearer ${resellerSAToken}"
			header "api-version", "2015-03-31"
			header "x-ms-correlation-id", corelationId
			header "x-ms-tracking-id", trackingId
		}
		
		if (resp.getStatus() != 200) {
			throw new UsageCollectionException("Unable to obtain usage: ${resp.getStatus()}, ${resp.text}")
		}
		
		def usage = []
		usage.addAll(resp.json.items)
		
		while (resp.json.containsKey("links") && resp.json.links.containsKey("next")) {
			def nextPage = URLDecoder.decode(resp.json.links.next.href, "UTF-8")
			resp = rest.get("${cspAPIEndpoint}/${nextPage}") {
				header 'Authorization', "Bearer ${resellerSAToken}"
				header "api-version", "2015-03-31"
				header "x-ms-correlation-id", corelationId
				header "x-ms-tracking-id", trackingId
			}
			
			if (resp.getStatus() != 200) {
				throw new UsageCollectionException("Unable to obtain usage: ${resp.getStatus()}, ${resp.text}, ${nextPage}")
			}
			
			usage.addAll(resp.json.items)
		}
		
		return usage
    }
	
	def obtainAzureADToken(appId, appKey, adAPIEndpoint, defaultDomain) {
		print new Date().getTime()
		print this.cachedAzureAdToken['expiresOn']
		if (this.cachedAzureAdToken['adToken'] != null && new Date().getTime() <= this.cachedAzureAdToken['expiresOn']) {
			print "reusing existing AD token"
			return this.cachedAzureAdToken['adToken']
		}
		print "generating new AD token"
		
		//obtain Azure AD token
		MultiValueMap<String, String> form = new LinkedMultiValueMap<String, String>()
		form.add("grant_type", "client_credentials")
		form.add("client_id", appId)
		form.add("client_secret", appKey)
		form.add("resource", "https://graph.windows.net")
		
		RestBuilder rest = new RestBuilder()
		
		def resp = rest.post("${adAPIEndpoint}/${defaultDomain}/oauth2/token?api-version=1.0") {
			accept("application/json")
			contentType("application/x-www-form-urlencoded")
			body(form)
		}
		
		if (resp.getStatus() != 200) {
			throw new UsageCollectionException("Unable to obtain Azure AD token: ${resp.getStatus()}, ${resp.text}")
		}
		
		def adToken = resp.json.access_token
		
		this.cachedAzureAdToken['adToken'] = adToken
		// this one is in seconds, normally it is expiring after 3600 seconds (60 minutes)
		// but we invalidate after 55
		this.cachedAzureAdToken['expiresOn'] = new Date().getTime() + 55*60*1000
		
		return adToken
	}
	
	def obtainResellerSAToken(cspAPIEndpoint, adToken) {
		if (this.cacheresellerSAToken['resellerSAToken'] != null && new Date().getTime() <= this.cacheresellerSAToken['expiresOn']) {
			print "reusing existing SA token"
			return this.cacheresellerSAToken['resellerSAToken']
		}
		print "generating new SA token"
		
		RestBuilder rest = new RestBuilder()
		MultiValueMap<String, String> form = new LinkedMultiValueMap<String, String>()

		// get the reseller AD token
		form.clear()
		form.add("grant_type", "client_credentials")
		def resp = rest.post("${cspAPIEndpoint}/my-org/tokens") {
			header 'Authorization', "Bearer ${adToken}"
			accept("application/json")
			contentType("application/x-www-form-urlencoded")
			body(form)
		}
		
		if (resp.getStatus() != 200) {
			throw new UsageCollectionException("Unable to obtain Reseller AD(SA - Salles Agent) token: ${resp.getStatus()}, ${resp.text}")
		}
		
		def resellerSAToken = resp.json.access_token
		
		this.cacheresellerSAToken['resellerSAToken'] = resellerSAToken
		// this one is in seconds, normally it is expiring after 900 seconds (15 minutes)
		// but we invalidate after 10 minutes
		this.cacheresellerSAToken['expiresOn'] = new Date().getTime() + 10*60*1000
		
		return resellerSAToken
	}
	
	def getResellerID(cspAPIEndpoint, tenantId, resellerSAToken) {
		// get the reseller ID
		if (this.resellerId != null) return this.resellerId
		
		RestBuilder rest = new RestBuilder()
		MultiValueMap<String, String> form = new LinkedMultiValueMap<String, String>()
		
		def corelationId = UUID.randomUUID().toString()
		def trackingId = UUID.randomUUID().toString()
		def resp = rest.get("${cspAPIEndpoint}/customers/get-by-identity?provider=AAD&type=tenant&tid=${tenantId}") {
			header 'Authorization', "Bearer ${resellerSAToken}"
			header "api-version", "2015-03-31"
			header "x-ms-correlation-id", corelationId
			header "x-ms-tracking-id", trackingId
		}
		
		if (resp.getStatus() != 200) {
			throw new UsageCollectionException("Unable to obtain Reseller ID: ${resp.getStatus()}, ${resp.text}")
		}
		
		def resellerId = resp.json.id
		this.resellerId = resellerId
		
		return resellerId
	}
}