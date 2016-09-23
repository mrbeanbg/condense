<div class="col-md-12 tier-definitions">

	<div class="col-md-12"><strong><g:message code="tiers.price.overrides.label" default="Tiers/Price overides"/>:</strong></div>
	<div class="col-md-12" style="padding-bottom: 10px;">
		<div class="col-md-3">
			<g:message code="included.quantity.label" default="Included Quantity"/>: <g:field id="includedQuantity" name="includedQuantity" />
		</div>
		<div class="col-md-3">
			<g:message code="start.quantity.label" default="Start Quantity"/>: <g:field id="startQuantity" name="startQuantity" />
		</div>
		<div class="col-md-2">
			<g:message code="adjustment.type.label" default="Adjustment type"/>: 
			<g:select name="overrideType" id="overrideType"
				from="${condense.ProductOverride$OverrideType?.values()}"
				keys="${condense.ProductOverride$OverrideType.values()*.name()}" required="" value="" />
		</div>
		<div class="col-md-2"><g:message code="adjustment.label" default="Adjustment"/>: <g:field id="amount" name="amount" /></div>
		<div class="col-md-1">
			<a href="#" class="btn btn-warning" id="addTier" data-productid="${currentProduct.id}">
				<g:message code="add.tier.label" default="Add tier"/>
			</a>
		</div>
		<div id="unable-to-add-row" class="col-md-12 alert alert-danger hide">
			<g:message code="error.unabletoaddtier.please.define.correct.tier"
				default="Unable to add tier. Please define correct pricing tier. Thank You!"/>
		</div>
	</div>
	<span id="tiers-container">
		<g:render template="tiers_container"/>
	</span>
	
	<div class="col-md-12"><hr /></div>
	
	<div class="col-md-12"><strong><g:message code="original.prices.label" default="Original prices"/>:</strong></div>
	<div class="col-md-12">
		<table class="table table-striped table-bordered table-hover">
			<thead>
				<th><g:message code="included.quantity.label" default="Included Quantity"/></th>
				<th><g:message code="start.quantity.label" default="Start Quantity"/></th>
				<th><g:message code="price.label" default="Price (USD)"/></th>
			</thead>
			<tbody>
				<g:each in="${tierDefinitions}" var="t">
					<tr>
						<td>${t.includedQuantity}</td>
						<td>${t.startQuantity}</td>
						<td>${t.price}</td>
					</tr>
				</g:each>
			</tbody>
		</table>
	</div>
</div>

<asset:script>
$(document).ready(function(){
	$("#addTier").unbind().click(function () {
		var includedQuantity = null;
		var startQuantity = null;
		
		var overrideType = $('#overrideType').val().trim();
		var amount = $('#amount').val().trim();
		var productId = $(this).data("productid");
		
		if ($('#includedQuantity').val().trim() == "") {
			includedQuantity = "0";
		} else {
			includedQuantity = $('#includedQuantity').val().trim();
		}
		
		if ($('#startQuantity').val().trim() == "") {
			startQuantity = "0";
		} else {
			startQuantity = $('#startQuantity').val().trim();
		}
		
		if (startQuantity == "" || amount == "") {
			$("#unable-to-add-row").removeClass('hide');
			return false;
		} else {
			$("#unable-to-add-row").addClass('hide');
		}
		
		$('#includedQuantity').val("");
		$('#startQuantity').val("");
		
		jQuery.ajax({
			type: 'POST',
			url: '<g:createLink controller="pricingSet" action="ajax_add_tier" />',
			data: {
				'currentProductId': productId,
				'currentPricingBookId': $("#currentPricingBook").val().trim(),
				'currentPricingSetId': $("#pricingSetInstanceId").val().trim(),
				'includedQuantity': includedQuantity,
				'startQuantity': startQuantity,
				'overrideType': overrideType,
				'amount': amount 
			},
			success: function(data,textStatus) {
				 	jQuery('#tiers-container').html(data);
			},
			error: function(XMLHttpRequest,textStatus,errorThrown){
			}
		});
		return false;
	});
	
	 $("#includedQuantity").inputmask({
	    	'alias': 'numeric',
	    	'groupSeparator': '',
	    	'autoGroup': true,
	    	'digits': 0,
	    	'digitsOptional': false,
	    	'placeholder': '0'});
	    	
	 $("#startQuantity").inputmask({
	    	'alias': 'numeric',
	    	'groupSeparator': '',
	    	'autoGroup': true,
	    	'digits': 0,
	    	'digitsOptional': false,
	    	'placeholder': '0'});
	 
	 $("#adjustment").inputmask({
	    	'alias': 'numeric',
	    	'groupSeparator': '',
	    	'autoGroup': true,
	    	'digits': 2,
	    	'digitsOptional': false,
	    	'placeholder': '0'});   	
});
</asset:script>