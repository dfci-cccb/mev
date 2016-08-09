define(["lodash"], function(_){"use strict";
    var service = function($http, $q, mevDb, DatasetResource){

        function getDatasets(){

            var localDbsPromise = mevDb.getDatasets()
                .then(function (dbs) {
                    return dbs
                })
                .catch(function (err) {
                    throw err;
                });
            var remoteDbsPromise = $http({
                    method : 'GET',
                    url : '/dataset',
                    params : {
                        format : 'json'
                    }
                })
                .then(function(remoteDbs, status, headers, config) {
                    return remoteDbs.data;
                });
            var allDbsPromise = $q.all([localDbsPromise, remoteDbsPromise])
                .then(function(dbs){
                    var local = dbs[0];
                    var remote = dbs[1];
                    console.debug("workspace local", local);
                    console.debug("workspace remote", remote);
                    var active = remote.map(function(name){
                        return {
                            id: name,
                            name: name,
                            isActive: true,
                            getStatus: mevDb.getStatus.bind(mevDb, name)
                        }
                    });
                    var inactive = _.difference(local, remote).map(function(name){
                        return {
                            id: name,
                            name: name,
                            isActive: false,
                            getStatus: mevDb.getStatus.bind(mevDb, name)
                        }
                    });

                    return _.concat(active, inactive);
                })
                .catch(function(e){
                    throw e;
                });
            return allDbsPromise;
        }
        function activateDataset(datasetId){
            
        }
        function getActiveDatasets(){
            return getDatasets().then(function(datasets){
                return datasets.filter(function(dataset){
                    return dataset.isActive;
                });
            });
        }
        function getInactiveDatasets(){
            return getDatasets().then(function(datasets){
                return datasets.filter(function(dataset){
                    return !dataset.isActive;
                });
            });
        }
        function activateDataset(dataset){
            return DatasetResource.activate(dataset);
        }
        function exportDataset(dataset){
            return DatasetResource.export(dataset);
        }
        function deleteDataset(datasetId){
            return mevDb.deleteDataset(datasetId);
        }
        function getDataset(datasetId){
            return getDatasets()
                .then(function(datasets){
                    return datasets.find(function(dataset){
                        return dataset.id === datasetId;
                    })
                })
        }
        this.getDatasets = getDatasets;
        this.getActiveDatasets = getActiveDatasets;
        this.getInactiveDatasets = getInactiveDatasets;
        this.activateDataset = activateDataset;
        this.deleteDataset = deleteDataset;
        this.getDataset = getDataset;
        this.exportDataset = exportDataset;
    };
    service.$inject=["$http", "$q", "mevDb", "mevDatasetRest"];
    service.$name="mevWorkspace";
    service.$provider="service";
    return service;
});