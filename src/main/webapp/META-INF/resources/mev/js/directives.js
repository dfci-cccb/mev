'use strict';

/* Directives */

angular.module('myApp.directives', [])
.directive('appVersion', ['version', function(version) {
	return function(scope, elm, attrs) {
	  elm.text(version);
	};
}])
.directive('filterSelector', ['$compile', function(compile) {
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
			    scope.selectedfilter = "Filter on:"
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
}])
.directive('visHeatmap', [function() {

	return {
			
		restrict: 'E',
		scope: {
			inputdata:"=",
			inputcolor:"=",
			pushtomarked:"&"
		},
		link: function (scope, element, attrs) {
			
				var visparams = {
					width : 600,
					height : 700,
					columnlabelgutter : 80,
					rowlabelgutter : 80,
				};
				
				var vis = d3.select(element[0])
					.append("svg")
					.attr("width", visparams.width )
					.attr("height", visparams.height);
			
			scope.$watch('inputdata', function(newdata, olddata) {
							
				vis.selectAll('*').remove();
				
				if (!newdata) {
					return;
				}
				    
				var values = newdata.data.map(function(x){return x.value});
				
				var threshold = 150;
				
				var colorScaleForward = function(j) {	 
					var value = d3.scale.linear()
						.domain([d3.min(values), d3.max(values)])
						.rangeRound([0, 255]);
					var output = 0;
					if (value(j) >= threshold ) {
						var layer2 = d3.scale.linear()
							.domain([125,255])
							.rangeRound([0,255]);
						output = layer2(value(j));  	
					}
					return output;
				};

				var colorScaleReverse = function(j) {	 
					var value = d3.scale.linear()
						.domain([d3.min(values), d3.max(values)])
						.rangeRound([255, 0]);
					var output = 0;
					if ( value(j) >= threshold ) {
						var layer2 = d3.scale.linear()
							.domain([255,125])
							.rangeRound([255, 0]);
						output = layer2(value(j));  	
					}
					return output;
				};

				var redColorControl = function(j, code) {
					var output = 0;
					if (code == "red") {
						output = colorScaleForward(j);
					} else {
						output = colorScaleForward(j);
					}
					return output;
					};

				var blueColorControl = function(j, code) {
					var output = 0;
					if (code == "blue") {
						output = colorScaleReverse(j);
					}
					return output;
				};

				var greenColorControl = function(j, code) {
					var output = 0;

					if (code == "red") {
						output = colorScaleReverse(j);
					} else {
						output = colorScaleForward(j);
					}

					return output;
				};
				
				var cellXPosition = d3.scale.ordinal()
						.domain(newdata.columnlabels)
						.rangeRoundBands([visparams.rowlabelgutter, visparams.width]);
								
				var cellYPosition = d3.scale.ordinal()
						.domain(newdata.rowlabels)
						.rangeRoundBands([visparams.columnlabelgutter, visparams.height]);

				var squares = vis.selectAll("rect")
						.data(newdata.data)
						.enter()
						.append("rect")
						.attr({
							"height": function(d){
								return .98*(cellYPosition(newdata.rowlabels[1]) - cellYPosition(newdata.rowlabels[0]));
							},
							"width": function(d){
								return .98*(cellXPosition(newdata.columnlabels[1]) - cellXPosition(newdata.columnlabels[0]));
							},
							"x": function(d, i) { return cellXPosition(d.col); },
							"y": function(d, i) { return cellYPosition(d.row); },
							"fill": function(d) {
								return "rgb(" + redColorControl(d.value, "red") + "," + greenColorControl(d.value, "red") + ","+ blueColorControl(d.value, "red")+")";
								
							},
							"value": function(d) { return d.value; },
							"index": function(d, i) { return i; },
							"row": function(d, i) { return d.row; },
							"column": function(d, i) { return d.col; }
						})
						.on('mouseover', function(d) {
							vis.append("text")
								.attr({
									"id": "tooltip",
									"x": cellXPosition(d.col),
									"y": cellYPosition(d.row) + 40,
								})
								.text("Gene: " + d.row + " Point: " + d.col + "\n Value: " + d.value);
						})
						.on('mouseout', function() { d3.select('#tooltip').remove(); })
						.on('click', function(d) {	
							scope.$apply( function() {
								scope.pushtomarked({inputindecies:d.row, inputdimension:"row"});
							});							
						});
						
				var xAxis = d3.svg.axis().scale(cellXPosition).orient("bottom");
				var yAxis = d3.svg.axis().scale(cellYPosition).orient("left");
				
				vis.append('g').attr("transform", "translate(0,"+ (visparams.rowlabelgutter - 20) +")").call(xAxis);
				vis.append('g').attr("transform", "translate(" + (visparams.columnlabelgutter) +",0)").call(yAxis);
				
				scope.changeColor = function(newcolor) {
				
					vis.selectAll('rect')
						.transition()
						.duration(500)
						.attr({
							"fill": function(d) {
								return "rgb(" + redColorControl(d.value, newcolor) + "," + greenColorControl(d.value, newcolor) + ","+ blueColorControl(d.value, newcolor)+")";
							}
						});
				};
			});
			
			scope.$watch('inputcolor', function(newdata, olddata) {

				if (newdata == olddata) {
					return;
				} 
				if (!newdata) {
				    return;	
				}
				else {
					scope.changeColor(newdata)
				}
				
				
				
			});
		}
	}
}]);
