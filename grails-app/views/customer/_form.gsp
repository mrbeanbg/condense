<%@ page import="condense.Customer" %>



<div class="form-group fieldcontain ${hasErrors(bean: customerInstance, field: 'cspCustomerId', 'error')} required">
	<label class="control-label col-md-2" for="cspCustomerId">
		<g:message code="customer.cspCustomerId.label" default="Csp Customer Id" />
		<span class="required-indicator">*</span>
	</label>
	<span class="controls col-md-10"><g:textField name="cspCustomerId" required="" value="${customerInstance?.cspCustomerId}"/>
</span>
</div>

<div class="form-group fieldcontain ${hasErrors(bean: customerInstance, field: 'pricingSet', 'error')} required">
	<label class="control-label col-md-2" for="pricingSet">
		<g:message code="customer.pricingSet.label" default="Pricing Set" />
		<span class="required-indicator">*</span>
	</label>
	<span class="controls col-md-10"><g:select id="pricingSet" name="pricingSet.id" from="${condense.PricingSet.list()}" optionKey="id" required="" value="${customerInstance?.pricingSet?.id}" class="many-to-one"/>
</span>
</div>

<div class="form-group fieldcontain ${hasErrors(bean: customerInstance, field: 'supportPlan', 'error')} ">
	<label class="control-label col-md-2" for="supportPlan">
		<g:message code="customer.supportPlan.label" default="Support Plan" />
		
	</label>
	<span class="controls col-md-10"><g:select id="supportPlan" name="supportPlan.id" from="${condense.SupportPlan.list()}" optionKey="id" value="${customerInstance?.supportPlan?.id}" class="many-to-one" noSelection="['null': '']"/>
</span>
</div>

<div class="form-group fieldcontain ${hasErrors(bean: customerInstance, field: 'subscriptions', 'error')} ">
	<label class="control-label col-md-2" for="subscriptions">
		<g:message code="customer.subscriptions.label" default="Subscriptions" />
		
	</label>
	<span class="controls col-md-10">
<ul class="one-to-many">
<g:each in="${customerInstance?.subscriptions?}" var="s">
    <li><g:link controller="subsciption" action="show" id="${s.id}">${s?.encodeAsHTML()}</g:link></li>
</g:each>
<li class="add">
<g:link controller="subsciption" action="create" params="['customer.id': customerInstance?.id]">${message(code: 'default.add.label', args: [message(code: 'subsciption.label', default: 'Subsciption')])}</g:link>
</li>
</ul>

</span>
</div>

