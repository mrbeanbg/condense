		<table class="table table-striped table-bordered table-hover">
			<thead>
				<tr>
					<th><g:message code="subscriptions.id.label" default="subscription Id" /></th>
					<g:if test="${showAction != null && showAction == true}">
					<th><g:message code="default.action.label" default="Action" /></th>
					</g:if>
				</tr>
			</thead>
			<tbody>
				<g:each in="${detailRows?}" var="s">
					<tr>
						<td>${s.subscriptionId}</td>
						<g:if test="${showAction != null && showAction == true}">
							<td>
							<g:remoteLink before="if(!confirm('Are you sure?')) return false" action="ajax_delete_row" params='[subscriptionId: "${s.subscriptionId}"]' update="detailsContent">
								${message(code: 'default.button.delete.label', default: 'Delete')}
							</g:remoteLink>
							</td>
						</g:if>
					</tr>
				</g:each>
			</tbody>
		</table>