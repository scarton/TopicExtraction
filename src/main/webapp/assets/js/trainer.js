var unsavedData = false;
var cloudTopics = new Array();;
function getRandomDoc(targetTitleID, targetId) {
	$.ajax({
		'url' : "getRandomDoc",
		type : 'GET',
		dataType : 'json',
		success : function(data) {
			$("#" + targetTitleID).html(data.title);
			getDocText(targetId, data.title);
			makeTagsForDoc("topic-list", data.title)
			$("#" + targetId).scrollTop(0);
//			$("#" + targetId).enscroll('resize');
		}
	});
}
function getDocText(targetId, file) {
	$.ajax({
		'url' : "getDocText/" + file,
		type : 'GET',
		dataType : 'html',
		success : function(data) {
			$("#" + targetId).html(data);
			// makeDocBoxScrollBar(targetId);
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
function makeCloudArray(data) {
	var tag_list = new Array();
	for (var i = 0; i < data.length; ++i) {
		var x = data[i];
		var h = x.text.hashCode();
		$.log(x.text+" = "+h);
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
			$("#" + cloudID).jQCloud(makeCloudArray(data));
		}
	});
}
function assignCloudTopic2Doc(topic) {
	$("#topic-list").tagit("createTag", topic);
}
function makeTagsForDoc(tagId, file) {
	$.ajax({
		'url' : "getTopicsFor/" + file,
		type : 'GET',
		dataType : 'json',
		success : function(data) {
			$("#" + tagId).tagit("removeAll");
			for (var i = 0; i < data.length; i++) {
				$.log('setting tag: ' + data[i]);
				$("#" + tagId).tagit("createTag", data[i]);
			}
		},
		error : function(jqXHR, textStatus, errorThrown) {
			;
		}
	});
	unsavedData = false;
}
function saveTagsForDoc(file, topics) {
	$.ajax({
		'url' : "setTopicsFor/" + file + "?topics=" + topics,
		type : 'GET',
		dataType : 'text',
		success : function(data) {
			$.log("saveTagesForDoc returns: " + data);
			unsavedData = false;
		},
		error : function(jqXHR, textStatus, errorThrown) {
			;
		}
	});

}
function setBindings(additive) {
	$.log("setting bindings, new topics can be created? " + additive);
	$("#next-button").click(
		function() {
			if (unsavedData) {
				if (confirm("You have unsaved changes that will be lost. Please click 'Cancel' and then save your changes. Or click 'Okay' to continue and lose your changes")) {
					getRandomDoc("doc-name", "document-content");
				}
			} else {
				getRandomDoc("doc-name", "document-content");
			}
		});
	$("#save-button").click(
		function() {
			saveTagsForDoc($("#doc-name").text(), $("#topic-list").tagit(
					"assignedTags"));

		});
	$("#topic-list").tagit({
		allowSpaces : true,
		preprocessTag : function(val) {
			var tl = val.toLowerCase();
			$.log("Adding tag: " + tl);
			return tl;
		},
		afterTagAdded : function(event, ui) {
			var tl = ui.tagLabel.toLowerCase();
			if (additive || cloudTopics[ui.tagLabel.hashCode()]==tl) {
				$.log("Added tag: " + tl);
				unsavedData = true;
			} else {
				$("#topic-list").tagit("removeTagByLabel", ui.tagLabel);
			}
		}
	});
	if (additive) {
		$("#additive-message").text("You can create original topics or select from the cloud below.");
	} else {
		$("#additive-message").text("You cannot create new topics, only select topics from the cloud below.");
	}
	
	
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