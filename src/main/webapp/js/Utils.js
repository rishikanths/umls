/**
 * 
 */
var processedConcepts = [];
var unProcessedConcepts = [];
function toD3jRadialFormat(term, child){
	addToList(term,"UP");
	var conceptMap = new Map();
	var imports = [];
	child.forEach(function(c){
		if($.inArray(c.name , unProcessedConcepts) == -1&& 
						$.inArray(c.name , processedConcepts) == -1){
			if(conceptMap.get(term)!=null)
				imports = conceptMap.get(term);
			imports.push(c.name);
			conceptMap.set(term,imports);
			if(c.children.length!=0){
					var tempMap = toD3jRadialFormat(c.name,c.children);
					tempMap.forEach(function(v,k){
						conceptMap.set(k,v);
					});
					addToList(term,"P");
					removeFromList(term,"UP");
				}else{
					conceptMap.set(c.name,[]);
					addToList(term,"P");
				}
			}
		else{
			if(conceptMap.get(term)!=null)
				imports = conceptMap.get(term);
			imports.push(c.name);
			conceptMap.set(term,imports);
		}
	});
	return conceptMap;
}

function M2J(conceptMap){
	var d3JSONRadial = {
			data : []
		};
	conceptMap.forEach(function(v,k){
		d3JSONRadial.data.push({"name":k,"imports":v});
	});
	return d3JSONRadial;
}
function addToList(term,type){
	if(type == "UP"){
		if($.inArray(term in unProcessedConcepts) == -1)
			unProcessedConcepts.push(term);
	}else if(type=="P"){
		if($.inArray(term in processedConcepts) == -1)
			processedConcepts.push(term);
	}
}
function removeFromList(term,type){
	if(type == "UP")
		unProcessedConcepts.splice(unProcessedConcepts.indexOf(term), 1);
	else if(type=="P")
		unProcessedConcepts.splice(unProcessedConcepts.indexOf(term), 1);
}

/*
 * function toD3jFormatRadial(term, child) { var d3JSONRadial = { data : [] };
 * var imports = []; child.forEach(function(c) { if(child.children!=null){ var
 * temp = toD3jFormat(term, child.children)
 * d3JSONRadial.data.push({"name":temp.name,"imports":temp.imports}); }else{
 * imports.push(term+_PREFIX+c.name);
 * d3JSONRadial.data.push({"name":term,"imports":imports}); } });
 * 
 * return d3JSONRadial;
 *  } function toJSONRadial(term,child){ child.forEach(function(c){ var dJSON =
 * {}; dJSON["name"] = term+_PREFIX+c.name; dJSON["imports"] = [];
 * c.children.forEach(function(cc) {
 * dJSON.imports.push(term+_PREFIX+c.name+_PREFIX+cc.name); });
 * d3JSONRadial.data.push(dJSON); if(c.children.length!=0)
 * toJSONRadial(term+_PREFIX+c.name,c.children); }); }
 */