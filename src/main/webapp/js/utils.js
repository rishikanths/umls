/**
 * 
 */

function add2Map(map,key,value){
	
	if(value.constructor==Array){
		var t = map.get(key);
		if(t == null)
			t = [];
		for(var i in value){
			if($.inArray(value[i].name, t) == -1)
				t.push(value[i].name);
		}
		map.set(key,t);
	}else{
		map.set(key,value);
	}
	return map;
}

function arr2String(value){
	var str = "";
	for(var i in value){
		str = str+value[i]+" ";
	}
	return str;
}

function resetGlobalVariable(){
	// From format.js;
	processedConcepts = [];
	unProcessedConcepts = [];
	// From information.js
	focusTerm = "";
	//From search.js
	adjacencyMap = new Map();
	termSemanticTypes = new Map();
	defaultRelation = "";
	if ($("#information").dialog("instance") != null)
		$("#information").dialog("close");
}
