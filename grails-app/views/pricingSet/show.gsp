
<%@ page import="condense.PricingSet" %>
<!DOCTYPE html>
<html>
	<head>
		<meta name="layout" content="main">
		<g:set var="entityName" value="${message(code: 'pricingSet.label', default: 'Pricing Set')}" />
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
			<div id="show-pricingSet" class="content scaffold-show" role="main">
				<h1><g:message code="default.show.label" args="[entityName]" /></h1>
				<g:if test="${flash.message}">
				<div class="alert alert-success" role="status">${flash.message}</div>
				</g:if>
				<g:if test="${flash.error}">
				<div class="alert alert-danger" role="status">${flash.error}</div>
				</g:if>
				<ul class="property-list pricingSet">
				
					<g:if test="${pricingSetInstance?.name}">
					<dl class="dl-horizontal">
						<dt id="name-label" class="property-label"><g:message code="pricingSet.name.label" default="Name" /></dt>
						
							<dd class="property-value" aria-labelledby="name-label"><g:fieldValue bean="${pricingSetInstance}" field="name"/></dd>
						
					</dl>
					</g:if>
				
					<g:if test="${pricingSetInstance?.defaultOverride}">
					<dl class="dl-horizontal">
						<dt id="defaultOverride-label" class="property-label"><g:message code="pricingSet.defaultOverride.label" default="Default Override" /></dt>
						
							<dd class="property-value" aria-labelledby="defaultOverride-label"><g:fieldValue bean="${pricingSetInstance}" field="defaultOverride"/></dd>
						
					</dl>
					</g:if>
				
					<g:if test="${pricingSetInstance?.customers}">
					<dl class="dl-horizontal">
						<dt id="customers-label" class="property-label"><g:message code="pricingSet.customers.label" default="Customers" /></dt>
						
							<g:each in="${pricingSetInstance.customers}" var="c">
							<dd class="property-value" aria-labelledby="customers-label"><g:link controller="customer" action="show" id="${c.id}">${c?.encodeAsHTML()}</g:link></dd>
							</g:each>
						
					</dl>
					</g:if>
				
					<g:if test="${pricingSetInstance?.dateCreated}">
					<dl class="dl-horizontal">
						<dt id="dateCreated-label" class="property-label"><g:message code="pricingSet.dateCreated.label" default="Date Created" /></dt>
						
							<dd class="property-value" aria-labelledby="dateCreated-label"><g:formatDate date="${pricingSetInstance?.dateCreated}" /></dd>
						
					</dl>
					</g:if>
				
					<g:if test="${pricingSetInstance?.lastUpdated}">
					<dl class="dl-horizontal">
						<dt id="lastUpdated-label" class="property-label"><g:message code="pricingSet.lastUpdated.label" default="Last Updated" /></dt>
						
							<dd class="property-value" aria-labelledby="lastUpdated-label"><g:formatDate date="${pricingSetInstance?.lastUpdated}" /></dd>
						
					</dl>
					</g:if>
				
					<g:if test="${pricingSetInstance?.productOverrides}">
					<dl class="dl-horizontal">
						<dt id="productOverrides-label" class="property-label"><g:message code="pricingSet.productOverrides.label" default="Product Overrides" /></dt>
						
							<g:each in="${pricingSetInstance.productOverrides}" var="p">
							<dd class="property-value" aria-labelledby="productOverrides-label"><g:link controller="productOverride" action="show" id="${p.id}">${p?.encodeAsHTML()}</g:link></dd>
							</g:each>
						
					</dl>
					</g:if>
				
				</ul>
				<g:form url="[resource:pricingSetInstance, action:'delete']" method="DELETE">
					<fieldset class="buttons">
						<g:link class="btn btn-primary" action="edit" resource="${pricingSetInstance}"><g:message code="default.button.edit.label" default="Edit" /></g:link>
						<g:actionSubmit class="btn btn-danger" action="delete" value="${message(code: 'default.button.delete.label', default: 'Delete')}" onclick="return confirm('${message(code: 'default.button.delete.confirm.message', default: 'Are you sure?')}');" />
					</fieldset>
				</g:form>
			</div>
		</div>
	</body>
</html>
