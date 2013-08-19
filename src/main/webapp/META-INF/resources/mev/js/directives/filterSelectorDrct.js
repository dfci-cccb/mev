drct.directive('filterSelector', ['$compile', function(compile) {
	return {
		
		restrict: 'C',
		scope: {
			inputfield: "=",
			pushToParamsIn: "&",
			pullFromParamsIn: "&",
			clearFunction: "&",
			inputcolor: "="
		},
		template: "<div class='holder'></div>",
		link: function (scope,element, attrs) {
			
			if (!compile || !scope) {
				return
			}
			
			if (scope.inputfield.info.type == "nominal") {
			
				var el = compile(
					"<hr>"+
					"<p>{{inputfield.name}}</p>" +
					'<div class="input-prepend">'+
      					'<span class="add-on"><i class="icon-search"></i></span>'+
      					'<input class="span2" ng-model="selectedfilter" type="text">'+
    				"</div>"+
					"<button class='btn' ng-click='pushToParamsIn({key:inputfield.reference, value:selectedfilter})'>Apply</button>" +
		        	"<button class='btn' ng-click='pullFromParamsIn({key:inputfield.reference})'>Clear</button>"
				)(scope);
				$('.holder', element).append(el);
				
			} else if (scope.inputfield.info.type == "quantitative") {
				var selectedfilter = function(right, left) {
					return "[" + right + "," + left + "]";
				}
				var el = compile(
					"<hr>" +
					"<p>{{inputfield.name}}</p>" +
					'Range:<br>'+
					'<input type="text" ng-model="rightbound" class="input-small" placeholder="Left Bound">'+
					'<input type="text" ng-model="leftbound" class="input-small" placeholder="Right Bound"><br>'+
					"<button class='btn' ng-click='pushToParamsIn({key:inputfield.reference, value:selectedfilter(leftbound, rightbound\)})'>Apply</button>" +
		        	"<button class='btn' ng-click='pullFromParamsIn({key:inputfield.reference})'>Clear</button>"
				)(scope);
				$('.holder', element).append(el);
			} else if (scope.inputfield.info.type == "ordinal") {
			    scope.selectedfilter = "Filter on:";
			    var applyfilter = function(selection) {
			      scope.pushToParamsIn({key:inputfield.reference, value:selection});
			    }
				var el = compile(
					"<hr>"+
					"<p>{{inputfield.name}}</p>" +
					"<div class='btn-group'>"+
					  "<button class='btn'>{{selectedfilter}}</button>"+
					  "<button class='btn dropdown-toggle' data-toggle='dropdown'>"+
					     "<span class='caret'></span>"+
					  "</button>"+
					  "<ul class='dropdown-menu'>"+
					    "<li ng-repeat='class in inputfield.info.range' ng-click='applyfilter({{class}})'>{{class}}</li>"+
					  "</ul>"+
					"</div>"
				)(scope);
				$('.holder', element).append(el);
				
			}
			
		}
	}
}]);