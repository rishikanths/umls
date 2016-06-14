var m = [ 20, 120, 20, 120 ], w = 1280 - m[1] - m[3], h = 800 - m[0] - m[2], i = 0, root;

var tree = d3.layout.tree().size([ h, w ]);

var diagonal = d3.svg.diagonal().projection(function(d) {
	return [ d.y, d.x ];
});

var vis = d3.select("#visual").append("svg:svg").attr("width", w + m[1] + m[3])
		.attr("height", h + m[0] + m[2]).append("svg:g").attr("transform",
				"translate(" + m[3] + "," + m[0] + ")");

function dendogram(data) {
	root = data;
	update(root);
}
function update(source) {
	var duration = d3.event && d3.event.altKey ? 5000 : 500;

	// Compute the new tree layout.
	var nodes = tree.nodes(root).reverse();

	// Normalize for fixed-depth.
	nodes.forEach(function(d) {
		d.y = d.depth * 180;
	});

	// Update the nodes…
	var node = vis.selectAll("g").data(nodes, function(d) {
		return d.id || (d.id = ++i);
	});

	// Enter any new nodes at the parent's previous position.
	var nodeEnter = node.enter().append("svg:g").attr("transform", function(d) {
		//console.log("@EnterNode");
		source.y0 = source.y0;
		return "translate(" + source.y0 + "," + source.x0 + ")";
	}).on("click", function(d) {
		toggle(d);
		update(d);
	});

	nodeEnter.append("svg:text").attr("x", function(d) {
		return d.children || d._children ? -10 : 10;
	}).attr("dy", ".35em").attr("text-anchor", function(d) {
		return d.children || d._children ? "end" : "start";
	}).text(function(d) {
		return d.name;
	}).style("fill-opacity", 1e-6);

	// Transition nodes to their new position.
	var nodeUpdate = node.transition().duration(duration).attr(
			"transform",
			function(d) {
				if (d.parent != null
						&& (d.children != null || d._children != null)) {
					var arr = document.getElementsByTagName("text");
					for (i = 0; i < arr.length; i++) {
						if (d.name == arr[i].innerHTML)
							d.y = d.y + arr[i].getComputedTextLength();
					}
				}
				return "translate(" + d.y + "," + d.x + ")";
			});

	nodeUpdate.select("text").style("fill-opacity", 1);

	// Transition exiting nodes to the parent's new position.
	var nodeExit = node.exit().transition().duration(duration).attr(
			"transform", function(d) {
				//console.log("@Exit");
				var arr = document.getElementsByTagName("text");
				for (i = 0; i < arr.length; i++) {
						if (d.name == arr[i].innerHTML)
							d.y = d.y + arr[i].getComputedTextLength();
					}
				return "translate(" + source.y + "," + source.x + ")";
			}).remove();

	nodeExit.select("text").style("fill-opacity", 1e-6);

	// Update the links…
	var link = vis
			.selectAll("path.link")
			.data(
					tree.links(nodes),
					function(d) {
						//console.log("@LinkSelectAll");
						//console.log(d.object);
						if (d.source.parent == null
								&& d.target.children != null){
							console.log(d.source.name+"----"+d.target.name+"*");
							var arr = document.getElementsByTagName("text");
							for (i = 0; i < arr.length; i++) {
								if (d.target.name == arr[i].innerHTML)
									d.target.y = d.target.y
											- arr[i].getComputedTextLength();
							}
						} else if ((d.target.children == null || d.target._children != null)
								&& d.target.parent.parent != null) {
							console.log(d.source.name+"----"+d.target.name+"**");
							var arr = document.getElementsByTagName("text");
							if(d.source.y0!=null){
							for (i = 0; i < arr.length; i++) {
								if (d.target.name == arr[i].innerHTML){
									console.log(arr[i].getComputedTextLength()+"&");									
									d.source.y0 = d.source.y0
											+ arr[i].getComputedTextLength();
									console.log(d.source.y0);
								}
							}
							}
						}
						return d.target.id;
					});

	// Enter any new links at the parent's previous position.
	link.enter().insert("svg:path", "g").attr("class", "link").attr("d",
			function(d) {
				console.log("@Insert");
				var o = {
					x : source.x,
					y : parseInt(source.y)
				};
				return diagonal({
					source : o,
					target : o
				});
			}).transition().duration(duration).attr("d", diagonal);

	// Transition links to their new position.
	link.transition().duration(duration).attr("d", diagonal);

	// Transition exiting nodes to the parent's new position.
	link.exit().transition().duration(duration).attr("d", function(d) {
		console.log("@ExitLink");
		var o = {
			x : source.x,
			y : parseInt(source.y)-300
		};

		return diagonal({
			source : o,
			target : o
		});
	}).remove();

	// Stash the old positions for transition.
	nodes.forEach(function(d) {
		//console.log("@NodeForEach");
		d.x0 = d.x;
		d.y0 = d.y;
	});
}

// Toggle children.
function toggle(d) {
	if (d.children) {
		d._children = d.children;
		d.children = null;
	} else {
		d.children = d._children;
		d._children = null;
	}
}