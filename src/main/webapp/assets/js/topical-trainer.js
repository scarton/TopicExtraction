var unsavedData = false;
var cloudTopics = new Array();
var visited = new Array();

function getRandomDoc(targetTitleID, targetId) {
	$.ajax({
		'url' : "getRandomDoc",
		type : 'GET',
		dataType : 'json',
		success : function(data) {
			$("#" + targetTitleID).html(data.title);
			visited.push(data.title);
			getDocText(targetId, data.title);
			makeTagsForDoc("topic-list", data.title)
			$("#" + targetId).scrollTop(0);
		}
	});
}
function getPrevDoc(targetTitleID, targetId) {
	if (visited.length>1) {
		visited.pop(); // top item is always the current ID, so pop back off.
		var id=visited.last();
		("Returning to "+id).flash();
		$.ajax({
			'url' : "getDoc/"+id,
			type : 'GET',
			dataType : 'json',
			success : function(data) {
				$("#" + targetTitleID).html(data.title);
				getDocText(targetId, data.title);
				makeTagsForDoc("topic-list", data.title)
				$("#" + targetId).scrollTop(0);
			}
		});
	} else {
		("No 'previous' document.").flash();
	}
}
function makeCloudArray(data) {
	var tag_list = new Array();
	for (var i = 0; i < data.length; ++i) {
		var x = data[i];
		var h = x.text.hashCode();
		//$.log(x.text+" = "+h);
		cloudTopics[h] = x.text; // collect these topics for use in other functions.
		tag_list.push({
			text : x.text,
			weight : x.weight,
			handlers : {
				click : function() {
					var zz = x;
					return function() {
						assignCloudTopic2Doc(zz.text);
					}
				}()
			},
			html : {
				title : x.weight + " weight"
			}
		});
	}
	return tag_list;
}
function setCloud(cloudID) {
	$.ajax({
		'url' : "getTopicsForCloud",
		type : 'GET',
		dataType : 'json',
		success : function(data) {
			$("#" + cloudID).jQCloud("update",makeCloudArray(data));
		}
	});
}
function assignCloudTopic2Doc(topic) {
	$("#topic-list").tagit("createTag", topic);
}
function makeTagsForDoc(tagId, file) {
	$.ajax({
		'url' : "getTruthFor/" + file,
		type : 'GET',
		dataType : 'json',
		success : function(data) {
			$("#" + tagId).tagit("removeAll");
			for (var i = 0; i < data.length; i++) {
				$.log('setting tag: ' + data[i]);
				$("#" + tagId).tagit("createTag", data[i]);
			}
			unsavedData=false; // these tags are already saved, so clear the unsaved flag, which was set by adding them.
		},
		error : function(jqXHR, textStatus, errorThrown) {
			;
		}
	});
	unsavedData = false;
}
function saveTagsForDoc(file, topics) {
	$.ajax({
		'url' : "setTruthFor/" + file + "?topics=" + topics,
		type : 'GET',
		dataType : 'text',
		success : function(data) {
			$.log("saveTagsForDoc returns: " + data);
			unsavedData = false;
			setCloud("tag-cloud");
			"Topics Saved.".flash();
		},
		error : function(jqXHR, textStatus, errorThrown) {
			("Error saving tags: "+errorThrown).flash();
		}
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
function setupTopicList(additive) {
	$("#topic-list").tagit({
		allowSpaces : true,
		preprocessTag : function(val) {
			var tl = val;
			$.log("Adding tag: " + tl);
			return tl;
		},
		afterTagAdded : function(event, ui) {
			var tl = ui.tagLabel;
			if (additive || cloudTopics[ui.tagLabel.hashCode()]==tl) {
				$.log("Added tag: " + tl);
				unsavedData = true;
			} else {
				$("#topic-list").tagit("removeTagByLabel", ui.tagLabel);
			}
		}
	});
}
function setBindings(binary, additive) {
	$.log("setting bindings, new topics can be created? " + additive);
	$("#prev-button").click(function(){prevDoc();});
	$("#next-button").click(function(){nextDoc();});
	$("#save-button").click(
		function() {
			saveTagsForDoc($("#doc-name").text(), $("#topic-list").tagit("assignedTags"));
			nextDoc();
		});
	if (!binary && additive) {
		$("#additive-message").text("You can create original topics or select from the cloud below.");
	} else {
		$("#additive-message").text("You cannot create new topics, only select topics from the cloud below.");
	}
	
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
