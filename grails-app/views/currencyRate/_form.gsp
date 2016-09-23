<%@ page import="condense.CurrencyRate" %>



<div class="form-group fieldcontain ${hasErrors(bean: currencyRateInstance, field: 'currency', 'error')} required">
	<label class="control-label col-md-2" for="currency">
		<g:message code="currencyRate.currency.label" default="Currency" />
		<span class="required-indicator">*</span>
	</label>
	<span class="controls col-md-10"><g:textField name="currency" required="" value="${currencyRateInstance?.currency}"/>
</span>
</div>

<div class="form-group fieldcontain ${hasErrors(bean: currencyRateInstance, field: 'rate', 'error')} required">
	<label class="control-label col-md-2" for="rate">
		<g:message code="currencyRate.rate.label" default="Rate" />
		<span class="required-indicator">*</span>
	</label>
	<span class="controls col-md-10"><g:field name="rate" value="${currencyRateInstance.rate}" required=""/>
</span>
</div>

