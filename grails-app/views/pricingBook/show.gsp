
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
				
					<g:if test="${tierDefinitions}">
<table class="table table-striped table-bordered table-hover">
				<thead>
						<tr>
							
							<td>Category</td>
							<td>Subcategory</td>
							<td>Region</td>
							<td>Name</td>
							<td>Resource ID</td>
							<td>Included Quantities</td>
							<td>Minimum Value</td>
							<td>Price (USD)</td>
						</tr>
					</thead>
					<tbody>
					<g:each in="${tierDefinitions}" status="i" var="tierDefinition">
						<tr class="${(i % 2) == 0 ? 'even' : 'odd'}">
						
							<td>${tierDefinition.product.category.name}</td>
						
							<td>${tierDefinition.product.subcategory?.name ?:""}</td>
							<td>${tierDefinition.product.region.name != "Unspecified region" ? tierDefinition.product.region.name : ""}</td>
							<td>${tierDefinition.product.name}</td>
							<td>${tierDefinition.product.guid}</td>
							<td>${tierDefinition.includedQuantity}</td>
							<td>${tierDefinition.startQuantity}</td>
							<td>${tierDefinition.price == 0 ? 0 : tierDefinition.price.stripTrailingZeros()}</td>
						</tr>
					</g:each>
					</tbody>
				</table>
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
