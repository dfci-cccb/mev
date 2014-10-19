(function(){
    define(['angular', './NetworkController', './lib/falseNetworkData'], function(angular, NetworkController, fN){


        angular.module('NetworkExplorer', [])
        .factory('GraphResource', ['$resource','$q', function($resource, $q){
           return {
        	   resource: undefined,
        	   create: function(params){
        		   //Function to call for graph parameters
        		   
        		   var self = this;
        		   
        		   self.resource = $resource(params)
        		   
        		   return
        	   },
        	   data: fN
           }
        }])
        .controller('NetworkController', ['$scope', '$rootScope', 'GraphResource', NetworkController])
        


    })

})()

