define(['angular', 'directives', 'services', 'controllers', 'setmanager/SetManager'], function(angular){
	'use strict';
	return angular.module('myApp', [
	     'myApp.directives', 
	     'myApp.services',
	     'myApp.controllers',
	     'Mev.SetManager',
	     'Mev.PresetManager'])
	.config(['$routeProvider', '$locationProvider', function($routeProvider, $locationProvider) {
	  
	  $routeProvider
		  .when('/dataset', {
			  templateUrl: '/container/view/partials/heatmap', 
			  controller: 'HeatmapCtrl'
		  })
		  .when('/dataset/:datasetName', {
			  templateUrl: '/container/view/partials/heatmap', 
			  controller: 'HeatmapCtrl'
		  })
		  .when('/home', {
              templateUrl: '/container/view/partials/importItems',
              controller: 'ImportsCtrl'
          });
		  
		  $routeProvider.otherwise({redirectTo: '/home'});
		  
		  //$locationProvider.html5Mode(true);
		  
		}]);
	
});