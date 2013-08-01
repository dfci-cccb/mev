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
      
      $httpBackend.whenPOST('data/undefined/marks', 'blank').respond(201, '');
      
      $httpBackend.whenGET('data/undefined/marks').respond({test: 'test'});
      $httpBackend.whenGET('data/undefined-2').respond({genes: 'test'});
      $httpBackend.whenGET('data/undefined-1')
                  .respond(
                     {genes:[

                       {
                         name:"test1",
                         symbol:"TEST1",
                         description:"This is the test1",
                         ensembleId: "TEST1ID",
                         pathways: "Test, Test, test"
                       },
                       {
                         name:"test2",
                         symbol:"TEST2",
                         description:"This is the test2",
                         ensembleId: "TEST2ID",
                         pathways: "Test2, Test2, test2"
                       } 

                     ],
                     maxPage: 2,
                     nearbyPages: [
                        {
                          number: 1,
                          link: "testdataset-1"
					    },
					    {
                          number: 2,
                          link: "testdataset-2"
					    },
					  ]
                    });
                  


      createController = function() {
		  
          $controller('GeneSelectCtrl', {'$scope': $rootScope});
          
      };

    }));

    afterEach(function() {

      $httpBackend.verifyNoOutstandingExpectation();
      $httpBackend.verifyNoOutstandingRequest();
   
    });

    
    it('should send a request to download the first page of data and update vars', function() {
       
       $httpBackend.expectGET('data/undefined-1');
       var controller = createController();
       $httpBackend.flush();

       expect($rootScope.genesCurrentPage.length).toBe(2);
       expect($rootScope.pageMax).toBe(2);
       expect($rootScope.currentPage).toBe(1);
       expect($rootScope.nearbyPages.length).toBe(2);
       
    });
    
    it('should update marked genes when genesMarked is called', function () {
      
      var controller = createController();
      $httpBackend.flush();
      
      $httpBackend.expectGET('data/undefined/marks').respond({test: 'test'});
      $rootScope.getMarks();
      $httpBackend.flush();
      
      expect($rootScope.genesMarked.test).toBe('test');
  
    });
	
    it('should update genesSearched when searchGene is called', function() {
        var controller = createController();
        $httpBackend.flush();
        
        $httpBackend.expectGET('data/undefined/q=test').respond({test: 'test'});
        $rootScope.searchGene('test');
        $httpBackend.flush();
        
        expect($rootScope.genesSearched.test).toBe('test');
    });
    
    it('should page up and page down when respective functions are called', function() {
		
        var controller = createController();
        $httpBackend.flush();
        
        expect($rootScope.currentPage).toBe(1);
        $httpBackend.expectGET('data/undefined-2').respond({genes: 'test'});
        $rootScope.pageUp();
        $httpBackend.flush();
        
        expect($rootScope.currentPage).toBe(2);
        
        $httpBackend.expectGET('data/undefined-1').respond({genes: 'test'});
        $rootScope.pageDown();
        $httpBackend.flush();
        
        expect($rootScope.currentPage).toBe(1);
        
    });
    
    it('should not update page up when currently on page 1', function() {
		
        var controller = createController();
        $httpBackend.flush();
        
        expect($rootScope.currentPage).toBe(1);
        
        $rootScope.pageDown();
        
        expect($rootScope.currentPage).toBe(1);
        
    });
    
    it('should not update page down when currently on max page', function() {
		
        var controller = createController();
        $httpBackend.flush();
        
        expect($rootScope.currentPage).toBe(1);
        $httpBackend.expectGET('data/undefined-2').respond({genes: 'test'});
        $rootScope.pageUp();
        $httpBackend.flush();
        
        expect($rootScope.currentPage).toBe(2);
        $rootScope.pageUp();
        
        expect($rootScope.currentPage).toBe(2);
    });
    
    it('should update genesMarked when addMark is called', function() {

        var controller = createController();
        $httpBackend.flush();
        

        $httpBackend.expectPOST('data/undefined/marks', 'blank').respond(201, '');
        $rootScope.addMark('blank');
        
        $httpBackend.expectGET('data/undefined/marks').respond({test: 'test'});
        $httpBackend.flush();
        
        expect($rootScope.genesMarked.test).toBe('test');
    });
    
  });
  
});
