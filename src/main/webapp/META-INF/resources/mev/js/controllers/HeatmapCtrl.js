ctrl.controller('HeatmapCtrl', ['$scope', '$routeParams', '$http', function($scope, $routeParams, $http) {

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
			$http.get({
				method:"GET", 
				url: 'heatmap/' + $scope.matrixLocation + '/data',
				params: $scope.getPageParams,
			})
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


  }]);
  
  ctrl.controller('AnalyzeCtrl', ['$scope', '$http', function($scope, $http) {
    
    //Get visualization json
    $http.get('data/visualization_data.json').
    
	success(function (data) {
        $scope.visualizationdata = data;
	});
	  
	$http.get('data/upload_data.json').
		  success(function (data) {
			  $scope.uploaddata = data;
		  });
      
	}]);
	
  