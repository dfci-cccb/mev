ctrl.controller('HeatmapCtrl', ['$scope', '$routeParams', '$http', function($scope, $routeParams, $http) {

	$scope.matrixlocation = $routeParams.matrixLocation;
	$scope.curstartrow = 0;
	$scope.curendrow = 39;
	$scope.curstartcol = 0;
	$scope.curendcol = 39;
	var firstpull = true;
	var pullallow = true;
	
	$scope.pageUp = function() {
		
		if ($scope.curstartrow == 0) {
			return;
		}
		
		--$scope.curstartrow;
		--$scope.curendrow;
		$scope.pullPage();
		
	}
	
	$scope.pageDown = function() {
		
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
		
		for (var index = 0; index < $scope.heatmapcells.length; ++index) {

				
				var inputobj = {
					value: $scope.heatmapcells[index],
					row: $scope.heatmaprows[Math.floor(index/($scope.heatmapcolumns.length))]['value'],
					col: $scope.heatmapcolumns[index%($scope.heatmapcolumns.length)]['value']
				}
				$scope.transformeddata.data.push(inputobj);
		}
		
		$scope.transformeddata.columnlabels = $scope.heatmapcolumns.map(function(d) { return d.value;});
		$scope.transformeddata.rowlabels = $scope.heatmaprows.map(function(d) { return d.value;});
		
		firstpull = false;

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
		
		$scope.heatmapcells = null;
		$scope.heatmaprows = null;
		$scope.heatmapcolumns = null;

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
	
	
}]);


