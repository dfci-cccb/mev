ctrl.controller('HeatmapCtrl', ['$scope', '$routeParams', '$http', function($scope, $routeParams, $http) {

	$scope.matrixlocation = $routeParams.matrixLocation;
	$scope.curstartrow = 0;
	$scope.curendrow = 80;
	$scope.curstartcol = 0;
	$scope.curendcol = 80;
	var firstpull = true;
	var pullallow = true;
	$scope.matrixsummary = undefined;
	$scope.selectionname = undefined;
	$scope.analyzeOptions = ["LIMMA", "EuclideanClustering"]
	
	$scope.selectedcells = new Object();

	$scope.selections = new Object();
	
	$http({
		method:"PUT",
		url:"heatmap/"+$scope.matrixlocation+"/summary/",
		params: {
			format:"json",
		}
	})
	.success( function(data) {
		$scope.matrixsummary = data;
		
		
		$scope.pullSelections("column");
		$scope.pullSelections("row");
	});
	
	$scope.analyzeEuclideanRequester = function() {
		
		$http({
			method:"GET",
			url:"heatmap/"+$scope.matrixlocation+"/analysis/EuclidianClustering" + "/" + $scope.EuclideanSelection,
			params: {
				format:"json",
				
			}
		})
		.success( function(data) {
			Alert("Success! Please wait for your analysis to complete.")
		})
		.error( function(data) {
		    alert("Something went wrong, please contact us if the problem persists.");	
		});
		
	}
	
	$scope.analyzeLimmaRequester = function() {

		$http({
			method:"GET",
			url:"heatmap/"+$scope.matrixlocation+"/analysis/limma" 
				+ "(" + $scope.LimmaSelection1 + "," + $scope.LimmaSelection2 + ")"
				+ "/" + $scope.LimmaOutputOption,
			params: {
				format:"json",
				
			}
		})
		.success( function(data) {
			Alert("Success! Please wait for your analysis to complete.")
		})
		.error( function(data) {
		    alert("Something went wrong, please contact us if the problem persists.");	
		});
		
	}
	
	$scope.pageUp = function() {
		
		if ($scope.curstartrow == 0) {
			return;
		}
		
		--$scope.curstartrow;
		--$scope.curendrow;
		$scope.pullPage();
		
	}
	
	$scope.pageDown = function() {

		if (($scope.matrixsummary.rows - 1) == $scope.curendrow) {
			return;
		}
		
		++$scope.curstartrow;
		++$scope.curendrow;
		$scope.pullPage();
		
	}
	
	$scope.pageLeft = function() {
		
		if ($scope.curstartcol == 0) {
			return;
		}
		
		--$scope.curstartcol;
		--$scope.curendcol;
		$scope.pullPage();
		
	}
	
	$scope.pageRight = function() {
		
		if (($scope.matrixsummary.columns - 1) == $scope.curendcol) {
			return;
		}
		
		++$scope.curstartcol;
		++$scope.curendcol;
		$scope.pullPage();
		
	}
	
	$scope.transformData = function() {
		
		if (!pullallow) {
			return;
		}
		
		if (!$scope.heatmapcells || !$scope.heatmaprows || !$scope.heatmapcolumns) {
			return;
		}
		
		if (firstpull) {
			if ($scope.heatmapcells.length < 1600) {
				pullallow = false;
			}
		} else {
			if ($scope.heatmapcells.length < 1600) {
				
				return;
			}
		}

		$scope.transformeddata = {data:[]};
		
		$scope.transformeddata.data = $scope.heatmapcells.map(function(cell, index){
			
			return {
				value: cell,
				row: $scope.heatmaprows[Math.floor(index/($scope.heatmapcolumns.length))]['value'],
				col: $scope.heatmapcolumns[index%($scope.heatmapcolumns.length)]['value']
			}
			
		})
		
		$scope.transformeddata.columnlabels = $scope.heatmapcolumns.map(function(d) { return d.value;});
		$scope.transformeddata.rowlabels = $scope.heatmaprows.map(function(d) { return d.value;});
		$scope.transformeddata.matrixsummary = $scope.matrixsummary;

		firstpull = false;

	};
	
	$scope.pullSelections = function(inputdimension) {

		$http({
			method:"GET",
			url:"heatmap/"+$scope.matrixlocation+"/selection/" + inputdimension,
			params: {
				format:"json"
			}
		})
		.success( function(data) {
			$scope.selections[inputdimension] = data;
		});

	};

	$scope.pushSelections = function(dimension) {
		
		if ($scope.selectedcells[dimension]) {
			
			$http({
				method:"PUT",
				url:"heatmap/"+$scope.matrixlocation+"/selection/"+dimension+"/"+$scope.selectionname,
				params: {
					format:"json"
				},
				data: '{"attributes": {"name":"'+ $scope.selectionname +'"}, "indices":['+$scope.selectedcells[dimension]+ ']}'
			})
			.success( function(data) {
				alert("Added!");
				
				$scope.pullSelections(dimension);
				$scope.selectionname = undefined;
				
			});
		}
		
		
	};
	
	//pull page function
	$scope.pullPage = function() {

		if (!$scope.matrixlocation) {
			return;
		}
		
		$scope.heatmapcells = null;
		$scope.heatmaprows = null;
		$scope.heatmapcolumns = null;

		$http({
			method:"GET",
			url:"heatmap/"+$scope.matrixlocation+"/data/"+"["+$scope.curstartrow+":"
				+$scope.curendrow+","+$scope.curstartcol+":"+$scope.curendcol+"]",
			params: {
				format:"json"
			}
		})
		.success( function(data) {
			$scope.heatmapcells = data.values;
			$scope.transformData();
		});
		
		$http({
			method:"GET",
			url:"heatmap/"+$scope.matrixlocation+"/annotation/column",
			params: {
				format:"json"
			}
		})
		.success( function(data) {
		
			$scope.heatmapcolumnannotations = data;
				
			$http({
				method:"GET",
				url:"heatmap/" + $scope.matrixlocation + "/annotation/column/" + $scope.curstartcol + "-" + $scope.curendcol + "/" + data[0],
				params: {
					format:"json"
				}
			})
			.success( function(columnData) {
				$scope.heatmapcolumns = columnData;
				$scope.transformData();
			});

			
		});
		
		$http({
			method:"GET",
			url:"heatmap/"+$scope.matrixlocation+"/annotation/row",
			params: {
				format:"json"
			}
		})
		.success( function(data) {
		
			$scope.heatmaprowannotations = data;
			var heatmaprowshold = [];
			
			$http({
					method:"GET",
					url:"heatmap/"+$scope.matrixlocation+"/annotation/row/" + $scope.curstartrow + "-" + $scope.curendrow + "/" + data[0],
					params: {
						format:"json"
					}
			})
			.success( function(rowData) {
					$scope.heatmaprows = rowData;
					$scope.transformData();
			});
			
		});
		
			

		
	};
	
	//Initial call for values
	
	$scope.pullPage();
	
	
	$scope.sendRowFile = function() {
			//Variable declarations
		
			
			var input = document.getElementById("file");
			var box = document.getElementById("progbox"); //DOM element of the growing progress bar
			var bar = document.getElementById("progbar"); //DOM element of the growing progress bar
			log.debug ("uploading files", input, input.files, input.files.length);
			var files = [];
			
			for (var i=0; i<input.files.length; i++){
				var formdata = new FormData; //Dynamically create a new form
				formdata.append('filedata', input.files[i]); //Append file object to the form
				formdata.append('format', 'tsv');
				formdata.append('name', input.files[i].name);
				files.push(formdata);
			}

			
			$scope.output = "Uploading..."; //Alert user that uploading has begun
			
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
						
						$scope.$apply(function(){ //$scope.$apply() is used so angular controller can update $scope.output upon the xhr.responseText assignment.
							$scope.output = xhr.responseText;
						});
						
						log.debug ("upload complete");
						
						bar.style.width = "0%";
						$scope.uploadName = undefined;
						box.style.visibility = "hidden";
						
					} else if (xhr && xhr.readyState == 4 && xhr.status != 200) {
					
						alert(xhr.status + ": " + xhr.statusText);
						bar.style.width = "0%";
						$scope.uploadName = undefined;
						box.style.visibility = "hidden";
					};
				};
				//Send the uploaded file.
				xhr.open('POST', '/heatmap/' + $scope.matrixlocation + "/annotation/" + "row", true);
				xhr.send(file);
			})
				
	};
	
	$scope.sendColFile = function() {
			//Variable declarations
		
			
			var input = document.getElementById("file");
			var box = document.getElementById("progbox"); //DOM element of the growing progress bar
			var bar = document.getElementById("progbar"); //DOM element of the growing progress bar
			log.debug ("uploading files", input, input.files, input.files.length);
			var files = [];
			
			for (var i=0; i<input.files.length; i++){
				var formdata = new FormData; //Dynamically create a new form
				formdata.append('filedata', input.files[i]); //Append file object to the form
				formdata.append('format', 'tsv');
				formdata.append('name', input.files[i].name);
				files.push(formdata);
			}

			
			$scope.output = "Uploading..."; //Alert user that uploading has begun
			
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
						
						$scope.$apply(function(){ //$scope.$apply() is used so angular controller can update $scope.output upon the xhr.responseText assignment.
							$scope.output = xhr.responseText;
						});
						
						log.debug ("upload complete");
						
						bar.style.width = "0%";
						$scope.uploadName = undefined;
						box.style.visibility = "hidden";
						
					} else if (xhr && xhr.readyState == 4 && xhr.status != 200) {
					
						alert(xhr.status + ": " + xhr.statusText);
						bar.style.width = "0%";
						$scope.uploadName = undefined;
						box.style.visibility = "hidden";
					};
				};
				//Send the uploaded file.
				xhr.open('POST', '/heatmap/' + $scope.matrixlocation + "/annotation/" + "column", true);
				xhr.send(file);
			})
				
	};
	
	
}]);
