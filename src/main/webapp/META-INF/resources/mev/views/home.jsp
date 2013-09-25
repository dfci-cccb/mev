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
</head>
<body>
  <div class="row">
  </div>
   <!-- Navbar -->

  <div class="navbar navbar-static-top">
    <div class="navbar-inner">
      <a class="brand" href=""><spring:message code="home.title"/></a>
      <ul class="nav pull-right">
        <li>
        <a class="off-screen-nav-button" data-effeckt="effeckt-off-screen-nav-right-overlay">
		<i class="icon-th-list"></i>
		</a></li>
      </ul>
    </div>
  </div>

  <nav class="effeckt-off-screen-nav" id="effeckt-off-screen-nav">
    <h4>
	  Menu
      <a href="" id="effeckt-off-screen-nav-close" class="effeckt-off-screen-nav-close"><i class="icon-resize-small icon-white"></i></a>
    </h4>

    <ul>
      <li><a href="#/analyze"><spring:message code="home.analyze"/></a></li>
      <li><a href="#/features"><spring:message code="home.features"/></a></li>
      <li><a href="#/news"><spring:message code="home.news"/></a></li>
      <li><a href="#/about"><spring:message code="home.about"/></a></li>
      <li><a href="#/help"><spring:message code="home.help"/></a></li>
    </ul>
    
    <h4> Uploads </h4>
    <ul>
      <li ng-repeat="visualization in menuheatmaplist"><a href="#/heatmap/{{visualization}}">{{visualization}}</a></li>
    </ul>
  </nav>

  <!-- View -->

  <div ng-view></div>

  <!-- Footer -->
  <hr>
  <div class="footer">
	<div class="row">
	<div class="span2 offset 1">
	  <a href="http://cccb.dfci.harvard.edu/"><img src="/resources/static/mev/img/cccb-logo.jpg" class="img-polaroid"></a>
	</div>
	<div class="span2 offset1">
		<p class="lead">Product</p>
		<table class="table">
		  <tbody>
			<tr><td><a href="#/analyze"><spring:message code="home.tutorial"/></a></td></tr>
			<tr><td><a href="#/features"><spring:message code="home.features"/></a></td></tr>
			<tr><td><a href="#/news"><spring:message code="home.news"/></a></td></tr>
		  </tbody>
		</table>
	</div>
	<div class="span2 offset1">
	  <p class="lead"><spring:message code="home.community"/></p>
		<table class="table">
		  <tbody>
			<tr><td><a href="https://www.facebook.com/dfci.cccb"><spring:message code="home.facebook"/></a></td></tr>
			<tr><td><a href="https://twitter.com/intent/user?screen_name=CCCBseq"><spring:message code="home.twitter"/></a></td></tr>
			<tr><td><a href="http://cccb.dfci.harvard.edu/cccb/about"><spring:message code="home.contactus"/></a></td></tr>
		  </tbody>
		</table>
	</div>
	</div>
  </div>

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

  <div><div class="build">Build: ${buildProperties['version']} Commit: ${buildProperties['git.commit.id']}</div></div>
</body>
</html>
