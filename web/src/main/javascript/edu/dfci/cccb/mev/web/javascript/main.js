/* Use requirejs to load other modules located under this package
 * 
 * Refer to edu.dfci.cccb.mev.web.configuration.container.ContainerConfigurations
 * for url locations, currently this folder is mapped at /container/javascript
 */

require
    .config ({
      baseUrl : "/container/javascript",
      paths : {
        jquery : [ 'http://codeorigin.jquery.com/jquery-2.0.3',
            '/library/webjars/jquery/2.0.3/jquery' ],
        angular : [
            'https://ajax.googleapis.com/ajax/libs/angularjs/1.1.4/angular.min',
            '/library/webjars/angularjs/1.1.4/angular' ],
        bootstrap : [
            'http://netdna.bootstrapcdn.com/twitter-bootstrap/2.3.2/js/bootstrap.min',
            '/library/webjars/bootstrap/2.3.2/js/bootstrap.min' ],
        d3: ['/library/webjars/d3js/3.1.5/d3.min']
      },
      shim : {
        'angular' : {
          exports : 'angular'
        },
        'd3':{
          exports:'d3'
        },
        'bootstrap' : {
          deps : [ 'jquery' ]
        },
        'bootstrap-css' : {
          deps : [ 'bootstrap' ]
        },
        'newick':{
          exports: 'newick'
        }

      },
      waitSeconds : "2"

    });

require ([ 'angular', 'app', 'jquery', 'bootstrap-css', 'bootstrap' ],
    function (angular, app, jquery) {

      'use strict';
      var $html = angular.element (document.getElementsByTagName ('html')[0]);
      angular.element ().ready (function () {
        $html.addClass ('ng-app');
        angular.bootstrap ($html, [ app['name'] ]);
      });

    });
