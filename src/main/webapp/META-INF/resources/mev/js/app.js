'use strict';


// Declare app level module which depends on filters, and services
angular.module('myApp', ['myApp.filters', 'myApp.services', 'myApp.directives', 'myApp.controllers']).
  config(['$routeProvider', function($routeProvider) {
	  
	$routeProvider.when('/home', {
		templateUrl: '/views/partials/home'
		});
		
	$routeProvider.when('/about', {
		templateUrl: '/views/partials/about'});
		
	$routeProvider.when('/analyze', {
		templateUrl: '/views/partials/analyze', 
		controller: 'AnalyzeCtrl'});
		
	$routeProvider.when('/features', {
		templateUrl: '/views/partials/features'});
		
	$routeProvider.when('/news', {
		templateUrl: '/views/partials/news'});
		
	$routeProvider.when('/help', {
		templateUrl: '/views/partials/help'});
		
	$routeProvider.when('/tutorial', {
		templateUrl: '/views/partials/tutorial'});

	$routeProvider.when('/heatmap/:matrixLocation', {
		templateUrl: '/views/partials/heatmap', 
		controller: 'HeatmapCtrl'});
		
	$routeProvider.when('/geneselect/:dataset', {
		templateUrl: '/views/partials/genelib', 
		controller: 'GeneSelectCtrl'});
		
    $routeProvider.otherwise({redirectTo: '/home'});

  }]);
