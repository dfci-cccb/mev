(function(){

    var deps = [
        'angular', 'd3', 'jquery',
        './lib/hierarchical_clustering',
        './lib/uniform_noise',
        './lib/random_id',
        './lib/edge_selector',
        './lib/greater_than_comparison',
        './lib/complete_linkage',
        './lib/treemap_visualization'
    ]

    define(deps, function (angular, d3, jquery, hierarchicalClustering, uniformNoise, randomID, edgeSelector, greaterThan, completeLinkage, treemapVis) {

        angular.module('NG4P.Treemap_Visualization',[])
        .factory('EdgeLinkageStrategiesFactory', [function(){
            return {'complete': completeLinkage}
        }])
        .factory('TreemapDrawingFactory', [function(){
            return treemapVis
        }])
        .factory('NumberComparisonFactory', [function(){
            return {'greaterThan': greaterThan}
        }])
        .factory('RandomStringGeneratorFactory', [function(){
            return randomID
        }])
        .factory('EdgeSelectionTournamentFactory', [function(){
            return edgeSelector
        }])
        .factory('NoiseMethodsFactory', [function(){
            return {'uniform': uniformNoise }
        }])
        .factory('ClusterMethodsFactory', [function(){
            return {'hierarchical':hierarchicalClustering}
        }])
        .factory('TreemapOptionsFactory',[function(){
            return {
                'clusteringMethods':['hierarchical'],
                'layoutMethods':['slice-dice', 'slice', 'dice', 'squarify'],
                'nodeAreaValue':['uniform','size'],
                'nodeGroupValue':[true, false],
                'colorEdgeByGroup':[true,false]
            }
        }])
        .factory('TreemapParametersFactory', [function(){
            return {
                'clusteringMethod': undefined,
                'layoutMethod': undefined,
                'nodeAreaValue': undefined,
                'nodeGroupValue': undefined,
                'colorEdgeByGroup': undefined
            }
        }])
        .directive('treemapVisualization',
        ['$rootScope','TreemapDrawingFactory', 'ClusterMethodsFactory', 'NoiseMethodsFactory', 'EdgeSelectionTournamentFactory',
         'RandomStringGeneratorFactory', 'NumberComparisonFactory', 'EdgeLinkageStrategiesFactory', 'TreemapParametersFactory',
         function($rootScope, TreemapDrawing, ClusterMethods, NoiseMethods,
                   EdgeSelectionTourney, RandomString, NumberComparison, EdgeLinkage, TreemapParameters){
            return {
                'scope': {'graph':'=graph'},
                'restrict':'E',
                'link': function(scope, element, attributes){

                    var treemap = new TreemapDrawing(d3.select(element[0]))
                    var treemaparams = TreemapParameters

                    scope.$watch('graph', function(newval, oldval){

                      if(newval){
                       
                        $rootScope.$emit('drawNetworkEvent', 'Treemap', {

                          'clusteringMethods':'hierarchical',
                          'layoutMethods':'slice-dice',
                          'nodeAreaValue':'uniform',
                          'nodeGroupValue':true,
                          'colorEdgeByGroup':true

                        })

                      }

                    })

                    $rootScope.$on('drawNetworkEvent', function(event, networkType, networkParams){

                        if (!(networkType=='Treemap') || !scope.graph){
                            return
                        }



                        networkParams.margin={top: 40, right: 10, bottom: 40, left: 10}
                        networkParams.width = jquery('body').width() *.55
                        networkParams.height = jquery('body').height() *.80

                        if (networkParams.clusteringMethods == 'hierarchical'){
                            drawHierarchical()
                        }

                        function drawHierarchical(){
                            
                            var behaviors = {
                              'onClick':function(){return},
                              'onMouseOver':function(){return}
                            }

                            try {
                                treemap
                                    .draw(ClusterMethods[networkParams.clusteringMethods](scope.graph,
                                            NumberComparison.greaterThan,
                                            EdgeLinkage.complete,
                                            EdgeSelectionTourney, RandomString), scope.graph, NoiseMethods.uniform,  networkParams, behaviors)
                            } catch(e) {
                                console.log(e.message)
                            }
                        }


                    })

                }

            }
        }])

    })


})();
