
<%@ page import="condense.PricingBook" %>
<!DOCTYPE html>
<html>
	<head>
		<meta name="layout" content="main">
		<g:set var="entityName" value="${message(code: 'pricingBook.label', default: 'Pricing Book')}" />
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
			<div id="show-pricingBook" class="content scaffold-show" role="main">
				<h1><g:message code="default.show.label" args="[entityName]" /></h1>
				<g:if test="${flash.message}">
				<div class="alert alert-success" role="status">${flash.message}</div>
				</g:if>
				<g:if test="${flash.error}">
				<div class="alert alert-danger" role="status">${flash.error}</div>
				</g:if>
				<ul class="property-list pricingBook">
				
					<g:if test="${pricingBookInstance?.fromDate}">
					<dl class="dl-horizontal">
						<dt id="fromDate-label" class="property-label"><g:message code="pricingBook.fromDate.label" default="From Date" /></dt>
						
							<dd class="property-value" aria-labelledby="fromDate-label"><g:formatDate date="${pricingBookInstance?.fromDate}" /></dd>
						
					</dl>
					</g:if>
				
					<g:if test="${pricingBookInstance?.dateCreated}">
					<dl class="dl-horizontal">
						<dt id="dateCreated-label" class="property-label"><g:message code="pricingBook.dateCreated.label" default="Date Created" /></dt>
						
							<dd class="property-value" aria-labelledby="dateCreated-label"><g:formatDate date="${pricingBookInstance?.dateCreated}" /></dd>
						
					</dl>
					</g:if>
				
					<g:if test="${pricingBookInstance?.lastUpdated}">
					<dl class="dl-horizontal">
						<dt id="lastUpdated-label" class="property-label"><g:message code="pricingBook.lastUpdated.label" default="Last Updated" /></dt>
						
							<dd class="property-value" aria-labelledby="lastUpdated-label"><g:formatDate date="${pricingBookInstance?.lastUpdated}" /></dd>
						
					</dl>
					</g:if>
				
					<g:if test="${pricingBookInstance?.tierDefinitions}">
					<dl class="dl-horizontal">
						<dt id="tierDefinitions-label" class="property-label"><g:message code="pricingBook.tierDefinitions.label" default="Tier Definitions" /></dt>
						
							<g:each in="${pricingBookInstance.tierDefinitions}" var="t">
							<dd class="property-value" aria-labelledby="tierDefinitions-label"><g:link controller="tierDefinition" action="show" id="${t.id}">${t?.encodeAsHTML()}</g:link></dd>
							</g:each>
						
					</dl>
					</g:if>
				
				</ul>
				<g:form url="[resource:pricingBookInstance, action:'delete']" method="DELETE">
					<fieldset class="buttons">
						<g:actionSubmit class="btn btn-danger" action="delete" value="${message(code: 'default.button.delete.label', default: 'Delete')}" onclick="return confirm('${message(code: 'default.button.delete.confirm.message', default: 'Are you sure?')}');" />
					</fieldset>
				</g:form>
			</div>
		</div>
	</body>
</html>
