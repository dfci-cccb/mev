(function(){
    define([], function(){


        return function($scope, $rootScope, GraphResource){
        	
        	$scope.graph = GraphResource.data;
        	
            $scope.predecessors = [];

            $scope.$on('GraphTransitionEvent', function(event, params){

                    GraphResource.update(params)

            })

            $scope.graphTransitionCallback = function(params){
            //Exposes graph transition event to scope
                $scope.$emit('GraphTransitionEvent', params)
            }


            $rootScope.$on('GraphCreateEvent', function(event, params){

                GraphResource.create(params)

            });

            $scope.$on('GraphTypeChangeEvent', function(event, graphParameters){
                $scope.graphParameters = graphParameters;
            });

        }

    })

})()

