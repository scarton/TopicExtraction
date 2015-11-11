<html>
<head>
    <link rel="icon" type="image/gif" href="assets/img/bookfavicon.png">
	<link rel="stylesheet" href="assets/css/foundation.css">
    <link rel="stylesheet" href="assets/css/trainer.css">
    <link rel="stylesheet" href="assets/css/jqcloud.css">
	<link rel="stylesheet" href="assets/css/vendor/jquery-ui.min.css">
    <link rel="stylesheet" href="assets/css/jquery.tagit.css">
    <script src="assets/js/vendor/jquery.js"></script>
    <script src="assets/js/vendor/modernizr.js"></script>
    <script src="assets/js/trainer-base.js"></script>
    <script src="assets/js/binary-trainer.js"></script>
    <script src="assets/js/enscroll-0.6.1.min.js"></script>
    <script src="assets/js/vendor/jquery.log.js"></script>
    <script src="assets/js/vendor/jquery-ui.min.js" type="text/javascript" charset="utf-8"></script>
    <script>
	    $(document).ready(function() {
	    	$.setLogging(true);
            setBindings();
            setProject('${project}');
            setBinaryBindings();
	        getRandomDoc("doc-name","document-content");
	        makeDocBoxScrollBar("document-content");
	    });
    </script>
    
</head>
<body>
	<div class="row fullWidth">
		<div class="large-12 columns">
			<div class="nav-bar right">
				<ul class="button-group">
                    <li><a href="#" id="prev-button" class="button small">Prev</a></li>
					<li><a href="#" id="next-button" class="button small">Next</a></li>
				</ul>
                <div class=""><a href="${root}">Return to Projects List</a></div>
				<div class="flash"></div>
			</div>
			<h1>
				${name}
			</h1>
			<h6>${title}</h6>
			<hr />
		</div>
	</div>


	<div class="row fullWidth">

		<div class="large-9 columns" role="content">
            <h3 id="doc-name"></h3>
			<article>
				<div class="doc-box" id="document-content">
				</div>
			</article>
		</div>

		<aside class="large-3 columns">
			<h5>Classification for this Document</h5>
            <div>
	            <input type="radio" value="affirmative" name="tag" class="tag" id="affirmative" />
	            <label for="affirmative">${affirmativeMessage}</label> 
	            <input type="radio" value="negative" name="tag" class="tag" id="negative" />			
                <label for="negative">${negativeMessage}</label> 
                <label for="reason">Reason?</label> 
	            <input type="text" name="reason" id="reason" />
                <a href="#" id="save-button" class="button small">Save/Next</a>
                <a href="#" id="clear-button" class="button small">Clear</a>
            </div>
		</aside>


	</div>
<%@ include file="/WEB-INF/pages/footer.jsp" %>
</body>
</html>
