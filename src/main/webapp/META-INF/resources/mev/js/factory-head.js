
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