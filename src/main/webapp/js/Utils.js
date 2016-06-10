/**
 * 
 */

function toD3jFormat_OLD(term, parents) {

	var jsonData = {};
	jsonData["name"] = term;
	jsonData["children"] = [];
	parents.forEach(function(parent) {
		var childData = {};
		childData["name"] = parent;
		jsonData.children.push(childData)
	});
	
	dendogram(jsonData);
}


function toD3jFormat(term, child) {

	var d3JSON = {};
	d3JSON["name"] = term;
	d3JSON["children"] = [];
	child.forEach(function(c) {
		var childData = {};
		childData["name"] = c.name;
		if(c.children.length!=0)
			childData = toD3jFormat(c.name,c.children);
		d3JSON.children.push(childData);
	});
	
	return d3JSON;
	
}