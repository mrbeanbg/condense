
<%@ page import="condense.Subscription" %>
<!DOCTYPE html>
<html>
	<head>
		<meta name="layout" content="main">
		<g:set var="entityName" value="${message(code: 'usages.label', default: 'Subscription')}" />
		<title>
			<g:message code="obtain.usage.for.label" default="Obtain Usage for" />: <g:fieldValue bean="${subscriptionInstance}" field="subscriptionId"/>
		</title>
	</head>
	<body>
		<div class="container">
			<div class="nav" role="navigation">
				<ul class="nav nav-pills">
					<li class="active"><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>
					<li><g:link class="list" action="index"><g:message code="default.list.label" args="[entityName]" /></g:link></li>
				</ul>
			</div>
			<div id="show-subscription" class="content scaffold-show" role="main">
				<h1><g:message code="obtain.usage.label" default="Obtain Usage" /></h1>
				<g:if test="${flash.message}">
				<div class="alert alert-success" role="status">${flash.message}</div>
				</g:if>
				<g:if test="${flash.error}">
				<div class="alert alert-danger" role="status">${flash.error}</div>
				</g:if>
				<g:hasErrors bean="${subscriptionInstance}">
				<div class="alert alert-danger">
					<ul class="errors" role="alert">
						<g:eachError bean="${subscriptionInstance}" var="error">
						<li <g:if test="${error in org.springframework.validation.FieldError}">data-field-id="${error.field}"</g:if>><g:message error="${error}"/></li>
						</g:eachError>
					</ul>
				</div>
				</g:hasErrors>
				<ul class="property-list subscription">
				
					<g:if test="${subscriptionInstance?.subscriptionId}">
					<dl class="dl-horizontal">
						<dt id="subscriptionId-label" class="property-label"><g:message code="subscription.subscriptionId.label" default="Subscription Id" /></dt>
						
							<dd class="property-value" aria-labelledby="subscriptionId-label"><g:fieldValue bean="${subscriptionInstance}" field="subscriptionId"/></dd>
						
					</dl>
					</g:if>
				
					<g:if test="${subscriptionInstance?.usageObtainedUntil}">
					<dl class="dl-horizontal">
						<dt id="usageObtainedUntil-label" class="property-label"><g:message code="subscription.usageObtainedUntil.label" default="Usage Obtained Until" /></dt>
						
							<dd class="property-value" aria-labelledby="usageObtainedUntil-label"><g:formatDate date="${subscriptionInstance?.usageObtainedUntil}" /></dd>
						
					</dl>
					</g:if>
				
					<g:if test="${subscriptionInstance?.customer}">
					<dl class="dl-horizontal">
						<dt id="customer-label" class="property-label"><g:message code="subscription.customer.label" default="Customer" /></dt>
						
							<dd class="property-value" aria-labelledby="customer-label">
								${subscriptionInstance?.customer?.encodeAsHTML()}
							</dd>
						
					</dl>
					</g:if>
				
					<g:if test="${subscriptionInstance?.dateCreated}">
					<dl class="dl-horizontal">
						<dt id="dateCreated-label" class="property-label"><g:message code="subscription.dateCreated.label" default="Date Created" /></dt>
						
							<dd class="property-value" aria-labelledby="dateCreated-label"><g:formatDate date="${subscriptionInstance?.dateCreated}" /></dd>
						
					</dl>
					</g:if>
				
					<g:if test="${subscriptionInstance?.lastUpdated}">
					<dl class="dl-horizontal">
						<dt id="lastUpdated-label" class="property-label"><g:message code="subscription.lastUpdated.label" default="Last Updated" /></dt>
						
							<dd class="property-value" aria-labelledby="lastUpdated-label"><g:formatDate date="${subscriptionInstance?.lastUpdated}" /></dd>
						
					</dl>
					</g:if>
				
				</ul>
				<hr />
				<div class="col-md-12">
					<g:form url="[resource:subscriptionInstance, action:'get_usage']" method="POST">
						<div class="col-md-4">
							<label>
								<g:message code="from.date" default="From Date" />:
							</label>
							<g:if test="${subscriptionInstance.usageObtainedUntil == null}">
								<g:datePicker name="startDate" precision="day" value="${startDate}" />
							</g:if>
							<g:else>
								<g:formatDate date="${startDate}" type="date" style="LONG"/>
							</g:else>
						</div>
						<div class="col-md-4">
							<label>
								<g:message code="to.date" default="To Date" />:
							</label>
							<g:datePicker name="endDate" precision="day" value="${endDate}"/>
						</div>
						<div class="col-md-4">
							<fieldset class="buttons">
								<g:submitButton name="get_usage" class="btn btn-primary" value="${message(code: 'obtain.label', default: 'Obtain')}" />
							</fieldset>
						</div>
					</g:form>
					
				</div>
				
			</div>
		</div>
	</body>
</html>
