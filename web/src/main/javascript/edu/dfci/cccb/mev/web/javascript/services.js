define ([ 'angular', 'd3' ], function (angular, d3) {

  return angular.module ('myApp.services', []).value ('appVersion', '0.1')
      .value ('appName', 'MEV: Multi-Experiment Viewer').factory (
          'mainMenuBarOptions', [ function () {
            return [ {
              value : "About",
              url : "#"
            }, {
              value : "Contact",
              url : "#"
            } ];
          } ]).factory ('analysisOptions', [ function () {

        // future feature

        return undefined;
        
      } ])
      .factory ('heatmapGenerator', [function () {
        return null;
      }])
      .factory ('QHTTP', [ '$http', '$q', function ($http, $q) {

        return function (params, callback_fn, error_fn) {
          var deferred = $q.defer ();

          $http (params).success (function (data, status) {

            deferred.resolve (callback_fn (data, status));

          }).error (function (data, status) {

            deferred.reject (error_fn (data, status));

          });

          return deferred.promise;
        };

      } ])
      .factory ('API', [ 'QHTTP', function (QHTTP) {

        return {

          heatmap : {
            get : function (url) {
              var params = {
                method : 'GET',
                url : 'heatmap/' + url + '?format=json',
                format : 'json'
              };
              return QHTTP (params, function (d, s) {
                return d;
              }, function (d, s) {
                return d, s;
              });

            }
          },
          hcl: {
            radial: {
              get: function(url){
                
                var params = {
                    method : 'GET',
                    url : 'heatmap/' + url + '?format=newick',
                    format : 'json'
                  };
                  return QHTTP (params, function (d, s) {
                    
                    return d3.text('heatmap/' + url + '?format=newick', function(data) {return data})
                    
                  }, function (d, s) {
                    return d, s;
                  });

              }
            },
            linear :{
              get: function(url){
                return null;
              }
            }
          }

        }
      } ]);

});