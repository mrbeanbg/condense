<g:each in="${matchingProducts}" var="p">
	<div class="alert alert-info col-md-12">
		<div class="col-md-9">${p.name} - ${p.region}</div>
		<div class="col-md-3">
			<a href="#"
				data-productid="${p.id}"
				class="btn btn-info manage-tiers">
				<g:message code="manageorview.prices.label" default="Manage/View Prices"/>
			</a>
		</div>
		<div class="clearfix col-md-12" id="tiers-for-pid${p.id}">
		</div>
	</div>
</g:each>
