/**
 * focusTerm - the term that is currently active in the "Details" dialog box. 
 * It is a combination of source*target - which is a unique.
 */
var focusTerm = "";
function detailedInformation(d, term){
	if(focusTerm!=d.source.name+"*"+d.target.name){
		if(focusTerm.indexOf(term) == -1)
			$( "#information" ).empty();
		$( "#information" ).append("<p><span id='termInfo'>"+d.target.name+"</span> "
				+"<a href='https://en.wikipedia.org/wiki/Is-a' target='_blank'>"
				+"<span id='relationInfo'>is a</span></a> "
				+"<span id='termInfo'>"+d.source.name+"</span>.<br/> or <br/>"
				+"<span id='termInfo'>"+d.source.name+"</span> <span id='relationInfo'> is the parent of </span>" 
				+"<span id='termInfo'>"+d.target.name+"<span></p>");
		
		$( "#information" ).append(getSemanticType(d.source.name,d.target.name));
		focusTerm = d.source.name+"*"+d.target.name;
		displayDialog();
	}
}
function detailedRelationInformation(d, term){
	if(focusTerm!=d.source.name+"*"+d.target.name){
		$( "#information" ).empty();
		adjacencyMap.forEach(function (val, key) {
			if($.inArray(term,val) != -1){
				var rel = key.substr(0,key.indexOf('*'));
				if(rel=="N/A")
					rel = "unknow_relationship"
				$( "#information" ).append("<p><span id='termInfo'>"+d.source.name+"</span> "
						+"<span id='relationInfo'>"+key.substr(0,key.indexOf('*'))+"</span> "
						+"<span id='termInfo'>"+d.target.name+"</span><hr/><br/> "
						+"The relationship <span id='relationInfo'>"+rel
						+" </span> is of type <span id='termInfo'>"+key.substr(key.indexOf('*')+1)
						+".</span> <span id='termInfo'>"+key.substr(key.indexOf('*')+1)
						+"</span> is described as - <span id='relationTypeDesc'>"
						+relationDescription.get(key.substr(key.indexOf('*')+1))
						+"</span>.<br/>Please refer to <a target='_blank'" +
						"href='https://www.nlm.nih.gov/research/umls/knowledge_sources/metathesaurus/release/abbreviations.html'>UMLS Abbreviations</a></p>");
				$( "#information" ).append(getSemanticType(d.source.name,d.target.name));
			}
		});
		focusTerm = d.source.name+"*"+d.target.name;
		displayDialog();
	}
}

function getSemanticType(source,target){
	var typeInfo = "<hr/><p id = 'semanticTypeBg'><span id='termInfo'>" +source
	+"</span><b> is of semantic type </b><span id='termInfo'>"+arr2String(termSemanticTypes.get(source))
	+"</span><br/><span id='termInfo'>"+ target
	+"</span> <b>is of semantic type </b><span id='termInfo'>"
	+arr2String(termSemanticTypes.get(target))+"</span></p><hr/>";
	
	return typeInfo;
}
function displayDialog(){
	$( "#information" ).dialog({
		width:500,
		appendTo: "#hierarchyDiv",
		maxHeight: 400,
		closeOnEscape: true,
		hide:false,
		position:{my:"right center", at:"right center"}
	});
}