// index.js

$(document).ready(
		function() {
			alert("Loaded");
			$.ajax({
				url : 'rest/umls',
				success : function(data, status, response) {
					alert("Success" + "\n Response - "
							+ JSON.stringify(response) + "\n\n Data - "
							+ JSON.stringify(data) + "\n\n Status - "
							+ JSON.stringify(status));
				},
				error : function(data, status, response) {
					alert("Error" + "\n Response - " + JSON.stringify(response)
							+ "\n\n Data - " + JSON.stringify(data)
							+ "\n\n Status - " + JSON.stringify(status));
				}
			})
		});