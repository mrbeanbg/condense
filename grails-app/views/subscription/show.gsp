
<%@ page import="condense.Subscription" %>
<!DOCTYPE html>
<html>
	<head>
		<meta name="layout" content="main">
		<g:set var="entityName" value="${message(code: 'usages.for.subscription.label', default: 'Usges for Subscription')}" />
		<title><g:message code="default.show.label" args="[entityName]" /></title>
	</head>
	<body>
		<div class="container">
			<div class="nav" role="navigation">
				<ul class="nav nav-pills">
					<li class="active"><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>
					<li><g:link class="list" action="index"><g:message code="default.list.label" args="[entityName]" /></g:link></li>
				</ul>
			</div>
			<div id="show-subscription" class="content scaffold-show" role="main">
				<h1><g:message code="default.show.label" args="[entityName]" /></h1>
				<g:if test="${flash.message}">
				<div class="alert alert-success" role="status">${flash.message}</div>
				</g:if>
				<g:if test="${flash.error}">
				<div class="alert alert-danger" role="status">${flash.error}</div>
				</g:if>
				<ul class="property-list subscription">
				
					<g:if test="${subscriptionInstance?.subscriptionId}">
					<dl class="dl-horizontal">
						<dt id="subscriptionId-label" class="property-label"><g:message code="subscription.subscriptionId.label" default="Subscription Id" /></dt>
							<dd class="property-value" aria-labelledby="subscriptionId-label"><g:fieldValue bean="${subscriptionInstance}" field="subscriptionId"/></dd>
					</dl>
					</g:if>
					
					<dl class="dl-horizontal">
						<dt id="subscriptionId-label" class="property-label"><g:message code="subscription.isActive.label" default="Is Active" /></dt>
						
							<dd class="property-value" aria-labelledby="subscriptionId-label"><g:fieldValue bean="${subscriptionInstance}" field="isActive"/></dd>
						
					</dl>
				
					<g:if test="${subscriptionInstance?.usageObtainedUntil}">
					<dl class="dl-horizontal">
						<dt id="usageObtainedUntil-label" class="property-label"><g:message code="subscription.usageObtainedUntil.label" default="Usage Obtained Until" /></dt>
						
							<dd class="property-value" aria-labelledby="usageObtainedUntil-label">
								<g:formatDate date="${subscriptionInstance.usageObtainedUntil}" type="date" style="LONG" />
							</dd>
						
					</dl>
					</g:if>
				
					<g:if test="${subscriptionInstance?.customer}">
					<dl class="dl-horizontal">
						<dt id="customer-label" class="property-label"><g:message code="subscription.customer.label" default="Customer" /></dt>
						
							<dd class="property-value" aria-labelledby="customer-label"><g:link controller="customer" action="show" id="${subscriptionInstance?.customer?.id}">${subscriptionInstance?.customer?.encodeAsHTML()}</g:link></dd>
						
					</dl>
					</g:if>
				
				</ul>
				
				<hr/>
				
				<div class="col-md-12" style="padding-bottom: 20px">
					<g:formRemote name="filterUsagesForm" update="usagesTable" method="GET"
						url="${[controller: 'subscription', action: 'usages', id: subscriptionInstance?.id]}">
						<div class="col-md-4">
							<label>
								<g:message code="from.date" default="From Date"/>:
							</label>
							<g:datePicker name="filterFromDate" precision="day" id="filterFromDate" value="${new Date()-1}"/>
						</div>
						<div class="col-md-4">
							<label>
								<g:message code="to.date" default="To Date" />:
							</label>
							<g:datePicker name="filterToDate" precision="day" id="filterFromDate"/>
						</div>
						<div class="col-md-4">
							<g:submitButton name="usages" class="btn btn-primary" value="${message(code: 'filter.usages', default: 'Filter Usages')}" />
						</div>
					</g:formRemote>
				</div>
				
				
				<div class="col-md-12" id="usagesTable">
					<g:render template="usages_table" />
				</div>
			</div>
		</div>
	</body>
</html>
