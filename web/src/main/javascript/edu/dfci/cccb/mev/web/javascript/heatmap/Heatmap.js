define(['jquery', 
        'angular',
        'extend',
        'd3',
        'project/Project', 'api/Api', 'colorbrewer/ColorBrewer'],
    function($, angular, extend, d3) {
	
	return angular
		.module('Mev.heatmap', ['Mev.Api', 'Mev.Project', 'd3colorBrewer'])
		.service('Heatmap.Modal', [function(){
		//Uncovered code
			return function(id){
				return {
					show: function(){
						$(id).modal('show');
					},
					hide: function(){
						$(id).modal('hide');
					}
				};
			};
			
		}])
        .service('Heatmap.saveCluster', [function(){
        	
        	return function(cluster) {

	        	var svg = d3.select("#svgWrapperHclTree_"+cluster.name).select("svg");
	        	 var html = svg
	        	 .attr("version", 1.1)
	             .attr("xmlns", "http://www.w3.org/2000/svg")
	             .node().parentNode.innerHTML;
	        	  
	        	 var imgsrc = 'data:image/svg+xml;base64,'+ btoa(html);
	//           var img = '<img src="'+imgsrc+'">';
	//           d3.select("#svgdataurl").html(img);
	        	  
	        	  
	        	 var canvas = document.querySelector("#canvasHclTree_"+cluster.name);
	        	 var context = canvas.getContext("2d");
	        	 canvas.width=svg.attr('width');
	        	 canvas.height=svg.attr('height');
	        	 
	        	 var image = new Image;
	        	 image.src = imgsrc;
	        	 image.onload = function() {
	        		                                         		 
	            	 canvas.style.opacity = 1;
	            	 context.beginPath();
	                 context.rect(0, 0, canvas.width, canvas.height);                                                 
	            	 context.fillStyle ="#FFFFFF";
	            	 context.fill();
	            	 
	            	 context.drawImage(image, 0, 0);
	            	 
	            	 canvas.toBlob(function(blob) {
	            		    saveAs(blob, cluster.name+".png");
	            	 });
//	                 var canvasdata = canvas.toDataURL("image/png");
//	                 var pngimg = '<img src="'+canvasdata+'">';
//	                 d3.select("#pngdataurl").html(pngimg);
//	                 var a = document.createElement("a");
//	                 a.download = "sample.png";
//	                 a.href = canvasdata;
//	                 a.click();
	            };
        	};
        }])
        .directive('viewManager', [function () {
            
            return {
                restrict: "E",
                rep1ace: true,
                template:'<button class="btn btn-success" ng-click="applyToHeatmap()" ><a></i>Reset Heatmap</a></button>',
                scope: {
                    project : '=project',
                },
                link: function(scope, element, attrs) {
                    scope.applyToHeatmap=function(){
                        
                        scope.project.generateView({
                            viewType:'heatmapView', 
                            labels:{
                                row:{keys:scope.project.dataset.row.keys}, 
                                column:{keys:scope.project.dataset.column.keys}
                            },
                            expression:{
                                min: scope.project.dataset.expression.min,
                                max: scope.project.dataset.expression.max,
                                avg: scope.project.dataset.expression.avg,
                            }
                        });                        
                        scope.$emit('ViewVisualizeTabEvent');
                    };
                    
                }
            }
            
        }])
		.controller('HeatmapCtrl', [
		'$scope', 
		'$routeParams',
        '$location',
        'DatasetResourceService',
        'ProjectFactory',
        'Heatmap.Modal',
        function($scope, $routeParams,  $loc, DatasetResourceService, ProjectFactory, modal) {
		    
			LoadingModal = modal('#loading');
			
			function downloadFailure(){
				LoadingModal.hide()
				$loc.path('/datasets'); //return back to datasets
				return
			};
			
			//case where there's no datasetName
			if (!$routeParams.datasetName) {
				downloadFailure();
			};
			
			$scope.project = undefined;

			//Build the project after getting datasetResponseObject
			LoadingModal.show();
			
			DatasetResourceService.get({
				datasetName: $routeParams.datasetName
			}, function(response){
			    $scope.project = ProjectFactory($routeParams.datasetName, response);
			    LoadingModal.hide();
			}, function(error){
				downloadFailure();
			});
			
			
			//Generate views and load analyses
			$scope.$watch('project.dataset', function(newval, oldval){
				if(newval && !oldval){
				    
					$scope.project.generateView({
					    viewType:'heatmapView', 
					    labels:{
					        row:{keys:$scope.project.dataset.row.keys}, 
					        column:{keys:$scope.project.dataset.column.keys}
					    },
					    expression:{
					        min: newval.expression.min,
					        max: newval.expression.max,
					        avg: newval.expression.avg,
					    }
					});
					
					$scope.project.dataset.loadAnalyses();
					
				}
				
			});
        	
        }]);
});
