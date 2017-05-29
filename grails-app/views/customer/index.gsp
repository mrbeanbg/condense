
<%@ page import="condense.Customer" %>
<!DOCTYPE html>
<html>
	<head>
		<meta name="layout" content="main">
		<g:set var="entityName" value="${message(code: 'customer.label', default: 'Customer')}" />
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
			<div id="list-customer" class="content scaffold-list" role="main">
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
							<g:sortableColumn property="cspCustomerPrimaryDomain" title="${message(code: 'customer.cspCustomerPrimaryDomain.label', default: 'CSP Customer Primary Domain')}" />
						
							<g:sortableColumn property="cspCustomerId" title="${message(code: 'customer.cspCustomerId.label', default: 'Csp Customer Id')}" />
							
							<th><g:message code="customer.cspDomain.label" default="CSP Domain" /></th>
							
							<th><g:message code="customer.externalId.label" default="External Id" /></th>
						
							<th><g:message code="customer.pricingSet.label" default="Pricing Set" /></th>
						
							<th><g:message code="customer.supportPlan.label" default="Support Plan" /></th>
						
							<g:sortableColumn property="dateCreated" title="${message(code: 'customer.dateCreated.label', default: 'Date Created')}" />
						
							<g:sortableColumn property="lastUpdated" title="${message(code: 'customer.lastUpdated.label', default: 'Last Updated')}" />
						
						</tr>
					</thead>
					<tbody>
					<g:each in="${customerInstanceList}" status="i" var="customerInstance">
						<tr class="${(i % 2) == 0 ? 'even' : 'odd'}">
						
						    <td><g:link action="show" id="${customerInstance.id}">${fieldValue(bean: customerInstance, field: "cspCustomerPrimaryDomain")}</g:link></td>
						
							<td>${fieldValue(bean: customerInstance, field: "cspCustomerId")}</td>
							
							<td>${fieldValue(bean: customerInstance, field: "cspDomain")}</td>
							
							<td>${fieldValue(bean: customerInstance, field: "externalId")}</td>
						
							<td>${fieldValue(bean: customerInstance, field: "pricingSet")}</td>
						
							<td>${fieldValue(bean: customerInstance, field: "supportPlan")}</td>
						
							<td><g:formatDate date="${customerInstance.dateCreated}" /></td>
						
							<td><g:formatDate date="${customerInstance.lastUpdated}" /></td>
						
						</tr>
					</g:each>
					</tbody>
				</table>
				<div class="pagination">
					<g:paginate total="${customerInstanceCount ?: 0}" />
				</div>
			</div>
		</div>
	</body>
</html>
