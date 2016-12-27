<%@ page import="condense.User" %>

<div class="form-group fieldcontain ${hasErrors(bean: userInstance, field: 'username', 'error')} required">
	<label class="control-label col-md-2" for="username">
		<g:message code="user.username.label" default="Username" />
		<span class="required-indicator">*</span>
	</label>
	<span class="controls col-md-10"><g:textField name="username" required="" value="${userInstance?.username}"/>
</span>
</div>

<g:if test="${userInstance.id == null}">
<div class="form-group fieldcontain ${hasErrors(bean: userInstance, field: 'password', 'error')} required">
	<label class="control-label col-md-2" for="password">
		<g:message code="user.password.label" default="Password" />
		<span class="required-indicator">*</span>
	</label>
	<span class="controls col-md-10"><g:textField name="password" required="" value="${userInstance?.password}"/>
</span>
</div>
</g:if>

<div class="form-group fieldcontain ${hasErrors(bean: userInstance, field: 'role', 'error')} required">
	<label class="control-label col-md-2" for="role">
		<g:message code="user.role.label" default="Role" />
		<span class="required-indicator">*</span>
	</label>
	<g:if test="${userInstance.id != null && userInstance.authorities}">
		<g:set var="theRole" value="${userInstance.authorities[0]}" scope="request"/>
	</g:if>
	<g:else>
		<g:set var="theRole" value="" scope="request"/>
	</g:else>
	<%
		def roleKeys = []
		def authorities = condense.Role.list()*.authority
		roleKeys = authorities.collect {message(code: it)}
	%>
	<span class="controls col-md-10">
		<g:select name="role" id="roleSelect" from="${roleKeys}" keys="${condense.Role.list()*.authority}" required="" value="${theRole}" />
    </span>
</div>
