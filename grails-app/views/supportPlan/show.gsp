
<%@ page import="condense.SupportPlan" %>
<!DOCTYPE html>
<html>
	<head>
		<meta name="layout" content="main">
		<g:set var="entityName" value="${message(code: 'supportPlan.label', default: 'Support Plan')}" />
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
			<div id="show-supportPlan" class="content scaffold-show" role="main">
				<h1><g:message code="default.show.label" args="[entityName]" /></h1>
				<g:if test="${flash.message}">
				<div class="alert alert-success" role="status">${flash.message}</div>
				</g:if>
				<g:if test="${flash.error}">
				<div class="alert alert-danger" role="status">${flash.error}</div>
				</g:if>
				<ul class="property-list supportPlan">
				
					<g:if test="${supportPlanInstance?.name}">
					<dl class="dl-horizontal">
						<dt id="name-label" class="property-label"><g:message code="supportPlan.name.label" default="Name" /></dt>
						
							<dd class="property-value" aria-labelledby="name-label"><g:fieldValue bean="${supportPlanInstance}" field="name"/></dd>
						
					</dl>
					</g:if>
				
					<g:if test="${supportPlanInstance?.minCharge}">
					<dl class="dl-horizontal">
						<dt id="minCharge-label" class="property-label"><g:message code="supportPlan.minCharge.label" default="Min Charge" /></dt>
						
							<dd class="property-value" aria-labelledby="minCharge-label"><g:fieldValue bean="${supportPlanInstance}" field="minCharge"/></dd>
						
					</dl>
					</g:if>
				
					<g:if test="${supportPlanInstance?.maxCharge}">
					<dl class="dl-horizontal">
						<dt id="maxCharge-label" class="property-label"><g:message code="supportPlan.maxCharge.label" default="Max Charge" /></dt>
						
							<dd class="property-value" aria-labelledby="maxCharge-label"><g:fieldValue bean="${supportPlanInstance}" field="maxCharge"/></dd>
						
					</dl>
					</g:if>
				
					<g:if test="${supportPlanInstance?.customers}">
					<dl class="dl-horizontal">
						<dt id="customers-label" class="property-label"><g:message code="supportPlan.customers.label" default="Customers" /></dt>
						
							<g:each in="${supportPlanInstance.customers}" var="c">
							<dd class="property-value" aria-labelledby="customers-label"><g:link controller="customer" action="show" id="${c.id}">${c?.encodeAsHTML()}</g:link></dd>
							</g:each>
						
					</dl>
					</g:if>
					
					<g:if test="${supportPlanInstance?.supportTiers}">
					<dl class="dl-horizontal">
						<dt id="supportTiers-label" class="property-label"><g:message code="supportPlan.supportTiers.label" default="Support Tiers" /></dt>
							<dd class="property-value" aria-labelledby="supportTiers-label">
								<g:render template="detail_table"/>
							</dd>
						
					</dl>
					</g:if>
				
					<g:if test="${supportPlanInstance?.dateCreated}">
					<dl class="dl-horizontal">
						<dt id="dateCreated-label" class="property-label"><g:message code="supportPlan.dateCreated.label" default="Date Created" /></dt>
						
							<dd class="property-value" aria-labelledby="dateCreated-label"><g:formatDate date="${supportPlanInstance?.dateCreated}" /></dd>
						
					</dl>
					</g:if>
				
					<g:if test="${supportPlanInstance?.lastUpdated}">
					<dl class="dl-horizontal">
						<dt id="lastUpdated-label" class="property-label"><g:message code="supportPlan.lastUpdated.label" default="Last Updated" /></dt>
						
							<dd class="property-value" aria-labelledby="lastUpdated-label"><g:formatDate date="${supportPlanInstance?.lastUpdated}" /></dd>
						
					</dl>
					</g:if>
				</ul>
				<g:form url="[resource:supportPlanInstance, action:'delete']" method="DELETE">
					<fieldset class="buttons">
						<g:link class="btn btn-primary" action="edit" resource="${supportPlanInstance}"><g:message code="default.button.edit.label" default="Edit" /></g:link>
						<g:actionSubmit class="btn btn-danger" action="delete" value="${message(code: 'default.button.delete.label', default: 'Delete')}" onclick="return confirm('${message(code: 'default.button.delete.confirm.message', default: 'Are you sure?')}');" />
					</fieldset>
				</g:form>
			</div>
		</div>
	</body>
</html>
