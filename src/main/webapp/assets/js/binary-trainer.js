function getRandomDoc(targetTitleID, targetId) {
	$.ajax({
		'url' : "getRandomDoc",
		type : 'GET',
		dataType : 'json',
		success : function(data) {
			currentGuid=data.guid;
			$("#" + targetTitleID).html(data.title);
			visited.push(data.guid);
			getDocText(targetId, data.guid);
			getTagForDoc(data.guid);
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
				getDocText(targetId, data.guid);
				getTagForDoc(data.guid);
				currentGuid=data.guid;
				$("#" + targetId).scrollTop(0);
			}
		});
	} else {
		("No 'previous' document.").flash();
	}
}
function getTagForDoc(id) {
	$.ajax({
		'url' : "getTagFor/" + id,
		type : 'GET',
		dataType : 'json',
		success : function(data) { 
			if (data.length>0) {
				$('#'+data[0].tag).prop('checked',true);
				$("#reason").val(data[0].reason);
			}
			unsavedData=false; // these tags are already saved, so clear the unsaved flag, which was set by adding them.
		},
		error : function(jqXHR, textStatus, errorThrown) {
			;
		}
	});
	unsavedData = false;
}
function saveTagForDoc(id, procon, reason) {
	$.ajax({
		'url' : "setTagFor/" + id + "?tag=" + procon + "&reason=" + reason,
		type : 'GET',
		dataType : 'text',
		success : function(data) {
			$.log("saveTagsForDoc returns: " + data);
			unsavedData = false;
			"Tag/Reason Saved...".flash();
		},
		error : function(jqXHR, textStatus, errorThrown) {
			("Error saving tags: "+errorThrown).flash();
		}
	});
}
function clear() {
	$("#reason").val("");
	unsavedData = false;
	$('input[name="tag"]').prop('checked', false);
}
function setBinaryBindings() {
	$("#save-button").click(function() {
		saveTagForDoc(currentGuid, $('input[name="tag"]:checked').val(), $("#reason").val());
		clear();
	});
	$("#clear-button").click(function() {
		clear();
	});
	$(".tag").change(function() {
		unsavedData = true;
	});
	$("#document-content").mouseup(function() {
//		$.log("Reason Value Length: "+$("#reason").val().length);
		if ($("#reason").val()=="") {
			$("#reason").val(copyReason());
			unsavedData = true;
		}
	});
}
