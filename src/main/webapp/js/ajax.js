
var currentRequest = null;

$(document).ajaxStop(function() {
	$("#tabs").LoadingOverlay("hide");
});
$(document).ajaxComplete(function() {
	$("#tabs").LoadingOverlay("hide");
});
$("#termSearch").autocomplete(
{
	minLength : 4,
	delay : 200,
	source : function(req, resp) {
		currentRequest = $.ajax({
			url : '/umls/search?term=' + $('#termSearch').val(),
			success : function(data, status, response) {
				resetGlobalVariable();
				conceptPath = [];
				$("#conceptPath").empty();
				var object = jQuery.parseJSON(data);
				if (object.length == 0) {
					var msg = "No matches found for term - <b>"+ $('#termSearch').val() + "</b>";
					displayMessage(msg);
					setFocus();
				} else {
					resp($.map(object, function(item) {
						return {
							label : formatName(item.name),
							value : item.cui
						};
					}));
				}
			},
			error : function(data, status, response) {
				var error = "Response - " + JSON.stringify(response)
					+ "\nStatus - " + JSON.stringify(status);
				displayError(error);
				$("#loading").css('display', 'none');
				setFocus();
			},
			beforeSend : function() {
				if (currentRequest != null) {
					currentRequest.abort();
					$("#tabs").LoadingOverlay("hide");
				}
				$("#message").css('display', 'none');
				$("#error").css('display', 'none');
				$("#hierarchy").empty();
				$("#hierarchyMessage").empty();
				$("#relation").empty();
				$("#relationRadioSelection").empty();
				$("#relationRadioSelectionDesc").empty();
				$("#hierarchyLayoutMenu").css("display", "none");
			}
		});
	},
	focus : function() {
		return false;
	},
	select : function(event, ui) {
		$("#tabs").LoadingOverlay("show");
		$("#selectedConceptCUI").text(ui.item.value);
		$("#termSearch").val(ui.item.label);
		searchTerm = reFormatName(ui.item.label);
		search.searchByCUI(ui.item.value, searchTerm, true);
		$("#visual").empty();
		return false;
	},

});
