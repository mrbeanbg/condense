									<g:hasErrors bean="${subscriptionInstance}">
									<div class="alert alert-danger">
										<ul class="errors" role="alert">
											<g:eachError bean="${subscriptionInstance}" var="error">
											<li <g:if test="${error in org.springframework.validation.FieldError}">data-field-id="${error.field}"</g:if>><g:message error="${error}"/></li>
											</g:eachError>
										</ul>
									</div>
									</g:hasErrors>
									
									<div class="row"><hr /></div>
									
									<g:each in="${subscriptionTransctions?.billingPeriods}" var="billingPeriod">
										<div class="row alert alert-info">
											<div class="col-md-12">
												<label><g:message code="transactions.calculated.in" default="Transactions calculated in" />: ${subscriptionTransctions.currency}</label>:
											</div>
											<div class="col-md-12">
												<label><g:message code="billing.period" default="Billing Period" /></label>:
												<g:formatDate date="${billingPeriod.fromDate}" type="date" style="LONG"/> - <g:formatDate date="${billingPeriod.toDate}" type="date" style="LONG"/>
											</div>
											<div class="col-md-12">
												<label><g:message code="transactions.subtotal" default="Transactions SUBTOTAL" /></label>:${billingPeriod.billingPeriodSubtotal}
											</div>
											<div class="col-md-12">
												<label><g:message code="transactions.support.charges" default="Transactions Support Charges" /></label>:${billingPeriod.billintPeriodSupportCharges}
											</div>
											<div class="col-md-12">
												<label><g:message code="transactions.total" default="Transactions TOTAL" />:</label>${billingPeriod.billingPeriodTotal}
											</div>
											<div class="col-md-12 tier-definitions">
												<table class="table table-striped table-bordered table-hover">
													<thead>
														<tr>
															<th><g:message code="product.type.info" default="Product Type Info"/></th>
															<th><g:message code="product.usage" default="Usage"/></th>
															<th><g:message code="product.pricing" default="Pricing"/></th>
															<th><g:message code="product.usage" default="Subtotal"/></th>
														</tr>
													</thead>
													<tbody>
													<g:each in="${billingPeriod.products}" var="p" status="i">
														<tr class="${(i % 2) == 0 ? 'even' : 'odd'}">
															<td>
																<g:message code="product.name" default="Name"/>: <strong>${p.name}</strong><br />
																<g:message code="product.resourceid" default="Resource ID"/>: ${p.productGuid}<br />
																<g:message code="product.category" default="Category"/>: ${p.category}<br />
																<g:message code="product.subcategory" default="Subcategory"/>: ${p.subcategory}<br />
																<g:message code="product.region" default="Region"/>: ${p.region}<br />
																<g:message code="product.unit" default="Unit"/>: ${p.unit}<br />
															</td>
															<td>${p.totalUsage}</td>
															<td>
																<table class="table table-striped table-bordered table-hover">
																	<g:each in="${p.usgeAndPricingDetails}" var="u" status="j">
																		<tr class="${(j % 2) == 0 ? 'even' : 'odd'}">
																			<g:message code="from.date" default="From Date"/>: <g:formatDate date="${u.fromDate}" type="date" style="SHORT"/><br />
																			<g:message code="to.date" default="To Date"/>: <g:formatDate date="${u.toDate}" type="date" style="SHORT"/><br />
																			<g:message code="included" default="Included"/>: ${u.included}<br />
																			<g:message code="prorated.included" default="Pro-Rata Included"/>: ${u.includedForPeriod}<br />
																			<g:message code="usage" default="Usage"/>: ${u.usage}<br />
																			<g:message code="price" default="Price"/>: ${u.price}<br />
																			<br />
																		</tr>
																	</g:each>
																</table>
															</td>
															<td>${p.subTotal}</td>
														</tr>
													</g:each>
													</tbody>
												</table>
											</div>
										</div>
										<div class="row"><hr /></div>
										<div class="row">
											
										</div>
									</g:each>