		<table class="table table-striped table-bordered table-hover">
			<thead>
				<tr>
					<th><g:message code="supportTier.startAmount.lablel" default="Start Amount" /></th>
					<th><g:message code="supportTier.endAmount.lablel" default="End Amount" /></th>
					<th><g:message code="supportTier.tierType.lablel" default="Tier Type" /></th>
					<th><g:message code="supportTier.rate.lablel" default="Rate" /></th>
					<g:if test="${showAction != null && showAction == true}">
					<th><g:message code="default.action.label" default="Action" /></th>
					</g:if>
				</tr>
			</thead>
			<tbody>
				<g:each in="${detailRows?}" var="t">
					<tr>
						<td>${t.startAmount}</td>
						<td>${t.endAmount}</td>
						<td>${t.tierType}</td>
						<td>${t.rate}</td>
						<g:if test="${showAction != null && showAction == true}">
							<td>
							<g:remoteLink before="if(!confirm('Are you sure?')) return false" action="ajax_delete_row" params='[startAmount: "${t.startAmount}"]' update="detailsContent">
								${message(code: 'default.button.delete.label', default: 'Delete')}
							</g:remoteLink>
							</td>
						</g:if>
					</tr>
				</g:each>
			</tbody>
		</table>