
<%@ page import="condense.CurrencyRate" %>
<!DOCTYPE html>
<html>
	<head>
		<meta name="layout" content="main">
		<g:set var="entityName" value="${message(code: 'currencyRate.label', default: 'CurrencyRate')}" />
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
			<div id="show-currencyRate" class="content scaffold-show" role="main">
				<h1><g:message code="default.show.label" args="[entityName]" /></h1>
				<g:if test="${flash.message}">
				<div class="alert alert-success" role="status">${flash.message}</div>
				</g:if>
				<g:if test="${flash.error}">
				<div class="alert alert-danger" role="status">${flash.error}</div>
				</g:if>
				<ul class="property-list currencyRate">
				
					<g:if test="${currencyRateInstance?.currency}">
					<dl class="dl-horizontal">
						<dt id="currency-label" class="property-label"><g:message code="currencyRate.currency.label" default="Currency" /></dt>
						
							<dd class="property-value" aria-labelledby="currency-label"><g:fieldValue bean="${currencyRateInstance}" field="currency"/></dd>
						
					</dl>
					</g:if>
				
					<g:if test="${currencyRateInstance?.rate}">
					<dl class="dl-horizontal">
						<dt id="rate-label" class="property-label"><g:message code="currencyRate.rate.label" default="Rate" /></dt>
						
							<dd class="property-value" aria-labelledby="rate-label">
								${currencyRateInstance.rate}
							</dd>
						
					</dl>
					</g:if>
				
					<g:if test="${currencyRateInstance?.dateCreated}">
					<dl class="dl-horizontal">
						<dt id="dateCreated-label" class="property-label"><g:message code="currencyRate.dateCreated.label" default="Date Created" /></dt>
						
							<dd class="property-value" aria-labelledby="dateCreated-label"><g:formatDate date="${currencyRateInstance?.dateCreated}" /></dd>
						
					</dl>
					</g:if>
				
					<g:if test="${currencyRateInstance?.lastUpdated}">
					<dl class="dl-horizontal">
						<dt id="lastUpdated-label" class="property-label"><g:message code="currencyRate.lastUpdated.label" default="Last Updated" /></dt>
						
							<dd class="property-value" aria-labelledby="lastUpdated-label"><g:formatDate date="${currencyRateInstance?.lastUpdated}" /></dd>
						
					</dl>
					</g:if>
				
				</ul>
				<g:form url="[resource:currencyRateInstance, action:'delete']" method="DELETE">
					<fieldset class="buttons">
						<g:link class="btn btn-primary" action="edit" resource="${currencyRateInstance}"><g:message code="default.button.edit.label" default="Edit" /></g:link>
						<g:actionSubmit class="btn btn-danger" action="delete" value="${message(code: 'default.button.delete.label', default: 'Delete')}" onclick="return confirm('${message(code: 'default.button.delete.confirm.message', default: 'Are you sure?')}');" />
					</fieldset>
				</g:form>
			</div>
		</div>
	</body>
</html>
