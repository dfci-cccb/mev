ctrl.controller('GeneSelectCtrl', ['$scope', '$http', '$routeParams', function($scope, $http, $routeParams) {

	
	$scope.tuples = [["GNDE3", "23", "yes"],
	                ["GNDE4", "15", "no"],
	                ["GNDE5", "18", "yes"],
	                ["GNDE7", "5", "no"],
	                ["GNDE12", "26", "no"]];
	
	$scope.headers = ["Column1", "Column2", "Column3"];
	
	$scope.fieldFilters = new Array;
	
	$scope.remAll = function() {
		$scope.fieldFilters = [];
	};
	
	$scope.remFilter = function(input){
		
		if ($scope.fieldFilters.length >= 1) {
			$scope.fieldFilters = $scope.fieldFilters.filter( function(filt){
				return filt.variable != input.variable
			})
		}
		
	};
	
	$scope.addFilter = function(input){
		
		if ($scope.fieldFilters.filter(function(filt) {return filt.variable == input.variable}).length == 0) {
			$scope.fieldFilters.push(input);
		} else {
			alert("You have already selected a filter for this attribute. Remove it first.");
		}
		
		$scope.modalinput = {variable:undefined, value:"Insert Value", operator:"="};
		
	};
	
	$scope.modalinput = {variable:undefined, value:"Insert Value", operator:"="};
	
	$scope.selectfilter = function(input){
		$scope.modalinput = {variable:input, value:"Insert Value", operator:"="};
	}
	
	$scope.reqQuery = function(reqPage) {
		
		if ($scope.fieldFilters.length > 0) {
			
			$http({
				method:"PUT",
				url:"heatmap/"+$routeParams.geneset+"/annotation/"+"row"+ "/filter",
				params: {
					format:"json",
					page: reqPage,
					request: $scope.fieldFilters
				}
			})
			.success( function(data) {
				$scope.tuples = data;
			})
			.error(function(){
				alert("error!")
			});
			
		}
		
	};


    
}]);
