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



var search = (function(){

	var adjacencyMap = new Map();
	var termSemanticTypes = new Map();
	var searchTerm = "";

	/**
	 * Ajax call to get information about the selected CUI.
	 * 
	 *  @param cui The concept CUI - unique for every concept
	 *  @param term
	 *  @param displayPathFlag
	 */
	var getCUI = function(cui, term, displayPathFlag) {
		$.ajax({
				url : '/umls/searchwithcui?cui=' + cui,
				success : function(data, status, response) {
					_resetVariables();
					var object = jQuery.parseJSON(data);
					adjacencyMap.clear();
					//var radio = $("#relationRadioSelection");
					//radio.empty();
					var radioTable = "<table id='radioTable'>";
					//radio.empty().append("<table id='radioTable'>");
					var CUIDiv = $("#CUI_Info");
					CUIDiv.empty().append("<label id='" + term + "'>" + cui+ "</label>");
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
								CUIDiv.append("<label id='"+ childObject[i].name + "'>"+ childObject[i].cui + "</label>");
							}
						} else if (prop == 'semanticTypes') {
							add2Map(termSemanticTypes, object.name,object[prop]);
						} else if (prop == 'adjacency') {
							var adjacency = object[prop];
							for (i = 0; i < adjacency.length; i++) {
								add2Map(termSemanticTypes,adjacency[i].object.name,adjacency[i].object.semanticTypes)
								adjacencyNames.push(adjacency[i].object.name);
								CUIDiv.append("<label id='"+ adjacency[i].object.name + "'>"
										+ adjacency[i].object.cui+ "</label>")
								var names = [];
								var temp = adjacency[i].predicate.relationName+ "*"+ adjacency[i].predicate.relationType
								if (adjacencyMap.get(temp) != null)
									names = adjacencyMap.get(temp)
								else
									radioTable+='<tr><td><input type="radio" '+ 'name="relations" '
										+ 'value="'+ adjacency[i].predicate.relationName
										+ '">'+ formatName(adjacency[i].predicate.relationName)
										+ "</input></td></tr>";
									/*$('<tr><td><input type="radio" '+ 'name="relations" '
													+ 'value="'+ adjacency[i].predicate.relationName
													+ '">'+ adjacency[i].predicate.relationName
													+ "</input></td></tr>").appendTo("#radioTable");*/
								names.push(adjacency[i].object.name);
								adjacencyMap.set(temp, names);
							}
							radioTable+="</table>";
							$("#relationRadioSelection").empty().append(radioTable);
							$('#relationRadioSelection input[type=radio]').each(function(r) {$(this).on("click",selectedRelation);});
							}
						}
					}
					if (displayPathFlag)
						displayPath(term);
					conceptMap = toD3JFormat(searchTerm, childObject);
					hierarchy.drawChart(M2J(conceptMap)); 
					$("#hierarchyLayoutSelection").val("radial");
					$("#hierarchyLayoutMenu").css("display", "block");
				},
				error : function(data, status, response) {
					var error = "Response - " + JSON.stringify(response)+ "\nStatus - " + JSON.stringify(status);
					displayError(error);
					setFocus();
				}
			})
		};
	/*
	 * Currently not used. 
	 */
	var getSynonyms = function(term) {
		var cui = $("label#" + term).text();
		var ele = $("#synonyms");
		ele.empty();
		var innerText = "";
		$.ajax({
			url : '/umls/searchsynonyms?cui=' + cui,
			success : function(data, status, response) {
				var object = jQuery.parseJSON(data);
				if (object != null) {
					innerText += "<table><thead><tr><th>Name</th><th>Source</th></tr></thead><tbody>";
					for ( var source in object) {
						for ( var i in object[source])
							innerText += "<tr><td>" + object[source][i]+ "</td><td>" + source + "</td></tr>";
					}
				}
				innerText += "</tbody></table>";
				ele.append(innerText);
			}
		});
	};

	var getDefinitions = function(term) {
		var cui = $("label#" + term).text();
		if (term == searchTerm)
			cui = $("#selectedConceptCUI").text();
		var ele = $("#definitions");
		ele.empty();
		var innerText = "";
		$.ajax({
			url : '/umls/searchdef?cui=' + cui,
			success : function(data, status, response) {
				var object = jQuery.parseJSON(data);
				if (object != null) {
					innerText += "<table class='definitionTable'><tbody>";
					if (object.length != 0) {
						for ( var def in object) {
							if (def % 2 == 0)
								innerText += "<tr class='even'><td style='padding:10px'>"+ object[def] + "</td></tr>";
							else
								innerText += "<tr><td style='padding:10px'>"+ object[def] + "</td></tr>";
						}
					} else
						innerText += "<tr><td>No definition founds for <b>"+ formatName(term) + "</b></td></tr>";
				}
				innerText += "</tbody></table>";
				ele.append(innerText);
			}
		});
		dialogs.showDeifinitionDialog(formatName(term));
	};
	
	var _resetVariables = function(){
		resetGlobalVariable();
		adjacencyMap = new Map();
		termSemanticTypes = new Map();
		defaultRelation = "";
		searchTerm = "";
		conceptMap = new Map();
	}
	
	return {
		getAdjacencyMap : function(){return adjacencyMap;},
		getConceptSemanticTypes: function(){return termSemanticTypes;},
		getSearchTerm: function(){return searchTerm},
		searchByCUI: getCUI,
		searchDefinition: getDefinitions
	}
	
})();
