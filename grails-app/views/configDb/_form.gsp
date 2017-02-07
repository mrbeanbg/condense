<%@ page import="condense.ConfigDb" %>



<div class="form-group fieldcontain ${hasErrors(bean: configDbInstance, field: 'fieldKey', 'error')} required">
	<label class="control-label col-md-2" for="fieldKey">
		<g:message code="configDb.fieldKey.label" default="Field Key" />
		<span class="required-indicator">*</span>
	</label>
	<span class="controls col-md-10"><g:textField name="fieldKey" required="" value="${configDbInstance?.fieldKey}"/>
</span>
</div>

<div class="form-group fieldcontain ${hasErrors(bean: configDbInstance, field: 'fieldVal', 'error')} required">
	<label class="control-label col-md-2" for="fieldVal">
		<g:message code="configDb.fieldVal.label" default="Field Val" />
		<span class="required-indicator">*</span>
	</label>
	<span class="controls col-md-10"><g:textField name="fieldVal" required="" value="${configDbInstance?.fieldVal}"/>
</span>
</div>

