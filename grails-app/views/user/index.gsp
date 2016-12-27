
<%@ page import="condense.User" %>
<!DOCTYPE html>
<html>
	<head>
		<meta name="layout" content="main">
		<g:set var="entityName" value="${message(code: 'user.label', default: 'User')}" />
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
			<div id="list-user" class="content scaffold-list" role="main">
				<h1><g:message code="default.list.label" args="[entityName]" /></h1>
				<g:if test="${flash.message}">
					<div class="alert alert-success" role="status">${flash.message}</div>
				</g:if>
				<table class="table table-striped table-bordered table-hover">
				<thead>
						<tr>
						
							<g:sortableColumn property="username" title="${message(code: 'user.username.label', default: 'Username')}" />
						
							<th>${message(code: 'user.role.label', default: 'Role')}</th>
						
							<g:sortableColumn property="dateCreated" title="${message(code: 'user.dateCreated.label', default: 'Date Created')}" />
						
							<g:sortableColumn property="lastUpdated" title="${message(code: 'user.lastUpdated.label', default: 'Last Updated')}" />
						
						</tr>
					</thead>
					<tbody>
					<g:each in="${userInstanceList}" status="i" var="userInstance">
						<tr class="${(i % 2) == 0 ? 'even' : 'odd'}">
						
							<td><g:link action="show" id="${userInstance.id}">${fieldValue(bean: userInstance, field: "username")}</g:link></td>
						
						
							<td>
								<g:message code='${userInstance.authorities[0]}' />
							</td>
						
							<td><g:formatDate date="${userInstance.dateCreated}" /></td>
						
							<td><g:formatDate date="${userInstance.lastUpdated}" /></td>
						
						</tr>
					</g:each>
					</tbody>
				</table>
				<div class="pagination">
					<g:paginate total="${userInstanceCount ?: 0}" />
				</div>
			</div>
		</div>
	</body>
</html>
