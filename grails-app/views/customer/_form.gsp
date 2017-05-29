<%@ page import="condense.Customer" %>



<div class="form-group fieldcontain ${hasErrors(bean: customerInstance, field: 'cspCustomerPrimaryDomain', 'error')} required">
	<label class="control-label col-md-2" for="cspCustomerPrimaryDomain">
		<g:message code="customer.cspCustomerPrimaryDomain.label" default="CSP Customer Primary Domain" />
		<span class="required-indicator">*</span>
	</label>
	<span class="controls col-md-10">
		<g:textField name="cspCustomerPrimaryDomain" required="" value="${customerInstance?.cspCustomerPrimaryDomain}"/>
	</span>
</div>
<div class="form-group fieldcontain ${hasErrors(bean: customerInstance, field: 'cspCustomerId', 'error')} required">
	<label class="control-label col-md-2" for="cspCustomerId">
		<g:message code="customer.cspCustomerId.label" default="CSP Customer Id" />
		<span class="required-indicator">*</span>
	</label>
	<span class="controls col-md-10"><g:textField name="cspCustomerId" required="" value="${customerInstance?.cspCustomerId}"/>
</span>
</div>

<div class="form-group fieldcontain ${hasErrors(bean: customerInstance, field: 'cspDomain', 'error')} required">
	<label class="control-label col-md-2" for="cspCustomerId">
		<g:message code="customer.cspDomain.label" default="CSP Domain" />
		<span class="required-indicator">*</span>
	</label>
	<span class="controls col-md-10"><g:textField name="cspDomain" required="" value="${customerInstance?.cspDomain}"/>
</span>
</div>

<div class="form-group fieldcontain ${hasErrors(bean: customerInstance, field: 'externalId', 'error')} required">
	<label class="control-label col-md-2" for="externalId">
		<g:message code="customer.externalId.label" default="External Id" />
		<span class="required-indicator">*</span>
	</label>
	<span class="controls col-md-10"><g:textField name="externalId" required="" value="${customerInstance?.externalId}"/>
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
		<g:message code="product.subscription.label" default="Subscription" />
		<g:textField name="subscription.subscriptionId" id="subscription-detail-id" value=""/>
		<a href="#" id="addSubscriptionId" class="btn btn-warning"><g:message code="add.label" default="Add" /></a>
	</span>
	<div class="alert alert-danger hide col-md-10" role="alert" id="unable-to-add-row">
		<g:message code="customer.cannot.add.subscription" default="Please provide subscriptonId that belongig to this customer" />
	</div>
</div>
<div class="form-group fieldcontain ${hasErrors(bean: customerInstance, field: 'subscriptions', 'error')} ">
	<div class="col-md-2">&nbsp;</div>
	<div class="pre-scrollable col-md-10" id="detailsContent">
		<g:render template="detail_table"/>
	</div>
</div>

<asset:script>
$(document).ready(function(){
	$("#addSubscriptionId").unbind().click(function () {
		if ($('#subscription-detail-id').val().trim() == "" || $('#subscription-detail-id').val().trim() == "") {
			$("#unable-to-add-row").removeClass('hide');
			return false;
		} else {
			$("#unable-to-add-row").addClass('hide');
		}
		jQuery.ajax({
			type: 'POST',
			url: '<g:createLink controller="customer" action="ajax_add_row" />',
			data: {
				'subscriptionId': $('#subscription-detail-id').val().trim()
			},
			success: function(data,textStatus) {
				 	jQuery('#detailsContent').html(data);
			},
			error: function(XMLHttpRequest,textStatus,errorThrown){
			}
		});
		return false;
	});
});
</asset:script>