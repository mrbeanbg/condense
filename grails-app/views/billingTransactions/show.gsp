
<%@ page import="condense.Subscription" %>
<!DOCTYPE html>
<html>
	<head>
		<meta name="layout" content="main">
		<g:set var="entityName" value="${message(code: 'billing.trsnsactions.for.subscription.label', default: 'Billing Transactions for Subscription')}" />
		<title><g:message code="default.show.label" args="[entityName]" /></title>
	</head>
	<body>
		<div class="container">
			<div class="nav" role="navigation">
				<ul class="nav nav-pills">
					<li class="active"><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>
					<li><g:link class="list" action="index"><g:message code="default.list.label" args="['Subscriptions']" /></g:link></li>
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
					<g:formRemote name="billingTransactions" update="transactionsTable" method="GET"
						url="${[controller: 'billingTransactions', action: 'obtain_transactions', id: subscriptionInstance?.id]}">
						<div class="col-md-3">
							<label>
								<g:message code="from.date" default="From Date"/>:
							</label>
							<div>
								<g:datePicker name="dateFrom" precision="day" id="fromDate" value="${startDate}"/>
							</div>
						</div>
						<div class="col-md-3">
							<label>
								<g:message code="to.date" default="To Date" />:
							</label>
							<div>
								<g:datePicker name="dateTo" precision="day" id="toDate" value="${toDate}"/>
							</div>
						</div>
						<div class="col-md-3">
							<label>
								<g:message code="currency" default="Currency" />:
							</label>
							<g:select name="currencyRate" from="${condense.CurrencyRate.list()}" optionKey="id" noSelection="${['null':'USD - 1.000000000000000']}" />
						</div>
						<div class="col-md-2">
							<g:submitButton name="transactions" class="btn btn-primary" value="${message(code: 'show.billing.transactions', default: 'Show Billing Transactions')}" />
						</div>
					</g:formRemote>
				</div>
				
				
				<div class="col-md-12" id="transactionsTable">
				</div>
			</div>
		</div>
	</body>
</html>
