define(["lodash", "pouchdb"], function(_, PouchDB){"use strict";
    var service = function mevDbService(mevContext, mevSettings, $q, $rootScope, $timeout){
        var _self = this;
        var db = new PouchDB("mev", {adapter: 'worker'});
        function ensureDataset(){
            var dataset = mevContext.get("dataset");
            if(!dataset)
                throw new Error("Could not locate dataset for current context: "  + JSON.stringify(mevContext));
            return dataset;
        }
        function getDataset(datasetId){
            if(mevSettings.db.enabled)
                return db.get(datasetId);
            else{
                var deferred = $q.defer();
                deferred.reject({
                    status: 501,
                    message: "Local db is disabled"
                });
                return deferred.promise;
            }
        }
        function putDataset(dataset, isRetry){
            return getDataset(dataset.id)
                .catch(function(e){
                    if(e.status === 404){
                        delete dataset._rev;
                        return _.assign(dataset, {
                            _id: dataset.id
                        });
                    } else
                        throw e;
                })
                .then(function(doc){
                    dataset._id = dataset.id;
                    dataset._rev = doc._rev;
                    var clone = _.cloneDeep(dataset);
                    clone.$promise = undefined;
                    clone._annotations = undefined;
                    clone.values=[];
                    clone.analyses=[];

                    _firePutStarted(dataset.id, "dataset");
                    return db.put(JSON.parse(JSON.stringify(clone)))
                        .then(function(){
                            _firePutCompleted(dataset.id, "dataset");
                            return arguments[0];
                        })
                        .catch(function(){
                            _firePutCompleted(dataset.id, "dataset");
                            return arguments[0];
                        });
                })
                .catch(function(e){
                    if(e.status===409)
                        putDataset(dataset, true);
                    else if(e.status === 501)
                        console.warn("Warning saving dataset locally", e);
                    else{
                        console.error("Error saving dataset locally:", e, dataset);
                        throw e;
                    }
                })
        }
        function getDatasets(){
            // return db.allDocs({startKey: "dataset::", endKey: "dataset::\uFFFF;"});
            if(mevSettings.db.enabled)
                return db.allDocs().then(function(result){
                    return _.uniq(result.rows
                        .filter(function(doc){
                            return doc.id.indexOf("/values64") > -1;
                        }).map(function(doc){
                            return doc.id.split("/")[0];
                        })
                    );
                });
            else
                return $q.when([]);
        }
        function formatDocId(path, datasetId){
            datasetId = datasetId
                ? datasetId
                : ensureDataset().id;
            path = _.isArray(path)
                ? path.join("/")
                : path
            return datasetId + "/" + path;
        }
        function _rejectDisabled(){
            var deferred = $q.defer();
            deferred.reject({
                status: 404,
                message: "Local db is disabled"
            });
            return deferred.promise;
        }
        function getDatasetValues(datasetId){
            if(!mevSettings.db.enabled)
                _rejectDisabled();
            return db.getAttachment(formatDocId("values", datasetId), "all");
        }
        function getDatasetValues64(datasetId){
            if(!mevSettings.db.enabled)
                _rejectDisabled();
            return db.getAttachment(formatDocId("values64", datasetId), "chunk0");
        }
        function putDatasetValues(blob, datasetId){
            _firePutStarted(
                datasetId || ensureDataset().id,
                "values");
            if(!mevSettings.db.enabled)
                return;
            var doc = {
                _id: formatDocId("values"),
                _attachments: {
                    "all": {
                        data: blob,
                        type: "application/octet-stream",
                        content_type : "application/octet-stream"
                    }
                }
            };
            db.put(doc)
                .then(function(){
                    _firePutCompleted(ensureDataset().id, "values");
                })
                .catch(function(){
                    _firePutCompleted(ensureDataset().id, "values");
                });
        }
        function _removeDocByRow(doc){
            return db.remove(doc.id, doc.value.rev);
            // console.debug("remove", doc);
        }
        function _getAllDatasetDocs(datasetId){
            return db.allDocs({
                    startKey: datasetId,
                    endKey: datasetId+"\uffff"
                })
                .then(function(docs){
                    _.remove(docs.rows, function(row){
                        return row.id !== datasetId && row.id.indexOf(datasetId+"/") !== 0;
                    })
                    return docs;
                });
        }
        function deleteDataset(datasetId){
            return _getAllDatasetDocs(datasetId)
                .then(function(docs){
                    return db.bulkDocs(
                        docs.rows.map(function(row){
                            return {
                                _id: row.id,
                                _rev: row.value.rev,
                                _deleted: true
                            }
                        }));
                });
            // return getDataset(datasetId)
            //     .then(_removeDoc)
            //     .finally(getDatasetValues.bind(datasetId))
            //     .finally(getDatasetValues64.bind(datasetId));
        }
        function getAnalyses(datasetId){
            if(!mevSettings.db.enabled)
                return $q.when([]);

            return db.allDocs()
                .then(function(result){
                    return result.rows.filter(function(doc){
                        return doc.id.indexOf(formatDocId(["analysis"], datasetId))>-1;
                    });
                })
                .then(function(docs){
                    return docs.map(function(doc){
                        return doc.id.replace(formatDocId(["analysis"], datasetId), "").replace("/", "");
                    });
                });
            // db.get(formatDocId(["analysis"], datasetId))
            //     .then(function(docs){
            //         return docs.map(function(doc){
            //             return doc.name;
            //         });
            //     });
        }
        function getAnalysesAll(datasetId){
            return getAnalyses(datasetId)
                .then(function(analysisNames){
                    return $q.all(analysisNames.map(getAnalysis.bind(null, datasetId)));
                });
        }
        function getAnalysis(datasetId, analysisId){
            if(!mevSettings.db.enabled)
                return _rejectDisabled();
            return db.get(formatDocId(["analysis", analysisId], datasetId));
        }
        function putAnalysis(datasetId, analysis, isRetry){
            if(!mevSettings.db.enabled)
                return _rejectDisabled();

            if(!isRetry)
                $rootScope.$broadcast("mev:db:put:started", datasetId, analysis);

            return getAnalysis(datasetId, analysis.name)
                .catch(function(e){
                    if(e.status === 404){
                        delete analysis._rev;
                        return _.assign(analysis, {
                            _id: formatDocId(["analysis", analysis.name], datasetId)
                        });
                    }else
                        throw new Error("Error putting analysis" + JSON.stringify(e));
                })
                .then(function(doc){
                    analysis._rev = doc._rev;
                    return db.put(JSON.parse(JSON.stringify(analysis)));
                })
                .then(function(){
                    $rootScope.$broadcast("mev:db:put:completed", datasetId, analysis);
                })
                .catch(function(e){
                    if(e.status===409)
                        putAnalysis(datasetId, analysis, true);
                    else{
                        console.error("Error saving analysis locally:" , datasetId, analysis, e);
                        throw e;
                    }
                });

        }
        function deleteAnalysis(datasetId, analysisName){
            return getAnalysis(datasetId, analysisName)
                .then(function(doc){
                    return db.remove(doc);
                });
        }
        function _firePutStarted(dataset, item){
            $rootScope.$broadcast("mev:db:put:started", dataset, item);
        }
        function _firePutCompleted(dataset, item){
            $timeout(function(){
                $rootScope.$broadcast("mev:db:put:completed", dataset, item);
            })
        }

        function putAnnotations(datasetId, dimension, blob, isRetry){
            if(!mevSettings.db.enabled)
                return;
            if(!isRetry)
                _firePutStarted(datasetId, dimension);

            var doc = {
                _id: formatDocId(["annotations", dimension], datasetId),
                _attachments: {
                    "all": {
                        data: blob,
                        type: "application/octet-stream",
                        content_type : "application/octet-stream"
                    }
                }
            };
            db.put(doc)
                .catch(function(e){
                    if(e.status===409)
                        putAnnotations(datasetId, dimension, blob, true);
                    else
                        throw e;
                })
                .then(_firePutCompleted.bind(_self, datasetId, dimension))
                .catch(_firePutCompleted.bind(_self, datasetId, dimension));
        }
        function getAnnotations(datasetId, dimension){
            return db.getAttachment(formatDocId(["annotations", dimension], datasetId), "all");
        }
        var status = {};
        function getStatus(datasetId){
            var keys = datasetId
                ? _.filter(_.keys(status), function(key){
                        return key.indexOf(datasetId) === 0;
                    })
                : _.keys(status);
            var msg = "saved";
            if(keys.length>0)
                msg = "saving "
                    + datasetId
                        ? keys[0].replace(datasetId+":", "")
                        : keys[0];

            // console.debug("status msg", msg);
            return msg;
        }
        function _formatStatusKey(dataset, item){
            return dataset + ":" + item;
        }
        $rootScope.$on("mev:db:put:started", function(event, dataset, item){
            status[_formatStatusKey(dataset, item)] = {
                dataset: dataset,
                item: item
            };
        });
        $rootScope.$on("mev:db:put:completed", function(event, dataset, item){
            delete status[_formatStatusKey(dataset, item)];
        });
        return {
            getDataset: getDataset,
            putDataset: putDataset,
            deleteDataset: deleteDataset,
            getDatasets: getDatasets,
            getDatasetValues: getDatasetValues,
            putDatasetValues: putDatasetValues,
            getDatasetValues64: getDatasetValues64,
            getAnalysis: getAnalysis,
            putAnalysis: putAnalysis,
            getAnalyses: getAnalyses,
            getAnalysesAll: getAnalysesAll,
            deleteAnalysis: deleteAnalysis,
            putAnnotations: putAnnotations,
            getAnnotations: getAnnotations,
            getStatus: getStatus,
            firePutStarted: _firePutStarted,
            firePutCompleted: _firePutCompleted,
            onPutStarted: function(callback){
                $rootScope.$on("mev:db:put:started", callback);
            },
            onPutCmopleted: function(callback){
                $rootScope.$on("mev:db:put:cmopleted", callback);
            }
        }
    };

    service.$name="mevDb";
    service.$provider="service";
    service.$inject=["mevContext", "mevSettings", "$q", "$rootScope", "$timeout"];
    return service;
});