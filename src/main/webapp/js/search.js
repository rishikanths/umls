/**
 * author
 * 
 * @Rishi Saripalle
 * 
 * adjacencyMap - the map that stores the relationships between the search term
 * and other terms. The key is the relationship (e.g., "may_be_treated_by") and
 * the value is an array of terms. For example, Malaria relationships will be
 * formated as {"may_be_treated_by" => ["XXX", "YYY"], "R2"=>[...]}
 * 
 * termSemanticTypes - the map will hold the semantic type of the concept or
 * relation. For example, Malaria is of semantic type Disease_or_Syndrome, etc.
 */
var adjacencyMap = new Map();
var termSemanticTypes = new Map();
var defaultRelation = "";
var searchTerm = "";
var currentRequest = null;
/**
 * Auto complete for term search.
 */
$(document).ajaxSend(function() {
	// $("#tabs").LoadingOverlay("show");
});
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
							var msg = "No matches found for term - <b>"
									+ $('#termSearch').val() + "</b>";
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
						// + "\nData - " + JSON.stringify(data)
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
				getCUI(ui.item.value, searchTerm, true);
				$("#visual").empty();
				return false;
			},

		});

function getCUI(cui, term, displayPathFlag) {
	$
			.ajax({
				url : '/umls/searchwithcui?cui=' + cui,
				success : function(data, status, response) {
					resetGlobalVariable();
					var object = jQuery.parseJSON(data);
					adjacencyMap.clear();
					var radio = $("#relationRadioSelection");
					radio.empty();
					var CUIDiv = $("#CUI_Info");
					CUIDiv.empty();
					CUIDiv.append("<label id='" + term + "'>" + cui
							+ "</label>");
					searchTerm = term;
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
					if (displayPathFlag)
						displayPath(term);
					conceptMap = toD3JFormat(searchTerm, childObject);
					dendogramRadial(M2J(conceptMap));
					dendogramRadialRelation(M2J(toD3JFormatSelectedRelation(
							searchTerm, defaultRelation)));
					checkSelection(defaultRelation, true);
					changeRelationText(searchTerm, defaultRelation);
					$("#hierarchyLayoutSelection").val("radial");
					$("#hierarchyLayoutMenu").css("display", "block");
				},
				error : function(data, status, response) {
					var error = "Response - " + JSON.stringify(response)
					// + "\nData - " + JSON.stringify(data)
					+ "\nStatus - " + JSON.stringify(status);
					displayError(error);
					setFocus();
				}
			})
}

function getSynonyms(term) {
	var cui = $("label#" + term).text();
	var ele = $("#synonyms");
	ele.empty();
	var innerText = "";
	$
			.ajax({
				url : '/umls/searchsynonyms?cui=' + cui,
				success : function(data, status, response) {
					var object = jQuery.parseJSON(data);
					if (object != null) {
						innerText += "<table><thead><tr><th>Name</th><th>Source</th></tr></thead><tbody>";
						for ( var source in object) {
							for ( var i in object[source])
								innerText += "<tr><td>" + object[source][i]
										+ "</td><td>" + source + "</td></tr>";
						}
					}
					innerText += "</tbody></table>";
					ele.append(innerText);
				}
			});
	displaySynonymsDialog();
}

function getDefinitions(term) {
	var cui = $("label#" + term).text();
	if (term == searchTerm)
		cui = $("#selectedConceptCUI").text();
	var ele = $("#definitions");
	ele.empty();
	var innerText = "";
	$
			.ajax({
				url : '/umls/searchdef?cui=' + cui,
				success : function(data, status, response) {
					var object = jQuery.parseJSON(data);
					if (object != null) {
						innerText += "<table class='definitionTable'><tbody>";
						if (object.length != 0) {
							for ( var def in object) {
								if (def % 2 == 0)
									innerText += "<tr class='even'><td style='padding:10px'>"
											+ object[def] + "</td></tr>";
								else
									innerText += "<tr><td style='padding:10px'>"
											+ object[def] + "</td></tr>";
							}
						} else
							innerText += "<tr><td>No definition founds for <b>"
									+ formatName(term) + "</b></td></tr>";
					}
					innerText += "</tbody></table>";
					ele.append(innerText);
				}
			});
	displayDefinitionDialog(formatName(term));
}
