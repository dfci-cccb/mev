
var fctry =  angular.module('myApp.factories', [])

fctry.factory('GETHeatmapFctry', ["$q", function($q) { 
	
	var output = function() {
	
		var deferred = $q.defer();
		
		$http({
			method:"GET",
			url:"heatmap/",
			params: {
				format:"json"
			}
		})
		.success( function(data, status, headers, config) {
			deferred.resolve({
				data: data,
				status: status,
				headers: headers,
				config: config
			});
		})
		.error( function(data,status,headers,config){
			deferred.reject({
				data: data,
				status: status,
				headers: headers,
				config: config
			});
		});
		
		return deferred;
		
	};
	
	return output();
	
}]);

fctry.factory('GETHeatmapDataFctry', [ function() { 

	var output = function(matrixlocation, curstartrow, curendrow, curstartcol, curendcol) {

		$http({
			method:"GET",
			url:"heatmap/"+matrixlocation+"/data",
			params: {
				format:"json",
				startRow:curstartrow,
				endRow:curendrow,
				startColumn:curstartcol,
				endColumn:curendcol
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
	
	return output();
	
}]);