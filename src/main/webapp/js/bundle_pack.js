/**
 * 
 */

var svgPack;
function packLayout(hierarchyData) {

	var div, format;
	var pack;

	var width = $("#hierarchy").width();
	var height = 900;

	$("#hierarchy").empty();
	div = d3.select("#hierarchy").insert("div", "h2").style("height",
			height + "px").style("-webkit-backface-visibility", "hidden");
	svgPack = div.append("svg:svg").attr("width", width).attr("height", height)
			.append("svg:g")
			.attr("transform", "translate(" + 2 + "," + 2 + ")");
	format = d3.format(",d");

	pack = d3.layout.pack().size([ width - 10, width - 10 ]);
	
	var root = getHierarchy(hierarchyData.data);
	var node = svgPack.selectAll(".node").data(root).enter()
			.append("g").attr("class", function(d) {
				return d.children ? "node" : "leaf node";
			}).attr("transform", function(d) {
				return "translate(" + d.x + "," + d.y + ")";
			});
	node.append("title").text(function(d) {
		return formatName(d.name) + "\n" + format(d.value);
	});
	node.append("circle").attr("r", function(d) {
		return d.r;
	});
	node.filter(function(d) {return !d.children;})
		.append("text").attr("dy", "0.3em").text(function(d) {return d.name.substring(0, d.r / 3);});
}