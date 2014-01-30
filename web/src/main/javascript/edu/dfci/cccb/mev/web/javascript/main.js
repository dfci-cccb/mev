/* Use requirejs to load other modules located under this package
 * 
 * Refer to edu.dfci.cccb.mev.web.configuration.container.ContainerConfigurations
 * for url locations, currently this folder is mapped at /container/javascript
 */

require.config ({
  baseUrl : "/container/javascript",
  paths : {
    jquery : [ 'http://codeorigin.jquery.com/jquery-2.0.3', '/library/webjars/jquery/2.0.3/jquery' ],
    angular : [ 'https://ajax.googleapis.com/ajax/libs/angularjs/1.1.4/angular.min',
               '/library/webjars/angularjs/1.1.4/angular' ],
    bootstrap : [ 'http://netdna.bootstrapcdn.com/twitter-bootstrap/2.3.2/js/bootstrap.min',
                 '/library/webjars/bootstrap/2.3.2/js/bootstrap.min' ],
    d3 : [ '/library/webjars/d3js/3.1.5/d3.min' ],
    dropzone : [ '/library/webjars/dropzone/3.7.1/dropzone-amd-module.min' ],
    retina : [ '/library/webjars/retinajs/0.0.2/retina' ],
    notific8 : [ 'notific8.min' ]
  },
  shim : {
    'angular' : {
      exports : 'angular'
    },
    'd3' : {
      exports : 'd3'
    },
    'bootstrap' : {
      deps : [ 'jquery' ]
    },
    'bootstrap-css' : {
      deps : [ 'bootstrap' ]
    },
    'notific8' : {
      deps : [ 'jquery' ],
      exports : 'notific8'
    }

  },
  waitSeconds : "2"

});

require ([ 'jquery',
          'angular',
          'app',
          'orefine/OrefineBridge',
          'bootstrap-css',
          'bootstrap',
          'setmanager/SetManager',
          'presets/PresetManager',
          'mainpanel/MainPanel',
          'retina' ], function (jquery, angular, app, orb) {

  'use strict';
  var $html = angular.element (document.getElementsByTagName ('html')[0]);
  angular.element ().ready (function () {
    $html.addClass ('ng-app');
    angular.bootstrap ($html, [ app['name'] ]);

  });

  window.OpenRefineBridge = orb;
});
