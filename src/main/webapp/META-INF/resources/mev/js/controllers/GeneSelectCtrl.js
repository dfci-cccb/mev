ctrl.controller('GeneSelectCtrl', ['$scope', '$http', '$routeParams', '$q', function($scope, $http, $routeParams, $q) {

	var annotations = new annotationsGetter;

	$scope.matrixsummary = undefined;
	$scope.headers = null;
	$scope.dimension = "row";
	$scope.page = 0;
	$scope.totalpages = 0;
	
	$scope.nearbypages = [0];
	
	$scope.range = [0, 100];
	
	$scope.totalrows = 100;
	
	
	function pullSummary() {

		$http({
			method:"GET",
			url:"heatmap/"+$routeParams.dataset+"/summary/",
			params: {
				format:"json",
			}
		})
		.success( function(data) {
			
			$scope.matrixsummary = data;
			
			if ($scope.totalrows%$scope.matrixsummary[($scope.dimension + "s")] > 0) {
				$scope.totalpages = Math.floor( ($scope.totalrows/$scope.matrixsummary[($scope.dimension + "s")]) + 1 );
			} else {
				$scope.totalpages = ($scope.totalrows/$scope.matrixsummary[($scope.dimension + "s")] ) ;
				$scope.nearbypages = d3.range($scope.totalpages - 1)
			};
			
			getPage(0);
			
		});
		
	}

	
	
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
	
	function getPage(page) {
		
		
		var arr = [0, 0];
		
		if (page == 0) {
			arr = d3.range(0, $scope.totalrows);
		} else {
			arr = d3.range( page * $scope.totalrows, (page + 1) * $scope.totalrows);
		}
		
		
		
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
			
			$scope.page = page;
			
			$scope.tuples = datas.map(function(data){
				console.log(data)
				return data.data;
			});
			
			
			
		});
		
	}
	
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

		$scope.dimension = input;
		annotations.get();
		pullSummary();
		
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
	
	
	//Initial Page Load
	annotations.get();
	pullSummary();


    
}]);
