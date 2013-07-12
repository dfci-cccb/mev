'use strict';

/* jasmine specs for controllers go here */

describe('myApp controllers', function(){
	
  beforeEach(module('myApp.controllers'));

  describe('HeatmapCtrl', function() {
	
    var scope, ctrl, $httpBackend;
	  
    beforeEach(inject(function($rootScope, $controller) {
		
      scope = $rootScope.$new(),
      ctrl = $controller('HeatmapCtrl', {$scope: scope});
            
    }));
    
    it('should have loaded matrix data from http request', function() {
      expect(scope.matrixLocation).toBeUndefined();
    });
    
    it('should generate default cell parameters that match default size', function() {
      //feature pending
    });

    it('should change cell parameters to small from some state', function() {
      //feature pending
    });
	  
    it('should change cell parameters to medium from some state', function() {
      //feature pending
    });

    it('should change cell parameters to large from some state', function() {
      //feature pending
    });
	  
  });
  
  describe('AnalyzeCtrl', function() {
	
    var scope, ctrl, $httpBackend;
	  
    beforeEach(inject(function($rootScope, $controller) {
		
      scope = $rootScope.$new(),
      ctrl = $controller('HeatmapCtrl', {$scope: scope});
            
    }));
    
    it('should load visualization data', function() {
      expect(scope.matrixLocation).toBeUndefined();
    });
    
    it('should load uploads data', function() {
      //feature pending
    });
	  
  });
  
});
