<%@ page import="condense.PricingBook" %>



<div class="form-group fieldcontain ${hasErrors(bean: pricingBookInstance, field: 'fromDate', 'error')} required">
	<label class="control-label col-md-2" for="fromDate">
		<g:message code="pricingBook.fromDate.label" default="From Date" />
		<span class="required-indicator">*</span>
	</label>
	<span class="controls col-md-10"><g:datePicker name="fromDate" precision="day"  value="${pricingBookInstance?.fromDate}"  />
</span>
</div>

<div class="form-group fieldcontain ${hasErrors(bean: pricingBookInstance, field: 'tierDefinitions', 'error')} ">
	<label class="control-label col-md-2" for="file">
		CSV File
		<span class="required-indicator">*</span>
	</label>
	<span class="controls col-md-10"><input type="file" name="file"></span>
</div>

