ctrl.controller('AnalyzeCtrl', ['$scope', '$rootScope', '$routeParams', '$http', function($scope, $rootScope, $routeParams, $http) {

	$scope.heatmaplist = [];
	
	$scope.retrieveHeatmaps = function() {

		$http({
				method:"GET",
				url:"heatmap/",
				params: {
					format:"json"
				}
			})
			.success( function(data) {
				$scope.heatmaplist = data;
				$rootScope.menuheatmaplist = data;
			});
	};
	
	$scope.output = "Select your file to upload.";
	//Function to upload files
	$scope.sendFile = function() {
			//Variable declarations
			var input = document.getElementById("file");
			var box = document.getElementById("progbox"); //DOM element of the growing progress bar
			var bar = document.getElementById("progbar"); //DOM element of the growing progress bar
			
			var upfile = input.files[0]; //Assign file object of the input file to a variable
			var formdata = new FormData; //Dynamically create a new form
			formdata.append('filedata', upfile); //Append file object to the form
			formdata.append('format', 'tsv');
			if ($scope.uploadName == undefined) {
				$scope.uploadName = "geneset";
			}
			formdata.append('name', $scope.uploadName);
			$scope.output = "Uploading..."; //Alert user that uploading has begun
			//AJAX code
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
				if (xhr.readyState == 4 && xhr.status == 200){
					
					$scope.$apply(function(){ //$scope.$apply() is used so angular controller can update $scope.output upon the xhr.responseText assignment.
						$scope.output = xhr.responseText;
						$scope.retrieveHeatmaps();
					
					});
					
					bar.style.width = "0%";
					$scope.uploadName = undefined;
					box.style.visibility = "hidden";
					
				} else if (xhr.readyState == 4 && xhr.status != 200) {
				
					alert(xhr.status + ": " + xhr.statusText);
					bar.style.width = "0%";
					$scope.uploadName = undefined;
					box.style.visibility = "hidden";
				};
			};
			//Send the uploaded file.
			xhr.open('POST', '/heatmap', true);
			xhr.send(formdata);
			

	};
	
	$scope.retrieveHeatmaps();
	
}]);
