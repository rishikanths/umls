/*
 * adjacencyMap - the map that stores the relationships between the search term and other terms. 
 * The key is the relationship (e.g., "may_be_treated_by") and the value is an array of terms. 
 * For example, Malaria relationships will be formated as {"may_be_treated_by" => ["XXX", "YYY"], "R2"=>[...]}
 */
var adjacencyMap = new Map();
/*
 * Auto complete for term search. 
 */
$("#termSearch").autocomplete({
	minLength:4,
	delay: 200,
	source: function(req,resp){
		$.ajax({
			url : 'rest/umls/search?term='+$('#termSearch').val(),
			success : function(data, status, response) {
				var object = jQuery.parseJSON(data);
				resp($.map(object, function (item) {
            		return {
                		label: item.name,
                		value: item.cui
            		};
        		}));
			}, 
			error : function(data, status, response) {
				alert("Error" + "\n Response - " + JSON.stringify(response)
						+ "\n\n Data - " + JSON.stringify(data)
						+ "\n\n Status - " + JSON.stringify(status));
			}
		});
	},
	focus: function() {
		return false;
    },
	select: function( event, ui ) {
		$("#selectedConceptCUI").text(ui.item.value);
		$("#termSearch").val(ui.item.label);
		getCUI(ui.item.value);
		$("#visual").empty();
    	return false;
    },
});

function getCUI(cui){
	$.ajax({
		url : 'rest/umls/searchwithcui?cui='+cui,
		success : function(data, status, response) {
			var object = jQuery.parseJSON(data);
			adjacencyMap.clear();
			var radio = $("#relationRadioSelection");
			radio.empty();
			var parentNames= [],adjacencyNames = [], childNames= [];
			var childObject,
				parentObject;
			if(object!=null){
				for(var prop in object){
					if(prop == 'hierarchy'){
						parentObject = object[prop];
						var parent = "";
						for(i=0;i<parentObject.length;i++){
							parentNames.push(parentObject[i].name);
							parent +=parentObject[i].name+", "; 
						}
					}else if(prop == 'children'){
						childObject = object[prop];
						var child = "";
						for(i=0;i<childObject.length;i++){
							childNames.push(childObject[i].name);
							child +=childObject[i].name+", "; 
						}
					}else if(prop == 'semanticTypes'){
						var types = object[prop];
						var type = "";
						for(i=0;i<types.length;i++){
							type +=types[i].name+", "; 
						}
					}
					else if(prop == 'adjacency'){
						var adjacency = object[prop];
						var relation = "";
						for(i=0;i<adjacency.length;i++){
							adjacencyNames.push(adjacency[i].object.name);
							var names = [];
							var temp = adjacency[i].predicate.relationName+"*"+adjacency[i].predicate.relationType
							if(adjacencyMap.get(temp)!=null)
								names = adjacencyMap.get(temp)
							else
								radio.append($('<input type="radio" ' +
										'name="relations" ' +
										'value="' +adjacency[i].predicate.relationName+'">' +
										adjacency[i].predicate.relationName+"</input>"));
							names.push(adjacency[i].object.name);
							adjacencyMap.set(temp,names);
							relation +=adjacency[i].predicate.relationName+"("+adjacency[i].predicate.relationType+") "+adjacency[i].object.name+", "; 
						}
						$('#relationRadioSelection input[type=radio]').each(function(r){
							 $(this).on("click",relationSelected);
						});
					}
				}
			}
			dendogramRadial(M2J(toD3jRadialFormat($('#termSearch').val(),childObject)));
			dendogramRadialRelation(M2J(toD3jRadialFormatRelation($('#termSearch').val(),adjacencyMap)));
		},
		error : function(data, status, response) {
			alert("Error" + "\n Response - " + JSON.stringify(response)
					+ "\n\n Data - " + JSON.stringify(data)
					+ "\n\n Status - " + JSON.stringify(status));
		}
	})
}
