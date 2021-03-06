

var treeHierarchy = (function (){
	
		var svg;
		var treeMargin = {
			top : 30,right : 20,bottom : 30,left : 20 },
			treeWidth = 1200 - treeMargin.left - treeMargin.right, barHeight = 50, barWidth = treeWidth * .8;
		var i = 0, duration = 400;
		var tree = d3.layout.tree().nodeSize([ 20, 60 ]);
		var treeDiagonal = d3.svg.diagonal().projection(function(d) {
			return [ d.y, d.x ];
		});
		var nodes, div, height;

		var _render = function(data){
			$("#hierarchy").empty();
			nodes = tree.nodes(data);
			height = Math.max(500, nodes.length * barHeight + treeMargin.top
					+ treeMargin.bottom);
			div = d3.select("#hierarchy").insert("div", "h2").style("height",
					treeWidth + "px").style("-webkit-backface-visibility", "hidden");
			svg = div.append("svg:svg").attr("width", treeWidth).attr("height", treeWidth)
					.append("svg:g").attr("transform",
							"translate(" + treeMargin.left + "," + treeMargin.top + ")");
			data.x0 = 0;
			data.y0 = 0;
			d3.select("svg").transition().duration(duration).attr("height", height);
			d3.select(self.frameElement).transition().duration(duration).style(
					"height", height + "px");
			nodes.forEach(function(d) {d.key = d.name; d.name = formatName(d.name);});
			nodes.forEach(function(d) {d.y = d.depth * 90;});
			// Compute the "layout".
			nodes.forEach(function(n, i) {n.x = i * barHeight;});
			// Update the nodes…
			var node = svg.selectAll("g.node").data(nodes, function(d) {
				return d.id || (d.id = ++i);
			});
			var nodeEnter = node.enter().append("g")
					.attr("class", _treeNodeClass)
					.attr("cui",function(d){
							return $("#"+d.key).text();
					}).attr("transform", function(d) {
						return "translate(" + data.y0 + "," + data.x0 + ")";
					}).style("opacity", 1e-6);
			// Enter any new nodes at the parent's previous position.
			nodeEnter.append("rect").attr("y", -barHeight / 2)
					.attr("height", barHeight).attr("width", barWidth).style("fill",
							_treeNodeColor).style("stroke-width", 3.5).style("stroke", "white")
					.on("click", _nodeClick)
					.on("mouseover", _mouseover)
					.on("mouseout", _mouseout);
		
			nodeEnter.append("text").attr("dy", 3.5).attr("dx", 5.5).text(function(d) {
				return d.name;
			});
			// Transition nodes to their new position.
			nodeEnter.transition().duration(duration).attr("transform", function(d) {
				return "translate(" + d.y + "," + d.x + ")";
			}).style("opacity", 1);
			node.transition().duration(duration).attr("transform", function(d) {
				return "translate(" + d.y + "," + d.x + ")";
			}).style("opacity", 1).select("rect").style("fill", _treeNodeColor);
			// Transition exiting nodes to the parent's new position.
			node.exit().transition().duration(duration).attr("transform", function(d) {
				return "translate(" + data.y + "," + data.x + ")";
			}).style("opacity", 1e-6).remove();
		
			// Update the links…
			var link = svg.selectAll("path.link").data(tree.links(nodes), function(d) {
				return d.target.id;
			});
			
			// Enter any new links at the parent's previous position.
			link.enter().insert("path", "g").attr("class", function(d) {
				return "link source-" + d.source.key + 
				" target-" + d.target.key;
				}).attr("d",
					function(d) {
						var o = {
							x : data.x0,
							y : data.y0
						};
						return treeDiagonal({
							source : o,
							target : o
						});
			}).transition().duration(duration).attr("d", treeDiagonal);
		
			link.transition().duration(duration).attr("d", treeDiagonal);
			link.exit().transition().duration(duration).attr("d", function(d) {
				var o = {
					x : data.x,
					y : data.y
				};
				return treeDiagonal({
					source : o,
					target : o
				});
			}).remove();
		
			nodes.forEach(function(d) {
				d.x0 = d.x;
				d.y0 = d.y;
			});
			graphHelpers.formatSVGText(svg,true);
	};

	var _nodeClick = function(d) {
		if (d.children) {
			d._children = d.children;
			d.children = null;
		} else {
			d.children = d._children;
			d._children = null;
		}
		updateTree(d);
	}

	var _treeNodeClass =function(d){
		if(d.children || d._children)
			return "node _mouseover";
		else
			return "node";
	}

	var _treeNodeColor=function(d) {
		return d._children ? "rgba(24, 104, 234, 0.5)"
				: d.children ? "rgba(93, 165, 232, 0.34)"
						: "rgba(163, 163, 163, 0.25)";
	};

	var _mouseover=function(d) {
		svg.selectAll("path.link.target-" + d.key).classed("target", true).each(
				_updatenodes("source", true,d.key,d));
		svg.selectAll("path.link.source-" + d.key).classed("source", true).each(
				_updatenodes("target", true,d.key,d));
	};

	var _mouseout = function(d) {
		svg.selectAll("path.link.source-" + d.key).classed("source", false).each(
				_updatenodes("target", false));
		svg.selectAll("path.link.target-" + d.key).classed("target", false).each(
				_updatenodes("source", false));
	}

	var _updatenodes=function(name, value, term,d) {
		return function(d) {
			if(value)
				information.showConceptInformation(d, term);
			if (value)
				this.parentNode.appendChild(this);
			svg.select("#node-" + d.name).classed(name, value);
		};
	};
	
	return {
		drawChart: _render
	};
})();
