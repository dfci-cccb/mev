define(["./WorkspaceList.tpl.html", "./WorkspaceList.less"], function(tempalte){
    var directive = function(mevWorkspace, mevAnnotationRepository){
        return {
            restrict: "EAC",
            template: tempalte,
            scope: {},
            link: function(scope, elm, attr, ctrl){
                function updateDatasetList(){
                    mevWorkspace.getDatasets().then(function(datasets){
                        scope.datasets = datasets;
                        scope.datasetsByStatus = {
                            active: scope.datasets.filter(function(dataset){
                                return dataset.isActive;
                            }),
                            inactive: scope.datasets.filter(function(dataset){
                                return !dataset.isActive;
                            })
                        }
                    });
                }
                scope.vm={
                    activate: function(dataset){
                        dataset.status = "activating";
                        mevWorkspace.activateDataset(dataset);
                    },
                    delete: function(datasetId){
                        if(confirm("Delete dataset '"+datasetId+"'?"))
                            return mevWorkspace.deleteDataset(datasetId)
                                .then(updateDatasetList);
                    },
                    showStatus: function(row){
                        var status = row.getStatus();
                        return status;
                    },
                    isSaved: function(row){
                        return row.getStatus()==="saved";
                    },
                    export: function(dataset){
                        mevWorkspace.exportDataset(dataset);
                    }
                };
                scope.$on("mev:datasets:list:refreshed", function(){
                    updateDatasetList();
                });
                updateDatasetList();
                scope.$on("mev:dataset:activated", function(event, dataset) {
                    dataset.status = "active";
                    var row = new mevAnnotationRepository("row");
                    row.import(dataset.id, "row");
                    var column = new mevAnnotationRepository("column");
                    column.import(dataset.id, "column");
                });
            }
        }
    }
    directive.$name = "mevWorkspaceList";
    directive.$provider = "directive";
    directive.$inject=["mevWorkspace", "mevAnnotationRepository"];
    return directive;
});