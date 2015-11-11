var unsavedData = false;
var visited = new Array();
var currentGuid;
var project;

function setProject(p) {
	project = p;
}
function getDocText(targetId, file) {
	$.ajax({
		'url' : project+"/getDocText/" + file,
		type : 'GET',
		dataType : 'html',
		success : function(data) {
			$("#" + targetId).html(data);
		}
	});
}
function makeDocBoxScrollBar(targetId) {
	$("#" + targetId).enscroll({
		showOnHover : true,
		verticalTrackClass : 'track3',
		verticalHandleClass : 'handle3'
	});
}
function prevDoc() {
	if (unsavedData) {
		if (confirm("You have unsaved changes that will be lost. Please click 'Cancel' and then save your changes. Or click 'Okay' to continue and lose your changes")) {
			getPrevDoc("doc-name", "document-content");
		}
	} else {
		getPrevDoc("doc-name", "document-content");
	}
}
function nextDoc() {
	if (unsavedData) {
		if (confirm("You have unsaved changes that will be lost. Please click 'Cancel' and then save your changes. Or click 'Okay' to continue and lose your changes")) {
			getRandomDoc("doc-name", "document-content");
		}
	} else {
		getRandomDoc("doc-name", "document-content");
	}
}
function copyReason() {
	var selectedText = "";
	if (window.getSelection){
		selectedText = window.getSelection();
	}else if (document.getSelection){
	    selectedText = document.getSelection();
	}else if (document.selection){
	    selectedText = document.selection.createRange().text;
	} 
	if (selectedText != ""){
	    selectedText = selectedText.toString();
	}
	return selectedText;
}
function setBindings() {
	$("#prev-button").click(function(){prevDoc();});
	$("#next-button").click(function(){nextDoc();});
	
	$(window).bind('keydown', function(event) {
	    if (event.ctrlKey || event.metaKey) {
	        switch (String.fromCharCode(event.which).toLowerCase()) {
	        case 's':
	            event.preventDefault();
				saveTagsForDoc($("#doc-name").text(), $("#topic-list").tagit("assignedTags"));
	            break;
	        }
	    } else {
//	        switch (event.which) {
//	        case 39: // right
//	            event.preventDefault();
//	            nextDoc();
//	            break;
//	        case 37: // left
//	            event.preventDefault();
//	            prevDoc();
//	            break;
//	    	case 38: // up
//	            event.preventDefault();
//	            break;
//            case 40: // down
//	            event.preventDefault();
//	            break;
//	        }
	    }
	});
}

function getWorkspaces() {
	$.ajax({
		'url' : "getWorkspaces",
		type : 'GET',
		dataType : 'html',
		success : function(data) {
			$("#workspaces").html(data);
			setWorkspaceLinks();
		}
	});
}

function setWorkspaceLinks() {
	$(".workspace-link").click(function() {
		var ws = $(this).attr("id");
		window.location.href=ws;
	})
}

String.prototype.hashCode = function() {
	var hash = 0, i, chr, len;
	if (this.length == 0)
		return hash;
	for (i = 0, len = this.length; i < len; i++) {
		chr = this.charCodeAt(i);
		hash = ((hash << 5) - hash) + chr;
		hash |= 0; // Convert to 32bit integer
	}
	return hash;
};
String.prototype.flash = function(){
	$(".flash").html(this).fadeIn("fast");
	setTimeout(function(){
		$(".flash").fadeOut("slow");
	}, 3500);
}
Array.prototype.last = function(){
    return this[this.length - 1];
};
