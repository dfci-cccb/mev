define(["mui",
	"./_controllers/DatasetsVM",
	"./_templates/views.datasets.tutorials.tpl.html",
	"./_templates/views.datasets.google.tpl.html",
	"./_templates/views.datasets.upload.tpl.html",
	"./_templates/views.datasets.geods.tpl.html",
	"./_templates/views.datasets.imports.tpl.html",
	"../../widgets/presets/widgets.presets.module",
	"./session/views.datasets.session.module"],
function(ng, DatasetsVM, tutorialsTemplate, googleTemplate, uploadTemplate, geodsTemplate, importsTemplate){
	var module = ng.module("mui.views.datasets", arguments, arguments);
	module.config(['$stateProvider', '$urlRouterProvider',
	     	function($stateProvider, $urlRouterProvider){
				$urlRouterProvider.when("/datasets", "/datasets/workspace");
	     		$stateProvider
					.state("root.datasetsOld", {
						url: "/datasetsOld",
						controller: "ImportsCtrl",
						controllerAs: "ImportsCtrl",
						parent: "root",
						templateUrl: "app/views/datasets/_templates/views.datasets.tpl.html",
						displayName: "datasets"
					})
					.state("root.datasets", {
						url: "/datasets",
						parent: "root",
						displayName: "datasets",
						templateUrl: "app/views/datasets/_templates/views.datasets2.tpl.html",
						// deepStateRedirect: {
						// 	default: {
						// 		state: "root.datasets.workspace"
						// 	}
						// }
						redirectTo: "root.datasets.imports.upload"
					})
					.state("root.datasets.imports", {
						parent: "root.datasets",
						displayName: false,
						views: {
							"imports": {
								template: "<div ui-view></div>"
							}
						},
						sticky: true,
						
					})
					.state("root.datasets.imports.tutorials", {
						url: "/tutorials",
						parent: "root.datasets.imports",
						template: tutorialsTemplate,
						displayName: "tutorials"
					})
					.state("root.datasets.imports.upload", {
						url: "/upload",
						parent: "root.datasets.imports",
						template: uploadTemplate,
						displayName: "upload"
					})
					.state("root.datasets.imports.tcga", {
						url: "/tcga",
						parent: "root.datasets.imports",
						template: "<div id=\"presetMgr\" presets-list></div>",
						displayName: "TCGA"
					})
					.state("root.datasets.imports.geods", {
						url: "/geods",
						parent: "root.datasets.imports",
						template: geodsTemplate,
						displayName: "GEO"
					})
					.state("root.datasets.imports.google", {
						url: "/google",
						parent: "root.datasets.imports",
						template: googleTemplate,
						displayName: "Google Drive"
					});
	}]);
	// module.controller("DatasetsVM", DatasetsVM);
	return module;
});
