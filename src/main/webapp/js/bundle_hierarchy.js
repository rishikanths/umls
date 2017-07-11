
var svgHierarchy;
function dendogramRadial(hierarchyData) {
	
	var width, height, rx, ry;
	var div, bundle, line, cluster;
	var nodes, splines, path;
	var offset = $("#hierarchy").offset();
	width = $("#hierarchy").width();
	height = width/2;
	rx = width / 2, ry = height / 2;
	
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

	/**
	 * To redraw a difference concept, the previous SVG has be removed. 
	 */
	$("#hierarchy").empty();
	div = d3.select("#hierarchy").insert("div", "h2").style("height", height + "px")
			.style("-webkit-backface-visibility","hidden");
	svgHierarchy = div.append("svg:svg").attr("width", width).attr("height", height).append(
			"svg:g").attr("transform", "translate(" + rx + "," + ry + ")");

	svgHierarchy.append("svg:path").attr("class", "arc").attr(
			"d",d3.svg.arc().outerRadius(ry - 120).innerRadius(0).startAngle(0)
					.endAngle(2 * Math.PI)).on("mousedown", mousedownSVGH);

	nodes = cluster.nodes(getHierarchy(hierarchyData.data)), links = getLinks(nodes);
	splines = bundle(links);
	path = svgHierarchy.selectAll("path.link").data(links).enter()
			.append("svg:path").attr(
					"class",
					function(d) {
						return "link source-" + d.source.key + " target-"
								+ d.target.key;
					}).attr("d", function(d, i) {
				return line(splines[i]);
			});

	svgHierarchy.selectAll("g.node").data(nodes.filter(function(n) {
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
		return formatName(d.key);
	}).on("mouseover", mouseoverSVGH).on("mouseout", mouseoutSVGH);

	d3.select("input[type=range]").on("change", function() {
		line.tension(this.value / 100);
		path.attr("d", function(d, i) {
			return line(splines[i]);
		});
	});
	enableDownload();
	formatGraphText(svgHierarchy,true);
}
function mouse(e) {
	return [ e.pageX - rx, e.pageY - ry ];
}

function mousedownSVGH() {
	var m0 = mouse(d3.event);
	d3.event.preventDefault();
}

function mouseoverSVGH(d) {
	svgHierarchy.selectAll("path.link.target-" + d.key).classed("target", true).each(
			updateNodesSVGH("source", true,d.key));

	svgHierarchy.selectAll("path.link.source-" + d.key).classed("source", true).each(
			updateNodesSVGH("target", true,d.key));
}

function mouseoutSVGH(d) {
	svgHierarchy.selectAll("path.link.source-" + d.key).classed("source", false).each(
			updateNodesSVGH("target", false));

	svgHierarchy.selectAll("path.link.target-" + d.key).classed("target", false).each(
			updateNodesSVGH("source", false));
}

function updateNodesSVGH(name, value, term) {
	return function(d) {
		if(value)
			detailedInformation(d, term);
		if (!value){
			this.parentNode.appendChild(this);
			focusTerm = "";
		}
		svgHierarchy.select("#node-" + d[name].key).classed(name, value);
	};
}

function cross(a, b) {
	return a[0] * b[1] - a[1] * b[0];
}

function dot(a, b) {
	return a[0] * b[0] + a[1] * b[1];
}