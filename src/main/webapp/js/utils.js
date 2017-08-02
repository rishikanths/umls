
function add2Map(map,key,value){
	
	if(value.constructor==Array){
		var t = map.get(key);
		if(t == null)
			t = [];
		for(var i in value){
			if($.inArray(value[i].name, t) == -1)
				t.push(value[i].name);
		}
		map.set(key,t);
	}else{
		map.set(key,value);
	}
	return map;
}

function arr2String(value){
	var str = "";
	for(var i in value){
		str = str+value[i]+" ";
	}
	return str;
}

function setFocus() {
	$("#termSearch").focus();
}

function setMessage(ele, message) {
	$("#" + ele).append("<p style='padding:10px;background-color: #f6f6f6;'><b>"+ message + "</b></p>");
}

function displayError(error) {
	$("#error").empty();
	$("#error").append("<p>" + error + "</p>");
	$("#error").css('display', 'block');
}

function displayMessage(msg) {
	$("#message").empty();
	$("#message").append("<p>" + msg + "</p>");
	$("#message").css('display', 'block');
}

function resetGlobalVariable(){
	
	processedConcepts = [];
	unProcessedConcepts = [];
	
	$("#hierarchyMessage").empty();
	information.reset();
	dialogs.closeInfoDialog();
	dialogs.closeDefinitionDialog();
	setFocus();
}

function formatName(name){
	return name.replace(/_/g," ");
}
function reFormatName(name){
	return name.replace(/ /g,"_");
}
