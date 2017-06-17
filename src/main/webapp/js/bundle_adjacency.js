var svgRelation;
function dendogramRadialRelation(relationalData) {

	var width, height, rx, ry;
	var div;
	var nodes, links, splines, path;

	width = $("#relation").width();
	height = 900;
	rx = width / 2, ry = height / 2;
	
	var cluster = d3.layout.cluster().size([ 360, ry - ry/2 ]).sort(function(a, b) {
		return d3.ascending(a.key, b.key);
	});

	var bundle = d3.layout.bundle();

	var line = d3.svg.line.radial().interpolate("bundle").tension(.85).radius(
			function(d) {
				return d.y;
			}).angle(function(d) {
		return d.x / 180 * Math.PI;
	});

	
	/**
	 * To redraw a difference concept, the previous SVG has be removed. 
	 */
	$("#relation").empty();
	div = d3.select("#relation").insert("div", "h2").style("height", height + "px")
			.style("-webkit-backface-visibility","hidden");
	
	svgRelation = div.append("svg:svg").attr("width", width).attr("height", height).append(
			"svg:g").attr("transform", "translate(" + rx + "," + ry + ")");

	svgRelation.append("svg:path").attr("class", "arc").attr(
			"d",d3.svg.arc().outerRadius(ry - 120).innerRadius(0).startAngle(0)
					.endAngle(2 * Math.PI)).on("mousedown", mousedownSVGR);

	nodes = cluster.nodes(getHierarchy(relationalData.data));
	links = getLinks(nodes);
	splines = bundle(links);
	path = svgRelation.selectAll("path.link").data(links).enter()
			.append("svg:path").attr(
					"class",
					function(d) {
						return "linkRel source-" + d.source.key + " target-"
								+ d.target.key;
					}).attr("d", function(d, i) {
				return line(splines[i]);
			});

	svgRelation.selectAll("g.node").data(nodes.filter(function(n) {
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
	}).on("mouseover", mouseoverSVGR).on("mouseout", mouseoutSVGR);

	d3.select("input[type=range]").on("change", function() {
		line.tension(this.value / 100);
		path.attr("d", function(d, i) {
			return line(splines[i]);
		});
	});
	
	formatGraphText(svgRelation, false);
	enableDownload();
}

function mousedownSVGR() {
	m0 = mouse(d3.event);
	d3.event.preventDefault();
}

function mouseoverSVGR(d) {
	svgRelation.selectAll("path.linkRel.target-" + d.key).classed("target", true).each(
			updateNodesSVGR("source", true, d.key));

	svgRelation.selectAll("path.linkRel.source-" + d.key).classed("source", true).each(
			updateNodesSVGR("target", true, d.key));

}

function mouseoutSVGR(d) {
	svgRelation.selectAll("path.linkRel.source-" + d.key).classed("source", false).each(
			updateNodesSVGR("target", false));

	svgRelation.selectAll("path.linkRel.target-" + d.key).classed("target", false).each(
			updateNodesSVGR("source", false));
}

function updateNodesSVGR(name, value, term) {
	return function(d) {
		if(value)
			detailedRelationInformation(d, term);
		if (!value){
			this.parentNode.appendChild(this);
			focusTerm="";
		}
		svgRelation.select("#node-" + d[name].key).classed(name, value);
	};
}
