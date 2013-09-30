drct.directive('visRangedBar', [function() {

	return {
			
		restrict: 'E',
		scope: {
			inputdata:"="
		},
		link: function (scope, element, attrs) {
			
			scope.marginleft = 5;
			scope.marginright = 5;
			scope.margintop = 30;
			scope.marginbottom = 20;
			scope.width = 300;
			scope.height = 60;
			
			var margin = {
				left: scope.marginleft,
				right: scope.marginright,
				top: scope.margintop,
				bottom: scope.marginbottom
			};
			
			var width = scope.width - margin.left - margin.right,
			height = scope.height - margin.top - margin.bottom;
			
			var svg = d3.select(element[0])
				.append("svg")
				.attr("class", "chart")
				.attr("pointer-events", "all")
				.attr("width", width + margin.left + margin.right)
				.attr("height", height + margin.top + margin.bottom);
			
			scope.$watch('inputdata', function(newdata, olddata) {
				
					if (newdata == olddata | !newdata) {
						return;
					}
				
					svg.selectAll('*').remove();

					var xScale = d3.scale.linear()
							.domain([ newdata.range[0], newdata.range[1] ])
							.rangeRound([margin.left, margin.left+ width]);
							
					var xAxis = d3.svg.axis()
						.scale(xScale)
							.orient("bottom");
					
					svg.append("g")
						.selectAll("text")
						.data([newdata])
						.enter()
						.append("text")
						.text(function(d){
							return d.cell.gene;
						})
						.attr("x", function(d){
							return 0
						})
						.attr("y", function(d){
							return 10;
						})
						
					svg.append("g")
						.selectAll("text")
						.data([newdata])
						.enter()
						.append("text")
						.text(function(d){
							return d.cell.sample;
						})
						.attr("x", function(d){
							return 0
						})
						.attr("y", function(d){
							return 20;
						})
					
					svg.append("g")
						.selectAll("rect")
						.data([newdata])
						.enter()
						.append("rect")
						.attr({
							"class": "bar",
							"height": function(d){
								return 20;
							},
							"width": function(d){
								var dx = xScale(1) - xScale(0);
								if (d.cell.value >=0){
									return Math.floor( d.cell.value*dx);
								} else {
									return Math.floor( Math.abs(d.cell.value*dx) );
								}
							},
							"x": function(d, i) { 
								if (d.cell.value >=0){
									return xScale(0);
								} else {
									return xScale(d.cell.value) ;
								}
							},
							"y": function(d, i) { return margin.top + height - 20; },
							"fill": function(d) {
								if (d.cell.value >= 0){
									return "rgb(255,0,0)";
								} else {
									return "rgb(0,0,255)";
								}
							},
							"value": function(d) { return d.cell.value; },
							"index": function(d, i) { return i; }
						});

					svg.append("g")
						.attr("class", "xAxis")
						.attr("transform", "translate(0," + (margin.top + height) + ")")
						.call(xAxis)
							.selectAll("text")  
							.style("text-anchor", "start");
			});
				
		}
		
	};
	
}]);
