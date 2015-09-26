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

function setCloud(cloudID) {
    $.ajax({
        'url':"getTopicsForCloud",
        type:'GET',
        dataType:'json',
        success: function(data){
            $("#"+cloudID).jQCloud(data);
            $(".cloud-word").click(function() {
            	alert("Clicked on "+this.text);
            });
        }
    });    
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
        },
        error: function(jqXHR,textStatus,errorThrown) {
        	;
        }
    });    
	
}
function setBindings() {
	$("#next-button").click(function() {
		getRandomDoc("doc-name","document-content");
	});
	$("#save-button").click(function() {
		saveTagsForDoc($("#doc-name").text(), $("#topic-list").tagit("assignedTags"));
		
	});
	$("#topic-list").tagit({
    	allowSpaces : true
	})
}