define([], function() {

        var config = function ($stateProvider, $urlRouterProvider) {
            $urlRouterProvider.deferIntercept();
            $stateProvider
                .state("root.datasets.workspace", {
                    url: "/workspace",
                    parent: "root.datasets",
                    displayName: "datasets",
                    views: {
                        "workspace@root.datasets": {
                            template: "<div mev-workspace-list></div>"
                        }
                    },
                    sticky: true,
                    // redirectTo: "root.datasets.imports.tcga"
                });
            ;
        };
        config.$inject = ['$stateProvider', '$urlRouterProvider'];
        config.$provider="config";     
        return config;
});
