define(["ng", "lodash"], function(ng, _){
	var module = ng.module("mui.views.dataset.analysis.anova", []);
	module.config(["$stateProvider", "$urlRouterProvider", function($stateProvider, $urlRouterProvider){}])
	.controller("AnovaVM", ["$scope", "$state", "$stateParams", "project", "analysis", "BoxPlotService",
	                        function($scope, $state, $stateParams, project, analysis, BoxPlotService){
		
		this.analysisId=$stateParams.analysisId;
		this.analysis=analysis;
		this.project=project;
		
		project.generateView({
            viewType:'heatmapView', 
            labels:{
                row:{keys:project.dataset.row.keys}, 
                column:{keys:project.dataset.column.keys}
            },
            expression:{
                min: project.dataset.expression.min,
                max: project.dataset.expression.max,
                avg: project.dataset.expression.avg,
            }
        });
		
		$scope.$on("ui:filteredResults",function($event, results){
			
			var groups = analysis.params.data.map(function(selectionName){
				return _.find(project.dataset.column.selections, function(selection){return selection.name===selectionName;});
			});
			
       		$scope.boxPlotGenes = BoxPlotService.prepareBoxPlotData(project.dataset, results, 
         		groups,
         		analysis.randomId);
		});
		
	}]);
	return module;
});