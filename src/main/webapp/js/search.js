$("#termSearch").autocomplete({
	minLength:3,
	delay: 200,
	autoFocus:true,
	source: function(req,resp){
		$.ajax({
			url : 'rest/umls/search?term='+$('#termSearch').val(),
			success : function(data, status, response) {
				var object = jQuery.parseJSON(data);
				$("#searchDisplay > tbody").empty();
				var table = $("#searchDisplay > tbody");
				if(object.length!=0){
					for(i=0;i<object.length;i++){
						var dataObj = object[i];
						table.append("<tr id='"+dataObj.cui+"'><td>"+dataObj.name+"</td></tr>");
					}
				}else{
					table.append("<tr><td>No terms starting with = "+$('#termSearch').val()+"</td></tr>")
				}
				$("#searchDisplay tr").click(function(e){
					var cui = this.id;
					getCUI(cui);
					$("#termSearch").val(this.innerText);
				});
			},
			error : function(data, status, response) {
				alert("Error" + "\n Response - " + JSON.stringify(response)
						+ "\n\n Data - " + JSON.stringify(data)
						+ "\n\n Status - " + JSON.stringify(status));
			}
		})
	}
});

function getCUI(cui){
	$.ajax({
		url : 'rest/umls/searchwithcui?cui='+cui,
		success : function(data, status, response) {
			var object = jQuery.parseJSON(data);
			$("#termInformationDisplay > tbody").empty();
			var table = $("#termInformationDisplay > tbody");
			var termMap = new Map();
			var parentNames= [],adjacencyNames = [], childNames= [];
			var childObject;
			if(object!=null){
				for(var prop in object){
					if(prop == 'hierarchy'){
						var parentObject = object[prop];
						var parent = "";
						for(i=0;i<parentObject.length;i++){
							parentNames.push(parentObject[i].name);
							parent +=parentObject[i].name+", "; 
						}
						table.append("<tr'><td>"+prop+"</td><td>"+parent+"</td></tr>");
					}else if(prop == 'children'){
						childObject = object[prop];
						var child = "";
						for(i=0;i<childObject.length;i++){
							childNames.push(childObject[i].name);
							child +=childObject[i].name+", "; 
						}
						table.append("<tr'><td>"+prop+"</td><td>"+child+"</td></tr>");
					}else if(prop == 'semanticTypes'){
						var types = object[prop];
						var type = "";
						for(i=0;i<types.length;i++){
							type +=types[i].name+", "; 
						}
						table.append("<tr'><td>"+prop+"</td><td>"+type+"</td></tr>");
					}
					else if(prop == 'adjacency'){
						var adjacency = object[prop];
						var relation = "";
						for(i=0;i<adjacency.length;i++){
							adjacencyNames.push(adjacency[i].object.name);
							relation +=adjacency[i].predicate.name+"("+adjacency[i].predicate.relationShipType+") "+adjacency[i].object.name+", "; 
						}
						table.append("<tr'><td>"+prop+"</td><td>"+relation+"</td></tr>");
					}else
						table.append("<tr'><td>"+prop+"</td><td>"+object[prop]+"</td></tr>");
				}
			}
			dendogram(toD3jFormat($('#termSearch').val(),childObject));
			//viz($('#termSearch').val(),parentNames,adjacencyNames);
		},
		error : function(data, status, response) {
			alert("Error" + "\n Response - " + JSON.stringify(response)
					+ "\n\n Data - " + JSON.stringify(data)
					+ "\n\n Status - " + JSON.stringify(status));
		}
	})
}
