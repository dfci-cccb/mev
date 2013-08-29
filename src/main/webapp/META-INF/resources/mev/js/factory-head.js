
var fctry =  angular.module('myApp.factories', []);

fctry.factory('ExposedHTTPRequestFctry', ['$q', '$http', '$scope', function($q, $http, $scope){
	
	var output = function(params) {
	
		var deffered = $q.defer();
		
		$http(params)
		.success( function(data, status, headers, config) {
			
			$scope.$apply( function(){
			
				deferred.resolve({
					data: data,
					status: status,
					headers: headers,
					config: config
				});

				
			});
						
		})
		.error( function(data,status,headers,config){
			
			$scope.$apply(function(){
			
				deferred.reject({
					data: data,
					status: status,
					headers: headers,
					config: config
				});
				
			});
			
			
		});
		
		return deferred.promise;
		
	}
	
}]);

fctry.factory('GETHeatmapFctry', ["$q", "$http", "ExposedHTTPRequestFctry", function($q, $http, ExposedHTTPRequestFctry) { 
	
	var output = function() {
		
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
	
	return output;
	
}]);

fctry.factory('GETHeatmapDataCellsFctry', ["$q", "$http", function($q, $http) { 

	var output = function(matrixlocation, curstartrow, curendrow, curstartcol, curendcol) {
		
		var deferred = $q.defer();
	
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
	}
	
	return output;
	
}]);