/**
 * This script captures all the events in the application such as click, selected, mouse-movements, etc.
 */
var KEYCODE_ESC = 27;

function selectedRelation(e) {
	var active = relationPagination(1);
	changeRelationText(searchTerm, e.currentTarget.value,active);
	$("#pagniationValue").html(0);
}

function changeRelationText(term, relation,navigationMsg) {
	var ele = $("#relationRadioSelectionDesc");
	ele.empty();
	ele.append("<p style='background:#f1f8ff'>The chart shows how <span class='termInfo'>"
			+ term + "</span> is connected with other terms with  <span class='relationInfo'>"
			+ relation + "</span> relation.");
	if(navigationMsg)
		ele.append(" Due to large number of medical concepts that are connected to <span class='termInfo'>"
				+ term + "</span> using <span class='relationInfo'>"
				+ relation + "</span> relation, only few of them are displayed at once. Use the " +
						"navigational arrows to browse more medical concepts. </p>");
	
}

function relationPagination(direction) {
	var relationMap = new Map();
	var rel = [];
	var navigationActive = false;
	var relation = $('input[name=relations]:checked').val();
	search.getAdjacencyMap().forEach(function(val, key) {
		if (key.indexOf(relation) != -1) {
			if (val.length > 60) {
				navigationActive = true;
				$("#relationNavigation").css('display', 'inline');
				var index = parseInt($("#pagniationValue").text());
				var temp = val.length;
				navigationLinks(index,val.length);
				// Move forward
				if(direction ==1){
					//within the size limits
					if (index < temp) {
						val = val.slice(index, index + 60);
						index+=60;
					} else if(index > temp){
						index = val.length;
						val = val.slice(index - 60, val.length);
						index-=60;
					}
					
				}else{
					if ((index < temp || index >= temp) && index>60) {
						val = val.slice(index-120, index-60);
						index-=60;
					} else if(index <=60){
						val = val.slice(0, 60);
						index = 0;
					}
					
				}
				navigationLinks(index,temp);
				$("#pagniationValue").html(index);
			}else
				$("#relationNavigation").css('display', 'none');
			for ( var i in val) {
				relationMap.set(val[i], []);
			}
			rel = $.merge(rel, val);
		}
	});
	relationMap.set(searchTerm, rel);
	adjacency.drawChart(M2J(relationMap));
	
	return navigationActive;
}

function navigationLinks(index,length){
	index == 0? $("#leftLink").css('display', 'none'): $("#leftLink").css('display', 'inline');
	index >= length?$("#rightLink").css('display', 'none'):$("#rightLink").css('display', 'inline');
}

var conceptPath = [];

function displayPath(term){
	var div, cui;
	div= $("#conceptPath");
	if(term!="")
		cui = $("label#"+term).text();
	if(conceptPath.length == 0){
		div.append("<span class='termInfo' cui='"+cui+"' term='"+term+"'>"+"<a href=\"javascript:computeDisplayPath('"+cui+"')\">"+term+"</a>"+"</span>");
		conceptPath = conceptPath.concat(Array.from(div.children()));
		div.empty();
	}
	else{
		div.empty();
		for(i =0;i<conceptPath.length;i++){
			div.append(conceptPath[i].outerHTML);
		}
		if(conceptPath.length == 1 || conceptPath.length%2 != 0)
			div.append("<img class='rightArrow' width='16' height='16' src='images/rightArrow2.png'/>");
		div.append("<span class='termInfo' cui='"+cui+"' term='"+term+"'>" +
				"<a href=\"javascript:computeDisplayPath('"+cui+"')\">"+formatName(term)+"</a></span>");
		conceptPath  = [];
		conceptPath = conceptPath.concat(Array.from(div.children()));
		$("#conceptPathDiv").css("display","inline");
	}
}

function computeDisplayPath(cui){
	var path = "", arr = [], e,img,i=0;
	do{
		e  = conceptPath[i];
		arr.push(e);
		i++;
	}while(e.getAttribute("cui")!=cui);
	e = conceptPath[i-1];
	conceptPath = [];
	var div= $("#conceptPath");
	div.empty();
	if(arr.length == 1){
		conceptPath.push(e);
		search.searchByCUI(e.getAttribute("cui"),e.getAttribute("term"),false);
		$("#conceptPathDiv").css("display","none");
	}else{
		conceptPath = conceptPath.concat(arr);
		for(i =0;i<arr.length;i++){
			div.append(arr[i].outerHTML);
		}
		search.searchByCUI(e.getAttribute("cui"),e.getAttribute("term"),false);
	}
}


/*
 * Currently the downloads are disabled. 
 */
function enableDownload() {
	$("#downloadHDiv").css("display", "block");
	$("#downloadRDiv").css("display", "block");
}

$(function() {
	setFocus();
	
	$("#tabs").tabs({
		activate : function(event, ui) {
			if (ui.newTab.attr('id') == "hierarchyView") {
				$("#relationDiv").css("display", "none");
				$("#relationRadioSelectionDesc").css("display", "none");

			} else {
				$("#relationDiv").css("display", "block");
				$("#relationRadioSelectionDesc").css("display", "block");
			}
		}
	});
	$("#hierarchyLayoutSelection").on("change", function() {
		var val = $(this).val();
		if (val == 'radial')
			hierarchy.drawChart(M2J(conceptMap));
		else if (val == 'tree')
			treeHierarchy.drawChart(toD3JTreeFormat(searchTerm))

	});
	$(document).keyup(
		function(e) {
			if (e.keyCode == KEYCODE_ESC) {
				if ($("#definitions").dialog("instance") != null
						&& $("#definitions").dialog("isOpen"))
					$("#definitions").dialog("close");
				else if ($("#information").dialog("instance") != null
						&& $("#information").dialog("isOpen"))
					$("#information").dialog("close");
			}
		});

	$(".download").on("click",
		function() {
			if ($("#relationDiv").css("display") == "none") {
				/*
				 * canvg("canvas", d3.select("#hierarchy
				 * svg").attr("version", 1.1).attr("xmlns",
				 * "http://www.w3.org/2000/svg").node().parentNode.innerHTML);
				 * var canvas = document.querySelector("canvas");
				 * canvas.toDataURL("image/png");
				 * $("#hiddenPng").attr('href', theImage);
				 * $("#hiddenPng").click()
				 */
				saveSvgAsPng($("#hierarchy").find("svg")[0], searchTerm
						+ "_hierarchy.png");
			} else {
				/*
				 * canvg('canvas', $("#relation").find("svg")[0]);
				 * $("#canvas").toDataURL("image/png");
				 */
				saveSvgAsPng($("#relation").find("svg")[0], searchTerm
						+ "_relation.png");
			}
	
		});
});