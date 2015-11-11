var cloudTopics = new Array();

function getRandomDoc(targetTitleID, targetId) {
	$.ajax({
		'url' : project+"/getRandomDoc",
		type : 'GET',
		dataType : 'json',
		success : function(data) {
			currentGuid=data.guid;
			$("#" + targetTitleID).html(data.title);
			visited.push(data.title);
			getDocText(targetId, data.guid);
			makeTagsForDoc("topic-list", data.guid)
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
			'url' : project+"/getDoc/"+id,
			type : 'GET',
			dataType : 'json',
			success : function(data) {
				currentGuid=data.guid;
				$("#" + targetTitleID).html(data.title);
				getDocText(targetId, data.guid);
				makeTagsForDoc("topic-list", data.guid)
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
		'url' : project+"/getTopicsForCloud",
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
function makeTagsForDoc(tagId, guid) {
	$.ajax({
		'url' : project+"/getTruthFor/" + guid,
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
function saveTagsForDoc(guid, topics) {
	$.ajax({
		'url' : project+"/setTruthFor/" + guid + "?topics=" + topics,
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
function setTopicalBindings(additive) {
	$.log("setting bindings, new topics can be created? " + additive);
	$("#save-button").click(
		function() {
			saveTagsForDoc(currentGuid, $("#topic-list").tagit("assignedTags"));
			nextDoc();
		});
	if (additive) {
		$("#additive-message").text("You can create original topics or select from the cloud below.");
	} else {
		$("#additive-message").text("You cannot create new topics, only select topics from the cloud below.");
	}
}
