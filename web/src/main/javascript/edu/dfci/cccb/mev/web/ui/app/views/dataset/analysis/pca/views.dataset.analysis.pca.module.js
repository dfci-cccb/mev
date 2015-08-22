define(['ng', 'lodash'], function(ng, _){

    var module = ng.module("mui.views.dataset.analysis.pca", [])

    module.config(["$stateProvider", "$urlRouterProvider", function($sP, $urlRP){ }])

    module.controller('PcaVM', ['$scope', '$state', '$stateParams', 'project', 'analysis', 
    function($scope, $state, $stateParams, project, analysis){

        this.analysisId = $stateParams.analysisId
        this.analysis = analysis
        this.project = project

    }])

    return module

})
