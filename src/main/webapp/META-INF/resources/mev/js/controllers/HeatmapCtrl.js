ctrl.controller('HeatmapCtrl', ['$scope', '$routeParams', '$http', function($scope, $routeParams, $http) {

	$scope.matrixlocation = $routeParams.matrixLocation;
	$scope.heatmapcells = [];
	$scope.heatmapcolumns = [];
	$scope.heatmaprows = [];
	$scope.transformeddata = [];
	$scope.selectedrows = [];
	$scope.inputname = [];
	$scope.inputgroup =[];
	
	$scope.transformData = function() {
		for (index = 0; index < $scope.heatmapcells.values.length; index++) {
			inputobj = {
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
	$scope.pullPage = function(startrow, endrow, startcol, endcol) {

		if (!scope.matrixlocation) {
			return;
		}

		$http({
			method:"GET",
			url:"heatmap/"+$scope.matrixlocation+"/data",
			params: {
				format:"json",
				startRow:startrow,
				endRow:endrow,
				startColumn:startcol,
				endColumn:endcol
			}
		})
		.success( function(data) {
			$scope.heatmapcells = data;
		});

		$scope.heatmapcolumns = [];
		for (var eachcol=startcol; eachcol<endcol; eachcol++) {
			$http({
				method:"GET",
				url:"heatmap/"+$scope.matrixlocation+"/annotation/dimension",
				params: {
					format:"json",
					dimension:"column",
					index:eachcol
				}
			})
			.success( function(data) {
				$scope.heatmapcolumns.push(data);
			});
		}

		$scope.heatmaprows = [];
		for (var eachrow=startrow; eachrow<endrow; eachrow++) {
			$http({
				method:"GET",
				url:"heatmap/"+$scope.matrixlocation+"/annotation/dimension",
				params: {
					format:"json",
					dimension:"row",
					index:eachrow
				}
			})
			.success( function(data) {
				$scope.heatmaprows.push(data);
			});
		}

	};
	
	//Initial call for values
	$http({
		method:"GET",
		url:"heatmap/"+$scope.matrixlocation+"/data",
		params: {
			format:"json"
		}
	})
	.success( function(data) {
		$scope.heatmapcells = data;
	});

	$http({
		method:"GET",
		url:"heatmap/"+$scope.matrixlocation+"/annotation/dimension",
		params: {
			format:"json",
			dimension:"column"
		}
	})
	.success( function(data) {
		$scope.heatmapcolumns = data;
	});


	$http({
		method:"GET",
		url:"heatmap/"+$scope.matrixlocation+"/annotation/dimension",
		params: {
			format:"json",
			dimension:"row"
		}
	})
	.success( function(data) {
		$scope.heatmaprows = data;
	});
	
}]);


