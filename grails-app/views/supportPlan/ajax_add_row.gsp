				<g:hasErrors bean="${recordToValidate}">
				<div class="alert alert-danger">
					<ul class="errors" role="alert">
						<g:eachError bean="${recordToValidate}" var="error">
						<li <g:if test="${error in org.springframework.validation.FieldError}">data-field-id="${error.field}"</g:if>><g:message error="${error}"/></li>
						</g:eachError>
					</ul>
				</div>
				</g:hasErrors>
				<g:render template="detail_table"/>