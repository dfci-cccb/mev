define(['jquery','angular'], function(jquery, angular){
	angular.module( 'Mev.SetManager', [])
		.directive('selectionSetManager', [function (){
			  return {				  		  
				  scope: {
					  heatmapId: '@heatmapId',
					  heatmapData: '=heatmapData'
				  },
				  controller: 'SelectionSetManagerCtl',				  
				  restrict : 'EA',
				  templateUrl : '/container/view/elements/setmanager/selectionSetManager'
			  };
		}])
		.directive('selectionSetList', [function (){
			return {
				scope: {
					selections: '=mevSelections'
					, baseUrl: '@mevBaseUrl'
					, setSelected: '&mevSayHello'
					, dimension: '@mevDemintion'
				},
				restrict : 'EA',
				transclude : true,
				replace : true,
				controller: 'SelectionSetManagerCtl',
				templateUrl : '/container/view/elements/setmanager/selectionSetList',
				link : function (scope, iElement, iAttrs, controller){
					
					return		
				}				
			};
		}])
		.directive('selectionSetEditForm', [function (){
			return {
				restrict: 'EA',
				replace: true,
				require: '^selectionSetList',
				templateUrl : '/container/view/elements/setmanager/selectionSetEditForm',				
			};
		}])
		.directive('myIframe', function(){
		    var linkFn = function(scope, element, attrs) {
		        element.find('iframe').bind('load', function (event) {
		          //event.target.contentWindow.scrollTo(0,400);
		        });
		        
		    };
		    return {
		      restrict: 'EA',
		      scope: {
		        ngSrc:'@ngSrc',
		        height: '@height',
		        width: '@width',
		        scrolling: '@scrolling',
		        annotationsUrl: '=annotationsUrl'
		      },
		      template: '<iframe scrolling="no" class="frame" height="{{height}}" width="{{width}}" frameborder="0" border="0" marginwidth="0" marginheight="0" scrolling="{{scrolling}}" ng-src="{{annotationsUrl}}"></iframe>',
		      link : linkFn
		    };
		  })
		.controller('SelectionSetManagerCtl', 
	    ['$scope', '$element', '$attrs', '$routeParams', '$http', 'alertService', 
		function($scope, $element, $attrs, $routeParams, $http, alertService){
			
			$scope.sayHelloCtl = function() {
				alert($scope.heatmapId + ":" + $scope.heatmapData.column.selections.length + ":" + $scope.$id);
			};		
			
			//alert(angular.toJson($attrs) );
						
			$scope.selectedItem = null;
			$scope.selectedItemTmp = null;
			$scope.annotationsUrl="hello";
			
			$scope.setSelected = function(item){
				$scope.selectedItem = item;
				$scope.selectedItemTmp = angular.copy(item);
				//alert("selected: " + item.name);
			};
			$scope.saveItem = function(item){
				//alert("save: " + item.name + " old: " + $scope.selectedItem.name);
				$scope.selectedItem.name = item.name;
				$scope.selectedItem.properties = item.properties;
			};
			$scope.showAnnotations = function(selection, dimention, annotationSource){
				$scope.$emit('ViewAnnotationsEvent', selection, dimention, annotationSource);				
				
			};
			
			$scope.selectionParams = {column:{name:undefined},
									  row:{name:undefined}};

			$scope.addMergedSelection = function(dimension){
				
				var elements = $scope.heatmapData[dimension].selections.filter(function(d){
					
					return (d.setSelectionChecked == "true") ? true : false
						
				})
			 
				if (elements.length > 1){
					
					var selectedSets = [];
					
					elements.map(function(d){
						d.keys.map(function(j){
							if (selectedSets.indexOf(j) < 0){
								selectedSets.push(j)
							}
						})
					});
			     
					$http({
					     method:"POST", 
					url:"/dataset/" + $routeParams.datasetName + "/" 
					 + dimension
					 + "/selection/",
					data:{
					 	name: $scope.selectionParams[dimension].name,
					 	properties: {
					 		selectionDescription: '',
					     		selectionColor: '#'+Math.floor(Math.random()*0xFFFFFF<<0).toString(16),                     
					     	},
					         keys: selectedSets
					     }
					})
					.success(function(response){
					         $scope.$emit('SeletionAddedEvent', dimension);
					         var message = "Added selection with name " + $scope.selectionParams[dimension].name + ".";
					         var header = "Heatmap Selection Addition";
					          
					         alertService.success(message,header);
					})
					.error(function(data, status, headers, config) {
					     var message = "Couldn't add new selection. If "
					         + "problem persists, please contact us.";
					
					      var header = "Heatmap Selection Problem (Error Code: "
					         + status
					         + ")";
					              
					      alertService.error(message,header);
					});
			         
			     } else {
			    	 var message = "Cannot merge a single selection set.";
				
				      var header = "Heatmap Selection Info";
				              
				      alertService.info(message,header);
			     };
			};		
			
			$scope.addItem = function(item){
				//alert('in addItem');				
				$scope.$apply(function(){
					
					if(item.dimension.toLowerCase()=="column"){
						//remove selection if already present
						$scope.heatmapData.column.selections = jquery.grep($scope.heatmapData.column.selections, function(e, i){return e.name==item.name;}, true);
						//re-add the updated selection
						$scope.heatmapData.column.selections.push(item);
					}else{
						$scope.heatmapData.row.selections = jquery.grep($scope.heatmapData.row.selections, function(e, i){return e.name==item.name;}, true);
						$scope.heatmapData.row.selections.push(item);						
					}
				});
				$scope.$emit('SeletionAddedEvent', item.dimension.toLowerCase());
			};
			
			
		}]);
});

