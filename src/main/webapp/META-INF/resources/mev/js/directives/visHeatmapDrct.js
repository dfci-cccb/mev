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
		template: "<div class='heatmap'></div>",
		link: function (scope, element, attrs) {
			
			
			var margin = {
					left: scope.marginleft,
					right: scope.marginright,
					top: scope.margintop,
					bottom: scope.marginbottom
				}
			var width = scope.width - margin.left - margin.right;
			var height = scope.height - margin.top - margin.bottom;
			
			var vis = d3.select(element[0])
				.append("svg")
				.attr("width", width + margin.left + margin.right)
				.attr("height", height + margin.top + margin.bottom)
				.append("g")

			scope.$watch('inputdata', function(newdata, olddata) {
				
				if (newdata == olddata | !newdata) {
					return;
				}
				
				vis.selectAll('*').remove();
			
				function zoom() {
					
					
					vis.select(".xAxis").call(xAxis)
						.selectAll("text")  
							.style("text-anchor", "start")
							.attr("dy", ( (cellXPosition.rangeBand() )) + "px")
							.attr("dx", "10px")
							.attr("transform", function(d) {
								return "rotate(-90)" 
							});
							
					vis.select(".yAxis").call(yAxis)
						.selectAll("text")  
							.style("text-anchor", "start")
							.attr("dy", ( cellYPosition.rangeBand()/2 ) + "px");
							
					cellwindow.selectAll(".heatmapcells").attr("transform", "translate(" + d3.event.translate + ")scale(" + d3.event.scale + ")");
					cellwindow.selectAll(".link").attr("transform", "translate(" + d3.event.translate + ")scale(" + d3.event.scale + ")");
					cellwindow.selectAll(".node").attr("transform", "translate(" + d3.event.translate + ")scale(" + d3.event.scale + ")");
			 
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
						.domain( newdata.columnlabels )
						.rangeRoundBands([ margin.left, margin.left + width]);
			 
				var cellYPosition = d3.scale.ordinal()
						.domain( newdata.rowlabels  )
						.rangeRoundBands([0,height]);
			 
				var cellXPositionLin = d3.scale.linear()
						.domain( d3.extent( d3.range(newdata.columnlabels.length) ) )
						.range([margin.left, margin.left + width - cellXPosition.rangeBand() ]);
			 
				var cellYPositionLin = d3.scale.linear()
						.domain( d3.extent( d3.range(newdata.rowlabels.length) ) )
						.range([margin.top, margin.top + height - cellYPosition.rangeBand() ]);
				
				var xAxis = d3.svg.axis().scale(cellXPositionLin).orient("top")
						.ticks(newdata.columnlabels.length)
						.tickFormat(function(d) {
							if (d % 1 == 0 && d >= 0 && d < newdata.columnlabels.length) {
								return invIndexXMapper(d);
							}
						});
						
				var yAxis = d3.svg.axis().scale(cellYPositionLin).orient("right")
						.ticks(newdata.rowlabels.length)
						.tickFormat(function(d) {
							if (d % 1 == 0 && d >= 0 && d < newdata.rowlabels.length) {
								return invIndexYMapper(d);
							}
						});
						
				var cellwindow = vis.append("svg").attr("class", "cellWindow")
					.attr("x", 0)
					.attr("y", margin.top)
					.attr("width", width+ margin.left)
					.attr("height", height)
					.call(d3.behavior.zoom().x(cellXPositionLin).y(cellYPositionLin).scaleExtent([1, 8]).on("zoom", zoom))
				
				vis.append("g").attr("class", "xAxis").attr("transform", "translate(0," + (margin.top) + ")")
					.call(xAxis)
					.selectAll("text")  
						.style("text-anchor", "start")
						.attr("dy", ( (cellXPosition.rangeBand() )) + "px")
						.attr("dx", "10px")
						.attr("transform", function(d) {
							return "rotate(-90)" 
						});
					
				vis.append("g").attr("class", "yAxis").attr("transform", "translate(" + (width + margin.left) + ")")
					.call(yAxis)
					.selectAll("text")  
							.style("text-anchor", "start")
							.attr("dy", ( cellYPosition.rangeBand()/2 ) + "px");
				
				cellwindow.append("g")
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
						"x": function(d, i) { return cellXPositionLin( indexXMapper(d.col) ); },
						"y": function(d, i) { return cellYPositionLin( indexYMapper(d.row) ) - margin.top; },
						"fill": function(d) {
							return "rgb(" + redColorControl(d.value, "red") + "," + greenColorControl(d.value, "red") + ","+ blueColorControl(d.value, "red")+")";
			 
						},
						"value": function(d) { return d.value; },
						"index": function(d, i) { return i; },
						"row": function(d, i) { return d.row; },
						"column": function(d, i) { return d.col; }
					});
					
				var treewidth = margin.left;
				var treeheight = height;
				var genes = new Array();
				
				function getter(node) {
					
					if (!node.children) {
						return(node.name);
					} else {
						for (i = 0; i < 2; ++i) {
							getter(node.children[i])
						}
					}
						
				}
				
				var cluster = d3.layout.cluster()
					.size([treeheight, treewidth -120])
					.separation(function(a,b){ //Define a separation of neighboring nodes. Make neighbor distances equidistant so they can align with heatmap.
						return a.parent == b.parent ? 5:5;
				});
				
				function elbow(d, i){
					return "M" + (treewidth - (d.source.distance * 100))  + "," + d.source.x
					+ "V" + d.target.x + "H" + (treewidth - (d.target.distance * 100));
				};
				
				function click(d){
					var nColor = '#ffffff'; //Initial nonselected color of a node.
					var pColor = '#cccccc'; //Initial nonselected color of a branch.
					
					var cir = d3.selectAll("svg") //Selects all the circles representing nodes but only those which were the clicked circle, using datum as the equality filter.
						.selectAll("circle")
						.filter(function(db){
							return d === db ? 1 : 0;
					});
					
					var path = d3.selectAll(".link") //Selects all paths but only those which have the same source coordinates as the node clicked.
						.filter(function(dp){
							return (d.x === dp.source.x && d.y === dp.source.y) ? 1 : 0;
						});
					//Check the state of the clicked node. If 'active' (color is green) swap to inactive colors and pass those colors down to all children and vice versa.
					if(cir.style('fill') == '#00ff00'){
							cir.style('fill', nColor)
								.transition().attr('r', 2).duration(500); //Change radius of nonactive nodes.
							path.transition().style('stroke', pColor).duration(500);
					}
					else{
						nColor = '#00ff00';
						pColor = '#00ff00';
						cir.style('fill', nColor)
							.transition().attr('r', 5).duration(500);
						path.transition().style('stroke', pColor).duration(500);
					};
					
					if(d.children){ //Check if the node clicked is not a leaf. If the node has children, travel down the three updating the colors to indicate selection.
						walk(d, nColor, pColor);
					}
					else{
						if(nColor == '#00ff00'){ //Check color to see if indicated action is a select/deselect
							if(genes.indexOf(d.name) == -1){ //Check if gene already is in the array.
								genes.push(d.name)
							}
						}
						else{ //Algorithm for removing genes from the list on a deselect.
							var index = genes.indexOf(d.name); //Get the index of the given gene in the gene array.
							genes.splice(index, 1); //Splice that gene out of the array using its gotten index.
						};
					};
					alert(genes);
				};
				//Function to walk down the tree from a selected node and apply proper color assignments based on selection.
				function walk(d, nColor, pColor){
					//alert(d.name);
					d.children.forEach(function(dc){ //Loop through each child, recursively calling walk() as necessary.
						d3.selectAll("svg")
							.selectAll("circle")
							.filter(function(db){
								return dc === db ? 1 : 0;
							})
							.transition().style("fill",nColor).duration(500)
							.transition().attr("r", 2).duration(500);
						
						d3.selectAll(".link")
							.filter(function(dp){
								return (dc.x === dp.source.x && dc.y === dp.source.y) ? 1 : 0;
							})
							.transition().style("stroke", pColor).duration(500);
							
						if(dc.children){ //Check if children exist, if so, recurse the previous function.
							walk(dc, nColor, pColor);
						}
						else{
							if(nColor == '#00ff00'){
								if(genes.indexOf(dc.name) == -1){
									genes.push(dc.name);
								};
							}
							else{
								var index = genes.indexOf(dc.name);
								genes.splice(index, 1);
							}
						};
					});
				};
				
				var nodes = cluster.nodes(newdata.tree)//Create the nodes based on the tree structure.
				 
				var link = cellwindow.selectAll("path") //Create the branches.
					.data(cluster.links(nodes))
					.enter().append("path")
					.attr("class", "link")
					.attr("d", elbow) //Call function elbow() so that the paths drawn are straight and not curved.
					
		 
				var node = cellwindow.selectAll("circle") //Take the data in nodes and create individual nodes.
					.data(nodes)
					.enter().append("circle")
					.attr("class","node")
					.attr("cx", function(d) {
						if( !(d.parent) ){
							return Math.floor( (treewidth - (d.distance * 100)) );
						} else {
							if( !(d.children) ){
								return  Math.floor(treewidth) ;
							} else {
								return Math.floor(treewidth - (d.distance * 100));
							}
						}
					})
					.attr("cy", function(d) {
						if( !(d.parent) ){
							return Math.floor(d.x);
						} else {
							if( !(d.children) ){
								return Math.floor(d.x);
							} else {
								return Math.floor(d.x) ;
							}
						}
					})
					.attr("r", 2)
					.on("click", function(d) {
						console.log(d);
						console.log( getter(d) );
					})
					.on("click", click);	
				
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