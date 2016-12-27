
<%@ page import="condense.User" %>
<!DOCTYPE html>
<html>
	<head>
		<meta name="layout" content="main">
		<g:set var="entityName" value="${message(code: 'user.label', default: 'User')}" />
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
			<div id="show-user" class="content scaffold-show" role="main">
				<h1><g:message code="default.show.label" args="[entityName]" /></h1>
				<g:if test="${flash.message}">
				<div class="alert alert-success" role="status">${flash.message}</div>
				</g:if>
				<ul class="property-list user">
				
					<g:if test="${userInstance?.username}">
					<dl class="dl-horizontal">
						<dt id="username-label" class="property-label"><g:message code="user.username.label" default="Username" /></dt>
						
							<dd class="property-value" aria-labelledby="username-label"><g:fieldValue bean="${userInstance}" field="username"/></dd>
						
					</dl>
					</g:if>
				
					<g:if test="${userInstance?.authorities[0]}">
					<dl class="dl-horizontal">
						<dt id="role-label" class="property-label"><g:message code="user.role.label" default="Role" /></dt>
						
							<dd class="property-value" aria-labelledby="role-label">
								<g:message code='${userInstance.authorities[0]}' />
							</dd>
						
					</dl>
					</g:if>
				
					<g:if test="${userInstance?.dateCreated}">
					<dl class="dl-horizontal">
						<dt id="dateCreated-label" class="property-label"><g:message code="user.dateCreated.label" default="Date Created" /></dt>
						
							<dd class="property-value" aria-labelledby="dateCreated-label"><g:formatDate date="${userInstance?.dateCreated}" /></dd>
						
					</dl>
					</g:if>
				
					<g:if test="${userInstance?.lastUpdated}">
					<dl class="dl-horizontal">
						<dt id="lastUpdated-label" class="property-label"><g:message code="user.lastUpdated.label" default="Last Updated" /></dt>
						
							<dd class="property-value" aria-labelledby="lastUpdated-label"><g:formatDate date="${userInstance?.lastUpdated}" /></dd>
						
					</dl>
					</g:if>
				
				</ul>
				<g:form url="[resource:userInstance, action:'delete']" method="DELETE">
					<fieldset class="buttons">
						<g:link class="btn btn-primary" action="edit" resource="${userInstance}"><g:message code="default.button.edit.label" default="Edit" /></g:link>
						<g:actionSubmit class="btn btn-danger" action="delete" value="${message(code: 'default.button.delete.label', default: 'Delete')}" onclick="return confirm('${message(code: 'default.button.delete.confirm.message', default: 'Are you sure?')}');" />
					</fieldset>
				</g:form>
			</div>
		</div>
	</body>
</html>
