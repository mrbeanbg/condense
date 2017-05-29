package condense

import grails.plugins.rest.client.RestBuilder
import grails.transaction.Transactional
import org.codehaus.groovy.grails.commons.GrailsApplication
import org.codehaus.groovy.grails.web.json.JSONElement
import org.springframework.util.LinkedMultiValueMap
import org.springframework.util.MultiValueMap
import grails.converters.JSON

@Transactional
class PartnerCenterService {
	
	GrailsApplication grailsApplication
	
	def cachedAzureAdToken = [token: null, expiresOn: null]
	def cacheresellerSAToken = [resellerSAToken: null, expiresOn: null]
	def cachedPartnerCenterAppCredentialsToken = [token: null, expiresOn: null]
	def resellerId = null

    def getUsage(customerId, subscriptionId, startTime, endTime) {
		def subscription = Subscription.findBySubscriptionId(subscriptionId)
		def cspProperties = grailsApplication.config.getProperty('csp')
		if (cspProperties == null || cspProperties.isEmpty()) {
			throw new Exception("There is no csp section defined in condense-config.properties")
		}
		def tokenizedDomain = subscription.customer.cspDomain.tokenize('.')
		def currentSection = "csp"
		tokenizedDomain.each {
			currentSection = currentSection + ".${it}"
			cspProperties = cspProperties.getProperty(it)
			if (cspProperties == null || cspProperties.isEmpty()) {
				throw new Exception("There is no ${currentSection} section defined in condense-config.properties")
			}
		}
		
		def adAPIEndpoint = cspProperties.getProperty("adAPIEndpoint")
		def defaultDomain = cspProperties.getProperty("defaultDomain")
		
		def appId = cspProperties.getProperty("appId")
		def appKey = cspProperties.getProperty("appKey")
		def tenantId = cspProperties.getProperty("tenantId")
		
		def cspAPIEndpoint = cspProperties.getProperty("cspAPIEndpoint")
		def partnerCenterApiRoot = cspProperties.getProperty("partnerCenterApiRoot")
		
		def corelationId = UUID.randomUUID().toString()
		def trackingId = UUID.randomUUID().toString()
		
		def adToken = obtainAzureADToken(appId, appKey, adAPIEndpoint, defaultDomain)
		def partnerCenterAppCredentialsToken = getPartnerCenterAppCredentialsToken(partnerCenterApiRoot, adToken)
		
		RestBuilder rest = new RestBuilder()
		MultiValueMap<String, String> form = new LinkedMultiValueMap<String, String>()
		
		print "${partnerCenterApiRoot}/v1/customers/${customerId}/subscriptions/${subscriptionId}/utilizations/azure?start_time=${startTime}&end_time=${endTime}&granularity=Daily&show_details=True"
		def resp = rest.get("${partnerCenterApiRoot}/v1/customers/${customerId}/subscriptions/${subscriptionId}/utilizations/azure?start_time=${startTime}&end_time=${endTime}&granularity=Daily&show_details=True") {
			header 'Authorization', "Bearer ${partnerCenterAppCredentialsToken}"
			header 'MS-RequestId', trackingId
			header 'MS-CorrelationId', corelationId
			accept("application/json")
		}
		
		if (resp.getStatus() != 200) {
			throw new UsageCollectionException("Unable to obtain usage: ${resp.getStatus()}, ${resp.text}")
		}
		
		def respJson = JSON.parse(_removeUTF8BOM(resp.getBody().toString()))
		
		def usage = []
		usage.addAll(respJson.items)
		
		while (respJson.containsKey("links") && respJson.links.containsKey("next")) {
			def nextPage = URLDecoder.decode(respJson.links.next.uri, "UTF-8")
			
			resp = rest.get("${partnerCenterApiRoot}/v1/${nextPage}") {
				header 'Authorization', "Bearer ${partnerCenterAppCredentialsToken}"
				header 'MS-ContinuationToken', URLDecoder.decode(respJson.links.next.headers[0]['value'], "UTF-8")
				accept("application/json")
			}
			
			if (resp.getStatus() != 200) {
				throw new UsageCollectionException("Unable to obtain usage: ${resp.getStatus()}, ${resp.text}, ${nextPage}")
			}
			
			respJson = JSON.parse(_removeUTF8BOM(resp.getBody().toString()))
			
			usage.addAll(respJson.items)
		}
		
		return usage
    }
	
	def obtainAzureADToken(appId, appKey, adAPIEndpoint, defaultDomain) {
		print new Date().getTime()
		print this.cachedAzureAdToken['expiresOn']
		if (this.cachedAzureAdToken['token'] != null && new Date().getTime() <= this.cachedAzureAdToken['expiresOn']) {
			print "reusing existing AD token"
			return this.cachedAzureAdToken['token']
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
		
		this.cachedAzureAdToken['token'] = adToken
		// this one is in seconds, normally it is expiring after 3600 seconds (60 minutes)
		// but we invalidate after 55
		this.cachedAzureAdToken['expiresOn'] = new Date().getTime() + 55*60*1000
		
		return adToken
	}
	
	def getPartnerCenterAppCredentialsToken(partnerCenterApiRoot, adToken) {
		print new Date().getTime()
		print this.cachedPartnerCenterAppCredentialsToken['expiresOn']
		if (this.cachedPartnerCenterAppCredentialsToken['token'] != null &&
			new Date().getTime() <= this.cachedPartnerCenterAppCredentialsToken['expiresOn']) {
			
			print "reusing existing App token"
			return this.cachedPartnerCenterAppCredentialsToken['token']
		}
		print "generating new App token"
		
		//obtain Partner Center App Credentials Token token
		MultiValueMap<String, String> form = new LinkedMultiValueMap<String, String>()
		form.add("grant_type", "jwt_token")
		
		RestBuilder rest = new RestBuilder()
		
		def resp = rest.post("${partnerCenterApiRoot}/generatetoken") {
			header 'Authorization', "Bearer ${adToken}"
			accept("application/json")
			contentType("application/x-www-form-urlencoded")
			body(form)
		}
		
		if (resp.getStatus() != 200) {
			throw new UsageCollectionException("Unable to obtain Partner Center token: ${resp.getStatus()}, ${resp.text}")
		}
		
		def partnerCenterAppCredentialsToken = resp.json.access_token
		
		this.cachedPartnerCenterAppCredentialsToken['token'] = partnerCenterAppCredentialsToken
		// this one is in seconds, normally it is expiring after 3600 seconds (60 minutes)
		// but we invalidate after 55
		this.cachedPartnerCenterAppCredentialsToken['expiresOn'] = new Date().getTime() + 55*60*1000
		
		return partnerCenterAppCredentialsToken
	}
	
	private static String _removeUTF8BOM(String s) {
		if (s.startsWith("\uFEFF")) {
			s = s.substring(1);
		}
		return s;
	}
}