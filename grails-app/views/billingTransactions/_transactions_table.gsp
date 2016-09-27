									<g:hasErrors bean="${subscriptionInstance}">
									<div class="alert alert-danger">
										<ul class="errors" role="alert">
											<g:eachError bean="${subscriptionInstance}" var="error">
											<li <g:if test="${error in org.springframework.validation.FieldError}">data-field-id="${error.field}"</g:if>><g:message error="${error}"/></li>
											</g:eachError>
										</ul>
									</div>
									</g:hasErrors>
									
									${subscriptionTransctions}
									
<%--									<table class="table table-striped table-bordered table-hover">--%>
<%--										<thead>--%>
<%--											<tr>--%>
<%--												<util:remoteSortableColumn update="usagesTable" action="usages" property="startTime" title="${message(code: 'start.date.label', default: 'Start Date')}" params="${[filterFromDate: params.filterFromDate, filterToDate: params.filterToDate].findAll {it.value} }" />--%>
<%--												--%>
<%--												<util:remoteSortableColumn update="usagesTable" action="usages" property="endTime" title="${message(code: 'end.date.label', default: 'End Date')}" params="${[filterFromDate: params.filterFromDate, filterToDate: params.filterToDate].findAll {it.value} }" />--%>
<%--											--%>
<%--												<util:remoteSortableColumn update="usagesTable" action="usages" property="name" title="${message(code: 'name.label', default: 'Name')}" params="${[filterFromDate: params.filterFromDate, filterToDate: params.filterToDate].findAll {it.value} }" />--%>
<%--											--%>
<%--												<util:remoteSortableColumn update="usagesTable" action="usages" property="quantity" title="${message(code: 'quantity.label', default: 'Quantity')}" params="${[filterFromDate: params.filterFromDate, filterToDate: params.filterToDate].findAll {it.value} }" />--%>
<%--											--%>
<%--												<util:remoteSortableColumn update="usagesTable" action="usages" property="region" title="${message(code: 'region.label', default: 'Region')}" params="${[filterFromDate: params.filterFromDate, filterToDate: params.filterToDate].findAll {it.value} }" />--%>
<%--												--%>
<%--											</tr>--%>
<%--										</thead>--%>
<%--										<tbody>--%>
<%--										<g:each in="${usageRecords}" var="u" status="i">--%>
<%--											<tr class="${(i % 2) == 0 ? 'even' : 'odd'}">--%>
<%--											--%>
<%--												<td>--%>
<%--													<g:formatDate date="${u.startTime}" type="date" style="SHORT"/>--%>
<%--												</td>--%>
<%--											--%>
<%--												<td>--%>
<%--													<g:formatDate date="${u.endTime}" type="date" style="SHORT"/>--%>
<%--												</td>--%>
<%--											--%>
<%--												<td>--%>
<%--													${fieldValue(bean: u, field: "name")}--%>
<%--												</td>--%>
<%--											--%>
<%--												<td>--%>
<%--													${u.quantity}--%>
<%--												</td>--%>
<%--												--%>
<%--												<td>--%>
<%--													${fieldValue(bean: u, field: "region")}--%>
<%--												</td>--%>
<%--											</tr>--%>
<%--										</g:each>--%>
<%--										</tbody>--%>
<%--									</table>--%>
<%--									--%>
<%--									<div class="pagination">--%>
<%--										<util:remotePaginate update="usagesTable" action="usages" total="${usageRecordsCount ?: 0}" params="${[id: subscriptionInstance?.id, filterFromDate: params.filterFromDate, filterToDate: params.filterToDate].findAll {it.value} }" />--%>
<%--									</div>--%>