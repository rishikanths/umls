function getHierarchy(classes) {
	var map = {};
	function find(name, data) {
		var node = map[name], i;
		if (!node) {
			node = map[name] = data || {
				name : (name),
				children : []
			};
			if (name.length) {
				node.parent = find(name.substring(0, i = name.lastIndexOf(".")));
				node.parent.children.push(node);
				node.key = name.substring(i + 1);
				node.name = formatName(node.name);
			}
		}
		return node;
	}
	classes.forEach(function(d) {
		find(d.name, d);
	});
	return map[""];
}

function getLinks(nodes) {
	var map = {}, imports = [];
	nodes.forEach(function(d) {
		map[d.key] = d;
	});
	nodes.forEach(function(d) {
		if (d.imports)
			d.imports.forEach(function(i) {
				imports.push({
					source : map[d.key],
					target : map[i]
				});
			});
	});

	return imports;
}
