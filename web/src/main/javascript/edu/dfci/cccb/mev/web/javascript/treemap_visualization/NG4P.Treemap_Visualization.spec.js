define(['angular', 'angular_mocks', './NG4P.Treemap_Visualization'], function (angular) {

    describe('NG4P.Treemap_Visualization', function () {


        beforeEach(function () {
            angular.mock.module('NG4P.Treemap_Visualization')
        })

        describe('treemap drawing factory', function(){
            var TreemapDraw

            beforeEach(function(){

                angular.mock.inject(function(_TreemapDrawingFactory_){
                    TreemapDraw = _TreemapDrawingFactory_
                })

            })


            it('should be defined', function(){
                expect(TreemapDraw).toBeDefined()
            })

        })

        describe('number comparison methods', function () {
            var NumberComparison
            beforeEach(function(){

                angular.mock.inject(function(_NumberComparisonFactory_){
                    NumberComparison = _NumberComparisonFactory_;
                })

            })

            it('should be defined', function () {
                //Non-Deterministic Algorithm. Cannot test.
                expect(NumberComparison.greaterThan).toBeDefined()
            })

        })

        describe('edge selection tournament methods', function () {

            var EdgeSelectionTournament
            beforeEach(function(){

                angular.mock.inject(function(_EdgeSelectionTournamentFactory_){
                    EdgeSelectionTournament = _EdgeSelectionTournamentFactory_;
                })

            })

            it('should be defined', function () {
                //Non-Deterministic Algorithm. Cannot test.
                expect(EdgeSelectionTournament).toBeDefined()
            })

        })

        describe('noise methods', function () {

            var NoiseMethods
            beforeEach(function(){

                angular.mock.inject(function(_NoiseMethodsFactory_){
                    NoiseMethods = _NoiseMethodsFactory_;
                })

            })

            it('should be defined', function () {
                //Non-Deterministic Algorithm. Cannot test.
                expect(NoiseMethods.uniform).toBeDefined()
            })

        })

        describe('random string generator', function () {

            var RandStringGen

            beforeEach(function(){

                angular.mock.inject(function(_RandomStringGeneratorFactory_){
                    RandStringGen = _RandomStringGeneratorFactory_;
                })

            })

            it('should be defined', function () {
                //Non-Deterministic Algorithm. Cannot test.
                expect(RandStringGen).toBeDefined()
            })

        })

        describe('graph clustering', function () {

            describe('cluster methods', function () {

                var ClusterMethods
                beforeEach(function(){

                    angular.mock.inject(function(_ClusterMethodsFactory_){
                        ClusterMethods = _ClusterMethodsFactory_;
                    })

                })

                it('should be defined', function () {
                    //Non-Deterministic Algorithm. Cannot test.

                    expect(ClusterMethods.hierarchical).toBeDefined()
                })

            })

        })





        describe('treemap options value', function () {

            var TreemapOptions

            beforeEach(function () {

                angular.mock.inject(function (_TreemapOptionsFactory_) {

                    TreemapOptions = _TreemapOptionsFactory_


                })

            })

            afterEach(function () {
                TreemapOptions = undefined;
            })

            it('should be defined', function () {
                expect(TreemapOptions).toBeDefined()
            })

            describe('default options', function () {

                describe('clustering options', function () {


                    var clusteringOptions

                    beforeEach(function () {
                        clusteringOptions = TreemapOptions.clusteringMethods
                    })

                    it('should have hierarchical as an option', function () {
                        expect(clusteringOptions.indexOf('hierarchical') < 0).toBeFalsy()
                    })

                })


                describe('treemap layout options', function () {

                    var layoutOptions

                    beforeEach(function () {
                        layoutOptions = TreemapOptions.layoutMethods
                    });

                    it('should have slice as an option', function () {
                        expect(layoutOptions.indexOf('slice') < 0).toBeFalsy()
                    })

                    it('should have squarify as an option', function () {
                        expect(layoutOptions.indexOf('squarify') < 0).toBeFalsy()
                    })

                    it('should have dice as an option', function () {
                        expect(layoutOptions.indexOf('dice') < 0).toBeFalsy()
                    })

                    it('should have slice-dice as an option', function () {
                        expect(layoutOptions.indexOf('slice-dice') < 0).toBeFalsy()
                    })


                })

                describe('treemap node area option', function () {

                    var areaOptions

                    beforeEach(function () {
                        areaOptions = TreemapOptions.nodeAreaValue
                    });

                    it('should have uniform as an option', function () {
                        expect(areaOptions.indexOf('uniform') < 0).toBeFalsy()
                    })

                    it('should have node size as an option', function () {
                        expect(areaOptions.indexOf('size') < 0).toBeFalsy()
                    })

                })

                describe('treemap node group option', function () {

                    var groupOptions

                    beforeEach(function () {
                        groupOptions = TreemapOptions.nodeGroupValue
                    });

                    it('should have true as an option', function () {
                        expect(groupOptions.indexOf(true) < 0).toBeFalsy()
                    })

                    it('should have false as an option', function () {
                        expect(groupOptions.indexOf(false) < 0).toBeFalsy()
                    })

                })

                describe('treemap color edge by group option', function () {

                    var colorEdgeByGroup

                    beforeEach(function () {
                        colorEdgeByGroup = TreemapOptions.colorEdgeByGroup
                    });

                    it('should have true as an option', function () {
                        expect(colorEdgeByGroup.indexOf(true) < 0).toBeFalsy()
                    })

                    it('should have false as an option', function () {
                        expect(colorEdgeByGroup.indexOf(false) < 0).toBeFalsy()
                    })

                })


            })
        })

    })

})
