/**
 * focusTerm - the term that is currently active in the "Details" dialog box. 
 */


var information = (function(){

	var focusTerm = "";
	var resetFocusTerm = function(){
		focusTerm = "";
	}
	var detailedInformation = function(d, term){
		if(focusTerm!=term){
			$( "#information" ).empty();			
			focusTerm = term;

			$( "#information" ).append("<span id='"+d.target.key+"' class='termInfo'>"+d.target.name
				+"<span id='definition' class='definition' title='Click for definiton'> (<a href=\"javascript:search.searchDefinition('"+d.target.key+"')\">def</a>) </span></span>"
				+"<a href='https://en.wikipedia.org/wiki/Is-a' target='_blank'> "
				+"<span class='relationInfo'> is a </span></a> "
				+"&nbsp;<span id='"+d.source.key+"' class='termInfo'>"+d.source.name
				+"<span id='definition' class='definition' title='Click for definiton'> (<a href=\"javascript:search.searchDefinition('"+d.source.key+"')\">def</a>) </span></span>"
				+" or "
				+"<span class='termInfo'>"+(d.source.name)+"</span> <span class='relationInfo'> is the parent of </span>" 
				+"<span class='termInfo'>"+(d.target.name)+"<span></p>");
			dialogs.showDialog(focusTerm);
		}
		
	};
	var detailedRelationInformation = function(d, term){
		if(focusTerm!=term){
			$( "#information" ).empty();			
			focusTerm = term;
			var selectedRelation = $('input[name=relations]:checked', '#relationRadioSelection').val();
			search.getAdjacencyMap().forEach(function (val, key) {
				if($.inArray(term,val) != -1 && searchTerm != term){
					var rel = key.substr(0,key.indexOf('*'));
					if(rel == selectedRelation){
						if(rel=="unknown")
							rel = "unknown relations with"
						$( "#information" ).append("<p><span id='"+d.source.key+"' class='termInfo'>"+d.source.name+"</span> "
								+"<span id='definition' class='definition' title='Click for definiton'> (<a href=\"javascript:search.searchDefinition('"+d.source.key+"')\">def</a>) </span></span>"
								+"<span class='relationInfo'>"+rel+"</span> "
								+"<span id='"+d.target.name+"'class='termInfo'>"+d.target.name
								+"<span id='definition' class='definition' title='Click for definiton'> (<a href=\"javascript:search.searchDefinition('"+d.target.key+"')\">def</a>) </span></span>"
								+"</span>"
								+"</p>");
						dialogs.showDialog(d.target.name);
					}
				}
				if(searchTerm == term){
					if ($("#information").dialog("instance") != null)
						$("#information").dialog("close");
				}
			});
		}
	};


	var getSemanticType = function(source,target){
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
	var findSynonyms = function(){
		$("#information span").hover(function(){
			$("#synonymsText").empty();
			$("#synonymsText").append("<p> Show all the synonyms for the term <b>"
					+"<a href=\"javascript:getSynonyms('"+this.innerHTML+"')\">"+this.innerHTML+"</b></a> in various  terminologies.");
		});
	}

	return{
		showConceptInformation:detailedInformation,
		showRelationInformation:detailedRelationInformation,
		reset:resetFocusTerm
	}

})();	


var dialogs = (function(){
	
	var displayInfoDialog=function(titleName){
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
	};

	var displaySynonymsDialog = function(){
		$("#synonyms").dialog({
			width:600,
			appendTo: "#main",
			maxHeight: 200,
			closeOnEscape: false,
			hide:true,
			position:{my:"left center", at:"left-80 center"}
		});
	};

	var displayDefinitionDialog= function(titleName){
		$("#definitions").dialog({
			width:800,
			title:'Definitions of '+ titleName,
			appendTo: "#main",
			maxHeight: 250,
			closeOnEscape: false,
			hide:true,
			position:{my:"right top", at:"right-100 top+100"}
		});
	};
	
	return {
		showDialog:displayInfoDialog,
		showDeifinitionDialog:displayDefinitionDialog,
		closeInfoDialog : function(){
			if ($("#information").dialog("instance") != null)
				$("#information").dialog("close");
		},
		closeDefinitionDialog : function(){
			if ($("#definitions").dialog("instance") != null)
				$("#definitions").dialog("close");
		},
	}
})();

