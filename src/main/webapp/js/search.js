/*
 * adjacencyMap - the map that stores the relationships between the search term and other terms. 
 * The key is the relationship (e.g., "may_be_treated_by") and the value is an array of terms. 
 * For example, Malaria relationships will be formated as {"may_be_treated_by" => ["XXX", "YYY"], "R2"=>[...]}
 * 
 * termSemanticTypes - the map will hold the semantic type of the concept or relation. For example, Malaria is
 * of semantic type Disease_or_Syndrome, etc. 
 */
var adjacencyMap = new Map();
var termSemanticTypes = new Map();
var defaultRelation = "";
/*
 * Auto complete for term search.
 */
$(document).ajaxSend(function() {
	// $("#tabs").LoadingOverlay("show");
});
$(document).ajaxStart(function() {
	$("#message").css('display', 'none');
	$("#error").css('display', 'none');
	$("#hierarchy").empty();
	$("#hierarchyMessage").empty();
	$("#relation").empty();
	$("#relationRadioSelection").empty();
	$("#relationRadioSelectionDesc").empty();
});
$(document).ajaxStop(function() {
	$("#tabs").LoadingOverlay("hide");
});
$("#termSearch").autocomplete(
		{
			minLength : 4,
			delay : 200,
			source : function(req, resp) {
				$.ajax({
					url : 'rest/umls/search?term=' + $('#termSearch').val(),
					success : function(data, status, response) {
						resetGlobalVariable();
						var object = jQuery.parseJSON(data);
						if (object.length == 0) {
							var msg = "No matches found for term - <b>"
									+ $('#termSearch').val() + "</b>";
							displayMessage(msg);
						} else {
							resp($.map(object, function(item) {
								return {
									label : item.name,
									value : item.cui
								};
							}));
						}
					},
					error : function(data, status, response) {
						var error = "Response - " + JSON.stringify(response)
								+ "\nData - " + JSON.stringify(data)
								+ "\nStatus - " + JSON.stringify(status);
						displayError(error);
						$("#loading").css('display', 'none');

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
				getCUI(ui.item.value);
				$("#visual").empty();
				return false;
			},
		});

function getCUI(cui) {
	$
			.ajax({
				url : 'rest/umls/searchwithcui?cui=' + cui,
				success : function(data, status, response) {
					var object = jQuery.parseJSON(data);
					adjacencyMap.clear();
					var radio = $("#relationRadioSelection");
					radio.empty();
					var CUIDiv = $("#CUI_Info");
					CUIDiv.empty();
					var parentNames = [], adjacencyNames = [], childNames = [];
					var childObject, parentObject;
					if (object != null) {
						for ( var prop in object) {
							if (prop == 'hierarchy') {
								parentObject = object[prop];
								for (i = 0; i < parentObject.length; i++)
									parentNames.push(parentObject[i].name);
							} else if (prop == 'children') {
								childObject = object[prop];
								for (i = 0; i < childObject.length; i++) {
									childNames.push(childObject[i].name);
									CUIDiv.append("<label id='"
											+ childObject[i].name + "'>"
											+ childObject[i].cui + "</label>");
								}
							} else if (prop == 'semanticTypes') {
								add2Map(termSemanticTypes, object.name,
										object[prop]);
							} else if (prop == 'adjacency') {
								var adjacency = object[prop];
								for (i = 0; i < adjacency.length; i++) {
									add2Map(termSemanticTypes,
											adjacency[i].object.name,
											adjacency[i].object.semanticTypes)
									adjacencyNames
											.push(adjacency[i].object.name);
									CUIDiv.append("<label id='"
											+ adjacency[i].object.name + "'>"
											+ adjacency[i].object.cui
											+ "</label>")
									var names = [];
									var temp = adjacency[i].predicate.relationName
											+ "*"
											+ adjacency[i].predicate.relationType
									defaultRelation = adjacency[i].predicate.relationName;
									if (adjacencyMap.get(temp) != null)
										names = adjacencyMap.get(temp)
									else
										radio
												.append($('<input type="radio" '
														+ 'name="relations" '
														+ 'value="'
														+ adjacency[i].predicate.relationName
														+ '">'
														+ adjacency[i].predicate.relationName
														+ "</input>"));
									names.push(adjacency[i].object.name);
									adjacencyMap.set(temp, names);
								}
								$('#relationRadioSelection input[type=radio]')
										.each(
												function(r) {
													$(this).on("click",
															relationSelected);
												});
							}
						}
					}
					dendogramRadial(M2J(toD3JFormat($('#termSearch').val(),
							childObject)));
					dendogramRadialRelation(M2J(toD3JFormatSelectedRelation($(
							'#termSearch').val(), defaultRelation)));
					checkSelection(defaultRelation, true);
					changeRelationText($('#termSearch').val(), defaultRelation);
				},
				error : function(data, status, response) {
					var error = "Response - " + JSON.stringify(response)
							+ "\nData - " + JSON.stringify(data)
							+ "\nStatus - " + JSON.stringify(status);
					displayError(error);
				}
			})
}

function getSynonyms(term){
	var cui = $("label#"+term).text();
	var ele = $("#synonyms");
	ele.empty();
	var innerText = "";
	$.ajax({
		url : 'rest/umls/searchsynonyms?cui=' + cui,
		success : function(data, status, response) {
			var object = jQuery.parseJSON(data);
			if(object!=null){
				innerText+="<table><thead><tr><th>Name</th><th>Source</th></tr></thead><tbody>";
				for(var source in object){
					for(var i in object[source])
						innerText+="<tr><td>"+object[source][i]+"</td><td>"+source+"</td></tr>";
				}
			}
			innerText+="</tbody></table>";
			ele.append(innerText);
		}
	});
	displaySynonymsDialog();
}
