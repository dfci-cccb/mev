ctrl.controller('HeatmapCtrl', ['$scope', '$routeParams', '$http', '$rootScope', function($scope, $routeParams, $http, $rootScope) {

	$scope.matrixlocation = $routeParams.dataset;
	$scope.curstartrow = 0;
	$scope.curendrow = 100;
	$scope.curstartcol = 0;
	$scope.curendcol = 50;
	var firstpull = true;
	var pullallow = true;
	$scope.matrixsummary = undefined;
	$scope.selectionname = undefined;
	$scope.analyzeOptions = ["LIMMA", "EuclideanClustering"]
	
	$scope.selectedcells = new Object();
	$scope.selectedcells.row = [];
	$scope.selectedcells.column = [];
	$scope.selections = new Object();
	
	$http({
		method:"GET",
		url:"/preferences/heatmap.color",
		params: {
			format:"json"
		}
	})
	.success( function(data) {
		$scope.heatmapcolor = data;
	});

	$scope.retrieveHeatmaps = function() {

		$http({
				method:"GET",
				url:"heatmap/",
				params: {
					format:"json"
				}
			})
			.success( function(data) {
				$rootScope.menuheatmaplist = data;
			});
	};
	
	$http({
		method:"PUT",
		url:"heatmap/"+$scope.matrixlocation+"/summary/",
		params: {
			format:"json",
		}
	})
	.success( function(data) {

		$scope.matrixsummary = data;
		
		$scope.pullSelections("column");
		$scope.pullSelections("row");
		
		
		if ($scope.matrixsummary.columnClustered) {
			
			$http({
				method:"GET",
				url:"heatmap/"+$scope.matrixlocation+"/analysis/EuclidianClustering" + "/" + "column",
				params: {
					format:"json",
				
				}
			})
			.success( function(data) {
				$scope.downloadedtree = data;
				$scope.transformData();
			})
			
		}
		
	});
	
	$scope.analyzeClustering = function(dimension) {
		
		$http({
			method:"GET",
			url:"heatmap/"+$scope.matrixlocation+"/analysis/"+ $scope.ClusterType +"Clustering" + "/" + $scope.ClusterDimension,
			params: {
				format:"json",
				
			}
		})
		.success( function(data) {
			alert("Clustering analysis complete!")
			$scope.retrieveHeatmaps();
		})
		.error( function(data) {
		    alert("Something went wrong, please contact us if the problem persists.");	
		});
		
	};
	
	$scope.analyzeLimmaRequester = function() {

		var inputurl = "heatmap/"+$scope.matrixlocation+"/analysis/limma" 
				+ "(" +$scope.LimmaDimension + "," + $scope.LimmaSelection1 + "," + $scope.LimmaSelection2 + ")"
				+ "/" + $scope.LimmaOutputOption;

		
			$("body").append("<iframe src='" + inputurl + "' style='display: none;' ></iframe>")
		
		
	}
	
	$scope.pageUp = function() {
		
		if ($scope.curstartrow == 0) {
			return;
		}
		
		--$scope.curstartrow;
		--$scope.curendrow;
		$scope.pullPage();
		
	}
	
	$scope.pageDown = function() {

		if (($scope.matrixsummary.rows - 1) == $scope.curendrow) {
			return;
		}
		
		++$scope.curstartrow;
		++$scope.curendrow;
		$scope.pullPage();
		
	}
	
	$scope.pageLeft = function() {
		
		if ($scope.curstartcol == 0) {
			return;
		}
		
		--$scope.curstartcol;
		--$scope.curendcol;
		$scope.pullPage();
		
	}
	
	$scope.pageRight = function() {
		
		if (($scope.matrixsummary.columns - 1) == $scope.curendcol) {
			return;
		}
		
		++$scope.curstartcol;
		++$scope.curendcol;
		$scope.pullPage();
		
	}
	
	$scope.transformData = function() {
		
		if (!pullallow) {
			return;
		}
		
		if (!$scope.heatmapcells || !$scope.heatmaprows || !$scope.heatmapcolumns) {
			return;
		}
		
		if (firstpull) {
			if ($scope.heatmapcells.length < 1600) {
				pullallow = false;
			}
		} else {
			if ($scope.heatmapcells.length < 1600) {
				
				return;
			}
		}
		
		if ($scope.matrixsummary.columnClustered && !$scope.downloadedtree) {
				return;
		}

		$scope.transformeddata = {data:[], tree:{}};
		
		$scope.transformeddata.data = $scope.heatmapcells.map(function(cell, index){
			
			return {
				value: cell,
				row: $scope.heatmaprows[Math.floor(index/($scope.heatmapcolumns.length))]['value'],
				col: $scope.heatmapcolumns[index%($scope.heatmapcolumns.length)]['value']
			}
			
		})
		
		$scope.transformeddata.columnlabels = $scope.heatmapcolumns.map(function(d) { return d.value;});
		$scope.transformeddata.rowlabels = $scope.heatmaprows.map(function(d) { return d.value;});
		$scope.transformeddata.matrixsummary = $scope.matrixsummary;
		$scope.transformeddata.tree.top = $scope.downloadedtree;
		firstpull = false;

	};
	
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
	
	$scope.requestTopTree = function() {
	

		
	};

	$scope.pushSelections = function(dimension) {
		
		if ($scope.selectedcells[dimension]) {
			
			$http({
				method:"PUT",
				url:"heatmap/"+$scope.matrixlocation+"/selection/"+dimension+"/"+$scope.selectionname,
				params: {
					format:"json"
				},
				data: '{"attributes": {"name":"'+ $scope.selectionname +'"}, "indices":['+$scope.selectedcells[dimension]+ ']}'
			})
			.success( function(data) {
				alert("Added!");
				
				$scope.pullSelections(dimension);
				$scope.selectionname = undefined;
				
			});
		}
		
		
	};
	
	//pull page function
	$scope.pullPage = function() {

		if (!$scope.matrixlocation) {
			return;
		}
		
		$scope.heatmapcells = null;
		$scope.heatmaprows = null;
		$scope.heatmapcolumns = null;

		$http({
			method:"GET",
			url:"heatmap/"+$scope.matrixlocation+"/data/"+"["+$scope.curstartrow+":"
				+$scope.curendrow+","+$scope.curstartcol+":"+$scope.curendcol+"]",
			params: {
				format:"json"
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
				url:"heatmap/" + $scope.matrixlocation + "/annotation/column/" + $scope.curstartcol + "-" + $scope.curendcol + "/COLUMN",
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
	
	//Initial call for values
	
	$scope.pullPage();
	
	
}]);
