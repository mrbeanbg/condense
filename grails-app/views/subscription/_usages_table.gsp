									<table class="table table-striped table-bordered table-hover">
										<thead>
											<tr>
												<util:remoteSortableColumn update="usagesTable" action="usages" property="startTime" title="${message(code: 'start.date.label', default: 'Start Date')}" defaultOrder="desc" />
												
												<util:remoteSortableColumn update="usagesTable" action="usages" property="endTime" title="${message(code: 'end.date.label', default: 'End Date')}" />
											
												<util:remoteSortableColumn update="usagesTable" action="usages" property="name" title="${message(code: 'name.label', default: 'Name')}" />
											
												<util:remoteSortableColumn update="usagesTable" action="usages" property="quantity" title="${message(code: 'quantity.label', default: 'Quantity')}" />
											
												<util:remoteSortableColumn update="usagesTable" action="usages" property="region" title="${message(code: 'region.label', default: 'Region')}" />
												
											</tr>
										</thead>
										<tbody>
										<g:each in="${usageRecords}" var="u" status="i">
											<tr class="${(i % 2) == 0 ? 'even' : 'odd'}">
											
												<td>
													<g:formatDate date="${u.startTime}" type="date" style="SHORT"/>
												</td>
											
												<td>
													<g:formatDate date="${u.endTime}" type="date" style="SHORT"/>
												</td>
											
												<td>
													${fieldValue(bean: u, field: "name")}
												</td>
											
												<td>
													${u.quantity}
												</td>
												
												<td>
													${fieldValue(bean: u, field: "region")}
												</td>
											</tr>
										</g:each>
										</tbody>
									</table>
									
									<div class="pagination">
										<util:remotePaginate update="usagesTable" action="usages" total="${usageRecordsCount ?: 0}" params="${[id: subscriptionInstance?.id]}"/>
									</div>