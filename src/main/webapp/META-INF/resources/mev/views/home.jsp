<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>

<!doctype html>
<html lang="en" ng-app="myApp" ng-controller="GlobalCtrl">
<head>
  <meta charset="utf-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title><spring:message code="home.title"/></title>

  <!-- Log4javascript -->
  <script src="/resources/static/webjars/log4javascript/${buildProperties['log4javascript.version']}/${profileProperties['log4javascript.source']}"></script>
  <script src="${clientLoggingProperties['log4javascript.mapping']}"></script>

  <!-- Retina JS -->
  <script src="/resources/static/webjars/retinajs/${buildProperties['retinajs.version']}/${profileProperties['retinajs.source']}"></script>

  <!-- Bootstrap -->
  <link rel="stylesheet" href="/resources/static/webjars/bootstrap/${buildProperties['bootstrap.version']}/${profileProperties['bootstrap.css.source']}"/>
  <link rel="stylesheet" href="/resources/static/webjars/bootstrap/${buildProperties['bootstrap.version']}/css/bootstrap-responsive.css"/>

  <!-- Effeckt -->
  <link rel="stylesheet" href="/resources/static/mev/css/effeckt/demo/demo.autoprefixed.css">
  <link rel="stylesheet" href="/resources/static/mev/css/effeckt/modules/off-screen-nav.css"/>
  <link rel="stylesheet" href="/resources/static/mev/css/app.css"/>
  
  <!-- AMRF css -->
  <link rel="stylesheet" href="/resources/static/mev/css/amrf.css"/>
</head>
<body class="ania">
  <div class="row">
  </div>
   <!-- Navbar -->

  <div class="navbar navbar-static-top" id="heading">
  	<div class="site-title">
  		<a class="logo custom-logo" href="https://dev.amrf-portal.org/"> <img alt="Adelson Medical Research Foundation" src="/resources/static/mev/img/spacer.png" width="453"> </a>
  		<a class="" id="back_to_amrf" href="https://dev.amrf-portal.org/">Back to AMRF Portal</a>
  	</div>  	  	
    <div class="navbar-inner">
      <a class="brand" href=""><spring:message code="home.title"/></a>
      <ul class="nav pull-right">
      	<li>
      	<a class="off-screen-nav-button" data-effeckt="effeckt-off-screen-nav-right-overlay">        
		Click here to begin:
		</a>
		</li>
        <li>
        <a class="off-screen-nav-button" data-effeckt="effeckt-off-screen-nav-right-overlay">
		<i class="icon-th-list"></i>
		</a></li>
      </ul>
    </div>
  </div>

  <nav class="effeckt-off-screen-nav effeckt-off-screen-nav-right-overlay effeckt-off-screen-nav-show" id="effeckt-off-screen-nav">
    <h4>
	  Menu
      <a href="" id="effeckt-off-screen-nav-close" class="effeckt-off-screen-nav-close"><i class="icon-remove icon-white"></i></a>
    </h4>

    <ul>
	  <li><a data-target="#UploadModal" data-toggle="modal">Upload New File</a></li>
    </ul>
    
    <h4> Datasets </h4>
    <ul>
      <li ng-repeat="visualization in menuheatmaplist"><a href="#/heatmap/{{visualization}}">{{visualization}}</a></li>
    </ul>
  </nav>

  <!-- View -->

 <div id="content">
  	<div ng-view></div>
 </div>  
  <!-- Footer -->
  <hr>
  <div class="footer">
	<div class="row">
	</div>
  </div>    
  <!-- Modals -->
  
	<div id="UploadModal" class="modal hide fade" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">

	  <div class="modal-header">
		<button type="button" class="close" data-dismiss="modal" aria-hidden="true"><i class="icon-remove"></i></button>
		<h3 id="myModalLabel">Upload File</h3>
	  </div>
	  
	  <div class="modal-body">
	  
			<div id="fileup">
				
				<div class="span3">
				
					<div class="row">
						<input id="datasetfile" type="file" multiple name="upload" />
					</div>
					
					<div class="row">
						<div class="progress progress-striped active" id="progbox" style="visibility: hidden;">
							<div class="bar" id="progbar"></div>
							
						</div>
					</div>
					
				</div>
				
			</div> 

	  </div>
	  
	  <div class="modal-footer">
		<button class="btn" aria-hidden="true" ng-click="sendFile()">Upload</button>
		<button class="btn btn-primary"  data-dismiss="modal" aria-hidden="true">Close</button>
	  </div>
	  
	</div>
	
	
  <!-- End Modals -->
 
  <script>window.jQuery || document.write('<script src="/resources/static/webjars/jquery/${buildProperties['jquery.version']}/${profileProperties['jquery.source']}"><\/script>')</script>
  
  <!--D3-->
  <script src="/resources/static/webjars/d3js/${buildProperties['d3.version']}/${profileProperties['d3.source']}"></script>

  <!-- Effeckt -->
  <script src="/resources/static/webjars/jquery/${buildProperties['jquery.version']}/${profileProperties['jquery.source']}"></script>
  <script src="/resources/static/mev/js/effeckt/modules/off-screen-nav.js"></script>
  
  <!--Bootstrap -->
  <script src="/resources/static/webjars/bootstrap/${buildProperties['bootstrap.version']}/${profileProperties['bootstrap.js.source']}"></script>

  <!--Angular-->
  <script src="/resources/static/webjars/angularjs/${buildProperties['angularjs.version']}/${profileProperties['angularjs.source']}"></script>
  <script src="/resources/static/mev/js/app.js"></script>
  <script src="/resources/static/mev/js/services.js"></script>
  <script src="/resources/static/mev/js/controllers.js"></script>
  <script src="/resources/static/mev/js/filters.js"></script>
  <script src="/resources/static/mev/js/directives.js"></script>
  
  <!-- Dropzone -->
  <script src="/resources/static/webjars/dropzone/${buildProperties['dropzone.version']}/${profileProperties['dropzone.source']}"></script>

<!-- 
  <div><div class="build">Build: ${buildProperties['version']} Commit: ${buildProperties['git.commit.id']}</div></div>
-->
  <div class="copyright">© Copyright Adelson Medical Research Foundation 2013</div>
</body>
</html>
