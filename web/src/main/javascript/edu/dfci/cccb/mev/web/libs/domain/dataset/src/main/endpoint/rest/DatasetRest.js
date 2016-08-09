define(["lodash"], function(_){
    var DatasetRest = function ($resource, $q, $http, $rootScope, mevDb) {
        var resource = $resource('/dataset/:datasetName/data', {format: "json"},
            {
                'get': {method: 'GET'},
                'getAll': {
                    url: '/dataset',
                    method: 'GET',
                    isArray: true
                },
                'subset': {
                    url: '/dataset/:datasetName/data/subset/export',
                    method: "POST"
                }
            });
        var DatasetResource = Object.create(resource);

        DatasetResource.get = function (params, data, callback) {
            var deferred = $q.defer();
            var cache = {
                $promise: deferred.promise,
                $resolved: false
            };
            var cachePromise = mevDb.getDataset(params.datasetName)
                .catch(function (e) {
                    if (e.status === 404 || e.status === 501) {
                        _.assign(cache, resource.get(params, data, callback))
                        return cache.$promise.then(function (response) {
                            mevDb.putDataset(_.assign(response, {id: params.datasetName}));
                            return response;
                        });
                    }
                }).then(function (response) {
                    return deferred.resolve(
                        _.extend(response, {
                            $promise: cache.$promise
                        })
                    );
                });
            return cache;
        };
        DatasetResource.getAll = function (params, data, callback) {
            var datasetsResource = resource.getAll(params, data, callback);
            datasetsResource.$promise.then(function (response) {
                $rootScope.$broadcast("mev:datasets:list:refreshed", response);
            });
            return datasetsResource;
        };
        DatasetResource.subset = function (params, data, callback) {
            data.name = params.datasetName + "--" + data.name;
            var datasetsResource = resource.subset(params, data, callback);
            datasetsResource.$promise.then(function (response) {
                $http({
                    method: "POST",
                    url: "/annotations/" + params.datasetName + "/annotation/row"
                    + "/export?destId=" + data.name
                });
                $http({
                    method: "POST",
                    url: "/annotations/" + params.datasetName + "/annotation/column"
                    + "/export?destId=" + data.name
                });
                DatasetResource.getAll();
            })
            return datasetsResource;
        };
        DatasetResource.uploadFile = function (file) {
            var formdata = new FormData;
            formdata.append('upload', file);
            formdata.append('name', file.name);
            var xhr = new XMLHttpRequest();
            xhr.upload.addEventListener("progress", function (e) {
                return;
            });
            xhr.onreadystatechange = function () {
                if (xhr.readyState == 4 && xhr.status == 200) {
                    DatasetResource.getAll();
                }
                ;
            };
            xhr.open("POST", "/dataset", true);
            xhr.send(formdata);
        };
        DatasetResource.activate = function(dataset){
            return mevDb.getDataset(dataset.id)
                .then(function(dataset){
                    return mevDb.getDatasetValues64(dataset.id)
                        .then(function(values){
                            var formdata = new FormData();
                            formdata.append('name', dataset.id);
                            formdata.append('rows', dataset.row.keys);
                            formdata.append('rowSelections', JSON.stringify(dataset.row.selections));
                            // formdata.append('rowSelections', new Blob([JSON.parse(JSON.stringify(dataset.row.selections))],
                            //     {
                            //         type: "application/json"
                            //     })
                            // );
                            formdata.append('columns', dataset.column.keys);
                            formdata.append('columnSelections', JSON.stringify(dataset.column.selections));

                            formdata.append('upload', values);
                            var xhr = new XMLHttpRequest();

                            xhr.upload.addEventListener("progress", function (e) {
                                return;
                            });
                            xhr.onreadystatechange = function () {
                                if (xhr.readyState == 4 && xhr.status == 200) {
                                    $rootScope.$broadcast("mev:dataset:activated", dataset);
                                    DatasetResource.getAll();
                                }
                                ;
                            };
                            xhr.open("POST", "/import/binary", true);
                            xhr.send(formdata);
                        });
                });
        }
        function formatDatasetName(name){
            return _.endsWith(name, ".zip")
                ? name.substring(0, name.length-4)
                : name;
        }
        DatasetResource.importZip = function(file){
            return mevDb.deleteDataset(formatDatasetName(file.name))
                .then(function(){
                    var formdata = new FormData;
                    formdata.append('upload', file);
                    formdata.append('name', file.name);
                    var xhr = new XMLHttpRequest();
                    xhr.upload.addEventListener("progress", function (e) {
                        return;
                    });
                    xhr.onreadystatechange = function () {
                        if (xhr.readyState == 4 && xhr.status == 200) {
                            DatasetResource.getAll();
                        }
                        ;
                    };
                    xhr.open("POST", "/import/zip", true);
                    xhr.send(formdata);
                });
        };
        DatasetResource.export = function(dataset){
            return mevDb.getDataset(dataset.id)
                .then(function(dataset){
                    return mevDb.getAnalysesAll(dataset.id)
                        .then(function(analyses){
                            var formdata = new FormData();
                            formdata.append('name', dataset.id);
                            formdata.append('rows', dataset.row.keys);
                            formdata.append('rowSelections', JSON.stringify(dataset.row.selections));
                            // formdata.append('rowSelections', new Blob([JSON.parse(JSON.stringify(dataset.row.selections))],
                            //     {
                            //         type: "application/json"
                            //     })
                            // );
                            formdata.append('columns', dataset.column.keys);
                            formdata.append('columnSelections', JSON.stringify(dataset.column.selections));

                            analyses.forEach(function(analysis){
                                formdata.append('analyses', JSON.stringify(analysis));
                            });
                            var xhr = new XMLHttpRequest();
                            xhr.upload.addEventListener("progress", function (e) {
                                return;
                            });
                            xhr.onreadystatechange = function () {
                                if (xhr.readyState == 4 && xhr.status == 200) {
                                    $rootScope.$broadcast("mev:dataset:exported", dataset);
                                    var blob = new Blob([xhr.response], {type: "octet/stream"});
                                    var fileName = dataset.id+".zip";
                                    saveAs(blob, fileName);
                                }
                                ;
                            };

                            xhr.open("POST", "/export/zip", true);
                            xhr.responseType = "arraybuffer";
                            xhr.setRequestHeader("Accept", "application/octet-stream");
                            xhr.send(formdata);
                        });
                });
        }

        return DatasetResource;
    }
    DatasetRest.$inject=['$resource', '$q', '$http', '$rootScope', "mevDb"];
    DatasetRest.$name="mevDatasetRest";
    DatasetRest.$provider="service";
    return DatasetRest;

});