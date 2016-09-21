<g:each in="${matchingTiers*.product}" var="p">
	<div class="alert alert-info">
		${p.name} - ${p.region}
	</div>
</g:each>
