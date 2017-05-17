/**
 * focusTerm - the term that is currently active in the "Details" dialog box. It
 * is a combination of source*target - which is a unique.
 */
var focusTerm = "";
function detailedInformation(d, term){
	if(focusTerm!=term){
		$( "#information" ).empty();			
		focusTerm = term;
	}
	{
		$( "#information" ).append("<p><span id='"+d.target.key+"' class='termInfo'>"+d.target.name
				+"<span id='definition' class='definition' title='Click for definiton'> (<a href=\"javascript:getDefinitions('"+d.target.key+"')\">def</a>) </span></span>"
				+"<a href='https://en.wikipedia.org/wiki/Is-a' target='_blank'> "
				+"<span class='relationInfo'> is a </span></a> "
				+"  <span id='"+d.source.key+"' class='termInfo'>"+d.source.name
				+"<span id='definition' class='definition' title='Click for definiton'> (<a href=\"javascript:getDefinitions('"+d.source.key+"')\">def</a>) </span></span>"
				+" or "
				+"<span class='termInfo'>"+(d.source.name)+"</span> <span class='relationInfo'> is the parent of </span>" 
				+"<span class='termInfo'>"+(d.target.name)+"<span></p>");
			  
		/*
		 * $( "#overlay-details" ).append("<p><span id='"+d.target.name+"'
		 * class='termInfo'>"+d.target.name +"<span id='definition'
		 * class='definition' title='Click for definiton'> (<a
		 * href=\"javascript:getDefinitions('"+d.target.name+"')\">def</a>)
		 * </span></span>" +"<a href='https://en.wikipedia.org/wiki/Is-a'
		 * target='_blank'> " +"<span class='relationInfo'> is a </span></a> " +"<span
		 * id='"+d.source.name+"' class='termInfo'>"+d.source.name +"<span
		 * id='definition' class='definition' title='Click for definiton'> (<a
		 * href=\"javascript:getDefinitions('"+d.source.name+"')\">def</a>)
		 * </span></span>" +" or " +"<span class='termInfo'>"+d.source.name+"</span>
		 * <span class='relationInfo'> is the parent of </span>" +"<span
		 * class='termInfo'>"+d.target.name+"<span></p>"); $(
		 * "#overlay-details"
		 * ).append(getSemanticType(d.source.name,d.target.name));
		 * $('#overlay-details').popup({ backgroundactive:true,
		 * horizontal:'center', vertical:'center', escape:true, outline:true });
		 * $('#overlay-details').popup('show');
		 */		
		$( "#information" ).append(getSemanticType(d.source,d.target));
		displayDialog(focusTerm);
		// findSynonyms();
	}
}
function detailedRelationInformation(d, term){
	if(focusTerm!=term){
		$( "#information" ).empty();			
		focusTerm = term;
	}
	{
		adjacencyMap.forEach(function (val, key) {
			if($.inArray(term,val) != -1 && searchTerm != term){
				var rel = key.substr(0,key.indexOf('*'));
				if(rel=="N/A")
					rel = "unknow_relationship"
				$( "#information" ).append("<p><span id='"+d.source.key+"' class='termInfo'>"+d.source.name+"</span> "
						+"<span id='definition' class='definition' title='Click for definiton'> (<a href=\"javascript:getDefinitions('"+d.source.key+"')\">def</a>) </span></span>"
						+"<span class='relationInfo'>"+key.substr(0,key.indexOf('*'))+"</span> "
						+"<span id='"+d.target.name+"'class='termInfo'>"+d.target.name
						+"<span id='definition' class='definition' title='Click for definiton'> (<a href=\"javascript:getDefinitions('"+d.target.key+"')\">def</a>) </span></span>"
						+"</span>"
						+"</p>");
						/*
						 * +"The relationship <span class='relationInfo'>"+rel +"
						 * </span> is of type <span
						 * class='termInfo'>"+key.substr(key.indexOf('*')+1) +".</span>
						 * <span
						 * class='termInfo'>"+key.substr(key.indexOf('*')+1) +"</span>
						 * is described as - <span id='relationTypeDesc'>"
						 * +relationDescription.get(key.substr(key.indexOf('*')+1)) +"</span>.<br/>Please
						 * refer to <a target='_blank'" +
						 * "href='https://www.nlm.nih.gov/research/umls/knowledge_sources/metathesaurus/release/abbreviations.html'>UMLS
						 * Abbreviations</a></p>");
						 */
				$( "#information" ).append(getSemanticType(d.source,d.target));
				displayDialog(d.target.name);
			}
			if(searchTerm == term){
				if ($("#information").dialog("instance") != null)
					$("#information").dialog("close");
			}
		});
		
	}
}

function getSemanticType(source,target){
	var typeInfo= "";
	if(arr2String(termSemanticTypes.get(target.key)) == arr2String(termSemanticTypes.get(source.key)))
		typeInfo = "<hr/><p id = 'semanticTypeBg'><span class='termInfo'>" +source.name
	+"</span><b> and <span class='termInfo'>"+ target.name +" </span> are of type </b><span class='termInfo'>"+arr2String(termSemanticTypes.get(source.key));	
	else
		typeInfo = "<hr/><p id = 'semanticTypeBg'><span class='termInfo'>" +source.name
		+"</span><b> is of type </b><span class='termInfo'>"+arr2String(termSemanticTypes.get(source.key))
		+"</span> & <span class='termInfo'>"+ target.name
		+"</span> <b>is of type </b><span class='termInfo'>"
		+arr2String(termSemanticTypes.get(target.key))+"</span></p><hr/>";
	
	return typeInfo;
}
function displayDialog(titleName){
	if ($("#definitions").dialog("instance") != null &&
			$("#definitions").dialog("isOpen"))
		$("#definitions").dialog("close");

	$( "#information" ).dialog({
		width:700,
		title:'Details on '+ titleName,
		appendTo: "#main",
		maxHeight: 300,
		closeOnEscape: true,
		hide:true,
		position:{my:"center top+10", at:"center top"}
	});
}

function displaySynonymsDialog(){
	$("#synonyms").dialog({
		width:600,
		appendTo: "#main",
		maxHeight: 200,
		closeOnEscape: false,
		hide:true,
		position:{my:"left center", at:"left-80 center"}
	});
}

function displayDefinitionDialog(titleName){
	$("#definitions").dialog({
		width:800,
		title:'Definitions of '+ titleName,
		appendTo: "#main",
		maxHeight: 250,
		closeOnEscape: false,
		hide:true,
		position:{my:"right top", at:"right-100 top+100"}
	});
}

function findSynonyms(){
	$("#information span").hover(function(){
		$("#synonymsText").empty();
		$("#synonymsText").append("<p> Show all the synonyms for the term <b>"
				+"<a href=\"javascript:getSynonyms('"+this.innerHTML+"')\">"+this.innerHTML+"</b></a> in various  terminologies.");
	});
}

