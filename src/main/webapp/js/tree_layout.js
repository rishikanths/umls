var treeMargin = {
	top : 30,
	right : 20,
	bottom : 30,
	left : 20
}, treeSVG, treeWidth = 1200 - treeMargin.left - treeMargin.right, barHeight = 50, barWidth = treeWidth * .8;

var i = 0, duration = 400, treeData;

var tree = d3.layout.tree().nodeSize([ 20, 60 ]);

var treeDiagonal = d3.svg.diagonal().projection(function(d) {
	return [ d.y, d.x ];
});
var treeNodes, treeDiv;

/*
 * var treeSVG = d3.select("body").append("svg").attr("width", treeWidth + treeMargin.left +
 * treeMargin.right).append("g").at tr("transform", "translate(" + treeMargin.left + "," +
 * treeMargin.top + ")");
 */
function treeLayout(data) {
	$("#hierarchy").empty();
	treeDiv = d3.select("#hierarchy").insert("div", "h2").style("height",
			treeWidth + "px").style("-webkit-backface-visibility", "hidden");
	treeSVG = treeDiv.append("svg:svg").attr("width", treeWidth).attr("height", treeWidth)
			.append("svg:g").attr("transform",
					"translate(" + treeMargin.left + "," + treeMargin.top + ")");
	treeData = data
	treeData.x0 = 0;
	treeData.y0 = 0;
	updateTree(treeData);
	//collapse();
}

function updateTree(source) {

	// Compute the flattened node list. TODO use d3.layout.hierarchy.
	treeNodes = tree.nodes(treeData);
	
	var height = Math.max(500, treeNodes.length * barHeight + treeMargin.top
			+ treeMargin.bottom);

	d3.select("svg").transition().duration(duration).attr("height", height);

	d3.select(self.frameElement).transition().duration(duration).style(
			"height", height + "px");
	treeNodes.forEach(function(d) {
		d.y = d.depth * 90;
	});
	// Compute the "layout".
	treeNodes.forEach(function(n, i) {
		n.x = i * barHeight;
	});

	// Update the nodes…
	var node = treeSVG.selectAll("g.node").data(treeNodes, function(d) {
		return d.id || (d.id = ++i);
	});

	var nodeEnter = node.enter().append("g").attr("class", treeNodeClass).attr(
			"transform", function(d) {
				return "translate(" + source.y0 + "," + source.x0 + ")";
			}).style("opacity", 1e-6);

	// Enter any new nodes at the parent's previous position.
	nodeEnter.append("rect").attr("y", -barHeight / 2)
			.attr("height", barHeight).attr("width", barWidth).style("fill",
					treeNodeColor).style("stroke-width", 3.5).style("stroke", "white")
			.on("click", treeNodeClick)
			.on("mouseover", treeLayoutMouseOver)
			.on("mouseout", treeLayoutMouseOut);
	/*
	 * nodeEnter.append("rect").attr("y", -barHeight / 2) .attr("height",
	 * barHeight-5).attr("width", barWidth-10).style("fill", treeNodeColor).on("click",
	 * click);
	 */

	nodeEnter.append("text").attr("dy", 3.5).attr("dx", 5.5).text(function(d) {
		return d.name;
	});

	// Transition nodes to their new position.
	nodeEnter.transition().duration(duration).attr("transform", function(d) {
		return "translate(" + d.y + "," + d.x + ")";
	}).style("opacity", 1);

	node.transition().duration(duration).attr("transform", function(d) {
		return "translate(" + d.y + "," + d.x + ")";
	}).style("opacity", 1).select("rect").style("fill", treeNodeColor);

	// Transition exiting nodes to the parent's new position.
	node.exit().transition().duration(duration).attr("transform", function(d) {
		return "translate(" + source.y + "," + source.x + ")";
	}).style("opacity", 1e-6).remove();

	// Update the links…
	var link = treeSVG.selectAll("path.link").data(tree.links(treeNodes), function(d) {
		return d.target.id;
	});
	
	/*var link = treeSVG.selectAll("path.link").data(tree.links(treeNodes))
				.enter().append("svg:path").attr("class",
					function(d) {
						return "link source-" + d.source.name + " target-"
							+ d.target.name;
				});*/

	// Enter any new links at the parent's previous position.
	link.enter().insert("path", "g").attr("class", function(d) {
				return "link source-" + d.source.name + 
				" target-" + d.target.name;
				}).attr("d",
					function(d) {
						var o = {
							x : source.x0,
							y : source.y0
						};
						return treeDiagonal({
							source : o,
							target : o
						});
			}).transition().duration(duration).attr("d", treeDiagonal);

	// Transition links to their new position.
	link.transition().duration(duration).attr("d", treeDiagonal);

	// Transition exiting nodes to the parent's new position.
	link.exit().transition().duration(duration).attr("d", function(d) {
		var o = {
			x : source.x,
			y : source.y
		};
		return treeDiagonal({
			source : o,
			target : o
		});
	}).remove();

	// Stash the old positions for transition.
	treeNodes.forEach(function(d) {
		d.x0 = d.x;
		d.y0 = d.y;
	});
}

function treeNodeClick(d) {
	if (d.children) {
		d._children = d.children;
		d.children = null;
	} else {
		d.children = d._children;
		d._children = null;
	}
	updateTree(d);
}

function treeNodeClass(d){
	if(d.children || d._children)
		return "node treeMouseOver";
	else
		return "node";
}

function treeNodeColor(d) {
	return d._children ? "rgba(24, 104, 234, 0.5)"
			: d.children ? "rgba(93, 165, 232, 0.34)"
					: "rgba(163, 163, 163, 0.25)";
}


function treeLayoutMouseOver(d) {
	treeSVG.selectAll("path.link.target-" + d.name).classed("target", true).each(
			updateTreeNodes("source", true,d.name,d));

	treeSVG.selectAll("path.link.source-" + d.name).classed("source", true).each(
			updateTreeNodes("target", true,d.name,d));
}

function treeLayoutMouseOut(d) {
	treeSVG.selectAll("path.link.source-" + d.name).classed("source", false).each(
			updateTreeNodes("target", false));

	treeSVG.selectAll("path.link.target-" + d.name).classed("target", false).each(
			updateTreeNodes("source", false));
}

function updateTreeNodes(name, value, term,d) {
	return function(d) {
		if(value)
			detailedInformation(d, term);
		if (value)
			this.parentNode.appendChild(this);
		treeSVG.select("#node-" + d.name).classed(name, value);
	};
}


function collapse(){
	treeNodes.forEach(function(d) {
		if(d.name != searchTerm){
			if (d.children) {
				d._children = d.children;
				d.children = null;
			}
			updateTree(d);
		}
	});
}
