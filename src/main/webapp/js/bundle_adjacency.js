var LEN_CONST = 50;
var INC_CONST = 20;
var len = LEN_CONST;
var inc=INC_CONST; 

var w = 1280, h = 800, rx = w / 2, ry = h / 2, m0, rotate = 0;

var splines_rel = [];

var cluster_rel = d3.layout.cluster().size([ 360, ry - 120 ]).sort(function(a, b) {
	return d3.ascending(a.key, b.key);
});

var bundle_rel = d3.layout.bundle();

var line_rel = d3.svg.line.radial().interpolate("bundle").tension(.85).radius(
		function(d) {
			return d.y;
		}).angle(function(d) {
	return d.x / 180 * Math.PI;
});

var div_rel, svg_rel;
function dendogramRadialRelation(data) {

	/**
	 * To redraw a difference concept, the previous SVG has be removed. 
	 */
	$("#relation").empty();
	div_rel = d3.select("#relation").insert("div", "h2").style("height", w + "px")
			.style("-webkit-backface-visibility","hidden");
	var ryTemp = parseInt(ry)+200;
	svg_rel = div_rel.append("svg:svg").attr("width", w).attr("height", w).append(
			"svg:g").attr("transform", "translate(" + rx + "," + ryTemp + ")");

	svg_rel.append("svg:path").attr("class", "arc").attr(
			"d",d3.svg.arc().outerRadius(ry - 120).innerRadius(0).startAngle(0)
					.endAngle(2 * Math.PI)).on("mousedown", mousedown_rel);

	updateRelation(data.data);
}
function updateRelation(relationData) {

	var nodes_rel = cluster_rel.nodes(getHierarchy(relationData)), links_rel = getImports(nodes_rel), splines_rel = bundle_rel(links_rel);

	var path_rel = svg_rel.selectAll("path.link").data(links_rel).enter()
			.append("svg:path").attr(
					"class",
					function(d) {
						return "linkRel source-" + d.source.key + " target-"
								+ d.target.key;
					}).attr("d", function(d, i) {
				return line(splines_rel[i]);
			});

	svg_rel.selectAll("g.node").data(nodes_rel.filter(function(n) {
		return !n.children;
	})).enter().append("svg:g").attr("class", "node").attr("id", function(d) {
		return "node-" + d.key;
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
	}).on("mouseover", mouseover_rel).on("mouseout", mouseout_rel);

	d3.select("input[type=range]").on("change", function() {
		line_rel.tension(this.value / 100);
		path_rel.attr("d", function(d, i) {
			return line(splines_rel[i]);
		});
	});
	
	formatGraphText(svg_rel);
}

function mousedown_rel() {
	m0 = mouse(d3.event);
	d3.event.preventDefault();
}

function mouseover_rel(d) {
	svg_rel.selectAll("path.linkRel.target-" + d.key).classed("target", true).each(
			updateNodes_rel("source", true, d.name));

	svg_rel.selectAll("path.linkRel.source-" + d.key).classed("source", true).each(
			updateNodes_rel("target", true, d.name));

}

function mouseout_rel(d) {
	svg_rel.selectAll("path.linkRel.source-" + d.key).classed("source", false).each(
			updateNodes_rel("target", false));

	svg_rel.selectAll("path.linkRel.target-" + d.key).classed("target", false).each(
			updateNodes_rel("source", false));
}

function updateNodes_rel(name, value, term) {
	return function(d) {
		if(value)
			detailedRelationInformation(d, term);
		if (value)
			this.parentNode.appendChild(this);
		svg_rel.select("#node-" + d[name].key).classed(name, value);
	};
}
