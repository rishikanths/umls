/**
 *  Captures all the events in the application such as click, selected, mouse movements, etc.
 */

function relationSelected(e){
	dendogramRadialRelation(M2J(toD3JFormatSelectedRelation
			(searchTerm,e.currentTarget.value)));
	changeRelationText(searchTerm,e.currentTarget.value);
}
function checkSelection(name, value){
	$("#relationRadioSelection input:radio[value='"+name+"']").attr("checked",value);
}

function changeRelationText(term,relation){
	var ele = $("#relationRadioSelectionDesc");
	ele.empty();
	ele.append("<p>The chart shows how <span id='termInfo'>"+term
			+"</span> is connected with other terms using  <span id='relationInfo'>"
			+relation+"</span> relation.</p>");
}
function displayError(error){
	$("#error").empty();
	$("#error").append("<p>"+error+"</p>");
	$("#error").css('display','block');
}
function displayMessage(msg){
	$("#message").empty();
	$("#message").append("<p>"+msg+"</p>");
	$("#message").css('display','block');
}

function formatGraphText(svg){
	svg.selectAll('text').each(function (d) {
		var el = d3.select(this);		
		if(el.text() == searchTerm)
			el.attr("class","searchTerm");
		el.text('');
		len = LEN_CONST;
		inc=INC_CONST;
		if(len>=d.name.length)
			el.text(d.name);
		/*var sIdnex = 0;
		  while(len<d.name.length){
			var tspan = el.append('tspan').text(d.name.slice(sIndex,inc+sIndex));
			tspan.attr('x', 0).attr('dy', 15);
			sIndex = sIndex+inc;
		}
		var tspan = el.append('tspan').text(d.name.slice(len-inc));*/
		if(len<d.name.length){
			var tspan = el.append('tspan').text(d.name.slice(0,30));
			tspan.attr('x', 0).attr('dy', 15);
			len = len+inc;
		}
		var tspan = el.append('tspan').text( "....");
		tspan.attr('x', 0).attr('dy', 15);
	});
}
function setMessage(ele, message){
	var ele = $("#"+ele);
	ele.append("<p style='padding:10px;background-color: #f6f6f6;'><b>"+message+"</b></p>");
}

function setFocus(){
	$("#termSearch").focus();
}
$(function() {
	setFocus();
	$( document ).tooltip();
	$("#tabs").tabs({
		activate: function( event, ui ) {
			if(ui.newTab.attr('id') == "hierarchyView"){
				$("#relationDiv").css("display","none");
				$("#relationRadioSelectionDesc").css("display","none");
				
			}
			else{
				$("#relationDiv").css("display","block");
				$("#relationRadioSelectionDesc").css("display","block");
			}
		}
	});
	$("#hierarchyLayoutSelection").on("change",function(){
         var val = $(this).val();
         if(val == 'radial')
        	 dendogramRadial(M2J(conceptMap));
         else if(val == 'tree')
        	 treeLayout(toD3JTreeFormat(searchTerm))
        	 
	});
});