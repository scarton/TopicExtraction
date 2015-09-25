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
        dataType:'html',
        success: function(data){
            $("#"+tagId).html(data).tagit();
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
		alert("Coming...");
	});
}