// This is a manifest file that'll be compiled into application.js.
//
// Any JavaScript file within this directory can be referenced here using a relative path.
//
// You're free to add application-wide JavaScript to this file, but it's generally better 
// to create separate JavaScript files as needed.
//
//= require jquery
//= require jquery-ui/jquery-ui
//= require jquery.inputmask.bundle
//= require jquery.toaster
//= require bootstrap
//= require bootstrap-dialog/bootstrap-dialog
//= require bootstrap-flash/bootstrap-flash
//= require_self

if (typeof jQuery !== 'undefined') {
	(function($) {
		$(document).ajaxStart(function() {
			$('#overlay').fadeIn();
		}).ajaxStop(function() {
			$('#overlay').fadeOut();
		}).ajaxError(function( event, jqxhr, settings, thrownError ) {
			var response = JSON.parse(jqxhr.responseText);
			$('#overlay').fadeOut();
			if ('error' in response) {
				$.toaster({ priority : 'danger', title : 'Error', message : response.error});
			} else {
				$.toaster({ priority : 'danger', title : 'Error', message : 'Error execuuting the AJAX request'});
			}
		});
	})(jQuery);
}
