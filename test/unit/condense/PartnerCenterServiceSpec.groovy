package condense

import java.util.Date;

import grails.test.mixin.TestFor

import org.codehaus.groovy.grails.commons.GrailsApplication;
import org.springframework.http.ResponseEntity

import grails.plugins.rest.client.RequestCustomizer
import spock.lang.Specification
import grails.plugins.rest.client.RestBuilder
import grails.plugins.rest.client.RestResponse

import org.springframework.http.HttpStatus
import org.springframework.util.LinkedMultiValueMap
import org.springframework.util.MultiValueMap

/**
 * See the API for {@link grails.test.mixin.services.ServiceUnitTestMixin} for usage instructions
 */
@TestFor(PartnerCenterService)
@Mock([PricingSet, Customer, Subscription])
class PartnerCenterServiceSpec extends Specification {
	
    def setup() {
    }

    def cleanup() {
    }

    void "test obtainAzureADToken no cache"() {
		
		given: "There is no cache"
			def String capturedUrl = null
			def requestCustomizer = Mock(RequestCustomizer)
			
			RestBuilder.metaClass.post = { String url, Closure closure ->
				capturedUrl = url
				closure.delegate = requestCustomizer
				closure.call()
				return new RestResponse(new ResponseEntity('{"access_token": "access_token"}', HttpStatus.OK))
			}

		when: "The obtainAzureADToken method is invoked"
			service.obtainAzureADToken('appId', 'appKey', 'adAPIEndpoint', 'defaultDomain')
		
		then: "The oauth2 AD new token is obtained trough REST API and stored in cache"
			assertEquals service.cachedAzureAdToken['token'], "access_token"
			assertEquals capturedUrl, "adAPIEndpoint/defaultDomain/oauth2/token?api-version=1.0"
			1 * requestCustomizer.accept('application/json')
			1 * requestCustomizer.contentType('application/x-www-form-urlencoded')
			1 * requestCustomizer.body(
				[
					'grant_type':['client_credentials'],
					'client_id':['appId'],
					'client_secret':['appKey'],
					'resource':['https://graph.windows.net']
				])
    }
	
	void "test obtainAzureADToken catch partner center network issues"() {
		given: "There is no cache and the oauth2 rest api is expected to return error"
			def String capturedUrl = null
			def requestCustomizer = Mock(RequestCustomizer)
			
			RestBuilder.metaClass.post = { String url, Closure closure ->
				capturedUrl = url
				closure.delegate = requestCustomizer
				closure.call()
				return new RestResponse(new ResponseEntity('error', HttpStatus.INTERNAL_SERVER_ERROR))
			}

		when: "The obtainAzureADToken is invoked"
			def exceptionThrown = false
			try {
				service.obtainAzureADToken('appId', 'appKey', 'adAPIEndpoint', 'defaultDomain')
			} catch (UsageCollectionException ucex) {
				if (ucex.message == 'Unable to obtain Azure AD token: 500, error') {
					exceptionThrown = true
				}
			}
		
		then: "The method should raise UsageCollectionException"
			assertTrue exceptionThrown
	}
	
	void "test obtainAzureADToken from cache"() {
		given: "There is a cache"
			service.cachedAzureAdToken = [
				token: "cachedToken",
				expiresOn: new Date().getTime() + 550*60*1000]

		when: "The method is invoked"
			def result = service.obtainAzureADToken('appId', 'appKey', 'adAPIEndpoint', 'defaultDomain')
		
		then: "The cached token is returned"
			assertEquals result, "cachedToken"
	}
	
	void "test obtainAzureADToken with expired cache"() {
		given: "There is cahed token, which is already expired"
			service.cachedAzureAdToken = [
				token: "cachedToken",
				expiresOn: 0]
			
			def String capturedUrl = null
			def requestCustomizer = Mock(RequestCustomizer)
			
			RestBuilder.metaClass.post = { String url, Closure closure ->
				capturedUrl = url
				closure.delegate = requestCustomizer
				closure.call()
				return new RestResponse(new ResponseEntity('{"access_token": "new_token"}', HttpStatus.OK))
			}

		when: "The obtainAzureADToken method is invoked"
			def result = service.obtainAzureADToken('appId', 'appKey', 'adAPIEndpoint', 'defaultDomain')
		
		then: "new token is obtained and stored in cache"
			assertEquals result, "new_token"
	}
	
	void "test getPartnerCenterAppCredentialsToken no cache"() {
		given: "There is no cache"
			def String capturedUrl = null
			def requestCustomizer = Mock(RequestCustomizer)
			
			RestBuilder.metaClass.post = { String url, Closure closure ->
				capturedUrl = url
				closure.delegate = requestCustomizer
				closure.call()
				return new RestResponse(new ResponseEntity('{"access_token": "access_token"}', HttpStatus.OK))
			}

		when: "There is no cache and the method is invoked"
			service.getPartnerCenterAppCredentialsToken('partnerCenterApiRoot', 'adToken')
		
		then: "new token is obtained and stored in cache"
			assertEquals service.cachedPartnerCenterAppCredentialsToken['token'], "access_token"
			assertEquals capturedUrl, 'partnerCenterApiRoot/generatetoken'
			1 * requestCustomizer.header('Authorization', "Bearer adToken")
			1 * requestCustomizer.accept('application/json')
			1 * requestCustomizer.contentType('application/x-www-form-urlencoded')
			1 * requestCustomizer.body(['grant_type':['jwt_token']])
	}
	
	void "test getPartnerCenterAppCredentialsToken catch partner center network issues"() {
		given: "There is no cache and the rest api is expected to return error"
			def String capturedUrl = null
			def requestCustomizer = Mock(RequestCustomizer)
			
			RestBuilder.metaClass.post = { String url, Closure closure ->
				capturedUrl = url
				closure.delegate = requestCustomizer
				closure.call()
				return new RestResponse(new ResponseEntity('error', HttpStatus.INTERNAL_SERVER_ERROR))
			}

		when: "The getPartnerCenterAppCredentialsToken is invoked"
			def exceptionThrown = false
			try {
				service.getPartnerCenterAppCredentialsToken('partnerCenterApiRoot', 'adToken')
			} catch (UsageCollectionException ucex) {
				if (ucex.message == 'Unable to obtain Partner Center token: 500, error') {
					exceptionThrown = true
				}
			}
		
		then: "The method should throw UsageCollectionException"
			assertTrue exceptionThrown
	}
	
	
	void "test getPartnerCenterAppCredentialsToken from cache"() {
		given: "There is a cache"
			service.cachedPartnerCenterAppCredentialsToken = [
				token: "cachedToken",
				expiresOn: new Date().getTime() + 550*60*1000]

		when: "The method is invoked"
			def result = service.getPartnerCenterAppCredentialsToken('partnerCenterApiRoot', 'adToken')
		
		then: "The cached token is returned"
			assertEquals result, "cachedToken"
	}
	
	void "test getPartnerCenterAppCredentialsToken with expired cache"() {
		given: "There is cahed token which is already expired"
			service.cachedPartnerCenterAppCredentialsToken = [
				token: "cachedToken",
				expiresOn: 0]

			def String capturedUrl = null
			def requestCustomizer = Mock(RequestCustomizer)
			
			RestBuilder.metaClass.post = { String url, Closure closure ->
				capturedUrl = url
				closure.delegate = requestCustomizer
				closure.call()
				return new RestResponse(new ResponseEntity('{"access_token": "new_token"}', HttpStatus.OK))
			}

		when: "The getPartnerCenterAppCredentialsToken method is invoked"
			def result = service.getPartnerCenterAppCredentialsToken('partnerCenterApiRoot', 'adToken')
		
		then: "new token is obtained and stored in cache"
			assertEquals result, "new_token"
	}
	
	void "test getUsage no csp section in config or csp section is empty"() {
		given: "We have subscription and don't have csp section in config"
			def pricingSet = new PricingSet(name: 'name', defaultOverride: 10).save()
			
			def customer = new Customer(
				cspCustomerPrimaryDomain: 'cspCustomerPrimaryDomain',
				cspCustomerId: 'cspCustomerId',
				cspDomain: 'cspDomain',
				externalId: 'externalId',
				pricingSet: pricingSet,
				supportPlan: null).save()
			
			def subscription = new Subscription(
				customer: customer,
				subscriptionId: 'subscriptionId',
				usageObtainedUntil: new Date(),
				isActive: true,
				isActiveChangedOn: null
			).save()
			
			grailsApplication.config.csp = null
			service.grailsApplication = grailsApplication
		
		when: "The getUsage method is executed"
			def exceptionThrown = false
			try {
				service.getUsage('customerId', 'subscriptionId', 'startTime', 'endTime')
			} catch (Exception ex) {
				if (ex.message == 'There is no csp section defined in condense-config.properties') {
					exceptionThrown = true
				}
			}
			
		then: "Exception is thorwn"
			assertTrue exceptionThrown
			
		
		when: "the csp section in config is empty and we call the getUsage method"
			grailsApplication.config.csp = []
			exceptionThrown = false
			try {
				service.getUsage('customerId', 'subscriptionId', 'startTime', 'endTime')
			} catch (Exception ex) {
				if (ex.message == 'There is no csp section defined in condense-config.properties') {
					exceptionThrown = true
				}
			}
			
		then: "Exception is thorwn"
			assertTrue exceptionThrown
	}
	
	void "test getUsage no domain csp subsection in config"() {
		given: "We have subscription and don't have domain subsection under the csp config"
			def pricingSet = new PricingSet(name: 'name', defaultOverride: 10).save()
			
			def customer = new Customer(
				cspCustomerPrimaryDomain: 'cspCustomerPrimaryDomain',
				cspCustomerId: 'cspCustomerId',
				cspDomain: 'cspDomain',
				externalId: 'externalId',
				pricingSet: pricingSet,
				supportPlan: null).save()
			
			def subscription = new Subscription(
				customer: customer,
				subscriptionId: 'subscriptionId',
				usageObtainedUntil: new Date(),
				isActive: true,
				isActiveChangedOn: null
			).save()
			
			grailsApplication.config.csp = new ConfigObject()
			grailsApplication.config.csp.put('domain', new ConfigObject())
			service.grailsApplication = grailsApplication
		
		when: "The getUsage method is called"
			def exceptionThrown = false
			try {
				service.getUsage('customerId', 'subscriptionId', 'startTime', 'endTime')
			} catch (Exception ex) {
				if (ex.message == 'There is no csp.cspDomain section defined in condense-config.properties') {
					exceptionThrown = true
				}
			}
			
		then: "Exception is thorwn"
			assertTrue exceptionThrown
	}
	
	void "test getUsage domain exists but not first level domain csp subsections in config"() {
		given: "We have subscription and we have the domain, but we don't have the first level domain subsection under the csp config"
			def pricingSet = new PricingSet(name: 'name', defaultOverride: 10).save()
			
			def customer = new Customer(
				cspCustomerPrimaryDomain: 'cspCustomerPrimaryDomain',
				cspCustomerId: 'cspCustomerId',
				cspDomain: 'cspDomain.domain',
				externalId: 'externalId',
				pricingSet: pricingSet,
				supportPlan: null).save()
			
			def subscription = new Subscription(
				customer: customer,
				subscriptionId: 'subscriptionId',
				usageObtainedUntil: new Date(),
				isActive: true,
				isActiveChangedOn: null
			).save()
			
			grailsApplication.config.csp = new ConfigObject()
			grailsApplication.config.csp.put('cspDomain', new ConfigObject())
			grailsApplication.config.csp.cspDomain.put('someConfigKey', null)
			service.grailsApplication = grailsApplication
		
		when: "The getUsage method is called"
			def exceptionThrown = false
			try {
				service.getUsage('customerId', 'subscriptionId', 'startTime', 'endTime')
			} catch (Exception ex) {
				if (ex.message == 'There is no csp.cspDomain.domain section defined in condense-config.properties') {
					exceptionThrown = true
				}
			}
			
		then: "Exception is thorwn"
			assertTrue exceptionThrown
	}
	
	void "test getUsage no successfull http response"() {
		given: "We have subscription and we have all csp configs defined, but the rest call will return something != 200"
			def pricingSet = new PricingSet(name: 'name', defaultOverride: 10).save()
			
			def customer = new Customer(
				cspCustomerPrimaryDomain: 'cspCustomerPrimaryDomain',
				cspCustomerId: 'cspCustomerId',
				cspDomain: 'cspDomain',
				externalId: 'externalId',
				pricingSet: pricingSet,
				supportPlan: null).save()
			
			def subscription = new Subscription(
				customer: customer,
				subscriptionId: 'subscriptionId',
				usageObtainedUntil: new Date(),
				isActive: true,
				isActiveChangedOn: null
			).save()
			
			grailsApplication.config.csp = new ConfigObject()
			grailsApplication.config.csp.put('cspDomain', new ConfigObject())
			grailsApplication.config.csp.cspDomain.put('adAPIEndpoint', 'adAPIEndpoint')
			grailsApplication.config.csp.cspDomain.put('defaultDomain', 'defaultDomain')
			grailsApplication.config.csp.cspDomain.put('appId', 'appId')
			grailsApplication.config.csp.cspDomain.put('appKey', 'appKey')
			grailsApplication.config.csp.cspDomain.put('tenantId', 'tenantId')
			grailsApplication.config.csp.cspDomain.put('cspAPIEndpoint', 'cspAPIEndpoint')
			grailsApplication.config.csp.cspDomain.put('partnerCenterApiRoot', 'partnerCenterApiRoot')
			service.grailsApplication = grailsApplication
			
			def String capturedUrl = null
			def requestCustomizer = Mock(RequestCustomizer)
			
			service.metaClass.obtainAzureADToken = { appId, appKey, adAPIEndpoint, defaultDomain ->
				return 'mockedAzureADToken'
			}
			
			service.metaClass.getPartnerCenterAppCredentialsToken = { partnerCenterApiRoot, adToken ->
				return 'mockedPartnerCenterAppCredentialsToken'
			}
			
			RestBuilder.metaClass.get = { String url, Closure closure ->
				capturedUrl = url
				closure.delegate = requestCustomizer
				closure.call()
				return new RestResponse(new ResponseEntity('error', HttpStatus.INTERNAL_SERVER_ERROR))
			}
		
		when: "The getUsage method is called"
			def exceptionThrown = false
			try {
				service.getUsage('customerId', 'subscriptionId', 'startTime', 'endTime')
			} catch (UsageCollectionException ex) {
				if (ex.message == 'Unable to obtain usage: 500, error') {
					exceptionThrown = true
				}
			}
			
		then: "UsageCollectionException is thorwn"
			assertEquals capturedUrl, 'partnerCenterApiRoot/v1/customers/customerId/subscriptions/subscriptionId/utilizations/azure?start_time=startTime&end_time=endTime&granularity=Daily&show_details=True'
			1 * requestCustomizer.header('Authorization', 'Bearer mockedPartnerCenterAppCredentialsToken')
			1 * requestCustomizer.header('MS-RequestId', _)
			1 * requestCustomizer.header('MS-CorrelationId', _)
			1 * requestCustomizer.accept('application/json')
			assertTrue exceptionThrown
	}
	
	void "test getUsage no pagination"() {
		given: "We have subscription and we have all csp configs defined the rest call will return non paginated result"
			def pricingSet = new PricingSet(name: 'name', defaultOverride: 10).save()
			
			def customer = new Customer(
				cspCustomerPrimaryDomain: 'cspCustomerPrimaryDomain',
				cspCustomerId: 'cspCustomerId',
				cspDomain: 'cspDomain',
				externalId: 'externalId',
				pricingSet: pricingSet,
				supportPlan: null).save()
			
			def subscription = new Subscription(
				customer: customer,
				subscriptionId: 'subscriptionId',
				usageObtainedUntil: new Date(),
				isActive: true,
				isActiveChangedOn: null
			).save()
			
			grailsApplication.config.csp = new ConfigObject()
			grailsApplication.config.csp.put('cspDomain', new ConfigObject())
			grailsApplication.config.csp.cspDomain.put('adAPIEndpoint', 'adAPIEndpoint')
			grailsApplication.config.csp.cspDomain.put('defaultDomain', 'defaultDomain')
			grailsApplication.config.csp.cspDomain.put('appId', 'appId')
			grailsApplication.config.csp.cspDomain.put('appKey', 'appKey')
			grailsApplication.config.csp.cspDomain.put('tenantId', 'tenantId')
			grailsApplication.config.csp.cspDomain.put('cspAPIEndpoint', 'cspAPIEndpoint')
			grailsApplication.config.csp.cspDomain.put('partnerCenterApiRoot', 'partnerCenterApiRoot')
			service.grailsApplication = grailsApplication
			
			def String capturedUrl = null
			def requestCustomizer = Mock(RequestCustomizer)
			
			service.metaClass.obtainAzureADToken = { appId, appKey, adAPIEndpoint, defaultDomain ->
				return 'mockedAzureADToken'
			}
			
			service.metaClass.getPartnerCenterAppCredentialsToken = { partnerCenterApiRoot, adToken ->
				return 'mockedPartnerCenterAppCredentialsToken'
			}
			
			RestBuilder.metaClass.get = { String url, Closure closure ->
				capturedUrl = url
				closure.delegate = requestCustomizer
				closure.call()
				return new RestResponse(new ResponseEntity('{"items": ["item1", "item2"]}', HttpStatus.OK))
			}
		
		when: "The getUsage method is called"
			def result = service.getUsage('customerId', 'subscriptionId', 'startTime', 'endTime')

		then: "The items from the first page are collected"
			assertEquals capturedUrl, 'partnerCenterApiRoot/v1/customers/customerId/subscriptions/subscriptionId/utilizations/azure?start_time=startTime&end_time=endTime&granularity=Daily&show_details=True'
			1 * requestCustomizer.header('Authorization', 'Bearer mockedPartnerCenterAppCredentialsToken')
			1 * requestCustomizer.header('MS-RequestId', _)
			1 * requestCustomizer.header('MS-CorrelationId', _)
			1 * requestCustomizer.accept('application/json')
			assertEquals result, ['item1', 'item2']
	}
	
	
	void "test getUsage with pagination"() {
		given: "We have subscription and we have all csp configs defined the rest call will return paginated result"
			def pricingSet = new PricingSet(name: 'name', defaultOverride: 10).save()
			
			def customer = new Customer(
				cspCustomerPrimaryDomain: 'cspCustomerPrimaryDomain',
				cspCustomerId: 'cspCustomerId',
				cspDomain: 'cspDomain',
				externalId: 'externalId',
				pricingSet: pricingSet,
				supportPlan: null).save()
			
			def subscription = new Subscription(
				customer: customer,
				subscriptionId: 'subscriptionId',
				usageObtainedUntil: new Date(),
				isActive: true,
				isActiveChangedOn: null
			).save()
			
			grailsApplication.config.csp = new ConfigObject()
			grailsApplication.config.csp.put('cspDomain', new ConfigObject())
			grailsApplication.config.csp.cspDomain.put('adAPIEndpoint', 'adAPIEndpoint')
			grailsApplication.config.csp.cspDomain.put('defaultDomain', 'defaultDomain')
			grailsApplication.config.csp.cspDomain.put('appId', 'appId')
			grailsApplication.config.csp.cspDomain.put('appKey', 'appKey')
			grailsApplication.config.csp.cspDomain.put('tenantId', 'tenantId')
			grailsApplication.config.csp.cspDomain.put('cspAPIEndpoint', 'cspAPIEndpoint')
			grailsApplication.config.csp.cspDomain.put('partnerCenterApiRoot', 'partnerCenterApiRoot')
			service.grailsApplication = grailsApplication
			
			def String capturedUrl = null
			def requestCustomizer = Mock(RequestCustomizer)
			
			service.metaClass.obtainAzureADToken = { appId, appKey, adAPIEndpoint, defaultDomain ->
				return 'mockedAzureADToken'
			}
			
			service.metaClass.getPartnerCenterAppCredentialsToken = { partnerCenterApiRoot, adToken ->
				return 'mockedPartnerCenterAppCredentialsToken'
			}
			
			RestBuilder.metaClass.get = { String url, Closure closure ->
				capturedUrl = url
				closure.delegate = requestCustomizer
				closure.call()
				if (url == 'partnerCenterApiRoot/v1/customers/customerId/subscriptions/subscriptionId/utilizations/azure?start_time=startTime&end_time=endTime&granularity=Daily&show_details=True') {
					return new RestResponse(new ResponseEntity('{"items": ["item1", "item2"], "links": {"next": {"uri": "nextPage", "headers": [{"key": "MS-ContinuationToken", "value": "h1"}] }}}', HttpStatus.OK))
				}
				
				if (url == 'partnerCenterApiRoot/v1/nextPage') {
					return new RestResponse(new ResponseEntity('{"items": ["item3", "item4"], "links": {"next": {"uri": "nextPage1", "headers": [{"key": "MS-ContinuationToken", "value": "h2"}] }}}', HttpStatus.OK))
				}
				
				if (url == 'partnerCenterApiRoot/v1/nextPage1') {
					return new RestResponse(new ResponseEntity('{"items": ["item5", "item6"]}', HttpStatus.OK))
				}
			}
		
		when: "The getUsage method is called"
			def result = service.getUsage('customerId', 'subscriptionId', 'startTime', 'endTime')

		then: "The items from all trhree pages are collected"
			assertEquals capturedUrl, 'partnerCenterApiRoot/v1/nextPage1'
			3 * requestCustomizer.header('Authorization', 'Bearer mockedPartnerCenterAppCredentialsToken')
			1 * requestCustomizer.header('MS-RequestId', _)
			1 * requestCustomizer.header('MS-CorrelationId', _)
			2 * requestCustomizer.header('MS-ContinuationToken', _)
			3 * requestCustomizer.accept('application/json')
			assertEquals result, ['item1', 'item2', 'item3', 'item4', 'item5', 'item6']
	}
	
	
	void "test getUsage with pagination rest error in following pages"() {
		given: "We have subscription and we have all csp configs defined. The rest call will return paginated result, but subsequent paging rest request will fail"
			def pricingSet = new PricingSet(name: 'name', defaultOverride: 10).save()
			
			def customer = new Customer(
				cspCustomerPrimaryDomain: 'cspCustomerPrimaryDomain',
				cspCustomerId: 'cspCustomerId',
				cspDomain: 'cspDomain',
				externalId: 'externalId',
				pricingSet: pricingSet,
				supportPlan: null).save()
			
			def subscription = new Subscription(
				customer: customer,
				subscriptionId: 'subscriptionId',
				usageObtainedUntil: new Date(),
				isActive: true,
				isActiveChangedOn: null
			).save()
			
			grailsApplication.config.csp = new ConfigObject()
			grailsApplication.config.csp.put('cspDomain', new ConfigObject())
			grailsApplication.config.csp.cspDomain.put('adAPIEndpoint', 'adAPIEndpoint')
			grailsApplication.config.csp.cspDomain.put('defaultDomain', 'defaultDomain')
			grailsApplication.config.csp.cspDomain.put('appId', 'appId')
			grailsApplication.config.csp.cspDomain.put('appKey', 'appKey')
			grailsApplication.config.csp.cspDomain.put('tenantId', 'tenantId')
			grailsApplication.config.csp.cspDomain.put('cspAPIEndpoint', 'cspAPIEndpoint')
			grailsApplication.config.csp.cspDomain.put('partnerCenterApiRoot', 'partnerCenterApiRoot')
			service.grailsApplication = grailsApplication
			
			def String capturedUrl = null
			def requestCustomizer = Mock(RequestCustomizer)
			
			service.metaClass.obtainAzureADToken = { appId, appKey, adAPIEndpoint, defaultDomain ->
				return 'mockedAzureADToken'
			}
			
			service.metaClass.getPartnerCenterAppCredentialsToken = { partnerCenterApiRoot, adToken ->
				return 'mockedPartnerCenterAppCredentialsToken'
			}
			
			RestBuilder.metaClass.get = { String url, Closure closure ->
				capturedUrl = url
				closure.delegate = requestCustomizer
				closure.call()
				if (url == 'partnerCenterApiRoot/v1/customers/customerId/subscriptions/subscriptionId/utilizations/azure?start_time=startTime&end_time=endTime&granularity=Daily&show_details=True') {
					return new RestResponse(new ResponseEntity('{"items": ["item1", "item2"], "links": {"next": {"uri": "nextPage", "headers": [{"key": "MS-ContinuationToken", "value": "h1"}] }}}', HttpStatus.OK))
				}
				
				if (url == 'partnerCenterApiRoot/v1/nextPage') {
					return new RestResponse(new ResponseEntity('{"items": ["item3", "item4"], "links": {"next": {"uri": "nextPage1", "headers": [{"key": "MS-ContinuationToken", "value": "h2"}] }}}', HttpStatus.OK))
				}
				
				if (url == 'partnerCenterApiRoot/v1/nextPage1') {
					return new RestResponse(new ResponseEntity('error', HttpStatus.INTERNAL_SERVER_ERROR))
				}
			}
		
		when: "The getUsage method is called"
			def exceptionThrown = false
			try {
				service.getUsage('customerId', 'subscriptionId', 'startTime', 'endTime')
			} catch (UsageCollectionException ex) {
				println ex.message
				println ex.message
				if (ex.message == 'Unable to obtain usage: 500, error, nextPage1') {
					exceptionThrown = true
				}
			}

		then: "The items from all trhree pages are collected"
			assertTrue exceptionThrown
	}
	
	void "test getUsage with pagination results containing BOM characters"() {
		given: "We have subscription and we have all csp configs defined the rest call will return paginated result that contains BOM characters"
			def pricingSet = new PricingSet(name: 'name', defaultOverride: 10).save()
			
			def customer = new Customer(
				cspCustomerPrimaryDomain: 'cspCustomerPrimaryDomain',
				cspCustomerId: 'cspCustomerId',
				cspDomain: 'cspDomain',
				externalId: 'externalId',
				pricingSet: pricingSet,
				supportPlan: null).save()
			
			def subscription = new Subscription(
				customer: customer,
				subscriptionId: 'subscriptionId',
				usageObtainedUntil: new Date(),
				isActive: true,
				isActiveChangedOn: null
			).save()
			
			grailsApplication.config.csp = new ConfigObject()
			grailsApplication.config.csp.put('cspDomain', new ConfigObject())
			grailsApplication.config.csp.cspDomain.put('adAPIEndpoint', 'adAPIEndpoint')
			grailsApplication.config.csp.cspDomain.put('defaultDomain', 'defaultDomain')
			grailsApplication.config.csp.cspDomain.put('appId', 'appId')
			grailsApplication.config.csp.cspDomain.put('appKey', 'appKey')
			grailsApplication.config.csp.cspDomain.put('tenantId', 'tenantId')
			grailsApplication.config.csp.cspDomain.put('cspAPIEndpoint', 'cspAPIEndpoint')
			grailsApplication.config.csp.cspDomain.put('partnerCenterApiRoot', 'partnerCenterApiRoot')
			service.grailsApplication = grailsApplication
			
			def String capturedUrl = null
			def requestCustomizer = Mock(RequestCustomizer)
			
			service.metaClass.obtainAzureADToken = { appId, appKey, adAPIEndpoint, defaultDomain ->
				return 'mockedAzureADToken'
			}
			
			service.metaClass.getPartnerCenterAppCredentialsToken = { partnerCenterApiRoot, adToken ->
				return 'mockedPartnerCenterAppCredentialsToken'
			}
			
			RestBuilder.metaClass.get = { String url, Closure closure ->
				capturedUrl = url
				closure.delegate = requestCustomizer
				closure.call()
				if (url == 'partnerCenterApiRoot/v1/customers/customerId/subscriptions/subscriptionId/utilizations/azure?start_time=startTime&end_time=endTime&granularity=Daily&show_details=True') {
					return new RestResponse(new ResponseEntity('\uFEFF{"items": ["item1", "item2"], "links": {"next": {"uri": "nextPage", "headers": [{"key": "MS-ContinuationToken", "value": "h1"}] }}}', HttpStatus.OK))
				}
				
				if (url == 'partnerCenterApiRoot/v1/nextPage') {
					return new RestResponse(new ResponseEntity('\uFEFF{"items": ["item3", "item4"], "links": {"next": {"uri": "nextPage1", "headers": [{"key": "MS-ContinuationToken", "value": "h2"}] }}}', HttpStatus.OK))
				}
				
				if (url == 'partnerCenterApiRoot/v1/nextPage1') {
					return new RestResponse(new ResponseEntity('\uFEFF{"items": ["item5", "item6"]}', HttpStatus.OK))
				}
			}
		
		when: "The getUsage method is called"
			def result = service.getUsage('customerId', 'subscriptionId', 'startTime', 'endTime')

		then: "The items from all trhree pages are collected"
			assertEquals capturedUrl, 'partnerCenterApiRoot/v1/nextPage1'
			3 * requestCustomizer.header('Authorization', 'Bearer mockedPartnerCenterAppCredentialsToken')
			1 * requestCustomizer.header('MS-RequestId', _)
			1 * requestCustomizer.header('MS-CorrelationId', _)
			2 * requestCustomizer.header('MS-ContinuationToken', _)
			3 * requestCustomizer.accept('application/json')
			assertEquals result, ['item1', 'item2', 'item3', 'item4', 'item5', 'item6']
	}
}
