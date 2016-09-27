<!DOCTYPE html>
<!--[if lt IE 7 ]> <html lang="en" class="no-js ie6"> <![endif]-->
<!--[if IE 7 ]>    <html lang="en" class="no-js ie7"> <![endif]-->
<!--[if IE 8 ]>    <html lang="en" class="no-js ie8"> <![endif]-->
<!--[if IE 9 ]>    <html lang="en" class="no-js ie9"> <![endif]-->
<!--[if (gt IE 9)|!(IE)]><!--> <html lang="en" class="no-js"><!--<![endif]-->
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
		<meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">
		<title><g:layoutTitle default="Grails"/></title>
		<meta name="viewport" content="width=device-width, initial-scale=1.0">
		<link rel="shortcut icon" href="${assetPath(src: 'favicon.ico')}" type="image/x-icon">
		<link rel="apple-touch-icon" href="${assetPath(src: 'apple-touch-icon.png')}">
		<link rel="apple-touch-icon" sizes="114x114" href="${assetPath(src: 'apple-touch-icon-retina.png')}">
  		<asset:stylesheet src="application.css"/>
		<asset:javascript src="application.js"/>
		<g:layoutHead/>
	</head>
	<body>
		<div id="overlay">
	        <asset:image src="ajax-loader.gif" alt="Loading" id="loading-indicator"/>
	    </div>
		<div class="navbar navbar-default navbar-static-top">
			<div class="navbar-inner">
				<div class="container">
					<div>
						<h1><g:message code="title.condense.app" default="Condense"/></h1></div>

					<div class="collapse navbar-collapse" id="bs-example-navbar-collapse-1">
						<ul class="nav navbar-nav">
							<li class="dropdown">
								<a href="#" class="dropdown-toggle" data-toggle="dropdown" role="button" aria-expanded="false">
									<g:message code="topnav.customer.management" default="Customer Management"/><span class="caret"></span></a>
								<ul class="dropdown-menu" role="menu">
									<li><g:link controller="customer" action="index"><g:message code="topnav.Customers" default="Customers"/></g:link></li>
								</ul>
							</li>
							<li class="dropdown">
								<a href="#" class="dropdown-toggle" data-toggle="dropdown" role="button" aria-expanded="false">
									<g:message code="topnav.customer.management" default="Pricing Management"/><span class="caret"></span></a>
								<ul class="dropdown-menu" role="menu">
									<li><g:link controller="pricingBook" action="index"><g:message code="topnav.pricing.Books" default="Pricing Books"/></g:link></li>
									<li><g:link controller="pricingSet" action="index"><g:message code="topnav.pricing.Set" default="Pricing Sets"/></g:link></li>
									<li><g:link controller="supportPlan" action="index"><g:message code="topnav.support.Plans" default="Support Plans"/></g:link></li>
									<li><g:link controller="currencyRate" action="index"><g:message code="topnav.currency.Rates" default="Currency Rates"/></g:link></li>
								</ul>
							</li>
							<li class="dropdown">
								<a href="#" class="dropdown-toggle" data-toggle="dropdown" role="button" aria-expanded="false">
									<g:message code="topnav.billing.usages.and.transactions" default="Usages and Billing Transacions"/><span class="caret"></span></a>
								<ul class="dropdown-menu" role="menu">
									<li><g:link controller="subscription" action="index"><g:message code="topnav.Usages" default="Subscriptions and Usages"/></g:link></li>
									<li><g:link controller="billingTransactions" action="index"><g:message code="topnav.billing.Transactions" default="Billing Transactions"/></g:link></li>
								</ul>
							</li>
						</ul>
					</div>
				</div>
			</div>
		</div>
		<g:layoutBody/>
		<div class="footer" role="contentinfo"></div>
		<div id="spinner" class="spinner" style="display:none;"><g:message code="spinner.alt" default="Loading&hellip;"/></div>
		<asset:deferredScripts/>
	</body>
</html>
