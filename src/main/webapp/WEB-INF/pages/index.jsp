<html>
<head>
    <link rel="icon" type="image/gif" href="assets/img/bookfavicon.png">
    <link rel="stylesheet" href="assets/css/foundation.css">
    <link rel="stylesheet" href="assets/css/trainer.css">
    <link rel="stylesheet" href="assets/css/vendor/jquery-ui.min.css">
    <script src="assets/js/vendor/jquery.js"></script>
    <script src="assets/js/vendor/modernizr.js"></script>
    <script src="assets/js/trainer-base.js"></script>
    <script src="assets/js/enscroll-0.6.1.min.js"></script>
    <script src="assets/js/vendor/jquery.log.js"></script>
    <script src="assets/js/vendor/jquery-ui.min.js" type="text/javascript" charset="utf-8"></script>
    <script>
        $(document).ready(function() {
        	getWorkspaces();
        });
    </script>
    
</head>
<body>
    <div class="row fullWidth">
            <h1>
                Workspace Selection or Creation
            </h1>
    </div>
    <div class="row fullWidth">
        <div class="large-4 columns">
            <div class="side-nav left">
                <h6>Click to select a workspace</h6>
                <ul class="side-nav" id="workspaces" />
                <div class="flash"></div>
            </div>
        </div>
        <div class="large-8 columns">
			<form>
			  <fieldset>
			    <legend>Project Attributes</legend>
			
			    <div class="row">
			      <div class="large-4 columns">
			        <label>Project</label>
			        <input type="text" id="project-id" placeholder="Unique Project ID">
			      </div>
			      <div class="large-4 columns">
			        <label>Name</label>
			        <input type="text" id="project-name" placeholder="Project Name">
			      </div>
			      <div class="large-4 columns">
			        <div class="row collapse">
			          <label>Input Label</label>
			          <div class="small-9 columns">
			            <input type="text" placeholder="small-9.columns">
			          </div>
			          <div class="small-3 columns">
			            <span class="postfix">.com</span>
			          </div>
			        </div>
			      </div>
			    </div>
			    <div class="row">
			      <div class="large-12 columns">
			        <label>Title</label>
			        <input type="text" id="project-title" placeholder="Project Title">
			      </div>
			    </div>
			  </fieldset>
			</form>
        </div>
    </div>

<%@ include file="/WEB-INF/pages/footer.jsp" %>
</body>
</html>
