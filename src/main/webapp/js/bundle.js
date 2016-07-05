var LEN_CONST = 50;
var INC_CONST = 20;
var len = LEN_CONST;
var inc=INC_CONST; 

var w = 1280, h = 800, rx = w / 2, ry = h / 2, m0, rotate = 0;

var splines = [];

var cluster = d3.layout.cluster().size([ 360, ry - 120 ]).sort(function(a, b) {
	return d3.ascending(a.key, b.key);
});

var bundle = d3.layout.bundle();

var line = d3.svg.line.radial().interpolate("bundle").tension(.85).radius(
		function(d) {
			return d.y;
		}).angle(function(d) {
	return d.x / 180 * Math.PI;
});

var div, svg;
function dendogramRadial(data) {

	/**
	 * To redraw a difference concept, the previous SVG has be removed. 
	 */
	$("#hierarchy").empty();
	div = d3.select("#hierarchy").insert("div", "h2").style("height", w + "px")
			.style("-webkit-backface-visibility","hidden");
	var ryTemp = parseInt(ry)+200;
	svg = div.append("svg:svg").attr("width", w).attr("height", w).append(
			"svg:g").attr("transform", "translate(" + rx + "," + ryTemp + ")");

	svg.append("svg:path").attr("class", "arc").attr(
			"d",d3.svg.arc().outerRadius(ry - 120).innerRadius(0).startAngle(0)
					.endAngle(2 * Math.PI)).on("mousedown", mousedown);

	update(data.data);
}
function update(root) {

	var nodes = cluster.nodes(packages.root(root)), links = packages
			.imports(nodes), splines = bundle(links);

	var path = svg.selectAll("path.link").data(links).enter()
			.append("svg:path").attr(
					"class",
					function(d) {
						return "link source-" + d.source.key + " target-"
								+ d.target.key;
					}).attr("d", function(d, i) {
				return line(splines[i]);
			});

	svg.selectAll("g.node").data(nodes.filter(function(n) {
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
	}).on("mouseover", mouseover).on("mouseout", mouseout);

	d3.select("input[type=range]").on("change", function() {
		line.tension(this.value / 100);
		path.attr("d", function(d, i) {
			return line(splines[i]);
		});
	});
	
	svg.selectAll('text').each(function (d) {
		var el = d3.select(this);		
		el.text('');
		len = LEN_CONST;
		inc=INC_CONST;
		if(len>=d.name.length)
			el.text(d.name);
		while(len<d.name.length){
			var tspan = el.append('tspan').text(d.name.slice(len-inc,len));
			tspan.attr('x', 0).attr('dy', 15);
			len = len+inc;
		}
		var tspan = el.append('tspan').text(d.name.slice(len-inc));
		tspan.attr('x', 0).attr('dy', 15);
	});
}


function mouse(e) {
	return [ e.pageX - rx, e.pageY - ry ];
}

function mousedown() {
	m0 = mouse(d3.event);
	d3.event.preventDefault();
}

function mouseover(d) {
	svg.selectAll("path.link.target-" + d.key).classed("target", true).each(
			updateNodes("source", true,d.name));

	svg.selectAll("path.link.source-" + d.key).classed("source", true).each(
			updateNodes("target", true,d.name));
}

function mouseout(d) {
	svg.selectAll("path.link.source-" + d.key).classed("source", false).each(
			updateNodes("target", false));

	svg.selectAll("path.link.target-" + d.key).classed("target", false).each(
			updateNodes("source", false));
	removeInformation();
}

function updateNodes(name, value, term) {
	return function(d) {
		if(value)
			detailedInformation(d, term);
		if (value)
			this.parentNode.appendChild(this);
		svg.select("#node-" + d[name].key).classed(name, value);
	};
}

function cross(a, b) {
	return a[0] * b[1] - a[1] * b[0];
}

function dot(a, b) {
	return a[0] * b[0] + a[1] * b[1];
}
