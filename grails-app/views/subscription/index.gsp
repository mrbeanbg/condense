
<%@ page import="condense.Subscription" %>
<!DOCTYPE html>
<html>
	<head>
		<meta name="layout" content="main">
		<g:set var="entityName" value="${message(code: 'subscription.label', default: 'Subscriptions and Usages')}" />
		<title><g:message code="default.list.label" args="[entityName]" /></title>
	</head>
	<body>
		<div class="container">
			<div class="nav" role="navigation">
				<ul class="nav nav-pills">
					<li class="active"><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>
				</ul>
			</div>
			<div id="list-subscription" class="content scaffold-list" role="main">
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
							<th><g:message code="subscription.customer.label" default="Customer" /></th>
							
							<g:sortableColumn property="subscriptionId" title="${message(code: 'subscription.subscriptionId.label', default: 'Subscription Id')}" />
						
							<g:sortableColumn property="usageObtainedUntil" title="${message(code: 'subscription.usageObtainedUntil.label', default: 'Usage Obtained Until')}" />
							
							<th><g:message code="actions.lable" default="Actions"/></th>
						
						</tr>
					</thead>
					<tbody>
					<g:each in="${subscriptionInstanceList}" status="i" var="subscriptionInstance">
						<tr class="${(i % 2) == 0 ? 'even' : 'odd'}">
						
							<td>
								<g:link controller="customer" action="show" id="${subscriptionInstance?.customer?.id}">
									${fieldValue(bean: subscriptionInstance, field: "customer")}
								</g:link>
							</td>
						
							<td>${fieldValue(bean: subscriptionInstance, field: "subscriptionId")}</td>
						
							<td>
								<g:if test="${subscriptionInstance.usageObtainedUntil == null}">
									<g:message code="no.usages.obtained" default="No usages obtained"/>
								</g:if>
								<g:else>
									<g:formatDate date="${subscriptionInstance.usageObtainedUntil}" type="date" style="LONG" />
								</g:else>
							</td>
							
							<td>
								<g:if test="${subscriptionInstance.usageObtainedUntil == null}">
									<g:link action="show" id="${subscriptionInstance.id}" class="btn btn-warning disabled">
										<g:message code="view.usages.label" default="View Usages"/>
									</g:link>
								</g:if>
								<g:else>
									<g:link action="show" id="${subscriptionInstance.id}" class="btn btn-warning">
										<g:message code="view.usages.label" default="View Usages"/>
									</g:link>
								</g:else>
								<g:link action="obtain_usage" id="${subscriptionInstance.id}" class="btn btn-warning">
									<g:message code="obtain.usage.manually.label" default="Obtain usage"/>
								</g:link>
							</td>
						
						</tr>
					</g:each>
					</tbody>
				</table>
				<div class="pagination">
					<g:paginate total="${subscriptionInstanceCount ?: 0}" />
				</div>
			</div>
		</div>
	</body>
</html>
