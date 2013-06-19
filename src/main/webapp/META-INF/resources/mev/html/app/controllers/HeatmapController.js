webmev.controller('HeatmapController', [

    //Injected Variables
    '$scope',
    //Services
    
    //Function
    function($scope) {
		
		$scope.dataset = [];
	
		d3.json($scope.datasource, function(error, data) {
				
				if (error) {

					//Error log if something bad happened
					console.log(error);

				} else {
					
					//Define data as global variable
					$scope.dataset = data;
					
					if (debug) {
						// Debug Flag for Cell Params Values
						console.log("Dataset:");
						console.log($scope.dataset);
						console.log(typeof dataset.columns)
						console.log(typeof dataset.rows)
					}
					
					//Initialize visualization
					generateMat1HeatMap(cell_params, debug);
			
				}
			});
		
    }

]);
