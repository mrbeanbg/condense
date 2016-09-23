
<%@ page import="condense.PricingSet" %>
<!DOCTYPE html>
<html>
	<head>
		<meta name="layout" content="main">
		<g:set var="entityName" value="${message(code: 'pricingSet.label', default: 'Pricing Set')}" />
		<title><g:message code="default.manage.label" default="Manage ${entityName}" args="[entityName]" /></title>
	</head>
	<body>
		<div class="container">
			<div class="nav" role="navigation">
				<ul class="nav nav-pills">
					<li class="active"><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>
					<li><g:link class="list" action="index"><g:message code="default.list.label" args="[entityName]" /></g:link></li>
					<li><g:link class="create" action="create"><g:message code="default.new.label" args="[entityName]" /></g:link></li>
				</ul>
			</div>
			<div id="show-pricingSet" class="content scaffold-show" role="main">
				<h1><g:message code="default.manage.label" default="Manage ${entityName}" args="[entityName]" /></h1>
				<g:if test="${flash.message}">
				<div class="alert alert-success" role="status">${flash.message}</div>
				</g:if>
				<g:if test="${flash.error}">
				<div class="alert alert-danger" role="status">${flash.error}</div>
				</g:if>
				<input type="hidden" id="pricingSetInstanceId" value="${pricingSetInstance.id}">
				<ul class="property-list pricingSet">
				
					<g:if test="${pricingSetInstance?.name}">
					<dl class="dl-horizontal">
						<dt id="name-label" class="property-label"><g:message code="pricingSet.name.label" default="Name" /></dt>
						
							<dd class="property-value" aria-labelledby="name-label"><g:fieldValue bean="${pricingSetInstance}" field="name"/></dd>
						
					</dl>
					</g:if>
				
					<g:if test="${pricingSetInstance?.defaultOverride}">
					<dl class="dl-horizontal">
						<dt id="defaultOverride-label" class="property-label"><g:message code="pricingSet.defaultOverride.label" default="Default Override" /></dt>
						
							<dd class="property-value" aria-labelledby="defaultOverride-label"><g:fieldValue bean="${pricingSetInstance}" field="defaultOverride"/> %</dd>
						
					</dl>
					</g:if>
				
					<g:if test="${pricingSetInstance?.customers}">
					<dl class="dl-horizontal">
						<dt id="customers-label" class="property-label"><g:message code="pricingSet.customers.label" default="Customers" /></dt>
						
							<g:each in="${pricingSetInstance.customers}" var="c">
							<dd class="property-value" aria-labelledby="customers-label">
								${c?.encodeAsHTML()}</dd>
							</g:each>
						
					</dl>
					</g:if>
				
					<g:if test="${pricingSetInstance?.dateCreated}">
					<dl class="dl-horizontal">
						<dt id="dateCreated-label" class="property-label"><g:message code="pricingSet.dateCreated.label" default="Date Created" /></dt>
						
							<dd class="property-value" aria-labelledby="dateCreated-label"><g:formatDate date="${pricingSetInstance?.dateCreated}" /></dd>
						
					</dl>
					</g:if>
				
					<g:if test="${pricingSetInstance?.lastUpdated}">
					<dl class="dl-horizontal">
						<dt id="lastUpdated-label" class="property-label"><g:message code="pricingSet.lastUpdated.label" default="Last Updated" /></dt>
						
							<dd class="property-value" aria-labelledby="lastUpdated-label"><g:formatDate date="${pricingSetInstance?.lastUpdated}" /></dd>
					</dl>
					</g:if>
				</ul>
				
				<g:if test="${categories?.size() > 0}">
					<div class="col-md-12">
						<hr />
						<div class="col-md-3"><b>Prices and Overrides:</b></div>
						<div class="col-md-3 pull-right">
							<g:message code="pricingBook.label" default="Pricing&nbsp;Book" />
							<g:select id="currentPricingBook" name="currentPricingBook"
									from="${pricingBooks}"
									optionKey="id"/>
						</div>
						
						<g:each in="${categories?}" var="c" status="i">
							<div class="well col-md-12">
								<div class="col-md-12">
									${c.categoryRepresentation}
									<div class="pull-right">
										<a href="#"
											data-catindex="${i}" data-categoryid="${c.categoryId}" data-subcategoryid="${c.subcategoryId}"
											class="btn btn-warning manage-products">
											<g:message code="manageorview.producst.label" default="Manage/View Products"/>
										</a>
									</div>
								</div>
								<div class="col-md-12" id="products-${i}" class="wellcol-md-12"></div>
							</div>
						</g:each>
					</div>
				</g:if>
			</div>
		</div>
	</body>
</html>


<asset:script>
$(document).ready(function(){
	var currentPricingSetId = $("#pricingSetInstanceId").val();
	
	var currentCatIndex = null;
	var currentCategoryId = null;
	var currentSubCategoryId = null;
	var currentPricingBookId = null;
	
	
	var currentProductId = null;
	
	$(".manage-products").unbind().click(function () {
		$('[id^=products-]').html("");
		currentProductId = null;
		
		currentCatIndex = $(this).data("catindex");
		currentCategoryId = $(this).data("categoryid");
		currentSubCategoryId = $(this).data("subcategoryid");
		if (currentSubCategoryId != null &&
			currentSubCategoryId == "null" || currentSubCategoryId == "") {
			currentSubCategoryId = null;
		}
		
		currentPricingBookId = $("#currentPricingBook").val().trim();

		ajaxGetProducts();
		return false;
	});
	
	$("#currentPricingBook").unbind().change(function () {
		currentProductId == null;
		currentPricingBookId = $("#currentPricingBook").val().trim();
		
		if (currentCatIndex != null && currentCategoryId != null) {
			ajaxGetProducts();
		}
	});
	
	function bindManageTiersButton() {
		$(".manage-tiers").unbind().click(function () {
			currentProductId = $(this).data("productid");
			
			$('[id^=tiers-for-pid]').html("");
			
			if (currentProductId != null) {
				ajaxGetTiers();
			}
			
			return false;
		});
	}
	
	function ajaxGetProducts() {
		jQuery.ajax({
			type: 'GET',
			url: '<g:createLink action="ajax_get_products" />',
			data: {
				'currentCategoryId': currentCategoryId,
				'currentSubCategoryId': currentSubCategoryId,
				'currentPricingBookId': currentPricingBookId
			},
			success: function(data,textStatus) {
					//jQuery('.manage-products').attr("disabled", true);
				 	jQuery('#products-' + currentCatIndex).html(data);
				 	bindManageTiersButton();
				 	
				 	if (currentProductId != null && currentProductId != null && $("#tiers-for-pid" + currentProductId) != null) {
				 		ajaxGetTiers();
				 	}
			},
			error: function(XMLHttpRequest,textStatus,errorThrown){
			}
		});
	}
	
	function ajaxGetTiers() {
		jQuery.ajax({
			type: 'GET',
			url: '<g:createLink action="ajax_get_tiers" />',
			data: {
				'currentPricingSetId': currentPricingSetId,
				'currentPricingBookId': currentPricingBookId,
				'currentProductId': currentProductId,
			},
			success: function(data,textStatus) {
				 	jQuery('#tiers-for-pid' + currentProductId).html(data);
			},
			error: function(XMLHttpRequest,textStatus,errorThrown){
			}
		});
	}
});
</asset:script>