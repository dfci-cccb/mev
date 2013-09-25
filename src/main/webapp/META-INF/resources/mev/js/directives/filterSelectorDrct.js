drct.directive('tupleSelector', ['$compile', function(compile) {
	return {
		restrict: 'E',
		scope: {
			inputfield: "="
		},
		template: "<input ng-model='value' />" +
					"<input ng-model='operator' />",
		link: function (scope, element, attrs) {
			
			scope.value = scope.inputfield.value;
			scope.operator = scope.inputfield.operator;
			
			scope.$watch('value', function(newdata, olddata) {
				scope.inputfield.value = newdata;
			})
			
			scope.$watch('operator', function(newdata, olddata) {
					scope.inputfield.operator = newdata;
			})
		}
	}
}]);
