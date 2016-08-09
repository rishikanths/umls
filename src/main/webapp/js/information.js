/**
 * focusTerm - the term that is currently active in the "Details" dialog box. 
 * It is a combination of source*target - which is a unique.
 */
var focusTerm = "";
function detailedInformation(d, term){
	if(focusTerm!=d.source.name+"*"+d.target.name){
		if(focusTerm.indexOf(term) == -1)
			$( "#information" ).empty();
		var tcui = $("#"+d.target.name).innerHTML;
		$( "#information" ).append("<p><span id='"+d.target.name+"' class='termInfo'>"+d.target.name
				+"<span id='definition' class='definition' title='Click for definiton'> (<a href=\"javascript:getDefinitions('"+d.target.name+"')\">def</a>) </span></span>"
				+"<a href='https://en.wikipedia.org/wiki/Is-a' target='_blank'>"
				+"<span class='relationInfo'> is a </span></a> "
				+"<span id='"+d.source.name+"' class='termInfo'>"+d.source.name
				+"<span id='definition' class='definition' title='Click for definiton'> (<a href=\"javascript:getDefinitions('"+d.source.name+"')\">def</a>) </span></span>"
				+".<br/> or <br/>"
				+"<span class='termInfo'>"+d.source.name+"</span> <span class='relationInfo'> is the parent of </span>" 
				+"<span class='termInfo'>"+d.target.name+"<span></p>");
		
		$( "#information" ).append(getSemanticType(d.source.name,d.target.name));
		focusTerm = d.source.name+"*"+d.target.name;
		displayDialog();
		//findSynonyms();
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
				$( "#information" ).append("<p><span id='"+d.source.name+"' class='termInfo'>"+d.source.name+"</span> "
						+"<span class='relationInfo'>"+key.substr(0,key.indexOf('*'))+"</span> "
						+"<span id='"+d.target.name+"'class='termInfo'>"+d.target.name+"</span><hr/><br/> "
						+"The relationship <span class='relationInfo'>"+rel
						+" </span> is of type <span class='termInfo'>"+key.substr(key.indexOf('*')+1)
						+".</span> <span class='termInfo'>"+key.substr(key.indexOf('*')+1)
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
	var typeInfo = "<hr/><p id = 'semanticTypeBg'><span class='termInfo'>" +source
	+"</span><b> is of semantic type </b><span class='termInfo'>"+arr2String(termSemanticTypes.get(source))
	+"</span><br/><span class='termInfo'>"+ target
	+"</span> <b>is of semantic type </b><span class='termInfo'>"
	+arr2String(termSemanticTypes.get(target))+"</span></p><hr/>";
	
	return typeInfo;
}
function displayDialog(){
	$( "#information" ).dialog({
		width:500,
		appendTo: "#main",
		maxHeight: 400,
		closeOnEscape: true,
		hide:false,
		position:{my:"right center", at:"right-80 center"}
	});
}

function displaySynonymsDialog(){
	$("#synonyms").dialog({
		width:600,
		appendTo: "#main",
		maxHeight: 200,
		closeOnEscape: true,
		hide:true,
		position:{my:"left center", at:"left-80 center"}
	});
}

function displayDefinitionDialog(){
	$("#definitions").dialog({
		width:800,
		appendTo: "#main",
		maxHeight: 250,
		closeOnEscape: true,
		hide:false,
		position:{my:"left", at:"left+100"}
	});
}


function findSynonyms(){
	$("#information span").hover(function(){
		$("#synonymsText").empty();
		$("#synonymsText").append("<p> Show all the synonyms for the term <b>"
				+"<a href=\"javascript:getSynonyms('"+this.innerHTML+"')\">"+this.innerHTML+"</b></a> in various  terminologies.");
	});
}

