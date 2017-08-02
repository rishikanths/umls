/**
 * processedConcepts - keeps track of processed concepts, i.e. the terms that
 * have been added to the map 
 * unProcessedConcepts - keeps track of unprocessed
 * concepts. This is required due to the cyclic nature of the UMLS
 * metathesaurus. For example, term Malaria can be related to itself using any
 * relationship. Hence, if "Malaria" is processed, the next time the term is
 * encountered it will be not be processed again.
 * 
 * relationDescription - stores all the relationship types and the respective
 * description.
 */
var processedConcepts = [];
var unProcessedConcepts = [];
var relationDescription = new Map();
var conceptMap;
/**
 * Every UMLS relationship has a type. The fully qualified type name if required. 
 * The application initially displayed this information, but the users's didnt understand it or ignored it. 
 */
$(function() {
	relationDescription.set("AQ", "Allowed qualifier");
	relationDescription.set("CHD",
			"has child relationship in a Metathesaurus source vocabulary");
	relationDescription.set("DEL", "Deleted concept");
	relationDescription.set("PAR",
			"has parent relationship in a Metathesaurus source vocabulary");
	relationDescription.set("QB", "can be qualified by.");
	relationDescription.set("RB", "has a broader relationship");
	relationDescription
			.set(
					"RL",
					"the relationship is similar or 'alike'. the two concepts are similar or 'alike'.");
	relationDescription.set("RN", "has a narrower relationship");
	relationDescription.set("RO",
			"has relationship other than synonymous, narrower, or broader");
	relationDescription.set("RQ", "related and possibly synonymous.");
	relationDescription.set("RU", "Related, unspecified");
	relationDescription.set("SIB",
			"has sibling relationship in a Metathesaurus source vocabulary.");
	relationDescription.set("SY", "source asserted synonymy.");
});
/**
 * toD3JFormat converts the search term child concepts into a format consumable
 * by the d3js graph structure. This function was initially written to handle the following use case. 
 * The application queried for all the child concepts of a search term until the depth of 4. 
 * This means, if A is the search concept, then let B C D be the direct children (Level 1) of A.
 * Then we search for all the child of B C D, Level 2 and so on.
 * 
 * In this version, we only show the direction children, i.e. B C D of A. The user can click on 
 * B or C or D to get its direct children. This reduces information overload and graph clutter. 
 * However, the function remains the same. 
 * 
 * @param term
 * @param child
 * @returns {Map}
 */
function toD3JFormat(term, child) {
	addToList(term, "UP");
	var conceptMap = new Map();
	var imports = [];
	if (child.length != 0) {
		child.forEach(function(c) {
			var cName = c.name;
			if ($.inArray(cName, unProcessedConcepts) == -1
					&& $.inArray(cName, processedConcepts) == -1) {
				add2Map(search.getConceptSemanticTypes(), cName, c.semanticTypes);
				if (conceptMap.get(term) != null)
					imports = conceptMap.get(term);
				imports.push(cName);
				conceptMap.set(term, imports);
				if (c.children.length != 0) {
					var tempMap = toD3JFormat(cName, c.children);
					tempMap.forEach(function(v, k) {
						conceptMap.set(k, v);
					});
					addToList(term, "P");
					removeFromList(term, "UP");
				} else {
					conceptMap.set(cName, []);
					addToList(term, "P");
				}
			} else {
				if (conceptMap.get(term) != null)
					imports = conceptMap.get(term);
				imports.push(cName);
				conceptMap.set(term, imports);
			}
		});
	}else{
		conceptMap.set(term,[]);
		setMessage("hierarchyMessage",formatName(term)+" doesn't have any childern.")
	}
	return conceptMap;
}

function toD3JTreeFormat(term){
	
	var data;
	data = constructTree(term);
	
	function constructTree(term){
		var temp = {};
		var child = [];
		temp.name=term;
		var children = conceptMap.get(term);
		if(children.length!=0){
			for(var c in children){
				var t = constructTree(children[c]);
				if(temp.children==null){
					temp.children=[];
					temp.children.push(t);
				}else{
					var arr = temp.children;
					arr.push(t);
					temp.children =arr;
				}
			}
		}
		return temp;
		
	}
	return data;
}

function M2J(map) {
	var d3JSONRadial = {
		data : []
	};
	map.forEach(function(v, k) {
		d3JSONRadial.data.push({
			"name" : k,
			"imports" : v
		});
	});
	return d3JSONRadial;
}
/*
 * Add and remove terms to/from processedConcepts and unProcessedConcepts
 */
function addToList(term, type) {
	if (type == "UP") {
		if ($.inArray(term in unProcessedConcepts) == -1)
			unProcessedConcepts.push(term);
	} else if (type == "P") {
		if ($.inArray(term in processedConcepts) == -1)
			processedConcepts.push(term);
	}
}
function removeFromList(term, type) {
	if (type == "UP")
		unProcessedConcepts.splice(unProcessedConcepts.indexOf(term), 1);
	else if (type == "P")
		unProcessedConcepts.splice(unProcessedConcepts.indexOf(term), 1);
}

/**
 * Converts the relationship map (obtained from search.js) to the radial format.
 * 
 * @param term The search term
 * @param map holds all the relational information about the search term. 
 * @returns {Map}
 */
function toD3JFormatRelation(term, map) {
	var conceptMap = new Map();
	var imports = [];
	map.forEach(function(val, key) {
		for ( var i in val) {
			conceptMap.set(val[i], []);
		}
		imports = $.merge(imports, val);
	});
	conceptMap.set(term, imports);
	return conceptMap;
}

/**
 * Generates the radial format based on the relation selected by the user. The
 * function uses the relationships populated in the adjacencyMap variable.
 * 
 * @param term
 * @param relation
 * @returns {Map}
 */
function toD3JFormatSelectedRelation(term, relation) {
	var conceptMap = new Map();
	var imports = [];
	adjacencyMap.forEach(function(val, key) {
		if (key.indexOf(relation) != -1) {
			for ( var i in val) {
				conceptMap.set(val[i], []);
			}
			imports = $.merge(imports, val);
		}
	});

	conceptMap.set(term, imports);
	return conceptMap;
}
