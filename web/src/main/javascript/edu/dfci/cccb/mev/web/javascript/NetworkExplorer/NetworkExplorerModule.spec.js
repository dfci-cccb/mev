(function(){

    define([ 'angular', './NetworkExplorerModule', 'angular_mocks'], function(angular, NWEM){

        beforeEach(function(){

            angular.mock.module('NetworkExplorer')

        })
        
        describe('Network Explorer Module', function(){
        
            describe('NetworkController', function(){
                
                var scope, graphResource, ctrl, rootScope;
               
                beforeEach(function(){

                    inject(function($rootScope, $controller, $timeout){

                        //Mock graph resource
                        graphResource = jasmine.createSpyObj('graphResource', ['update', 'create'])

                        graphResource.data = 'zed';
                        
                        scope = $rootScope.$new();
                        rootScope = $rootScope;

                        ctrl = $controller('NetworkController', {
                            $scope: scope,
                            $rootScope: rootScope,
                            GraphResource: graphResource
                        })

                    })

                })
                
                describe('scope', function(){
                	it('should add GraphResource.data to graph property', function(){
                		expect(scope.graph).toBe('zed')
                	})
                })

                describe('when GraphCreateEvent is raised', function(){
                    
                    var param = {id: 'foo'}
                    
                    beforeEach(function(){
                        rootScope.$emit('GraphCreateEvent', param)
                    })
                    
                    it('should call GraphResource.update with specified graph id', function(){
                        expect(graphResource.create).toHaveBeenCalledWith(param)
                    })
                    
                })

                describe('when GraphTransitionEvent is raised', function(){

                        var param = {foo:'bar'}
                    
                        beforeEach(function(){
                            scope.$emit('GraphTransitionEvent', param)
                        })
                        
                        it('should call graphResource with specified graph id and specified node id', function(){
                            expect(graphResource.update).toHaveBeenCalledWith(param)
                        })
                                            
                })
                
                describe('GraphTransition callback', function(){
                    
                    it('should be defined on the scope', function(){
                        expect(scope.graphTransitionCallback).toBeDefined()
                    })
                    
                    describe('when raised', function(){
                        
                        var param = {id: 'foo', node:'bar'}
                        
                        beforeEach(function(){
                            spyOn(scope, '$emit')
                            scope.graphTransitionCallback(param)
                        })
                        
                        it('should raise GraphTransitionEvent with specified params', function(){
                            
                            expect(scope.$emit).toHaveBeenCalledWith('GraphTransitionEvent', param)
                        })
                    })
                })
            })
            
        })

    })

})()

