
<%@ page import="condense.ConfigDb" %>
<!DOCTYPE html>
<html>
	<head>
		<meta name="layout" content="main">
		<g:set var="entityName" value="${message(code: 'configDb.label', default: 'ConfigDb')}" />
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
			<div id="show-configDb" class="content scaffold-show" role="main">
				<h1><g:message code="default.show.label" args="[entityName]" /></h1>
				<g:if test="${flash.message}">
				<div class="alert alert-success" role="status">${flash.message}</div>
				</g:if>
				<g:if test="${flash.error}">
				<div class="alert alert-danger" role="status">${flash.error}</div>
				</g:if>
				<ul class="property-list configDb">
				
					<g:if test="${configDbInstance?.fieldKey}">
					<dl class="dl-horizontal">
						<dt id="fieldKey-label" class="property-label"><g:message code="configDb.fieldKey.label" default="Field Key" /></dt>
						
							<dd class="property-value" aria-labelledby="fieldKey-label"><g:fieldValue bean="${configDbInstance}" field="fieldKey"/></dd>
						
					</dl>
					</g:if>
				
					<g:if test="${configDbInstance?.fieldVal}">
					<dl class="dl-horizontal">
						<dt id="fieldVal-label" class="property-label"><g:message code="configDb.fieldVal.label" default="Field Val" /></dt>
						
							<dd class="property-value" aria-labelledby="fieldVal-label"><g:fieldValue bean="${configDbInstance}" field="fieldVal"/></dd>
						
					</dl>
					</g:if>
				
					<g:if test="${configDbInstance?.dateCreated}">
					<dl class="dl-horizontal">
						<dt id="dateCreated-label" class="property-label"><g:message code="configDb.dateCreated.label" default="Date Created" /></dt>
						
							<dd class="property-value" aria-labelledby="dateCreated-label"><g:formatDate date="${configDbInstance?.dateCreated}" /></dd>
						
					</dl>
					</g:if>
				
					<g:if test="${configDbInstance?.lastUpdated}">
					<dl class="dl-horizontal">
						<dt id="lastUpdated-label" class="property-label"><g:message code="configDb.lastUpdated.label" default="Last Updated" /></dt>
						
							<dd class="property-value" aria-labelledby="lastUpdated-label"><g:formatDate date="${configDbInstance?.lastUpdated}" /></dd>
						
					</dl>
					</g:if>
				
				</ul>
				<g:form url="[resource:configDbInstance, action:'delete']" method="DELETE">
					<fieldset class="buttons">
						<g:link class="btn btn-primary" action="edit" resource="${configDbInstance}"><g:message code="default.button.edit.label" default="Edit" /></g:link>
						<g:actionSubmit class="btn btn-danger" action="delete" value="${message(code: 'default.button.delete.label', default: 'Delete')}" onclick="return confirm('${message(code: 'default.button.delete.confirm.message', default: 'Are you sure?')}');" />
					</fieldset>
				</g:form>
			</div>
		</div>
	</body>
</html>
