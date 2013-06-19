//Main config page for application
//Module Declaration
var webmev = angular.module('webmev', [
    'webmev.directives',
    'webmev.controllers'
]);

drcts = angular.module('webmev.directives', []);
ctlrs = angular.module('webmev.controllers', []);

//Routing Configuations
webmev.config(function($routeProvider) {
    $routeProvider
        .when('/home',
            {
				//controller: '/app/controllers/HomeController',
				templateURL: 'app/partials/view_home.html'
			})
		.when('/heatmap',
		    {
				//controller: '/app/controllers/HeatmapController',
				templateUrl: 'app/partials/view_heatmap.html'
			})
		.otherwise(
		    {
				redirectTo: '/home'
			});
	
});

