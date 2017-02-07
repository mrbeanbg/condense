
<%@ page import="condense.ConfigDb" %>
<!DOCTYPE html>
<html>
	<head>
		<meta name="layout" content="main">
		<g:set var="entityName" value="${message(code: 'configDb.label', default: 'ConfigDb')}" />
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
			<div id="list-configDb" class="content scaffold-list" role="main">
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
						
							<g:sortableColumn property="fieldKey" title="${message(code: 'configDb.fieldKey.label', default: 'Field Key')}" />
						
							<g:sortableColumn property="fieldVal" title="${message(code: 'configDb.fieldVal.label', default: 'Field Val')}" />
						
							<g:sortableColumn property="dateCreated" title="${message(code: 'configDb.dateCreated.label', default: 'Date Created')}" />
						
							<g:sortableColumn property="lastUpdated" title="${message(code: 'configDb.lastUpdated.label', default: 'Last Updated')}" />
						
						</tr>
					</thead>
					<tbody>
					<g:each in="${configDbInstanceList}" status="i" var="configDbInstance">
						<tr class="${(i % 2) == 0 ? 'even' : 'odd'}">
						
							<td><g:link action="show" id="${configDbInstance.id}">${fieldValue(bean: configDbInstance, field: "fieldKey")}</g:link></td>
						
							<td>${fieldValue(bean: configDbInstance, field: "fieldVal")}</td>
						
							<td><g:formatDate date="${configDbInstance.dateCreated}" /></td>
						
							<td><g:formatDate date="${configDbInstance.lastUpdated}" /></td>
						
						</tr>
					</g:each>
					</tbody>
				</table>
				<div class="pagination">
					<g:paginate total="${configDbInstanceCount ?: 0}" />
				</div>
			</div>
		</div>
	</body>
</html>
