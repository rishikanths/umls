

function viz(term, parents, adjacency) {
	
	
	var diameter = 500,
    radius = diameter / 2,
    innerRadius = radius - 120;

var cluster = d3.layout.cluster()
    .size([360, innerRadius]) // For Radial cluster (x,y) where x - radius of the cluster and y the depth of the chart  
    .sort(null)
    .value(function(d) { return d.size; });

var bundle = d3.layout.bundle();

var line = d3.svg.line.radial()
    .interpolate("bundle")
    .tension(.85)
    .radius(function(d) { return d.y; })
    .angle(function(d) { return d.x / 180 * Math.PI; });

var svg = d3.select("visual").append("svg")
    .attr("width", diameter)
    .attr("height", diameter)
  .append("g")
    .attr("transform", "translate(" + radius + "," + radius + ")");

var link = svg.append("g").selectAll(".link");
var node = svg.append("g").selectAll(".node");

  var nodes = cluster.nodes(termNodes(term, parents, adjacency));
  var links = termAdjacency(term, nodes, adjacency);

  link = link
      .data(bundle(links))
    .enter().append("path")
      .each(function(d) { d.source = d[0], d.target = d[d.length - 1]; })
      .attr("class", "link")
      .attr("d", line);

  node = node
      .data(nodes.filter(function(n) { return !n.children; }))
    .enter().append("text")
      .attr("class", "node")
      .attr("dy", ".31em")
      .attr("transform", function(d) { return "rotate(" + (d.x - 90) + ")translate(" + (d.y + 8) + ",0)" + (d.x < 180 ? "" : "rotate(180)"); })
      .style("text-anchor", function(d) { return d.x < 180 ? "start" : "end"; })
      .text(function(d) { return d.key; })
      .on("mouseover", mouseovered)
      .on("mouseout", mouseouted);
  
  d3.select(self.frameElement).style("height", diameter + "px");
}

function mouseovered(d) {
  node
      .each(function(n) { n.target = n.source = false; });

  link
      .classed("link--target", function(l) { if (l.target === d) return l.source.source = true; })
      .classed("link--source", function(l) { if (l.source === d) return l.target.target = true; })
    .filter(function(l) { return l.target === d || l.source === d; })
      .each(function() { this.parentNode.appendChild(this); });

  node
      .classed("node--target", function(n) { return n.target; })
      .classed("node--source", function(n) { return n.source; });
}

function mouseouted(d) {
  link
      .classed("link--target", false)
      .classed("link--source", false);

  node
      .classed("node--target", false)
      .classed("node--source", false);
}



// Lazily construct the package hierarchy from class names.
function termNodes(term, parents, adjacency) {
  var map = {};
  var emptyNode = {name:"",children:[],key:""};
  var termNode = {name:term,children:[],key:term};
  map[term] = termNode;
  map[""] = emptyNode;
  function createParentNode(name){
	  var parentNode = {name:name,children:[],key:name};
	  map[name] = parentNode;
	  emptyNode.children.push(parentNode);
	  parentNode.parent = emptyNode;
	  parentNode.children.push(termNode);
	  termNode.parent = parentNode;
  }
  
  function createAdjacencyNode(name){
	  if(map[name]==null){
		  var adjacencyNode = {name:name,children:[],key:name};
		  adjacencyNode.children.push(termNode);
		  emptyNode.children.push(adjacencyNode);
		  map[name] = adjacencyNode;
	  }
  }

  parents.forEach(function(d) {
    createParentNode(d);
  });
  
  adjacency.forEach(function(d) {
	  createAdjacencyNode(d);
  });

  return map[""];
}

// Return a list of imports for the given array of nodes.
function termAdjacency(term, nodes, adjacency) {
  var map = {},
      links = [], termNode;

  // Compute a map from name to node.
  nodes.forEach(function(d) {
	 if(d.name == term)
		 termNode = d;
    map[d.name] = d;
  });
  
  // For each import, construct a link from the source to target node.
  nodes.forEach(function(d) {
	  links.push({source: termNode, target: map[d.name]});
  });

  return links;
}
