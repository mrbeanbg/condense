<%@ page import="condense.ConfigDb" %>



<div class="form-group fieldcontain ${hasErrors(bean: currentDefaultPricingSet, field: 'fieldVal', 'error')} required">
	<label class="control-label col-md-2" for="fieldKey">
		<g:message code="configDb.default.pricing.set.label" default="Default Pricing Set" />
		<span class="required-indicator">*</span>
	</label>
	<span class="controls col-md-10">
		<g:select id="currentDefaultPricingSet" name="currentDefaultPricingSet.fieldVal" from="${condense.PricingSet.list()}" optionKey="id" required="" value="${currentDefaultPricingSet?.fieldVal}" noSelection="${['null':'NULL']}" />
</span>
</div>

<div class="form-group fieldcontain ${hasErrors(bean: currentdefaultSupportPlan, field: 'fieldVal', 'error')} required">
	<label class="control-label col-md-2" for="fieldVal">
		<g:message code="configDb.default.support.plan.label" default="Default Support Plan" />
		<span class="required-indicator">*</span>
	</label>
	<span class="controls col-md-10"><g:select id="currentdefaultSupportPlan" name="currentdefaultSupportPlan.fieldVal" from="${condense.SupportPlan.list()}" optionKey="id" required="" value="${currentdefaultSupportPlan?.fieldVal}" noSelection="${['null':'NULL']}" />
</span>
</div>

