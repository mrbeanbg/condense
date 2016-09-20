<%@ page import="condense.SupportPlan" %>

<div class="form-group fieldcontain ${hasErrors(bean: supportPlanInstance, field: 'name', 'error')} required">
	<label class="control-label col-md-2" for="name">
		<g:message code="supportPlan.name.label" default="Name" />
		<span class="required-indicator">*</span>
	</label>
	<span class="controls col-md-10"><g:textField name="name" required="" value="${supportPlanInstance?.name}"/>
</span>
</div>

<div class="form-group fieldcontain ${hasErrors(bean: supportPlanInstance, field: 'minCharge', 'error')} ">
	<label class="control-label col-md-2" for="minCharge">
		<g:message code="supportPlan.minCharge.label" default="Min Charge" />
		
	</label>
	<span class="controls col-md-10">
		<g:field name="minCharge" id="minChargeId" value="${fieldValue(bean: supportPlanInstance, field: 'minCharge')}"/>
	</span>
</div>

<div class="form-group fieldcontain ${hasErrors(bean: supportPlanInstance, field: 'maxCharge', 'error')} ">
	<label class="control-label col-md-2" for="maxCharge">
		<g:message code="supportPlan.maxCharge.label" default="Max Charge" />
		
	</label>
	<span class="controls col-md-10">
		<g:field name="maxCharge" id="maxChargeId" value="${fieldValue(bean: supportPlanInstance, field: 'maxCharge')}"/>
	</span>
</div>

<g:if test="${supportPlanInstance?.customers}">
<div class="form-group fieldcontain ${hasErrors(bean: supportPlanInstance, field: 'customers', 'error')} ">
	<label class="control-label col-md-2" for="customers">
		<g:message code="supportPlan.customers.label" default="Customers" />
		
	</label>
	<span class="controls col-md-10">
		<ul class="one-to-many">
		<g:each in="${supportPlanInstance?.customers?}" var="c">
		    <li>${c?.encodeAsHTML()}</li>
		</g:each>
		<li class="add">
		<g:link controller="customer" action="create" params="['supportPlan.id': supportPlanInstance?.id]">${message(code: 'default.add.label', args: [message(code: 'customer.label', default: 'Customer')])}</g:link>
		</li>
		</ul>
	</span>
</div>
</g:if>


<div class="form-group fieldcontain ${hasErrors(bean: supportPlanInstance, field: 'supportTiers', 'error')} ">
	<label class="control-label col-md-2" for="supportTiers">
		<g:message code="supportPlan.supportTiers.label" default="Support Tiers" />
	</label>
	<span class="controls col-md-10">
		<g:message code="supportTier.startAmount.label" default="Start Amount" />
		<g:textField name="supportTier.startAmount" id="supportTier-startAmount" value=""/>
		
		<g:message code="supportTier.tierType.lablel" default="Tier Type" />
		<g:select name="supportTier.tierType" id="supportTier-tierType"
		   from="${condense.SupportTier$TierType?.values()}"
		   keys="${condense.SupportTier$TierType.values()*.name()}" required="" value="" />
		
		<g:message code="supportTier.rate.lablel" default="Rate" />
		<g:textField name="supportTier.rate" id="supportTier-rate" value=""/>

		<a href="#" id="addSupportTier" class="btn btn-warning"><g:message code="add.label" default="Add" /></a>
	</span>
	<div class="alert alert-danger hide col-md-10" role="alert" id="unable-to-add-row">
		<g:message code="supportPlan.cannot.add.supportTier" default="Please define the support tier" />
	</div>
</div>
<div class="form-group fieldcontain ${hasErrors(bean: supportPlanInstance, field: 'supportTiers', 'error')} ">
	<div class="col-md-2">&nbsp;</div>
	<div class="pre-scrollable col-md-10" id="detailsContent">
		<g:render template="detail_table"/>
	</div>
</div>

<asset:script>
$(document).ready(function(){
	$("#addSupportTier").unbind().click(function () {
		var startAmount = null;
		var tierType = $('#supportTier-tierType').val().trim();
		var rate = $('#supportTier-rate').val().trim();
		
		if ($('#supportTier-startAmount').val().trim() == "") {
			startAmount = 0;
		} else {
			startAmount = $('#supportTier-startAmount').val().trim();
		}
		if (tierType == "" || rate == "") {
			$("#unable-to-add-row").removeClass('hide');
			return false;
		} else {
			$("#unable-to-add-row").addClass('hide');
		}
		
		$('#supportTier-startAmount').val("");
		$('#supportTier-rate').val("");
		
		jQuery.ajax({
			type: 'POST',
			url: '<g:createLink controller="supportPlan" action="ajax_add_row" />',
			data: {
				'startAmount': startAmount,
				'tierType': tierType,
				'rate': rate 
			},
			success: function(data,textStatus) {
				 	jQuery('#detailsContent').html(data);
			},
			error: function(XMLHttpRequest,textStatus,errorThrown){
			}
		});
		return false;
	});
	
	$("#minChargeId").inputmask({
	    	'alias': 'numeric',
	    	'groupSeparator': '',
	    	'autoGroup': true,
	    	'digits': 2,
	    	'digitsOptional': false,
	    	'placeholder': '0'});
	    	
	$("#maxChargeId").inputmask({
	    	'alias': 'numeric',
	    	'groupSeparator': '',
	    	'autoGroup': true,
	    	'digits': 2,
	    	'digitsOptional': false,
	    	'placeholder': '0'});
	
	$("#supportTier-startAmount").inputmask({
	    	'alias': 'numeric',
	    	'groupSeparator': '',
	    	'autoGroup': true,
	    	'digits': 0,
	    	'digitsOptional': false,
	    	'placeholder': '0'});
	 
	 $("#supportTier-rate").inputmask({
	    	'alias': 'numeric',
	    	'groupSeparator': '',
	    	'autoGroup': true,
	    	'digits': 2,
	    	'digitsOptional': false,
	    	'placeholder': '0'});   	
});
</asset:script>
