
var diameter = $("#visual").height();
if(diameter<600)
	diameter = 800;
var radius = diameter / 2, innerRadius = radius-200;

var cluster = d3.layout.cluster().size([ 360, innerRadius ]).sort(null).value(
		function(d) {
			return d.size;
		});

var bundle = d3.layout.bundle();

var line = d3.svg.line.radial().interpolate("bundle").tension(.85).radius(
		function(d) {
			return d.y;
		}).angle(function(d) {
	return d.x / 180 * Math.PI;
});

function dendogramRadial(data) {
	$("#visual").empty();
	if(document.getElementsByTagName("svg")[0]!=null)
		document.getElementsByTagName("svg")[0].remove();
	root = data.data;
	update(root);
}
function update(root) {

	var svg = d3.select("#visual").append("svg").attr("width", diameter).attr(
			"height", diameter).append("g").attr("transform",
			"translate(" + radius + "," + radius + ")");

	var link = svg.append("g").selectAll(".link"), node = svg.append("g")
			.selectAll(".node");

	var nodes = cluster.nodes(packageHierarchy(root)); 
	var links = packageImports(nodes);

	link = link.data(bundle(links)).enter().append("path").each(function(d) {
		d.source = d[0], d.target = d[d.length - 1];
	}).attr("class", "link").attr("d", line);

	node = node.data(nodes.filter(function(n) {
		return !n.children;
	})).enter().append("text").attr("class", "node").attr("dy", ".31em").attr(
			"transform",
			function(d) {
				return "rotate(" + (d.x - 90) + ")translate(" + (d.y + 8)
						+ ",0)" + (d.x < 180 ? "" : "rotate(180)");
			}).style("text-anchor", function(d) {
		return d.x < 180 ? "start" : "end";
	}).text(function(d) {
		return d.key;
	}).on("mouseover", mouseovered).on("mouseout", mouseouted)

	// Handle reloading the chart with a new master node
	.on("click", function(d) {
		$("#nodeid").text("Current master node: " + d.name);
	});
	
	svg.selectAll('text').each(function (d) {
		var el = d3.select(this);		
		var len = 20,inc=20, total = d.name.length;
		el.text('');
		if(len>=total)
			el.text(d.name);
		while(len<total){
			var tspan = el.append('tspan').text(d.name.slice(len-inc,len));
			tspan.attr('x', 0).attr('dy', inc);
			len = len+inc;
		}
		var tspan = el.append('tspan').text(d.name.slice(len));
		tspan.attr('x', 0).attr('dy', 10);
	});
}
function mouseovered(d) {
	node.each(function(n) {
		n.target = n.source = false;
	});

	link.classed("link--target", function(l) {
		if (l.target === d)
			return l.source.source = true;
	}).classed("link--source", function(l) {
		if (l.source === d)
			return l.target.target = true;
	}).filter(function(l) {
		return l.target === d || l.source === d;
	}).each(function() {
		this.parentNode.appendChild(this);
	});

	node.classed("node--target", function(n) {
		return n.target;
	}).classed("node--source", function(n) {
		return n.source;
	});
}

function mouseouted(d) {
	link.classed("link--target", false).classed("link--source", false);

	node.classed("node--target", false).classed("node--source", false);
}

d3.select(self.frameElement).style("height", diameter + "px");

function packageHierarchy(classes) {
	var map = {};

	function find(name, data) {
		var node = map[name], i;
		if (!node) {
			node = map[name] = data || {
				name : name,
				children : []
			};
			if (name.length) {
				node.parent = find(name.substring(0, i = name.lastIndexOf(".")));
				node.parent.children.push(node);
				node.key = name.substring(i + 1);
			}
		}
		return node;
	}

	classes.forEach(function(d) {
		find(d.name, d);
	});

	return map[""];
}

function packageImports(nodes) {
	var map = {}, imports = [];

	nodes.forEach(function(d) {
		map[d.name] = d;
	});

	nodes.forEach(function(d) {
		if (d.imports)
			d.imports.forEach(function(i) {
				imports.push({
					source : map[d.name],
					target : map[i]
				});
			});
	});

	return imports;
}
