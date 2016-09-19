<!DOCTYPE html>
<html>
	<head>
		<meta name="layout" content="main">
		<g:set var="entityName" value="${message(code: 'pricingBook.label', default: 'PricingBook')}" />
		<title>Import Pricing Book</title>
	</head>
	<body>
		<div class="container">
			<div class="nav" role="navigation">
				<ul class="nav nav-pills">
					<li class="active"><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>
					<li><g:link class="list" action="index"><g:message code="default.list.label" args="[entityName]" /></g:link></li>
				</ul>
			</div>
			<div id="create-pricingBook" class="content scaffold-create" role="main">
				<h1><g:message code="default.create.label" args="[entityName]" /></h1>
				<g:if test="${flash.message}">
				<div class="alert alert-success" role="status">${flash.message}</div>
				</g:if>
				<g:if test="${flash.error}">
				<div class="alert alert-danger" role="status">${flash.error}</div>
				</g:if>
				<g:hasErrors bean="${pricingBookInstance}">
				<div class="alert alert-danger">
					<ul class="errors" role="alert">
						<g:eachError bean="${pricingBookInstance}" var="error">
						<li <g:if test="${error in org.springframework.validation.FieldError}">data-field-id="${error.field}"</g:if>><g:message error="${error}"/></li>
						</g:eachError>
					</ul>
				</div>
				</g:hasErrors>
				<g:uploadForm  url="[resource:pricingBookInstance, action:'save']" class="form-horizontal" >
					<fieldset class="form">
						<g:render template="form"/>
					</fieldset>
					<fieldset class="buttons">
						<g:submitButton name="create" class="btn btn-primary" value="${message(code: 'default.button.import.label', default: 'Import')}" />
					</fieldset>
				</g:uploadForm>
			</div>
		</div>
	</body>
</html>
