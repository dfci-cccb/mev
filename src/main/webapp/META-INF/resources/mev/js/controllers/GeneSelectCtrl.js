ctrl.controller('GeneSelectCtrl', ['$scope', '$http', '$routeParams', '$q', function($scope, $http, $routeParams, $q) {

	var annotations = new annotationsGetter;

	$scope.matrixsummary = undefined;
	$scope.headers = null;
	$scope.dimension = "column";
	$scope.currentpage = 0;
	$scope.totalpages = 0;
	
	$scope.nearbypages = [0];
	
	$scope.range = [0, 100];
	
	$scope.totalrows = 50;
	
	$scope.selections = new Object();
	
	
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
			
			if ($scope.matrixsummary[($scope.dimension + "s")]%$scope.totalrows > 0) {
				$scope.totalpages = Math.floor( ($scope.matrixsummary[($scope.dimension + "s")]/$scope.totalrows) + 1 );
				
			} else {
				$scope.totalpages = ($scope.matrixsummary[($scope.dimension + "s")]/$scope.totalrows ) ;
			};

			$scope.getPage(0);
			
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
	
	$scope.getPage = function(page) {
		
		var arr = [0, 0];
		
		if (page <= 0) {
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
			
			$scope.currentpage = page;
			
			$scope.tuples = datas.map(function(data){
				return data.data;
			});
			
			if (page < 3) {
				
				$scope.nearbypages = [0, 1, 2, 3, 4]
				
				
			} else {
				$scope.nearbypages = d3.range($scope.totalpages - 1).filter(function(inpage) {
					return (Math.abs(inpage - $scope.currentpage) <= 2)
				});

			}
			
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
	
	
	$scope.analyzeLimmaRequester = function() {

		var matrixLocation = $scope.matrixlocation;
		var limmaDimension = $scope.LimmaDimension;
		var limmaSelection1 = $scope.LimmaSelection1;
		var limmaSelection2 = $scope.LimmaSelection2;
		

		var inputurl = "heatmap/"+$scope.matrixlocation+"/analysis/limma" 
				+ "(" +$scope.LimmaDimension + "," + $scope.LimmaSelection1 + "," + $scope.LimmaSelection2 + ")";

		alert('Your analysis will complete soon.\n\n'+
				'Experiment: '+limmaSelection1 + '\n' +
				'Control: '+ limmaSelection2 + '\n' +
				'Dimension: '+ limmaDimension + '\n');
		
		$http({
			method:"HEAD",
			url: inputurl,
		})
		.success( function(data) {
			
			alert('Your analysis has completed. \n\n'+
				'Experiment: '+limmaSelection1 + '\n' +
				'Control: '+ limmaSelection2 + '\n' +
				'Dimension: '+ limmaDimension + '\n'
			);
			
		})
		.error(function(data){
			
			alert('Your analysis has failed! \n\n'+
				'Experiment: '+limmaSelection1 + '\n' +
				'Control: '+ limmaSelection2 + '\n' +
				'Dimension: '+ limmaDimension + '\n'
			);
			
		})
		
	}
	
	$scope.pullAllSelections = function() {
		$scope.pullSelections('row');
		$scope.pullSelections('column');
	}
	
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
				return filt.attribute != input.attribute
			})
		}
		
	};
	
	$scope.addFilter = function(input){
		
		
		if ($scope.fieldFilters.filter(function(filt) {return filt.attribute == input.attribute}).length == 0) {
			$scope.fieldFilters.push(input);
		} else {
			alert("You have already selected a filter for this attribute. Remove it first.");
		}
		
		$scope.modalinput = {attribute:undefined, operand:"Insert Value", operator:"="};
		
	};
	
	$scope.modalinput = {attribute:undefined, operand:"Insert Value", operator:"="};
	
	$scope.selectfilter = function(input){
		$scope.modalinput = {attribute:input, operand:"Insert Value", operator:"="};
	}
	
	$scope.reqQuery = function(reqPage) {
		
		$q.all([0].map(function(rowid){	
			
			return $http({
				method:"PUT",
				url:"heatmap/"+$routeParams.dataset+"/annotation/" + $scope.dimension + "/search",
				data: $scope.fieldFilters,
				params: {
					format:"json",
				}
			})
			.success( function(data) {
				return data;
			})
			
		})).then(function(indiceslist) {

			

			var arr = indiceslist[0].data;
			
			$http({
				method:"PUT",
				url:"heatmap/"+$scope.matrixlocation+"/selection/"+ $scope.dimension +"/"+$scope.selectionname,
				params: {
					format:"json"
				},
				data: '{"attributes": {"name":"'+ $scope.selectionname +'"}, "indices":['+ arr + ']}'
			})
			.success( function(data) {
				
				$scope.pullAllSelections()
				$scope.selectionname = undefined;
				
			});
			
			var filtarr = arr.filter(function(obj, index) {
				return (index < 50)	
			})
		
			$q.all(filtarr.map(function(rowid){	
				
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
			
		})
		
		
		
		
	};
	
	
	//Initial Page Load
	$scope.pullAllSelections()
	annotations.get();
	pullSummary();
	
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
							annotations.get();
							pullSummary();
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
							
							annotations.get();
							pullSummary();
						
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
