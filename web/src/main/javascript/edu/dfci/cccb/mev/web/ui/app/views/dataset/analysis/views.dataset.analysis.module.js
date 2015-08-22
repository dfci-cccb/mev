define(["ng", "lodash", 
        "./hcl/views.dataset.analysis.hcl.module", 
        "./kmeans/views.dataset.analysis.kmeans.module",
        "./ttest/views.dataset.analysis.ttest.module",
        "./fisher/views.dataset.analysis.fisher.module",
        "./anova/views.dataset.analysis.anova.module",
        "./limma/views.dataset.analysis.limma.module",
        "./deseq/views.dataset.analysis.deseq.module",
        "./nmf/views.dataset.analysis.nmf.module",
        "./pca/views.dataset.analysis.pca.module",
        "./survival/views.dataset.analysis.survival.module"], function(ng, _){
	var module = ng.module("mui.views.dataset.analysis", ["mui.widgets.analysis", 
	                                                      "mui.views.dataset.analysis.hcl", 
	                                                      "mui.views.dataset.analysis.kmeans",
	                                                      "mui.views.dataset.analysis.ttest",
	                                                      "mui.views.dataset.analysis.fisher",
	                                                      "mui.views.dataset.analysis.anova",
	                                                      "mui.views.dataset.analysis.limma", 
	                                                      "mui.views.dataset.analysis.deseq",
	                                                      "mui.views.dataset.analysis.nmf",
	                                                      "mui.views.dataset.analysis.pca",
	                                                      "mui.views.dataset.analysis.survival",
	                                                      "Mev.AnalysisAccordionCollection"]);
	module.config(["$stateProvider", "$urlRouterProvider", "AnalysisTypes", function($stateProvider, $urlRouterProvider, AnalysisTypes){				
		$stateProvider		
		.state("root.dataset.analysis", {			
			url: "analysis/{analysisType}/{analysisId}/",			
			parent: "root.dataset",	
			displayName: "{{analysis.name}} analysis",
			templateProvider: ["$stateParams", "$http", function($stateParams, $http){
				console.debug("root.dataset.analysis templateProvider ", $stateParams.analysisType);
				var templateUrl="app/views/dataset/analysis/default/view.analysis.default.tpl.html";
				
				
				var analysisType = AnalysisTypes[$stateParams.analysisType];
				
				if(analysisType && analysisType.shortName){
					templateUrl=templateUrl.replace("default", analysisType.shortName).replace("default", analysisType.shortName);
				}
				console.debug("analysis templateUrl:", templateUrl);
   	     		return $http.get(templateUrl).then(function(response){
   	     			console.debug("analysis templateProvider response:", templateUrl, response);
   	     			return response.data;
   	     		});
			}],
			controllerProvider: ["$state", "$stateParams", function($state, $stateParams){
				console.debug("DatasetAnalysisVM", $state, $state.is("root.dataset.analysis"), AnalysisTypes[$stateParams.analysisType]);		
				
				return AnalysisTypes[$stateParams.analysisType].viewModel;
//				if($state.is("root.dataset.analysis")){
//					$state.go(".result", {resultId: analysis.result[0].name});
				
//				}
								
			}],
			controllerAs: "DatasetAnalysisVM",
			resolve: {
				analysis: ["$stateParams", "project", "dataset", function($stateParams, project, dataset){
					console.debug("root.dataset.analysis resolve", $stateParams, project, dataset);
					var analysis = _.find(dataset.analyses, function(analysis){ return analysis.name===$stateParams.analysisId; });					
					console.debug("root.dataset.analysis resolve analyis", analysis);					
					return analysis;
				}]
			}
		})				
		.state("root.dataset.analysis.result", {			
			url: "result/:resultId",
			parent: "root.dataset.analysis",
			template: "<div>Result: {{AnalysisResultVM.resultId}}" +
//						"<div>{{AnalysisResultVM.result}}</div>" +
						"<project-analysis-result result-item=\"AnalysisResultVM.result\" result-name=\"{{AnalysisResultVM.result.name}}\"></project-analysis-result>" +
					  "</div>",
			controller: ["$stateParams", "result", function($stateParams, result){
				this.resultId=$stateParams.resultId;
				this.result=result;
			}],
			controllerAs: "AnalysisResultVM", 
			resolve: {
				result: ["$stateParams", "analysis", function($stateParams, analysis){
					console.debug("root.dataset.analysis.result resolve", $stateParams, analysis);
					var result = _.find(analysis.result, function(result){ return result.name===$stateParams.resultId; });					
					console.debug("root.dataset.analysis.result resolved", result);
					return result;
				}]
			}
		});
	}]);
	
	module.config(["resultsTableDefaultsProvider", function(resultsTableDefaultsProvider){
		resultsTableDefaultsProvider.setOrdering("pValue");
	}]);
	
	module.directive("mevAnalysis", ["$compile", function($compile){
		return {
			restrict: "AE",
			scope: {
				analysis: "=mevAnalysis",				
			},
			template: "<div mev-analysis-result></div>",			
			controller: function(){},			
			controllerAs: "MevAnalysisVM",
			link: function(scope, elm, attr, ctrl){
				
			}
		};
	}])
	.directive("mevAnalysisResult", ["$compile", function($compile){
		return {
			restrict: "AE",
			scope: {
				analysis: "=mevAnalysisResult",				
			},
			template: "<div></div>",			
			controller: function(){},			
			controllerAs: "MevAnalysisResultVM",
			link: function(scope, elm, attr, ctrl){
				
			}
		};
	}]);
	
	return module;
});
