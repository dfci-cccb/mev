ctrl.controller('AnalyzeCtrl', ['$scope', '$routeParams', '$http', function($scope, $routeParams, $http) {

	$scope.heatmaplist = [];
	
	$scope.retrieveHeatmaps = function() {

		alert('before get heatmap/');	
		$http({
				method:"GET",
				url:"heatmap/",
				params: {
					format:"json"
				}
			})
			.success( function(data) {
				$scope.heatmaplist = data
				alert($scope.heatmaplist);
			});
	};
	
	$scope.output = "Select your file to upload.";
	//Function to upload files
	$scope.sendFile = function() {
			//Variable declarations
			var input = document.getElementById("file");
			var bar = document.getElementById("bar"); //DOM element of the growing progress bar
			var percent = document.getElementById("percent"); //Value reported inside progress bar
			var upfile = input.files[0]; //Assign file object of the input file to a variable
			var formdata = new FormData; //Dynamically create a new form
			formdata.append('filedata', upfile); //Append file object to the form
			formdata.append('format', 'tsv');
			formdata.append('name', 'tsv');
			$scope.output = "Uploading..."; //Alert user that uploading has begun
			//AJAX code
			var xhr = new XMLHttpRequest();
			//Event Listener that will provide progress to the user
			xhr.upload.addEventListener("progress", function(e){
				var pc = parseInt((e.loaded / e.total * 100)); //Calculate % completion
				//Update the progress elements with the percent completion quantity.
				bar.style.width = pc + "%";
				percent.innerHTML = pc + "%";
			});
			//Event Listener that will output the server response (if any) as well as update the user feedback elements to 100% completion.
			xhr.onreadystatechange = function(){
				if (xhr.readyState == 4 && xhr.status == 200){
					$scope.$apply(function(){ //$scope.$apply() is used so angular controller can update $scope.output upon the xhr.responseText assignment.
						$scope.output = xhr.responseText;
						$scope.retrieveHeatmaps();
					});
					bar.style.width = "100%";
					percent.innerHTML = "100%";
					alert('send completed');
					
				};
			};
			//Send the uploaded file.
			xhr.open('POST', '/heatmap', true);
			alert('before send');
			xhr.send(formdata);
			

	};
	
	$scope.retrieveHeatmaps();
	
}]);