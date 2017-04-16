(function(){

    define(['d3'], function(d3){

       return function(module){

           module
           .directive('pcaTest', ['pathService', 'pcaTransform', 'pcaMulti', function(paths, transform, generatePCA){
        	   return {
                   restrict: 'E',
                   templateUrl: paths.module + '/templates/pcaAccordion.test.tpl.html',
                   scope:{
                  	 project: "="
                   },
                   link: function(scope){
                  	 var analysis = {
                  		 "type": "Principal Components", 
                  		 "name":"blah",
                  		 "dimension": "column",
  	                	 'data': [{
  	                		 'name':"GSM74875",
  	                		 'loadings':[
  	                		    {"component":"PC1", value:-3.2955012871945},
  	                		    {"component":"PC2", value:-0.235472001413024},
  	                		    {"component":"PC3", value:1.04249074656539}
  	                		 ]
  	                	 },{
  	                		 'name':"GSM74876", 
  	                		 'loadings':[
  	                		    {"component":"PC1", value:-1.80447105406053},
  	                		    {"component":"PC2", value:0.287475153917424},
  	                		    {"component":"PC3", value:3.68914714714928}
  	                		 ]
  	                	 },{
  	                		 'name':"GSM74877", 
  	                		 'loadings':[
  	                		    {"component":"PC1", value:-3.80096208662079},
  	                		    {"component":"PC2", value:-1.38637595593331},
  	                		    {"component":"PC3", value:0.937030542306353}
  	                		 ]
  	                	 },{
  					          'name':"GSM74878", 
  				              'loadings':[
  				                {"component":"PC1", value:-2.5913389355804},
  	                		    {"component":"PC2", value:1.06757234256587},
  	                		    {"component":"PC3", value:-4.27747174481988}
  					        ]
  	                	 },{
  					          'name':"GSM74879", 
  				              'loadings':[
  				                {"component":"PC1", value:-3.08578471434393},
  	                		    {"component":"PC2", value:0.245581855720878},
  	                		    {"component":"PC3", value:-1.39536472909427}
  					        ]
  	                	 },{
  					          'name':"GSM74880", 
  				              'loadings':[
  				                {"component":"PC1", value:-3.05339947074892},
  	                		    {"component":"PC2", value:0.263186168972665},
  	                		    {"component":"PC3", value:-1.2524157562358}
  					        ]
  	                	 },{
  					          'name':"GSM74881", 
  				              'loadings':[
  				                {"component":"PC1", value:-2.20138689513356},
  	                		    {"component":"PC2", value:0.462750214352787},
  	                		    {"component":"PC3", value:-0.52726916976642}
  					        ]
  	                	 },{
  					          'name':"GSM74882", 
  				              'loadings':[
  				                  {"component":"PC1","value":-3.47609381386014},
  				                  {"component":"PC2","value":-0.414456624382498},
  				                  {"component":"PC3","value":-0.467270143772668}
  				              ]
  	                	 },{
  					          'name':"GSM74883", 
  				              'loadings':[
  				                  {"component":"PC1","value":-1.27328296766838},
  				                  {"component":"PC2","value":-0.260004429052355},
  				                  {"component":"PC3","value":5.80215190950443}
  				              ]
  	                	 },{
  					          'name':"GSM74884", 
  				              'loadings':[
  				                  {"component":"PC1","value":-2.78191358630203},
  				                  {"component":"PC2","value":-0.133281607645152},
  				                  {"component":"PC3","value":-0.0797505224325964}
  				              ]
  	                	 },{
  					          'name':"GSM74885", 
  				              'loadings':[
  				                  {"component":"PC1","value":-2.88608366671699},
  				                  {"component":"PC2","value":0.408509459230724},
  				                  {"component":"PC3","value":-0.664432222919673}
  				              ]
  	                	 },{
  					          'name':"GSM74886", 
  				              'loadings':[
  				                  {"component":"PC1","value":-3.08286656750136	},
  				                  {"component":"PC2","value":0.00787304312534814},
  				                  {"component":"PC3","value":-0.667415341281304}
  				              ]
  	                	 },{
  					          'name':"GSM74887", 
  				              'loadings':[
  				                  {"component":"PC1","value":-2.96974743579303},
  				                  {"component":"PC2","value":-0.0251302969464261},
  				                  {"component":"PC3","value":-0.567014441355642}
  				              ]
  	                	 },{
  					          'name':"GSM74888", 
  				              'loadings':[
  				                  {"component":"PC1","value":7.15796259120083},
  				                  {"component":"PC2","value":-7.77488052481726},
  				                  {"component":"PC3","value":1.00201102490504}
  				              ]
  	                	 },{
  					          'name':"GSM74889", 
  				              'loadings':[
  				                  {"component":"PC1","value":5.18018497296154},
  				                  {"component":"PC2","value":-6.17629468954715},
  				                  {"component":"PC3","value":-1.05967039538184}
  				              ]
  	                	 },{
  					          'name':"GSM74890", 
  				              'loadings':[
  				                  {"component":"PC1","value":5.0739769703575},
  				                  {"component":"PC2","value":5.40882704304901},
  				                  {"component":"PC3","value":0.558354650005061}
  				              ]
  	                	 },{
  					          'name':"GSM74891", 
  				              'loadings':[
  				                  {"component":"PC1","value":7.12971078058349},
  				                  {"component":"PC2","value":8.37598901961317},
  				                  {"component":"PC3","value":0.666384293151479}
  				              ]
  	                	 },{
  					          'name':"GSM74892", 
  				              'loadings':[
  				                  {"component":"PC1","value":5.91552392041355},
  				                  {"component":"PC2","value":0.0318662169722301},
  				                  {"component":"PC3","value":-1.31253142531795}
  				              ]
  	                	 },{
  					          'name':"GSM74893", 
  				              'loadings':[
  				                  {"component":"PC1","value":5.84547324600768},
  				                  {"component":"PC2","value":-0.153734387782931},
  				                  {"component":"PC3","value":-1.42696442120898}
  				              ]
  	                	 }],
  	                	 'variances':[{'name':"PC1", 'value':4.28743917531459}, 
  	                	              {'name':"PC2", 'value':3.34996602217847}, 
  	                	              {'name':"PC3", 'value':2.10032793494325}],
  	                	 'randomId': "FTGR",
  	                	 'selections':[{ 
  	                		 name:"S1", 
  	                		 color:"#b4aa84", 
  	                		 elements:[
  	                		     "GSM74875",
  	                		     "GSM74876",
  	                		     "GSM74877",
	                		     "GSM74878",
	                		     "GSM74879",
  	                		     "GSM74880",
  	                		     "GSM74881",
	                		     "GSM74882",
	                		     "GSM74883",
	                		     "GSM74884",
	            	         ]
  	                	 },{ 
  	                		 name:"S12", 
  	                		 color:"#ef3a84", 
  	                		 elements:[
								"GSM74885",
								"GSM74886",
								"GSM74887",
								"GSM74888",
								"GSM74889",
								"GSM74890",
								"GSM74891",
								"GSM74892",
								"GSM74893",
	            	         ]
  	                	 }]
                  	 };

                     scope.analysis = analysis;
                  	 
                   }
          	   }
             }])
           .directive('pcaAccordion', ['pathService', 'alertService', function(paths, alertService){

               return {
                 restrict: 'E',
                 templateUrl: paths.module + '/templates/pcaAccordion.tpl.html',
                 scope: {
                   analysis: "=analysis",
                   project: "=project"
                 },
                 link: function(scope){
                	 
                	 scope.visParams = {
                		'width': 700,
                		'height': 520
                	 }
                     
                     scope.selectionParams = {
                         'name':undefined,
                		 'dimension': {
                			 'x': undefined,
                             'y': undefined
                         },
                         'samples':[],
                         'color': '#' + Math
	                         .floor(Math.random() * 0xFFFFFF << 0)
	                         .toString(16)
                     }
                       
                     
                     scope.$watch('analysis', function(analysis, oldval){

                         if (analysis) {
                        	 scope.pc = {data:transformToPoints()}
                         }

                     })

                     scope.$watch('selectionParams.dimension.x', function(newval, oldval){
                         if (newval){
                             scope.pc = {data:transformToPoints()}
                         }
                         return
                     }) 
                     
                     scope.$watch('selectionParams.dimension.y', function(newval, oldval){
                         if (newval){
                             scope.pc = {data:transformToPoints()}
                         }
                         return
                     })
                     
                     scope.addSelections = function(){
                    	 
                         var selectionData = {
                             name: scope.selectionParams.name,
                             properties: {
                                 selectionDescription: '',
                                 selectionColor: scope.selectionParams.color,
                             },
                             keys: scope.selectionParams.samples.map(function(sample){return sample['id']})
                         };

                         scope.project.dataset.selection.post({
                                 datasetName: scope.project.dataset.datasetName,
                                 dimension: "column"

                             }, selectionData,
                             function (response) {
                                 scope.project.dataset.resetSelections('column')
                                 var message = "Added " + scope.selectionParams.name + " as new Selection!";
                                 var header = "Heatmap Selection Addition";
                                 
                                 scope.selectionParams.color = '#' + Math
                                 .floor(Math.random() * 0xFFFFFF << 0)
                                 .toString(16)

                                 alertService.success(message, header);
                             },
                             function (data, status, headers, config) {
                                 var message = "Couldn't add new selection. If " + "problem persists, please contact us.";

                                 var header = "Selection Addition Problem (Error Code: " + status + ")";

                                 alertService.error(message, header);
                             });
                     }
                	 
                	 function transformToPoints(){
                		 
                		 var data = {
                			     'labels': {
                                     x: scope.selectionParams.dimension.x.name,
                                     y: scope.selectionParams.dimension.y.name
                                 },
                                 'points': undefined
                        	 }
                		 
                        	 
                         data.points = scope.analysis.data
                         .map(function(sample, index){
                        	 
                        	 var x = sample.loadings.filter(function(comp){ 
                        		 return comp.component == scope.selectionParams.dimension.x.name
                        	 })[0].value
                        	 
                        	 var y = sample.loadings.filter(function(comp){ 
                        		 return comp.component == scope.selectionParams.dimension.y.name 
                    		 })[0].value
                    		 
                    		 var fill = scope.analysis.selections.filter(function(selection){
                    			 
                        		 var present = selection.elements
                        		 .filter(function(element){
                        			 return element == sample.name
                        		 }).length > 0
                        		 
                        		 return present
                        		 
                        	 })[0].color
                    		 
                    		 
                             return {
                            	 x:  x, 
                            	 y: y, 
                            	 id: sample.name, 
                            	 fill: fill
                             }
                         })
                         
                         return data
                	 };
                 }
              }

           }])

       } 


    })


})()
