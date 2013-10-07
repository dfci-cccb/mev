'use strict';


// Declare app level module which depends on filters, and services
angular.module('myApp', ['myApp.filters', 'myApp.services', 'myApp.directives', 'myApp.controllers']).
  config(['$routeProvider', function($routeProvider) {
	  
	$routeProvider.when('/home', {
		templateUrl: '/views/partials/home',
		});
	
	$routeProvider.when('/heatmap/:dataset', {
		templateUrl: '/views/partials/heatmap', 
		controller: 'HeatmapCtrl'});
		
    $routeProvider.otherwise({redirectTo: '/home'});

  }])
  .run(['$rootScope', '$http', function($rootScope, $http){
	
	$rootScope.retrieveHeatmaps = function() {

		$http({
				method:"GET",
				url:"heatmap/",
				params: {
					format:"json"
				}
			})
			.success( function(data) {
				$rootScope.menuheatmaplist = data;
			});
	};
	
	$rootScope.sendFile = function() {
			//Variable declarations
		
			
			var input = document.getElementById("datasetfile");
			var box = document.getElementById("progbox"); //DOM element of the growing progress bar
			var bar = document.getElementById("progbar"); //DOM element of the growing progress bar
			var files = [];
			
			for (var i=0; i<input.files.length; i++){
				var formdata = new FormData; //Dynamically create a new form
				formdata.append('filedata', input.files[i]); //Append file object to the form
				formdata.append('format', 'tsv');
				formdata.append('name', input.files[i].name);
				files.push(formdata);
			}

			
			//$scope.output = "Uploading..."; //Alert user that uploading has begun
			
			files.map(function(file, i) {
				
				var xhr = new XMLHttpRequest();
				box.style.visibility = "visible";
				//Event Listener that will provide progress to the user
				xhr.upload.addEventListener("progress", function(e){
					var pc = parseInt((e.loaded / e.total * 100)); //Calculate % completion
					//Update the progress elements with the percent completion quantity.
					bar.style.width = pc + "%";
				});
				//Event Listener that will output the server response (if any) as well as update the user feedback elements to 100% completion.
				xhr.onreadystatechange = function(){
					if (xhr && xhr.readyState == 4 && xhr.status == 200){
						
						$rootScope.$apply(function(){ //$scope.$apply() is used so angular controller can update $scope.output upon the xhr.responseText assignment.
							//$scope.output = xhr.responseText;
							$rootScope.retrieveHeatmaps();
						
						});
						
						bar.style.width = "0%";
						//$scope.uploadName = undefined;
						box.style.visibility = "hidden";
						
					} else if (xhr && xhr.readyState == 4 && xhr.status != 200) {
						
						alert(xhr.status + ": " + xhr.statusText + "\n" + "\n" + "Error Details:" + "\n" + "\n" + xhr.responseText );
						bar.style.width = "0%";
						//$scope.uploadName = undefined;
						box.style.visibility = "hidden";
					};
				};
				//Send the uploaded file.
				xhr.open('POST', '/heatmap', true);
				xhr.send(file);
			})

	};
	
	$rootScope.retrieveHeatmaps();
	
  }]);
