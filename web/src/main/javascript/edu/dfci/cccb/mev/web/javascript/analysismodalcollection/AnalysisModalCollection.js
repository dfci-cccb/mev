define(['angular', 'alertservice/AlertService'], function(angular){
	
	var module = angular.module('Mev.AnalysisModalCollection', ['Mev.AlertService'])
	
	module.path = "container/javascript/analysismodalcollection/"
	
	module.service('')
	.directive('modalAnova',[ 'alertService',
        function(alertService) { 
            return {
                restrict : 'C',
                scope : {
                	dataset : '=heatmapDataset'
                },
                templateUrl : "/container/view/elements/anovaModalBody",
                link : function(scope, elems, attrs) {
                    
                    scope.params = {
                        name: undefined,
                        selections: [],
                        dimension: {
                            name : 'Columns',
                            value : 'column'
                        },
                        pvalue: undefined,
                        mtc: false
                    };
                    
                    scope.options = {
                        dimensions : [{
                            name : 'Rows',
                            value : 'row'
                        }, {
                            name : 'Columns',
                            value : 'column'
                        }]
                    };
                    
                    scope.addSelection = function(decked){
                      if (scope.params.selections.indexOf(decked.name) < 0 && scope.params.selections.length < 3) {
                          scope.params.selections.push(decked.name)
                      }
                    };
                    
                    scope.$watch('params.dimension', function(newval){
                        if(newval){
                            scope.params.selections = []
                        }
                    })
                    
                    scope.testInit = function(){
                        
                        var message = "Starting ANOVA for "
                            + scope.params.name + " analysis.";

                        var header = "ANOVA";
                        
                        if (scope.params.selections.length < 2) {
                            
                            message = "Can't start ANOVA for "
                                + scope.params.name + " with less than two groups.";

                            header = "ANOVA";
                            
                            alertService.info(message,header);
                            return
                        }
                        
                        alertService.info(message,header);
                        
                        var analysisData = {
                        	datasetName : scope.dataset.datasetName,
                        	analysisType : "anova",
                        	analysisName : scope.params.name,
                        	analysisParams : 'dimension='
                                + scope.params.dimension.value
                                + ',pval='
                                + scope.params.pvalue
                                + ',mtc='
                                + scope.params.mtc
                        }
                        
                        
                        scope.dataset.analysis
                        .postf(analysisData, JSON.stringify(scope.params.selections),
	                        function(data, status, headers, config) {
	                            
                        		scope.dataset.loadAnalyses();
                        		
	                            var message = "ANOVA for "
	                            	+ scope.params.name + " complete!";
	
	                            var header = "ANOVA";
	                                         
	                            alertService.success(message,header);
	                                    
	                         }, function(data, status, headers, config) {
	                            
	                            var message = "Could not perform ANOVA. If "
	                                + "problem persists, please contact us.";
	                            var header = "Clustering Problem (Error Code: "
	                                + status
	                                + ")";
	                            alertService.error(message,header);
                            
                        });
                    };
                }
            }
    }])
    .directive('modalFTest', ['alertService', function(alertService){
    	
    	return {
    		restrict: 'C',
    		scope: {
    			dataset : '=heatmapDataset'
    		},
    		templateUrl : module.path + 'templates/fTest.tpl.html',
    		link : function(scope, elements, attributes){
    			
    			scope.params = {
    				name: undefined,
    				dimension: {
                        name : 'Rows',
                        value : 'row'
                    },
                    population: undefined,
    				selection1: undefined,
    				selection2: undefined,
    				threshold: undefined,
    				simulate: undefined,
    				hypothesis: undefined
    			}
    			
    			scope.options = {
					dimension : [{name : 'Rows', value : 'row'}, {name : 'Columns',value : 'column'}],
                    simulate: [{name:'True', value:true}, {name:'False', value:false}],
                    hypothesis: [{name:'Two-sided', value:'two.sided'},{name:'Greater', value:'greater'},{name:'Less', value:'less'}]
    			}
    			
    			scope.testInit = function(){
    				
    				var success = function(data, status, headers, config){

						scope.dataset.loadAnalyses()
                		var message = "Fisher's Test analysis for "
                			+ scope.params.name + " complete!"

                        var header = "Fisher's Test Analysis"

                        alertService.success(message,header)
    				}
    				
    				var failure = function(data, status, headers, config) {
                        var message = "Could not perform Fisher's Test analysis. If "
                            + "problem persists, please contact us.";
                        var header = "Fisher's Test Problem (Error Code: "
                            + status
                            + ")";
                        alertService.error(message,header);  
    				}
    				
    				var experiments = [];

    				var groups = [];
    				
    				var selection_dimension = (scope.params.dimension.value == 'row') ? 'column' : 'row'
    					
    				if (scope.dataset.selections[selection_dimension].keys > 1 
    						|| scope.dataset.selections[selection_dimension].keys < 1){
    					console.log("Failed T-Test Initialization")
    					failure({}, "Initialization Failure")
    					return
    				}
    				
    				
    				for (selection in scope.dataset.selections[selection_dimension]) {
    					
    					if (scope.dataset.selections[selection_dimension][selection].name == scope.params.selection1.name ){
    						groups.push(scope.dataset.selections[selection_dimension][selection].keys)
    					}
    				}
    				
    				for (selection in scope.dataset.selections[selection_dimension]) {
    					if (scope.dataset.selections[selection_dimension][selection].name == scope.params.selection2.name ){
    						groups.push(scope.dataset.selections[selection_dimension][selection].keys)
    					}
    				}
    				
					var experiments = []
					
					console.log(scope.params.population)
				
					for (population in scope.params.population.keys) {
						
						experiments.push({
							dimension: scope.params.dimension.value,
							groups: groups,
							population: scope.params.population.keys[population],
							threshold: parseFloat(scope.params.threshold)
    					})
    					
    				}
    				

					var tables = []
					
					for (experiment in experiments){
						
						
						try {
							tables.push(scope.dataset.expression.statistics().contingency(experiments[experiment]) )
						} catch (err) {
							
							failure({}, "Initialization Failure ("+err.message+")")
							return
						}
						
					}
					
					for (table in tables){
						
						console.log({
	    					m: tables[table][0].above,
	    					n: tables[table][1].above,
	    					s: tables[table][0].below,
	    					t: tables[table][1].below,
	    					hypothesis: scope.params.hypothesis.value,
	    					simulate: scope.params.simulate.value
	    				})
						
						scope.dataset.analysis.post3({
							datasetName:scope.dataset.datasetName,
							analysisType:'fisher',
							analysisName:scope.params.name + "-" + scope.params.population.keys[table]
						}, {
	    					m: tables[table][0].above,
	    					n: tables[table][1].above,
	    					s: tables[table][0].below,
	    					t: tables[table][1].below,
	    					hypothesis: scope.params.hypothesis.value,
	    					simulate: scope.params.simulate.value
	    				}, success, failure)
						
					}
					
    			}
    		}
    	}
    	
    }])
    .directive('modalWilcoxon', ['alertService', function(alertService){
    	
    	return {
    		restrict: 'C',
    		scope: {
    			dataset : '=heatmapDataset'
    		},
    		templateUrl : module.path + 'templates/wilcoxonTest.tpl.html',
    		link : function(scope, elements, attributes){
    			
    			console.log(scope.dataset)
    			
    			scope.params = {
    				name: undefined,
    				selection1: undefined,
    				selection2: undefined,
    				pair: undefined,
    				confidentInterval: undefined,
    				hypothesis: undefined
    			}
    			
    			scope.options = {
    				pair: [{name:'True', value:true}, {name:'False', value:false}],
                    confidentInterval: [{name:'True', value:true}, {name:'False', value:false}],
                    hypothesis: [{name:'Two-sided', value:'two.sided'},{name:'Greater', value:'greater'},{name:'Less', value:'less'}]
    			}
    			
    			scope.testInit = function(){
    				
    				var success = function(data, status, headers, config){

						scope.dataset.loadAnalyses()
                		var message = "Wilcoxon Test analysis for "
                			+ scope.params.name + " complete!"

                        var header = "Wilcoxon Test Analysis"

                        alertService.success(message,header)
    				}
    				
    				var failure = function(data, status, headers, config) {
                        var message = "Could not perform Wilcoxon Test analysis. If "
                            + "problem persists, please contact us.";
                        var header = "Wilcoxon Test Problem (Error Code: "
                            + status
                            + ")";
                        alertService.error(message,header);  
    				}
    				
    				scope.dataset.analysis.post4({
						datasetName:scope.dataset.datasetName,
						analysisType:'wilcoxon',
						analysisName:scope.params.name,
						first: scope.params.selection1,
    					second: scope.params.selection2,
    					pair: scope.params.pair.value,
    					confidentInterval: scope.params.confidentInterval.value,
    					hypothesis: scope.params.hypothesis.value
					}, success, failure)
    			}
    		}
    	}
    	
    }])
    .directive('modalTTest',['alertService',
        function(alertService) { 
            return {
                restrict : 'C',
                scope : {
                	dataset : '=heatmapDataset'
                },
                templateUrl : "/container/view/elements/tTestModalBody",
                link : function(scope, elems, attrs) {
                    
                    scope.params = {
                            name: undefined,
                            sampleType: {label:"", url:""},
                    		pValue: 0.05,
                    		multitestCorrection: false,
                    		assumeEqualVariance: false
                    };
                    
                    scope.options = {
                    		sampleTypes: [{label: "one sample", url:"one_sample_ttest"}, 
                    		        {label: "two sample", url:"two_sample_ttest"}]
                    };
                    scope.isOneSample = function(){
                		return scope.params.sampleType.url=='one_sample_ttest';
                	};
                	scope.isTwoSample = function(){
                		return scope.params.sampleType.url=='two_sample_ttest';
                	};
                	scope.getPostData = function(){
                		var postRequest = {
                			name: scope.params.name,
                			pValue: scope.params.pValue,
                			multTestCorrection: scope.params.multitestCorrection,
                			experimentName: scope.params.analysisExperiment.name
                		};
                		if(scope.isOneSample()){                                			
                			postRequest.userMean=scope.params.userMean;
                		}else{
                			postRequest.controlName=scope.params.analysisControl.name;
                		}
                		if(scope.isTwoSample()){
                			postRequest.assumeEqualVariance=scope.params.assumeEqualVariance
                		}
                		return postRequest;                                		
                	};                                	
                    scope.testInit = function(){
                        

                    	scope.dataset.analysis.post({
                        	datasetName : scope.dataset.datasetName, 
                            analysisType : scope.params.sampleType.url
                            
                    	}, scope.getPostData(), 
                        function(data, status, headers, config) {
                    	
                    		scope.dataset.loadAnalyses()
                    		var message = "t-Test analysis for "
                    			+ scope.params.name + " complete!";

                            var header = "t-Test Analysis";
                                         
                            alertService.success(message,header);
                                    
                        },
                        
                        function(data, status, headers, config) {
                            
                            var message = "Could not perform t-Test. If "
                                + "problem persists, please contact us.";
                            var header = "Clustering Problem (Error Code: "
                                + status
                                + ")";
                            alertService.error(message,header);                                            
                            
                        });
                    };
                }
            };
    }])
    .directive('modalHierarchical', ['alertService',
        function(alertService) {

            return {
                restrict : 'C',
                templateUrl : "/container/view/elements/hierarchicalbody",
                scope : {
                	dataset : "=heatmapDataset"
                },
                link : function(scope, elems, attrs) {
                    
                    scope.options = {
                            metrics : [{name:"Euclidean", value:"euclidean"},
                                       {name:"Manhattan", value:"manhattan"},
                                       {name:"Pearson", value:"pearson"}],
                            linkage : [{name:"Complete", value:'complete'},
                                       {name:"Average", value:'average'},
                                       {name:"Single", value:'single'}],
                            dimensions : [{
                                            name : 'Rows',
                                            value : 'row'
                                        }, {
                                            name : 'Columns',
                                            value : 'column'
                                        }]
                    };
                    
                    scope.params = {
                            metric : scope.options.metrics[0],
                            dimension : scope.options.dimensions[1],
                            linkage : scope.options.linkage[0],
                            name : undefined
                    }
                    
                    scope.params.selectedMetric = {name:"Euclidean", value:"euclidean"}

                    scope.testInit = function() {

                        var message = "Started clustering analysis for "
                            + scope.params.name;

                        var header = "Hierarchical Clustering Analysis";
                         
                        alertService.info(message,header);

                        var analysisData = {
                        	name : scope.params.name,
                        	dimension : scope.params.dimension.value,
                        	metric : scope.params.metric.value,
                        	linkage : scope.params.linkage.value
                        };
                        
//                        scope.dataset.analysis.postf({
//                            datasetName : scope.dataset.datasetName,
//                            analysisType : 'hcl',
//                            analysisName : analysisData.name,
//                            analysisParams : analysisData.dimension + ','
//                                + analysisData.metric + ','
//                                + analysisData.linkage
//                        }, {},
                        scope.dataset.analysis.post({
                        	datasetName : scope.dataset.datasetName, 
                            analysisType : 'hcl'
                            
                    	}, analysisData, 
                        function(data, status, headers, config) {
                            
                        	scope.dataset.loadAnalyses();
                        	
                            var message = "Clustering analysis for "
                                + scope.params.name + " complete!";

                            var header = "Hierarchical Clustering Analysis";
                             
                            alertService.success(message,header);
                                                                
                        },
                        function(data, status, headers, config) {
                            
                            var message = "Could not perform clustering. If "
                                + "problem persists, please contact us.";
                            var header = "Clustering Problem (Error Code: "
                                + status
                                + ")";
                            alertService.error(message,header);
                            
                        });

                    };

                    function resetSelections() {
                        scope.params = {
                                metric : scope.options.metrics[0],
                                dimension : scope.options.dimensions[1],
                                linkage : scope.options.linkage[0],
                                name : undefined
                        }
                    }

                }

            };

        }])
        .directive(
            'modalKmeans',['alertService',
            function(alertService) {

                return {
                    restrict : 'C',
                    scope : {
                    	dataset : "=heatmapDataset"
                    },
                    templateUrl : "/container/view/elements/kMeansBody",
                    link: function(scope){
                        
                        scope.options = {
                                'dimensions':[{'name': 'Rows', 'value':'row'},
                                              {'name': 'Columns', 'value':'column'} ],
                                'clusters':[2, 3, 4, 5, 6, 7, 8],
                                'metrics':[{'name': 'Euclidean', 'value':'euclidean'} ],
                                'iterations': [100, 1000]
                        }
                        
                        scope.params = {
                                'analysisName':'',
                                'analysisDimension':scope.options.dimensions[0],
                                'analysisClusters': scope.options.clusters[3],
                                'analysisMetric':scope.options.metrics[0],
                                'analysisIterations':scope.options.iterations[0],
                                'analysisConvergence': 0
                        }
                        
                        scope.testInit = function(){
                            
                            var message = "Started K-Means analysis for "
                                + scope.params.analysisName;

                            var header = "K-Means Clustering Analysis";
                             
                            alertService.info(message,header);
                            
                            var analysisData = {
                            	name : scope.params.analysisName,
                            	dimension : scope.params.analysisDimension,
                            	clusters : scope.params.analysisClusters,
                            	metric : scope.params.analysisMetric,
                            	iterations : scope.params.analysisIterations,
                            	convergence : scope.params.analysisConvergence
                            };
                            
                            scope.dataset.analysis.postf({

	                            datasetName : scope.dataset.datasetName,
	                            analysisType : 'kmeans',
	                            analysisName : analysisData.name,
	                            analysisParams : "dimension=" + analysisData.dimension.value
	                                + ",k=" + analysisData.clusters
	                                + ",metric=" + analysisData.metric.value
	                                + ",iterations=" + analysisData.iterations
	                                + ",convergence=" + analysisData.convergence
                            }, {},
                            
                            function(data, status, headers, config) {
                                
                            	scope.dataset.loadAnalyses();
                            	
                                var message = "K-Means analysis for "
                                    + scope.params.analysisName + " complete!";

                                var header = "K-Means Clustering Analysis";
                                 
                                alertService.success(message,header);
	                            
	                        },
                                            
                            function(data, status, headers, config) {
                                        
                                var message = "Could not perform k-means clustering. If "
                                    + "problem persists, please contact us.";
                                var header = "Clustering Problem (Error Code: "
                                    + status
                                    + ")";
                                alertService.error(message,header);
                                
                            });
                        }
                    }

                };

            }])
            
            .directive('modalLimma', [ "alertService",
                function(alertService) {

                    return {
                        restrict : 'C',
                        templateUrl : "/container/view/elements/limmaBody",
                        scope : {dataset : "=heatmapDataset"},
                        link : function(scope, elems, attrs) {

                            scope.available = {
                            		
	                            'dimensions' : [{
	                                name : "Column",
	                                value : "column"
	                            }],
	                            'species' : [{
		                                name : "Human",
		                                value : "human"
		                            },{
		                                name : "Rat",
		                                value : "rat"
		                            },{
		                                name : "Mouse",
		                                value : "mouse"
	                            }],
	                            'goType':[{
		                                name : "Biological Process",
		                                value : "BP"
		                            },{
		                                name : "Molecular Function",
		                                value : "MF"
		                            },{
		                                name : "Cellular Component",
		                                value : "CC"
	                            }],
	                            'testType':[{
	                                name : "Fisher",
	                                value : "Fisher"
	                            },{
	                                name : "Kolmogorov-Smirnov",
	                                value : "KS test"
	                            }],
                            }
                            
                            scope.params = {
                                	
                        		name : undefined,
                        		dimension : scope.available.dimensions[0],
                        		experiment : undefined,
                        		control : undefined,
                        		species : scope.available.species[0],
                        		goType: scope.available.goType[0],
                        		testType: scope.available.testType[0],
                        		goAnalysis: false
                        	
                        	};
                            
                            

                            scope.testInit = function(goAnalysis) {
                            	
                            	//Fail if no selections
                            	
                            	if (!scope.params.control || !scope.params.experiment || !scope.params.name){
                            		
                            		var message =  
                                        'Undefined parameters for LIMMA.';

                                    var header = "LIMMA Analysis Error";
                                     
                                    alertService.error(message,header);
                                    scope.params.name = undefined;
                    				return
                            		
                            		
                            		return
                            	}
                            	
                            	//Grab keys for each selection
                            	var controlSet = scope.dataset.selections[scope.params.dimension.value]
                            		.filter(function(group){
                            			return group.name == scope.params.control.name
                            		})[0].keys
                            		
                            	var experimentSet = scope.dataset.selections[scope.params.dimension.value]
	                        		.filter(function(group){
	                        			return group.name == scope.params.experiment.name
	                        		})[0].keys
	                        	
	                        	//Fail if matching elements in sets
	                        	
	                        	for (var i = 0; i < controlSet.length; i++){
	                        		for (var j = 0; j < experimentSet.length; j++){
	                        			if (controlSet[i] == experimentSet[j] ){
	                        				
	                        				var message =  
	                                            'Set intersection is not empty for LIMMA.';

	                                        var header = "LIMMA Analysis Error";
	                                         
	                                        alertService.error(message,header);
	                                        scope.params.name = undefined;
	                        				return
	                        			}
	                        		}
	                        	}
                  
                                var message = "Started limma analysis for "
                                    + scope.params.name;

                                var header = "LIMMA Analysis";
                                 
                                alertService.info(message,header);

                                var analysisData = {
                            		analysisType: 'limma',
                                	datasetName: scope.dataset.datasetName,
                                	analysisName : scope.params.name,
                                	
                                	dimension : scope.params.dimension.value,
                                	experiment : scope.params.experiment.name,
                                	control : scope.params.control.name,
                                	species : (goAnalysis) ? scope.params.species.value : undefined,
                                	go : (goAnalysis) ? scope.params.goType.value : undefined,
                                	test : (goAnalysis) ? scope.params.testType.value : undefined
                                };
                                
                                scope.dataset.analysis.post3(analysisData, {
                                	
                                },
                                function(data, status, headers, config) {
                                    
                                	scope.dataset.loadAnalyses();
                                	
                                    var message = "Completed limma analysis for "
                                        + analysisData.analysisName;

                                    var header = "LIMMA Analysis Complete";
                                     
                                    alertService.success(message,header);
                                    
                                    
                                },
                                function(data, status, headers, config) {
                                    var message = "Error on limma analysis for "
                                        + analysisData.analysisName;

                                    var header = "LIMMA Analysis Problem (Error Code: "
                                        + status
                                        + ")";
                                     
                                    alertService.error(message,header);
                                    
                                });                                              
                                


                                scope.params.name = undefined;

                            };

                        }

                    };

                }])
                

            	return module


	
});