drct.directive('visHeatmap', [function() {

	return {
			
		restrict: 'E',
		scope: {
			inputdata:"=",
			inputcolor:"=",
			showlabels: "=",
			width: "=",
			height: "=",
			marginleft: "=",
			marginright: "=",
			margintop: "=",
			marginbottom: "=",
			pushtomarked:"&"
		},
		link: function (scope, element, attrs) {
			
			var margin = {
					left: scope.marginleft,
					right: scope.marginright,
					top: scope.margintop,
					bottom: scope.marginbottom
				},
				width = scope.width - margin.left - margin.right,
				height = scope.height - margin.top - margin.bottom;
				
			var vis = d3.select(element[0])
				.append("svg")
				.attr("width", width + margin.left + margin.right)
				.attr("height", height + margin.top + margin.bottom)
				.append("g")
					.call(d3.behavior.zoom().scaleExtent([1, 10]).on("zoom", zoom))
				.append("g");
			
			function zoom() {
				vis.attr("transform", "translate(" + d3.event.translate + ")scale(" +d3.event.scale + ")");
			}
			
			scope.$watch('inputdata', function(newdata, olddata) {
							
				vis.selectAll('*').remove();
				
				if (!newdata) {
					return;
				}
				
				var threshold = 150;
				
				var colorScaleForward = function(j) {
					
					var value = d3.scale.linear()
						.domain(d3.extent(newdata.data, function(x){return x.value} ))
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
						.domain(d3.extent(newdata.data, function(x){return x.value} ))
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
						.rangeRoundBands([margin.left, margin.left + width]);
								
				var cellYPosition = d3.scale.ordinal()
						.domain(newdata.rowlabels)
						.rangeRoundBands([margin.top, margin.top + height]);
				
				if (scope.showlabels) {
				
					vis.append("g").attr("class", "xAxis").selectAll("text")
						.data(newdata.columnlabels)
						.enter()
						.append("text")
						.text(function(d){
							return d;
						})
						.attr("font-size", function() {
							if (cellXPosition.rangeBand() >= 12) {
								return 12
							} else {
								return cellXPosition.rangeBand()
							}
						})
						.attr("transform", "rotate(-90)")
						.attr("x", function(d) {
							return ( - (margin.top) );
						})
						.attr("y", function(d) {
							return cellXPosition(d) + cellXPosition.rangeBand()
						});
						
					vis.append("g").attr("class", "yAxis").selectAll("text")
						.data(newdata.rowlabels)
						.enter()
						.append("text")
						.text(function(d){
							return d;
						})
						.attr("font-size", function() {
							if (cellYPosition.rangeBand() >= 12) {
								return 12
							} else {
								return cellYPosition.rangeBand()
							}
						})
						.attr("x", function(d) {
							return margin.left + width + cellXPosition.rangeBand();
						})
						.attr("y", function(d) {
							return cellYPosition(d) + cellYPosition.rangeBand()
						});
					
				}
				
				vis.selectAll("rect")
					.data(newdata.data)
					.enter()
					.append("rect")
					.attr({
						"class": "heatmapcells",
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
					});

			});
			
			scope.$watch('inputcolor', function(newdata, olddata) {

				if (newdata == olddata | !newdata) {
					return;
				} else if (vis) {
					
					vis.selectAll('rect')
						.transition()
						.duration(500)
						.attr({
							"fill": function(d) {
								return "rgb(" + redColorControl(d.value, scope.inputcolor) + "," + greenColorControl(d.value, scope.inputcolor) + ","+ blueColorControl(d.value, scope.inputcolor)+")";
							}
						});
				}
				
				
				
			});
		}
	}
}]);