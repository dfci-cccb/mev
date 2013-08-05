'use strict';

/* jasmine specs for controllers go here */

describe('myApp controllers', function(){
	
  beforeEach(module('myApp.controllers'));
  
  describe('AnalyzeCtrl', function() {

    var $rootScope, createController, $httpBackend;

    beforeEach(inject(function($injector) {

      $httpBackend = $injector.get('$httpBackend');

      $rootScope = $injector.get('$rootScope');

      var $controller = $injector.get('$controller');
      
      $httpBackend.whenGET('data/visualization_data.json')
          .respond({"test":0
          });

      $httpBackend.whenGET('data/upload_data.json')
          .respond({"test":0
          });

      createController = function() {
        $controller('AnalyzeCtrl', {'$scope': $rootScope});
      };

    }));
    
    afterEach(function() {
		
      $httpBackend.verifyNoOutstandingExpectation();
      $httpBackend.verifyNoOutstandingRequest();
      
    });

    it("should load visualization data from get request", function() {
       
       $httpBackend.expectGET('data/visualization_data.json');
       var controller = createController();
       $httpBackend.flush();
       
       expect($rootScope.visualizationdata.test).toBe(0);
             
    });

    it("should load user's stored data information from get request", function() {
       
       $httpBackend.expectGET('data/upload_data.json');
       var controller = createController();
       $httpBackend.flush();

       expect($rootScope.uploaddata.test).toBe(0);
    });

  });

  describe('HeatmapCtrl', function() {
    
    var $rootScope, createController, $httpBackend;
   
    beforeEach(inject(function($injector) {

      $httpBackend = $injector.get('$httpBackend');
      $rootScope = $injector.get('$rootScope');
      var $controller = $injector.get('$controller');

      $httpBackend.whenGET('data/subs/undefined-0.json')
          .respond({
		      data: "test",
		      maxpage: 2,
		      pages: [
					{
					  "index": "All",
					  "class": ""
					},
					{
					  "index": "1",
					  "class": ""
					},
					{
					  "index": "2",
					  "class": "active"
					}]
          });
          
      $httpBackend.whenGET('data/subs/undefined-1.json')
          .respond({
              data: 'test',
              maxpage: 2,
              pages: [
					{
					  "index": "All",
					  "class": ""
					},
					{
					  "index": "1",
					  "class": ""
					},
					{
					  "index": "2",
					  "class": "active"
					}]
           });
  
      
      createController = function() {
        $controller('HeatmapCtrl', {'$scope': $rootScope});
      };

    }));

    afterEach(function() {
		
      $httpBackend.verifyNoOutstandingExpectation();
      $httpBackend.verifyNoOutstandingRequest();
      
    });
    
    it('should not add dupicate rows to the marked rows object', function() {

	   $httpBackend.expectGET('data/subs/undefined-0.json');
       var controller = createController();
       $httpBackend.flush();
       
       $rootScope.storeRow('testrow');
       $rootScope.storeRow('testrow2');
       $rootScope.storeRow('testrow');
       
       expect($rootScope.markedRows.length).toBe(2);
       
	});
	
	it('should clear all marked rows when clearAllRows is called', function() {
		
		$httpBackend.expectGET('data/subs/undefined-0.json');
		var controller = createController();
		$httpBackend.flush();

		$rootScope.storeRow('testrow');
		$rootScope.storeRow('testrow2');
		$rootScope.storeRow('testrow3');
		
		$rootScope.clearAllRows();
		expect($rootScope.markedRows.length).toBe(0);
		
		
	});
	
	it('should not remove a row from the marked rows object if an attempt is made to remove a nonexisting row', function() {
	   
	   $httpBackend.expectGET('data/subs/undefined-0.json');
       var controller = createController();
       $httpBackend.flush();
       
       expect($rootScope.markedRows.length).toBe(0);
       $rootScope.storeRow('testrow');
       $rootScope.storeRow('testrow2');
       $rootScope.removeRow('testrow3');
       
       expect($rootScope.markedRows.length).toBe(2);
	});
    
    it('should fetch data and store ininitial vars when page is loaded', function() {

       $httpBackend.expectGET('data/subs/undefined-0.json');
       var controller = createController();
       $httpBackend.flush();

       expect($rootScope.heatmapdata).toBe('test');
       expect($rootScope.maxpage).toBe(2);
       expect($rootScope.view).toBe('all');
       expect($rootScope.currentpage).toBe(0);

    });
    
    it('should fetch paged data and store into dataset when requestPage is called', function() {

       var controller = createController();
       $httpBackend.flush();
       
       $httpBackend.expectGET('data/subs/undefined-1.json');
       $rootScope.requestPage(1);
       $httpBackend.flush();
       
       expect($rootScope.heatmapdata).toBe('test');
       expect($rootScope.maxpage).toBe(2);
       expect($rootScope.view).toBe('page');
       expect($rootScope.currentpage).toBe(1);
       
    });
    
    it('should update vizColor when updateColor is called', function() {
		
		var controller = createController();
        $httpBackend.flush();
		
        expect($rootScope.vizcolor).toBe("red");
        $rootScope.updateColor("blue");
        expect($rootScope.vizcolor).toBe("blue");
        
    });
    
    it('should not allow vizColor to be an undefined class', function() {
		
		var controller = createController();
        $httpBackend.flush();

        $rootScope.updateColor(20);
        expect($rootScope.vizcolor).toBe("red");
        
    });

    it('should store selected row data into markedRows when storeRow is called', function() {
		
		var controller = createController();
        $httpBackend.flush();
        
        $rootScope.storeRow('testrow');
        expect($rootScope.markedRows).toContain('testrow');
        
    });
    
    it('should remove the row data index from markedRows when removeRow is called with index parameter', function() {
        
        var controller = createController();
        $httpBackend.flush();
        
        $rootScope.storeRow('testrow');
        $rootScope.storeRow('testrow2');
        $rootScope.removeRow('testrow');
        expect($rootScope.markedRows).not.toContain('testrow');
        
    });
   
   
  });

  describe('GeneSelectCtrl', function() {
    
    var $rootScope, createController, $httpBackend;
   
    beforeEach(inject(function($injector) {

      $httpBackend = $injector.get('$httpBackend');
      $rootScope = $injector.get('$rootScope');
      var $controller = $injector.get('$controller');
      
      createController = function() {
		  
          $controller('GeneSelectCtrl', {'$scope': $rootScope});
          
      };
      $httpBackend.whenGET('data/geneset?ensembleid=GFzu1&format=json&page=1')
          .respond({
			 	"fields": [
			        {
			    	    "name": "Name",
		    	    	"reference": "name",
		    	    	"info": {
			    	        "type": "nominal"
			    	    }
			        },
			        {
                        "name": "Ensemble ID",
                        "reference": "ensembleid",
                        "info": {
			    	        "type": "nominal"
			    	    }
                    }
			    ],
			    "tuples": [
                    {
                        "name": "BGD2",
                        "ensembleid": "GFzu1"
                    }
                ],
                "page_id": 1,
                "total_pages": 2 
		  });
      
      $httpBackend.whenGET('data/geneset?format=json&page=2')
          .respond({

              
			"fields": [
			    {
				    "name": "Name",
			    	"reference": "name",
			    	"info": {
			    	    "type": "nominal"
			    	}
			    },
			    {
                    "name": "Ensemble ID",
                    "reference": "ensembleid",
                    "info": {
			    	    "type": "nominal"
			    	}
                }
			],
			"tuples": [
	    		{
                    "name": "BGD3",
                    "ensembleid": "GFD1"
                },
                {
                    "name": "BGD4",
                    "ensembleid": "GFD1"
                }
            ],
            "page_id": 2,
            "total_pages": 2
            
          });
          
      $httpBackend.whenGET('data/geneset?format=json&page=1&testkey2=testvalue2')
          .respond({});
          
      $httpBackend.whenGET('data/geneset?format=json&page=1')
          .respond({

              
			"fields": [
			    {
				    "name": "Name",
			    	"reference": "name",
			    	"info": {
			    	    "type": "nominal"
			    	}
			    },
			    {
                    "name": "Ensemble ID",
                    "reference": "ensembleid",
                    "info": {
			    	    "type": "nominal"
			    	}
                }
			],
			"tuples": [
	    		{
                    "name": "BGD1",
                    "ensembleid": "GFD1"
                },
                {
                    "name": "BGD2",
                    "ensembleid": "GFzu1"
                }
            ],
            "page_id": 1,
            "total_pages": 2
            
          });

    }));

    afterEach(function() {

      $httpBackend.verifyNoOutstandingExpectation();
      $httpBackend.verifyNoOutstandingRequest();
   
    });

    
    it('should send a request to download the initial page of data and update vars', function() {
		
		$httpBackend.expectGET('data/geneset?format=json&page=1');
        var controller = createController();
        $httpBackend.flush();
        
        expect($rootScope.fields[0]["name"]).toBe("Name");
        expect($rootScope.currentpage).toBe(1)
        
    });
    
    it('should overwrite new get params when push to params on a field is updated', function() {
		var controller = createController();
        $httpBackend.flush();
        
        $rootScope.pushToParams("testkey", "testvalue");
        $rootScope.pushToParams("testkey", "testvalue2");
        
        expect($rootScope.getPageParams["testkey"]).toBe("testvalue2");
	});

    it('should not make a request for a page that is not available', function() {

        var controller = createController();
        $httpBackend.flush();
        
        $rootScope.getPage("3");
        expect($rootScope.currentpage).toBe(1);

    });
    
    it('should update marked genes when genes are marked by user', function () {
      
        var controller = createController();
        $httpBackend.flush();
        
        $rootScope.markRow("BGD1");
        expect($rootScope.markedRows.indexOf("BGD1")).not.toBe(-1);

    });
    
    it('should clear filter values from filter options in getpageparams when purge is called', function () {
		var controller = createController();
        $httpBackend.flush();
        
        $rootScope.pushToParams("testkey", "testvalue");
        $rootScope.pushToParams("testkey2", "testvalue2");
        $rootScope.pushToParams("testkey3", "testvalue3");
        $rootScope.purgeParams()
        
        expect($rootScope.getPageParams["testkey"]).toBeUndefined();
        expect($rootScope.getPageParams["testkey2"]).toBeUndefined();
        expect($rootScope.getPageParams["testkey3"]).toBeUndefined();
	});

    it('should update marked genes when user asks to remove genes is called', function() {

        var controller = createController();
        $httpBackend.flush();
        
        $rootScope.markRow("BGD1");
        $rootScope.markRow("BGD2");
        $rootScope.removeRow("BGD1");
        expect($rootScope.markedRows.indexOf("BGD1")).toBe(-1);
        
    });
    
    it('should update genes list when filter is applied', function() {

        var controller = createController();
        $httpBackend.flush();
        
        $httpBackend.expectGET('data/geneset?ensembleid=GFzu1&format=json&page=1');
        $rootScope.getPageParams["ensembleid"] = "GFzu1";
        $rootScope.getPage(1);
        $httpBackend.flush();
        
        expect($rootScope.tuples[0]["name"]).toBe("BGD2");
        expect($rootScope.tuples.length).toBe(1);
        
    });
	
    it('should return nothing when filter request is sent and no objects are received', function() {
		
        var controller = createController();
        $httpBackend.flush();

    });
    
    it('should page up when user requests', function() {
		
        var controller = createController();
        $httpBackend.flush();
        
        $httpBackend.expectGET('data/geneset?format=json&page=2');
        $rootScope.getPage("2");
        $httpBackend.flush();
        
        expect($rootScope.currentpage).toBe(2);
    
    });
    
    it('should call getPage with correct parameters', function() {
		
        var controller = createController();
        $httpBackend.flush();
        
        $rootScope.pushToParams("testkey", "testvalue");
        $rootScope.pushToParams("testkey2", "testvalue2");
        $rootScope.pullFromParams("testkey");
        $httpBackend.expectGET('data/geneset?format=json&page=1&testkey2=testvalue2')
        $rootScope.getPage("1")
        $httpBackend.flush();
    
    });
    
    it('should add keys to request object when add keys function is called', function() {
		
        var controller = createController();
        $httpBackend.flush();
        
        $rootScope.pushToParams("testkey", "testvalue");
        expect($rootScope.getPageParams["testkey"]).not.toBeUndefined;
    });
    
    it('should remove keys to request object when remove keys function is called', function() {
		
        var controller = createController();
        $httpBackend.flush();
        
        $rootScope.pushToParams("testkey", "testvalue");
        $rootScope.pushToParams("testkey2", "testvalue2");
        $rootScope.pullFromParams("testkey");
        expect($rootScope.getPageParams["testkey"]).toBeUndefined;
        
    });

    it('should page down when user requests', function() {
		
        var controller = createController();
        $httpBackend.flush();
        
        $rootScope.getPage("2");
        $httpBackend.flush();
        $rootScope.getPage("1");
        $httpBackend.flush();
        
        expect($rootScope.currentpage).toBe(1);
    

    });
    
  });
  
});
