
<%@ page import="condense.SupportPlan" %>
<!DOCTYPE html>
<html>
	<head>
		<meta name="layout" content="main">
		<g:set var="entityName" value="${message(code: 'supportPlan.label', default: 'Support Plan')}" />
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
			<div id="list-supportPlan" class="content scaffold-list" role="main">
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
						
							<g:sortableColumn property="name" title="${message(code: 'supportPlan.name.label', default: 'Name')}" />
						
							<g:sortableColumn property="minCharge" title="${message(code: 'supportPlan.minCharge.label', default: 'Min Charge')}" />
						
							<g:sortableColumn property="maxCharge" title="${message(code: 'supportPlan.maxCharge.label', default: 'Max Charge')}" />
						
							<g:sortableColumn property="dateCreated" title="${message(code: 'supportPlan.dateCreated.label', default: 'Date Created')}" />
						
							<g:sortableColumn property="lastUpdated" title="${message(code: 'supportPlan.lastUpdated.label', default: 'Last Updated')}" />
						
						</tr>
					</thead>
					<tbody>
					<g:each in="${supportPlanInstanceList}" status="i" var="supportPlanInstance">
						<tr class="${(i % 2) == 0 ? 'even' : 'odd'}">
						
							<td><g:link action="show" id="${supportPlanInstance.id}">${fieldValue(bean: supportPlanInstance, field: "name")}</g:link></td>
						
							<td>
								<g:if test="${supportPlanInstance.minCharge == null || supportPlanInstance.minCharge == 0}">
									Undefined Min Charge
								</g:if>
								${fieldValue(bean: supportPlanInstance, field: "minCharge")}
							</td>
						
							<td>
								<g:if test="${supportPlanInstance.maxCharge == null || supportPlanInstance.maxCharge == 0}">
									Undefined Max Charge
								</g:if>
								${fieldValue(bean: supportPlanInstance, field: "maxCharge")}
							</td>
						
							<td><g:formatDate date="${supportPlanInstance.dateCreated}" /></td>
						
							<td><g:formatDate date="${supportPlanInstance.lastUpdated}" /></td>
						
						</tr>
					</g:each>
					</tbody>
				</table>
				<div class="pagination">
					<g:paginate total="${supportPlanInstanceCount ?: 0}" />
				</div>
			</div>
		</div>
	</body>
</html>
