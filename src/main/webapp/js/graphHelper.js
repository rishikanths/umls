var graphHelpers = (function(){
	var _computeHierarchy = function(classes) {
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
	};

	var _generateLinks = function (nodes) {
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
	};
	
	var _formatGraphText = function(svg, addLink) {
		var isSearchTerm = false;
		svg.selectAll('text').each(
			function(d) {
				var el = d3.select(this);
				if (el.text() == formatName(searchTerm)
						|| el.text() == searchTerm) {
					el.attr("class", "searchTerm");
					isSearchTerm = true;
				}
				el.text('');
				if (d.name.length <= 30) {
					if (!isSearchTerm && addLink) {
						var cui = $("#" + d.key).text();
						el.append('a').attr('href',
								'javascript:search.searchByCUI(\'' + cui + '\',\'' + d.key
										+ '\',true)').text(d.name);
					} else
						el.text(d.name);
				} else {
					if (addLink) {
						var cui = $("#" + d.key).text();
						el.append('a').attr(
								'href', 'javascript:search.searchByCUI(\'' + cui + '\',\'' + d.key
										+ '\',true)').text(d.name.slice(0, 25)+ " .. ...");
					} else
						el.append('tspan').text(d.name.slice(0, 25) + " .. ...");
				}
				isSearchTerm = false;
			}
		);
	}

	
	return{
		computeHierarchy:_computeHierarchy,
		generateLinks:_generateLinks,
		formatSVGText:_formatGraphText
	};
	
})();