
<%@ page import="condense.Subscription" %>
<!DOCTYPE html>
<html>
	<head>
		<meta name="layout" content="main">
		<g:set var="entityName" value="${message(code: 'subscription.label', default: 'Subscription')}" />
		<title><g:message code="default.show.label" args="[entityName]" /></title>
	</head>
	<body>
		<div class="container">
			<div class="nav" role="navigation">
				<ul class="nav nav-pills">
					<li class="active"><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>
					<li><g:link class="list" action="index"><g:message code="default.list.label" args="[entityName]" /></g:link></li>
					<li><g:link class="create" action="create"><g:message code="default.new.label" args="[entityName]" /></g:link></li>
				</ul>
			</div>
			<div id="show-subscription" class="content scaffold-show" role="main">
				<h1><g:message code="default.show.label" args="[entityName]" /></h1>
				<g:if test="${flash.message}">
				<div class="alert alert-success" role="status">${flash.message}</div>
				</g:if>
				<g:if test="${flash.error}">
				<div class="alert alert-danger" role="status">${flash.error}</div>
				</g:if>
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
						
							<dd class="property-value" aria-labelledby="usageObtainedUntil-label">
								<g:formatDate date="${subscriptionInstance.usageObtainedUntil}" type="date" style="LONG" />
							</dd>
						
					</dl>
					</g:if>
				
					<g:if test="${subscriptionInstance?.customer}">
					<dl class="dl-horizontal">
						<dt id="customer-label" class="property-label"><g:message code="subscription.customer.label" default="Customer" /></dt>
						
							<dd class="property-value" aria-labelledby="customer-label"><g:link controller="customer" action="show" id="${subscriptionInstance?.customer?.id}">${subscriptionInstance?.customer?.encodeAsHTML()}</g:link></dd>
						
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
				
					<g:if test="${subscriptionInstance?.usageRecords}">
					<dl class="dl-horizontal">
						<dt id="usageRecords-label" class="property-label"><g:message code="subscription.usageRecords.label" default="Usage Records" /></dt>
							<dd class="property-value" aria-labelledby="usageRecords-label">
								<div class="col-md-12" id="usagesTable"></div><g:render template="usages_table" /></div>
							</dd>
						
					</dl>
					</g:if>
				
				</ul>
				<g:form url="[resource:subscriptionInstance, action:'delete']" method="DELETE">
					<fieldset class="buttons">
						<g:link class="btn btn-primary" action="edit" resource="${subscriptionInstance}"><g:message code="default.button.edit.label" default="Edit" /></g:link>
						<g:actionSubmit class="btn btn-danger" action="delete" value="${message(code: 'default.button.delete.label', default: 'Delete')}" onclick="return confirm('${message(code: 'default.button.delete.confirm.message', default: 'Are you sure?')}');" />
					</fieldset>
				</g:form>
			</div>
		</div>
	</body>
</html>
