'use strict'

/* Controllers */

angular.module('myApp.controllers', [])
  .controller('HeatmapCtrl', ['$scope', '$routeParams', '$http', function($scope, $routeParams, $http) {

    $scope.matrixLocation = $routeParams.matrixLocation;
    $scope.markedRows = [];
    $scope.vizcolor = "red";
    $scope.colors = ["red", "blue"];
    $scope.hoverdata = 0;
    $scope.view = "";
    
    $scope.updateColor = function(newcolor) {
		if ($scope.colors.indexOf(newcolor) != -1) {
			$scope.vizcolor = newcolor;
		}
	}
	
	$scope.clearAllRows = function() {
		$scope.markedRows = [];
	}
	
	$scope.requestPage = function(page) {
		if (page <= $scope.maxpage && page >= 0) {
			$http.get('data/subs/' + $scope.matrixLocation + '-' + page + '.json')
             .success(function (returnobject) {
                $scope.heatmapdata = returnobject.data;
                $scope.view = 'page';
                $scope.currentpage = page;
                $scope.maxpage = returnobject.pages.length - 1;
                $scope.allpages = returnobject.pages;
                $scope.viztitle = returnobject.title;
	         });
		}
	}
	
    $scope.storeRow = function(inputrow) {
		var possibleindex = $scope.markedRows.indexOf(inputrow);
		if (possibleindex == -1) {
			$scope.markedRows.push(inputrow);
		}	
	}
	
	$scope.removeRow = function(outputrow) {
		var outputrowindex = $scope.markedRows.indexOf(outputrow);
		if (outputrowindex != -1) {
			$scope.markedRows.splice(outputrowindex, 1);
		}	
	}
	
    $scope.requestAll = function() {
	
        $http.get('data/subs/' + $scope.matrixLocation + '-0.json')
             .success(function (returnobject) {
                $scope.heatmapdata = returnobject.data;
                $scope.view = 'all';
                $scope.currentpage = 0;
                $scope.maxpage = returnobject.pages.length - 1;
                $scope.allpages = returnobject.pages;
                $scope.viztitle = returnobject.title;
	         });
	}
	
	$scope.requestAll();


  }])
  .controller('AnalyzeCtrl', ['$scope', '$http', function($scope, $http) {
    
    //Get visualization json
    $http.get('data/visualization_data.json').
    
	success(function (data) {
        $scope.visualizationdata = data;
	});
	  
	$http.get('data/upload_data.json').
		  success(function (data) {
			  $scope.uploaddata = data;
		  });
      
	}])
  .controller('UploadCtrl', ['$scope', function($scope){
		$scope.output = "Select your file to upload.";
        //Function to upload files
        $scope.sendFile = function() {
			//Variable declarations
			var input = document.getElementById("file");
			var bar = document.getElementById("bar"); //DOM element of the growing progress bar
			var percent = document.getElementById("percent"); //Value reported inside progress bar
			var upfile = input.files[0]; //Assign file object of the input file to a variable
			var formdata = new FormData; //Dynamically create a new form
			formdata.append('upload', upfile); //Append file object to the form
			$scope.output = "Uploading..."; //Alert user that uploading has begun
			//AJAX code
			var xhr = new XMLHttpRequest();
			//Event Listener that will provide progress to the user
			xhr.upload.addEventListener("progress", function(e){
				var pc = parseInt((e.loaded / e.total * 100)); //Calculate % completion
				//Update the progress elements with the percent completion quantity.
				bar.style.width = pc + "%";
				percent.innerHTML = pc + "%";
			});
			//Event Listener that will output the server response (if any) as well as update the user feedback elements to 100% completion.
			xhr.onreadystatechange = function(){
				if (xhr.readyState == 4 && xhr.status == 200){
					$scope.$apply(function(){ //$scope.$apply() is used so angular controller can update $scope.output upon the xhr.responseText assignment.
						$scope.output = xhr.responseText;
					});
					bar.style.width = "100%";
					percent.innerHTML = "100%";
				};
			};
			//Send the uploaded file.
			xhr.open('POST', 'http://bioed.bu.edu/cgi-bin/students_13/correiak/upload.py', true); //Action (2nd parameter) is currently set to my school webspace for testing purposes. To be changed.
			xhr.send(formdata);
      };
  }])
  .controller('GeneSelectCtrl', ['$scope', '$http', '$routeParams', function($scope, $http, $routeParams) {
    
    //Variables
    $scope.project = $routeParams.project;
    $scope.markedRows = []
    $scope.getPageParams = {};

    //Functions
    $scope.pushToParams = function(key, value) {
	    $scope.getPageParams[key]=value;
	}
	
	$scope.pullFromParams = function(key) {
	    delete $scope.getPageParams[key];	
	}
	
	$scope.purgeParams = function() {
	    $scope.getPageParams = {};	
	}
	
    $scope.getPage = function(pagenum) {
		if (pagenum < 0 || pagenum > $scope.totalpages) {
		
		} else {
            
            $scope.getPageParams["id"] = $scope.project;
		    $scope.getPageParams["page"] = pagenum;
		    $scope.getPageParams["format"] = "json";
		    
		    $http({
				method:"GET", 
				url:'data/geneset',
				params: $scope.getPageParams,
			})
			.success( function(data) {
				$scope.tuples = data.tuples;
				$scope.fields = data.fields;
				$scope.currentpage = data.page_id;
				$scope.totalpages = data.total_pages;
			});
		 }
    }
    
    $scope.markRow = function(row) {
	
		if ($scope.markedRows.indexOf(row) == -1) {
			$scope.markedRows.push(row);
		}
		
	}
	
	$scope.removeRow = function(row) {
	
		var rowindex = $scope.markedRows.indexOf(row);
		if (rowindex != -1) {
			$scope.markedRows.splice(rowindex, 1);
		}	
		
	}

   $scope.getPage(1);
    
  }]);
