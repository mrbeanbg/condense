		<g:if test="${overridesRepresentation?.size() == 0}">
			<div class="col-md-12 alert alert-warning">
				<g:message code="thedefaul.override.will.be.used.please.define.tiers.to.customize"
						default="The DEFAULT override will be used for calculations. Please define tiers if you wish to customise the prices further."/>
			</div>
		</g:if>
		<g:else>
			<div class="col-md-12">
				<div class="col-md-12">
					<table class="table table-striped table-bordered table-hover">
						<thead>
							<tr>
								<th><g:message code="included.quantity.label" default="Included Quantity"/></th>
								<th><g:message code="start.quantity.label" default="Start Quantity"/></th>
								<th><g:message code="end.quantity.label" default="End Quantity"/></th>
								<th><g:message code="adjustment.type.label" default="Adjustment type"/></th>
								<th><g:message code="adjustment.label" default="Adjustment"/></th>
								<g:if test="${showAction != null && showAction == true}">
								<th><g:message code="default.action.label" default="Action" /></th>
								</g:if>
							</tr>
						</thead>
						<tbody>
							<g:each in="${overridesRepresentation?}" var="o">
								<tr>
									<td>${o.includedQuantity}</td>
									<td>${o.startQuantity}</td>
									<td>${o.endQuantity}</td>
									<td>${o.overrideType}</td>
									<td>${o.amount}</td>
									<g:if test="${showAction != null && showAction == true}">
										<td>
										<g:remoteLink before="if(!confirm('Are you sure?')) return false" action="ajax_delete_row" params='[startQuantity: "${o.startQuantity}"]' update="tiers-container">
											${message(code: 'default.button.delete.label', default: 'Delete')}
										</g:remoteLink>
										</td>
									</g:if>
								</tr>
							</g:each>
						</tbody>
					</table>
				</div>
			</div>
		</g:else>
	
	
		<div class="col-md-12"><hr /></div>
	
		<div class="col-md-12"><strong><g:message code="expected.prices.label" default="Expected prices"/>:</strong></div>
		<div class="col-md-12">the expected prices</div>