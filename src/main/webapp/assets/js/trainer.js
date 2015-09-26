var unsavedData=false;
function getRandomDoc(targetTitleID,targetId) {
    $.ajax({
        'url':"getRandomDoc",
        type:'GET',
        dataType:'json',
        success: function(data){
        	$("#"+targetTitleID).html(data.title);
        	getDocText(targetId, data.title);
    		makeTagsForDoc("topic-list",data.title)
        }
    });    
}
function getDocText(targetId, file) {
    $.ajax({
        'url':"getDocText/"+file,
        type:'GET',
        dataType:'html',
        success: function(data){
        	$("#"+targetId).html(data);
        	//makeDocBoxScrollBar(targetId);
        }
    });    
}
function makeDocBoxScrollBar(targetId) {
	$("#"+targetId).enscroll({
	    showOnHover: true,
	    verticalTrackClass: 'track3',
	    verticalHandleClass: 'handle3'
	});        
}
function makeCloudArray(data) {
	var tag_list = new Array();
	for ( var i = 0; i < data.length; ++i ) 
	{
			var x = data[i];
			tag_list.push({
					text: x.text,
					weight: x.weight,
		            handlers : {click: function() { 
		                var zz = x;
		                return function() {
		                	assignCloudTopic2Doc(zz.text);
		                }
		            }()},
					html: {title: x.weight + " weight"}
			});
	}
	return tag_list;
}
function setCloud(cloudID) {
    $.ajax({
        'url':"getTopicsForCloud",
        type:'GET',
        dataType:'json',
        success: function(data){
            $("#"+cloudID).jQCloud(makeCloudArray(data));
        }
    });    
}
function assignCloudTopic2Doc(topic) {
	$("#topic-list").tagit("createTag", topic);
}
function makeTagsForDoc(tagId,file) {
    $.ajax({
        'url':"getTopicsFor/"+file,
        type:'GET',
        dataType:'json',
        success: function(data){
        	$("#"+tagId).tagit("removeAll");
        	for (var i = 0; i < data.length; i++) {
        		$.log('setting tag: ' + data[i]);
        		$("#"+tagId).tagit("createTag", data[i]);
        	}
        },
        error: function(jqXHR,textStatus,errorThrown) {
        	;
        }
    });    
}
function saveTagsForDoc(file, topics) {
    $.ajax({
        'url':"setTopicsFor/"+file+"?topics="+topics,
        type:'GET',
        dataType:'text',
        success: function(data){
        	$.log("saveTagesForDoc returns: "+data);
        	unsavedData=false;
        },
        error: function(jqXHR,textStatus,errorThrown) {
        	;
        }
    });    
	
}
function setBindings() {
	$("#next-button").click(function() {
		if (unsavedData) {
			if (confirm("You have unsaved changes that will be lost. Please click 'Cancel' and then save your changes. Or click 'Okay' to continue and lose your changes")) {
				getRandomDoc("doc-name","document-content");
			}
		} else {
			getRandomDoc("doc-name","document-content");
		}
	});
	$("#save-button").click(function() {
		saveTagsForDoc($("#doc-name").text(), $("#topic-list").tagit("assignedTags"));
		
	});
	$("#topic-list").tagit({
    	allowSpaces : true,
    	beforeTagAdded : function(event, ui) {
    		unsavedData=true;
    	}
	})
}