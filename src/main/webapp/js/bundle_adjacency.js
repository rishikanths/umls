
var adjacency = (function (){
	var svg;
	var width, height, rx, ry;
	var div, bundle, line, cluster;
	var nodes, links, splines, path;
	width = $("#relation").width();
	height = width*.75;
	rx = width / 2, ry = height/2;
	
	var _render = function (relationalData) {
		
		cluster = d3.layout.cluster().size([360,ry/2]).sort(function(a, b) {
			return d3.ascending(a.key, b.key);
		});
	
		bundle = d3.layout.bundle();
		line = d3.svg.line.radial().interpolate("bundle").tension(.85).radius(
				function(d) {
					return d.y;
				}).angle(function(d) {
			return d.x / 180 * Math.PI;
		});
		$("#relation").empty();
		div = d3.select("#relation").insert("div", "h2").style("height", height + "px")
				.style("-webkit-backface-visibility","hidden");
		
		svg = div.append("svg:svg").attr("width", width).attr("height", height).append(
				"svg:g").attr("transform", "translate(" + rx + "," + ry + ")");
	
		svg.append("svg:path").attr("class", "arc").attr(
				"d",d3.svg.arc().outerRadius(ry - 120).innerRadius(0).startAngle(0)
						.endAngle(2 * Math.PI)).on("mousedown", _mousedown);
	
		nodes = cluster.nodes(graphHelpers.computeHierarchy(relationalData.data));
		links = graphHelpers.generateLinks(nodes);
		splines = bundle(links);
		path = svg.selectAll("path.link").data(links).enter()
				.append("svg:path").attr("class",function(d) {
							return "linkRel source-" + d.source.key + " target-"+ d.target.key;
						}).attr("d", function(d, i) {
					return line(splines[i]);
				});
	
		svg.selectAll("g.node").data(nodes.filter(function(n) {
			return !n.children;
		})).enter().append("svg:g").attr("class", "node").attr("id", function(d) {
			return "node-" + d.key;
		}).attr("cui",function(d){
			return $("#"+d.key).text();
		}).attr("transform", function(d) {
			return "rotate(" + (d.x - 90) + ")translate(" + d.y + ")";
		}).append("svg:text").attr("dx", function(d) {
			return d.x < 180 ? 8 : -8;
		}).attr("dy", ".31em").attr("text-anchor", function(d) {
			return d.x < 180 ? "start" : "end";
		}).attr("transform", function(d) {
			return d.x < 180 ? null : "rotate(180)";
		}).text(function(d) {
			return d.key;
		}).on("mouseover", _mouseover).on("mouseout", _mouseout);
	
		d3.select("input[type=range]").on("change", function() {
			line.tension(this.value / 100);
			path.attr("d", function(d, i) {
				return line(splines[i]);
			});
		});
		graphHelpers.formatSVGText(svg, false);
	};

	var _mousedown = function() {
		m0 = mouse(d3.event);
		d3.event.preventDefault();
	};

	var _mouseover = function(d) {
		svg.selectAll("path.linkRel.target-" + d.key).classed("target", true).each(
				_updateNodes("source", true, d.key));
	
		svg.selectAll("path.linkRel.source-" + d.key).classed("source", true).each(
				_updateNodes("target", true, d.key));
	};

	var _mouseout=function(d) {
		svg.selectAll("path.linkRel.source-" + d.key).classed("source", false).each(
				_updateNodes("target", false));
	
		svg.selectAll("path.linkRel.target-" + d.key).classed("target", false).each(
				_updateNodes("source", false));
	};

	var _updateNodes = function(name, value, term) {
		return function(d) {
			if(value)
				information.showRelationInformation(d, term);
			if (!value){
				this.parentNode.appendChild(this);
				information.reset();
			}
			svg.select("#node-" + d[name].key).classed(name, value);
		};
	};
	
	return {
		drawChart: _render
	};
	
})();
