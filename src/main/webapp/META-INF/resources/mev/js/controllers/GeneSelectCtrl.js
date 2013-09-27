ctrl.controller('GeneSelectCtrl', ['$scope', '$http', '$routeParams', '$q', function($scope, $http, $routeParams, $q) {

	var annotations = new annotationsGetter;

	$scope.matrixsummary = undefined;
	$scope.headers = null;
	$scope.dimension = "row";
	$scope.page = 0;
	$scope.range = [0, 100];
	
	function tuple(name) {
		this.name = name;
		this.data = [];
		this.add = function(attr, val){
			this.data.push({
				attribute:attr,
				value:val})
		};
		this.get = function(attr){
			return this.data.filter(function(element){
				return (element.attribute == attr)
			});
		};
	};
	
	function annotationsGetter(){
		
		this.get = function (){
			
			$http({
				method:"GET",
				url:"heatmap/"+$routeParams.dataset+"/annotation/"+ $scope.dimension,
				params: {
					format:"json"
				}
			})
			.success( function(data) {
				$scope.headers = data;
			})
			.error(function(){
				alert("Could not pull row attributes.");
			});
			
		};
		
	};
	
	$http({
		method:"GET",
		url:"heatmap/"+$routeParams.dataset+"/summary/",
		params: {
			format:"json",
		}
	})
	.success( function(data) {
		$scope.matrixsummary = data;
		getTuples(0, $scope.matrixsummary[($scope.dimension + "s")]);
	});
	
	function getTuples(startind, endind){
	
		var arr = d3.range(startind, endind)
		
		$q.all(arr.map(function(rowid){	
			
			return $http({
				method:"GET",
				url:"heatmap/"+$routeParams.dataset+"/annotation/" + $scope.dimension + "/"+ rowid,
				params: {
					format:"json",
				}
			})
			.success( function(data) {
				return data;
			});
			
		}))
		.then(function(datas){
			$scope.tuples = datas.map(function(data){
				return data.data;
			});
		});
		
	};
	
	
	
	$scope.changeDimension = function(input){
		console.log(input)
		$scope.dimension = input;
		annotations.get();
		getTuples(0, $scope.matrixsummary[($scope.dimension + "s")]);
		
	}

	
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
		
	};
	
	
	annotations.get();


    
}]);
