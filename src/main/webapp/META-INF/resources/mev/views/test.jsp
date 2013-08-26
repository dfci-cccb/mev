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

  <div class="test" id="test.d3"></div>
  <div class="test" id="test.angular"></div>
  
  

  <!--Bootstrap -->
  <script src="/resources/static/webjars/bootstrap/${buildProperties['bootstrap.version']}/${profileProperties['bootstrap.js.source']}"></script>
  <!--D3-->
  <script src="/resources/static/webjars/d3js/${buildProperties['d3.version']}/${profileProperties['d3.source']}"></script>

  <!-- Effeckt -->
  <script src="/resources/static/webjars/jquery/${buildProperties['jquery.version']}/${profileProperties['jquery.source']}"></script>

  <script>window.jQuery || document.write('<script src="/resources/static/webjars/jquery/${buildProperties['jquery.version']}/${profileProperties['jquery.source']}"><\/script>')</script>
  <script src="/resources/static/mev/js/effeckt/modules/off-screen-nav.js"></script>

  <!--Angular-->
  <script src="/resources/static/webjars/angularjs/${buildProperties['angularjs.version']}/${profileProperties['angularjs.source']}"></script>
  <script src="/resources/static/mev/js/app.js"></script>
  <script src="/resources/static/mev/js/services.js"></script>
  <script src="/resources/static/mev/js/controllers.js"></script>
  <script src="/resources/static/mev/js/filters.js"></script>
  <script src="/resources/static/mev/js/directives.js"></script>
  
  <!-- Tests -->
  <script src="/resources/static/mev/js/test.js"></script>
  


  <div><div class="build">Build: ${buildProperties['version']} Commit: ${buildProperties['git.commit.id']}</div></div>
</body>
</html>
