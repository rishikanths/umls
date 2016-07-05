/**
 * 
 */
var focusTerm = "";
function detailedInformation(d, term){
	if(focusTerm!=d.source.name && focusTerm!=d.target.name )
		$( "#information" ).empty();
	$( "#information" ).append("<p><span id='termInfo'>"+d.target.name+"</span> "
			+"<a href='https://en.wikipedia.org/wiki/Is-a' target='_blank'>"
			+"<span id='relationInfo'>is a</span></a> "
			+"<span id='termInfo'>"+d.source.name+"</span>.<br/> or <br/>"
			+"<span id='termInfo'>"+d.source.name+"</span> <span id='relationInfo'> is the parent of </span>" 
			+"<span id='termInfo'>"+d.target.name+"<span></p>")
	focusTerm = term;
	$( "#information" ).dialog({
		width:500,
		maxHeight: 400,
		closeOnEscape: true,
		hide:false,
		position:{my:"right center", at:"right center"}
	});
}
function detailedRelationInformation(d, term){
	if(focusTerm!=d.source.name && focusTerm!=d.target.name )
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
					+"</span>.<span id='termInfo'>"+key.substr(key.indexOf('*')+1)+" </span> is described as - '"
					+relationDescription.get(key.substr(key.indexOf('*')+1))
					+"'.<br/>Please refer to <a target='_blank'" +
					"href='https://www.nlm.nih.gov/research/umls/knowledge_sources/metathesaurus/release/abbreviations.html'>UMLS Abbreviations</a></p>");
		}
	});
	
	focusTerm = term;
	$( "#information" ).dialog({
		width:500,
		maxHeight: 400,
		closeOnEscape: true,
		hide:false,
		position:{my:"right center", at:"right center"}
	});
}

function removeInformation(){
}