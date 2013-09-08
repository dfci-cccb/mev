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
			
			var svg = d3.select(element[0])
				.append("svg")
				.attr("width", width + margin.left + margin.right)
				.attr("height", height + margin.top + margin.bottom)
			
			scope.$watch('inputdata', function(newdata, olddata) {
							
				svg.selectAll('*').remove();
				
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
			 
				
				var xkeylabels = newdata.columnlabels.map( function(d, i){ return { key:d, val:i} } );
				var ykeylabels = newdata.rowlabels.map( function(d, i){ return { key:d, val:i} } );
				
				var indexXMapper = d3.scale.ordinal()
						.domain( newdata.columnlabels.map(function(d, i) { return d; } ) )
						.range( newdata.columnlabels.map(function(d, i){ return i; } ) );
						
				var indexYMapper = d3.scale.ordinal()
						.domain( newdata.rowlabels.map(function(d, i) { return d; } ) )
						.range( newdata.rowlabels.map(function(d, i){ return i; } ) );
						
				var invIndexXMapper = d3.scale.ordinal()
						.domain( indexXMapper.range() )
						.range( indexXMapper.domain() );
						
				var invIndexYMapper = d3.scale.ordinal()
						.domain( indexYMapper.range() )
						.range( indexYMapper.domain() );
						
				var cellXPosition = d3.scale.ordinal()
						.domain( d3.range(xkeylabels.length)  )
						.rangeRoundBands([margin.left, margin.left + width]);
			 
				var cellYPosition = d3.scale.ordinal()
						.domain( d3.range(ykeylabels.length)  )
						.rangeRoundBands([margin.top, margin.top + height]);
			 
				var cellXPositionLin = d3.scale.linear()
						.domain( d3.extent( d3.range(xkeylabels.length) ) )
						.range([margin.left, margin.left + width - cellXPosition.rangeBand() ]);
			 
				var cellYPositionLin = d3.scale.linear()
						.domain( d3.extent( d3.range(ykeylabels.length) ) )
						.range([margin.top, margin.top + height - cellYPosition.rangeBand() ]);
			 
				
				var xAxis = d3.svg.axis().scale(cellXPositionLin).orient("top")
						.ticks(newdata.columnlabels.length)
						.tickFormat(function(d) {
							if (d % 1 == 0) {
								return invIndexXMapper(d);
							}
						});
						
				var yAxis = d3.svg.axis().scale(cellYPositionLin).orient("right")
						.ticks(newdata.rowlabels.length)
						.tickFormat(function(d) {
							if (d % 1 == 0) {
								return invIndexYMapper(d);
							}
						});
				
				var vis = svg.append("g")
						.call(d3.behavior.zoom().x(cellXPositionLin).y(cellYPositionLin).scaleExtent([1, 10]).on("zoom", function() {
							
							vis.select(".xAxis").call(xAxis)
								.selectAll("text")  
									.style("text-anchor", "start")
									.attr("font-size", (cellXPosition.rangeBand() - 2 ) + "px")
									.attr("dy", (cellXPosition.rangeBand() + 3) + "px")
									.attr("dx", "10px")
									.attr("transform", function(d) {
										return "rotate(-90)" 
									});
									
							vis.select(".yAxis").call(yAxis)
								.selectAll("text")
									.attr("font-size", (cellYPosition.rangeBand() - 2 )+ "px")
									.style("text-anchor", "start")
									.attr("dy", (12) + "px");
									
							vis.select(".heatmapcells").attr("transform", "translate(" + d3.event.translate + ")scale(" + d3.event.scale + ")");
						}))
						.append("g");
				
				vis.append("g").attr("class", "xAxis").attr("transform", "translate(0," + (margin.top) + ")")
					.call(xAxis)
					.selectAll("text")  
						.style("text-anchor", "start")
						.attr("font-size", (cellXPosition.rangeBand() - 2 ) + "px")
						.attr("dy", (cellXPosition.rangeBand() + 3) + "px")
						.attr("dx", "10px")
						.attr("transform", function(d) {
							return "rotate(-90)" 
						});
					
				vis.append("g").attr("class", "yAxis").attr("transform", "translate(" + (width + margin.left) + ")")
					.call(yAxis)
					.selectAll("text")  
						.attr("font-size", (cellYPosition.rangeBand() - 2 )+ "px")
						.style("text-anchor", "start")
						.attr("dy", (12) + "px");
				
				vis.append("g")
					.attr("class", "heatmapcells").selectAll("rect")
					.data(newdata.data)
					.enter()
					.append("rect")
					.attr({
						"class": "cells",
						"height": function(d){
							return .95*( cellYPosition.rangeBand() );
						},
						"width": function(d){
							return .95*( cellXPosition.rangeBand() );
						},
						"x": function(d, i) { return cellXPosition(indexXMapper(d.col)); },
						"y": function(d, i) { return cellYPosition(indexYMapper(d.row)); },
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