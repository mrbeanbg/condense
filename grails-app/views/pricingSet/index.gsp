
<%@ page import="condense.PricingSet" %>
<!DOCTYPE html>
<html>
	<head>
		<meta name="layout" content="main">
		<g:set var="entityName" value="${message(code: 'pricingSet.label', default: 'Pricing Set')}" />
		<title><g:message code="default.list.label" args="[entityName]" /></title>
	</head>
	<body>
		<div class="container">
			<div class="nav" role="navigation">
				<ul class="nav nav-pills">
					<li class="active"><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>
					<li><g:link class="create" action="create"><g:message code="default.new.label" args="[entityName]" /></g:link></li>
				</ul>
			</div>
			<div id="list-pricingSet" class="content scaffold-list" role="main">
				<h1><g:message code="default.list.label" args="[entityName]" /></h1>
				<g:if test="${flash.message}">
					<div class="alert alert-success" role="status">${flash.message}</div>
				</g:if>
				<g:if test="${flash.error}">
					<div class="alert alert-danger" role="status">${flash.error}</div>
				</g:if>
				<table class="table table-striped table-bordered table-hover">
				<thead>
						<tr>
						
							<g:sortableColumn property="name" title="${message(code: 'pricingSet.name.label', default: 'Name')}" />
						
							<g:sortableColumn property="defaultOverride" title="${message(code: 'pricingSet.defaultOverride.label', default: 'Default Override')}" />
						
							<g:sortableColumn property="dateCreated" title="${message(code: 'pricingSet.dateCreated.label', default: 'Date Created')}" />
						
							<g:sortableColumn property="lastUpdated" title="${message(code: 'pricingSet.lastUpdated.label', default: 'Last Updated')}" />
						
						</tr>
					</thead>
					<tbody>
					<g:each in="${pricingSetInstanceList}" status="i" var="pricingSetInstance">
						<tr class="${(i % 2) == 0 ? 'even' : 'odd'}">
						
							<td><g:link action="show" id="${pricingSetInstance.id}">${fieldValue(bean: pricingSetInstance, field: "name")}</g:link></td>
						
							<td>${fieldValue(bean: pricingSetInstance, field: "defaultOverride")}</td>
						
							<td><g:formatDate date="${pricingSetInstance.dateCreated}" /></td>
						
							<td><g:formatDate date="${pricingSetInstance.lastUpdated}" /></td>
						
						</tr>
					</g:each>
					</tbody>
				</table>
				<div class="pagination">
					<g:paginate total="${pricingSetInstanceCount ?: 0}" />
				</div>
			</div>
		</div>
	</body>
</html>
