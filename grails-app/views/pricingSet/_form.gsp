<%@ page import="condense.PricingSet" %>

<div class="form-group fieldcontain ${hasErrors(bean: pricingSetInstance, field: 'name', 'error')} required">
	<label class="control-label col-md-2" for="name">
		<g:message code="pricingSet.name.label" default="Name" />
		<span class="required-indicator">*</span>
	</label>
	<span class="controls col-md-10"><g:textField name="name" required="" value="${pricingSetInstance?.name}"/>
</span>
</div>

<div class="form-group fieldcontain ${hasErrors(bean: pricingSetInstance, field: 'defaultOverride', 'error')} required">
	<label class="control-label col-md-2" for="defaultOverride">
		<g:message code="pricingSet.defaultOverride.label" default="Default Override" />
		<span class="required-indicator">*</span>
	</label>
	<span class="controls col-md-10"><g:field name="defaultOverride" type="number" value="${pricingSetInstance.defaultOverride}" required=""/>
</span>
</div>
