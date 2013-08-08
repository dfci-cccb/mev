ctrl.controller('HeatmapCtrl', ['$scope', '$routeParams', '$http', function($scope, $routeParams, $http) {

	$scope.matrixlocation = $routeParams.matrixLocation;
	$scope.curstartrow = 0;
	$scope.curendrow = 0;
	$scope.curstartcol = 39;
	$scope.curendcol = 39;
	
	$scope.pageUp = function() {
		
		++$scope.curstartrow;
		++$scope.curendrow;
		$scope.pullPage();
		
	}
	
	$scope.pageDown = function() {
		
		--$scope.curstartrow;
		--$scope.curendrow;
		$scope.pullPage();
		
	}
	
	$scope.pageLeft = function() {
		
		--$scope.curstartcol;
		--$scope.curendcol;
		$scope.pullPage();
		
	}
	
	$scope.pageRight = function() {
		
		++$scope.curstartcol;
		++$scope.curendcol;
		$scope.pullPage();
		
	}
	
	
	$scope.transformData = function() {
		
		if (!$scope.heatmapcells) {
			return;
		}
		console.log($scope.heatmapcells);
		for (var index = 0; index < $scope.heatmapcells.values.length; index++) {
			var inputobj = {
				value: $scope.heatmapcells.values[index],
				row: $scope.heatmaprows[Math.floor(index/$scope.heatmapcolumns.length)],
				col: $scope.heatmapcolumns[index%$scope.heatmapcolumns.length]
			}
			$scope.transformeddata.push(inputobj);
		}
	};
	
	$scope.markRow = function(inputindecies, inputdimension) {

		$http({
			method:"PUT",
			url:"heatmap/"+$scope.matrixlocation+"/selection/" + inputdimension,
			params: {
				format:"json",
				name: $scope.inputname,
				color: $scope.inputgroup,
				indecies: inputindecies
			}
		})
		.success( function(data) {
			return;
		});

	}
	
	//pull page function
	$scope.pullPage = function() {

		if (!$scope.matrixlocation) {
			return;
		}

		$http({
			method:"GET",
			url:"heatmap/"+$scope.matrixlocation+"/data",
			params: {
				format:"json",
				startRow:$scope.curstartrow,
				endRow:$scope.curendrow,
				startColumn:$scope.curstartcol,
				endColumn:$scope.curendcol
			}
		})
		.success( function(data) {
			$scope.heatmapcells = data;
			console.log(data);
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
		});

		var heatmapcolshold = [];
		for (var eachcol=$scope.curstartcol; eachcol<$scope.curendcol; eachcol++) {
			$http({
				method:"GET",
				url:"heatmap/"+$scope.matrixlocation+"/annotation/column/" +eachcol+ "/" + $scope.heatmapcolumnannotations[0],
				params: {
					format:"json"
				}
			})
			.success( function(data) {
				heatmapcolshold.push(data);
			});
		}
		$scope.heatmapcolumns = heatmapcolshold;
		
		var heatmaprowshold = [];
		for (var eachrow=$scope.curstartrow; eachrow<$scope.curendrow; eachrow++) {
			$http({
				method:"GET",
				url:"heatmap/"+$scope.matrixlocation+"/annotation/row/" +eachrow+ "/" + $scope.heatmaprownotations[0],
				params: {
					format:"json",
					index:eachrow
				}
			})
			.success( function(data) {
				heatmaprowshold.push(data);
			});
		}
		
		$scope.heatmaprows = heatmaprowshold;
		$scope.transformData();
		
	};
	
	//Initial call for values
	
	$scope.pullPage()
	
	
}]);


