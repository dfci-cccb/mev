ctrl.controller('GeneSelectCtrl', ['$scope', '$http', '$routeParams', function($scope, $http, $routeParams) {
    
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