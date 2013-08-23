drct.directive('visHeatmap', [function() {

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
				.attr("height", visparams.height)
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
						
				var xAxis = vis.append("g").selectAll("text")
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
							return (- visparams.columnlabelgutter );
						})
						.attr("y", function(d) {
							return cellXPosition(d) + cellXPosition.rangeBand()
						});
						
				var yAxis = vis.append("g").selectAll("text")
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
							return 1;
						})
						.attr("y", function(d) {
							return cellYPosition(d) + cellYPosition.rangeBand()
						});

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
						});
						
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