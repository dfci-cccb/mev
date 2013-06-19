drcts.directive('visHeatmap', function() {
	/* Heatmap visualization function.
	 * 
	 */
	return {
		restrict: 'E',
		scope: {
            foo: "="
        },
		link: function (scope, element, attrs) {
			
			scope.$watch('foo', function(data) {
				
				/* At some point make this as a function that takes in data location
				 * 
				 */

				// Global Variables
				var debug = true;
				var dataset;
				var cell_params = {
					width: 50,
					height: 20
				}

				if (debug) {
					// Debug Flag for Cell Params Values
					console.log("Cell_Params:")
					console.log(cell_params)
					console.log("Cell Width: " + cell_params.width + " (" + typeof cell_params.width + ")");
					console.log("Cell Height: " + cell_params.height + " (" + typeof cell_params.height + ")");
				}

				//Functions
				var generateMat1HeatMap = function(cell_params, debug) {

					/* Builds SVG heatmap visualization based on matrix 1 standard
					 * format.
					 * 
					 * Parameters:
					 * 
					 * Outputs:
					 */

					// Visualization Parameters
					var vis_params = {
						// Assumed SVG window will be in an iframe to avoid run off 
						// visualization, so these params are built around data.
						width : 0,
						height : 0,
						horizontal_padding : 30,
						vertical_padding : 10
					}
					
					if (debug) {
						// Debug flag for Vis Params 
						console.log("Vis_Params:")
						console.log(vis_params)
						console.log("Width: " + vis_params.width + " (" + typeof vis_params.width + ")");
						console.log("Height: " + vis_params.height + " (" + typeof vis_params.height + ")");
						console.log("HR Padding: " + vis_params.horizontal_padding + " (" + typeof vis_params.horizontal_padding + ")");
						console.log("VR Padding: " + vis_params.vertical_padding + " (" + typeof vis_params.vertical_padding + ")");
					}
					
					// Calculate window parameters from dataset
					vis_params.width = (dataset.columns * cell_params.width) + (vis_params.vertical_padding*2);
					vis_params.height = (dataset.rows * cell_params.height) + (vis_params.horizontal_padding*2);


					if (debug) {
						// Debug flag to view if vis_params initiated correctly
						console.log("Updated Vis_Params:")
						console.log(vis_params)
						console.log("Width: " + vis_params.width + " (" + typeof vis_params.width + ")");
						console.log("Height: " + vis_params.height + " (" + typeof vis_params.height + ")");
						console.log("HR Padding: " + vis_params.horizontal_padding + " (" + typeof vis_params.horizontal_padding + ")");
						console.log("VR Padding: " + vis_params.vertical_padding + " (" + typeof vis_params.vertical_padding + ")");
					}
					
					//Build SVG Window
					var svg = d3.select(element[0])
						.append("svg")
						.attr("width", vis_params.width)
						.attr("height", vis_params.height);
						
					svg.selectAll("rect")
						.data(dataset.data)
						.enter()
						.append("rect")
						.attr("height", cell_params.height)
						.attr("width", cell_params.width)
						.attr("x", function(d, i) {
							return xCellScale(i,dataset.columns)
						})
						.attr("y", function(d, i) {
							return yCellScale(i,dataset.columns);
						})
						.attr("fill", function(d) {
								return "rgb(0," + colorCellScale(d) + ", 0 )"
							}
						);

				}

				var xCellScale = function(index, cols) {
					/* Function for giving cell x position based on index value of Type1
					 * HeatMap Matrices
					 * 
					 * Parameters:
					 * 
					 * Outputs:
					 */
					 var output = (index%cols)*cell_params.width;
					 return output;
				}

				var yCellScale = function(index, cols) {
					/* Function for giving cell y position based on index value of Type1
					 * HeatMap Matrices
					 * 
					 * Parameters:
					 * 
					 * Outputs:
					 */
					 
					 var output = Math.floor(index/cols) * cell_params.height;
					 return output;
				}

				var colorCellScale = function(j) {
					/* Function for giving cell color based on value of Type1
					 * HeatMap Matrices
					 * 
					 * Parameters:
					 * 
					 * Outputs:
					 */
					 
					 var output = d3.scale.linear()
						.domain([d3.min(dataset.data), d3.max(dataset.data)])
						.rangeRound([0, 255]);
					return output(j);
				}
				//Main

				// Data Download
				d3.json("/resources/static/mev/html/app/partials/visualizations/sample-data/affy.first20.json", function(error, data) {
					
					if (error) {

						//Error log if something bad happened
						
						console.log(error);

					} else {
						
						//Define data as global variable
						dataset = data;
						
						if (debug) {
							// Debug Flag for Cell Params Values
							console.log("Dataset:");
							console.log(dataset);
							console.log(typeof dataset.columns)
							console.log(typeof dataset.rows)
						}
						
						//Initialize visualization
						generateMat1HeatMap(cell_params, debug);
						
					}
				});
		
			});
		}
		
	};
});
