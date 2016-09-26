
<%@ page import="condense.Customer" %>
<!DOCTYPE html>
<html>
	<head>
		<meta name="layout" content="main">
		<g:set var="entityName" value="${message(code: 'customer.label', default: 'Customer')}" />
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
			<div id="show-customer" class="content scaffold-show" role="main">
				<h1><g:message code="default.show.label" args="[entityName]" /></h1>
				<g:if test="${flash.message}">
				<div class="alert alert-success" role="status">${flash.message}</div>
				</g:if>
				<g:if test="${flash.error}">
				<div class="alert alert-danger" role="status">${flash.error}</div>
				</g:if>
				<ul class="property-list customer">
				
					<g:if test="${customerInstance?.cspCustomerId}">
					<dl class="dl-horizontal">
						<dt id="cspCustomerId-label" class="property-label"><g:message code="customer.cspCustomerId.label" default="Csp Customer Id" /></dt>
						
							<dd class="property-value" aria-labelledby="cspCustomerId-label"><g:fieldValue bean="${customerInstance}" field="cspCustomerId"/></dd>
						
					</dl>
					</g:if>
				
					<g:if test="${customerInstance?.pricingSet}">
					<dl class="dl-horizontal">
						<dt id="pricingSet-label" class="property-label"><g:message code="customer.pricingSet.label" default="Pricing Set" /></dt>
						
							<dd class="property-value" aria-labelledby="pricingSet-label"><g:link controller="pricingSet" action="manage" id="${customerInstance?.pricingSet?.id}">${customerInstance?.pricingSet?.encodeAsHTML()}</g:link></dd>
						
					</dl>
					</g:if>
				
					<g:if test="${customerInstance?.supportPlan}">
					<dl class="dl-horizontal">
						<dt id="supportPlan-label" class="property-label"><g:message code="customer.supportPlan.label" default="Support Plan" /></dt>
						
							<dd class="property-value" aria-labelledby="supportPlan-label"><g:link controller="supportPlan" action="show" id="${customerInstance?.supportPlan?.id}">${customerInstance?.supportPlan?.encodeAsHTML()}</g:link></dd>
						
					</dl>
					</g:if>
					
					<g:if test="${customerInstance?.subscriptions}">
					<dl class="dl-horizontal">
						<dt id="subscriptions-label" class="property-label"><g:message code="customer.subscriptions.label" default="Subscriptions" /></dt>
						
							<g:each in="${customerInstance.subscriptions}" var="s">
							<dd class="property-value" aria-labelledby="subscriptions-label">
								<g:link controller="subscription" action="show" id="${s.id}">
									${s?.encodeAsHTML()}
								</g:link>
							</dd>
							</g:each>
						
					</dl>
					</g:if>
				
					<g:if test="${customerInstance?.dateCreated}">
					<dl class="dl-horizontal">
						<dt id="dateCreated-label" class="property-label"><g:message code="customer.dateCreated.label" default="Date Created" /></dt>
						
							<dd class="property-value" aria-labelledby="dateCreated-label"><g:formatDate date="${customerInstance?.dateCreated}" /></dd>
						
					</dl>
					</g:if>
				
					<g:if test="${customerInstance?.lastUpdated}">
					<dl class="dl-horizontal">
						<dt id="lastUpdated-label" class="property-label"><g:message code="customer.lastUpdated.label" default="Last Updated" /></dt>
						
							<dd class="property-value" aria-labelledby="lastUpdated-label"><g:formatDate date="${customerInstance?.lastUpdated}" /></dd>
						
					</dl>
					</g:if>
				
				</ul>
				<g:form url="[resource:customerInstance, action:'delete']" method="DELETE">
					<fieldset class="buttons">
						<g:link class="btn btn-primary" action="edit" resource="${customerInstance}"><g:message code="default.button.edit.label" default="Edit" /></g:link>
						<g:actionSubmit class="btn btn-danger" action="delete" value="${message(code: 'default.button.delete.label', default: 'Delete')}" onclick="return confirm('${message(code: 'default.button.delete.confirm.message', default: 'Are you sure?')}');" />
					</fieldset>
				</g:form>
			</div>
		</div>
	</body>
</html>
