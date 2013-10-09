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
	
	$scope.analyzeClustering = function() {
		
		var matrixlocation = $scope.matrixlocation;
		var ClusterType = $scope.ClusterType;
		var ClusterDimension = $scope.ClusterDimension;
		
		alert("Clustering on " + ClusterDimension + ". Please wait. Your dataset will be generated soon.")
		
		$http({
			method:"GET",
			url:"heatmap/"+$scope.matrixlocation+"/analysis/"+ $scope.ClusterType +"Clustering" + "/" + $scope.ClusterDimension,
			params: {
				format:"json",
				
			}
		})
		.success( function(data) {
			alert("Clustering analysis on " + ClusterDimension + " complete!\n\n"+
			"Cluster Type: "+ClusterType + "\n\n" + "Your heatmap has been generated.")
			$scope.retrieveHeatmaps();
		})
		.error( function(data) {
		    alert("Something went wrong, please contact us if the problem persists.");	
		});
		
	};
	
	$scope.downloadLimmaRequester = function(LimmaDimension, LimmaSelection1, LimmaSelection2) {

		var inputurl = "heatmap/"+$routeParams.dataset+"/analysis/limma" 
				+ "(" +LimmaDimension + "," + LimmaSelection1 + "," + LimmaSelection2 + ")"
				+ "/" + "full";

		
			$("body").append("<iframe src='" + inputurl + "' style='display: none;' ></iframe>")
		
		
	}
	
	$scope.createNewHeatmap = function(LimmaDimension, LimmaSelection1, LimmaSelection2) {
		
		console.log("heatmap/"+$scope.matrixlocation+"/export/"+ LimmaDimension
			+ "?format=json&selection=" + LimmaSelection1 + "&selection=" + LimmaSelection2);
		
		$http({
			method:"POST",
			url:"heatmap/"+$scope.matrixlocation+"/export/"+ LimmaDimension
			+ "?format=json&selection=" + LimmaSelection1 + "&selection=" + LimmaSelection2
		})
		.success(function(data){
			alert("New Heatmap Created with "+ LimmaSelection1 + " and " + LimmaSelection2+ "!");
			$scope.retrieveHeatmaps();
		})
		.error(function(data){
			alert("New Heatmap Creation Failed with "+ LimmaSelection1 + " and " + LimmaSelection2+ "!");
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
			$scope.pullLimmaAnalysis()
		})
		.error(function(data){
			
			alert('Your analysis has failed! \n\n'+
				'Experiment: '+limmaSelection1 + '\n' +
				'Control: '+ limmaSelection2 + '\n' +
				'Dimension: '+ limmaDimension + '\n'
			);
			
		})
	}
	
	$scope.pullLimmaAnalysis = function() {
	
		$scope.limmaPreviousAnalysis = []
	
		$http({
			method:"GET",
			url:"heatmap/"+$scope.matrixlocation+"/analysis/limma/"+"column",
			params: {
				format:"json"
			}
			
		})
		.success( function(data) {
			
			if (data.length >0) {
				data.map(function(columnLimma, index){
					$scope.limmaPreviousAnalysis.push({
						control: columnLimma.control,
						experiment: columnLimma.experiment,
						dimension: "column"
					});
				});
			}
			
			
		});
		
		$http({
			method:"GET",
			url:"heatmap/"+$scope.matrixlocation+"/analysis/limma/"+"row",
			params: {
				format:"json"
			}
			
		})
		.success( function(data) {
					
			if (data.length >0) {
				data.map(function(columnLimma){
					$scope.limmaPreviousAnalysis.push({
						control: columnLimma.control,
						experiment: columnLimma.experiment,
						dimension: "row"
					});
				});
				
			}
			
		});
	};
	
	
	
	$scope.analyzeLimmaViewRequester = function(LimmaDimension, LimmaSelection1, LimmaSelection2) {
		
		$http({
				method:"GET",
				url:"heatmap/"+$scope.matrixlocation+"/analysis/limmaView" 
				+ "(" +LimmaDimension + "," + LimmaSelection1 + "," + LimmaSelection2 + ")"
				+ "/" + "significant",
				params: {
					format:"json"
				}
			})
			.success( function(data) {
				
				$scope.limmaviewtablerows = data;
				
			})
			.error(function(data){
			
				alert("There was an error pulling your Limma significant request. \n\n"+
				'Experiment: '+limmaSelection1 + '\n' +
				'Control: '+ limmaSelection2 + '\n' +
				'Dimension: '+ limmaDimension + '\n')
				
			});
		
	};
	
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
