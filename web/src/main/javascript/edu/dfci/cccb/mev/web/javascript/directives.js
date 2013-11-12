define (
    [ 'angular', 'jquery', 'd3', 'newick', 'services' ],
    function (angular, jq, d3, newick) {

      return angular
          .module ('myApp.directives', [])
          .directive ('appVersion', [ 'appVersion', function (version) {
            return function (scope, elm, attrs) {
              elm.text (version);
            };
          } ])
          .directive ('appName', [ 'appName', function (name) {
            return function (scope, elm, attrs) {
              elm.text (name);
            };
          } ])
          .directive ('mainNavigation',
              [ 'mainMenuBarOptions', function (opts) {
                return {
                  restrict : 'A',
                  templateUrl : '/container/view/elements/mainNavigation',
                  link : function (scope) {
                    scope.menu = opts;
                  }
                };
              } ])
          .directive ('heatmapPanels', function () {
            return {
              restrict : 'A',
              templateUrl : '/container/view/elements/heatmapPanels',
              link : function (scope, elems, attrs) {

                jq ('#closeRight').hide ();
                jq ('#closeLeft').hide ();

                scope.expandLeft = function () {

                  jq ('#rightPanel').hide ();
                  jq ('#expandLeft').hide ();
                  jq ('#closeLeft').show ();
                  jq ('#leftPanel').show ();
                  jq ('#leftPanel').attr ("class", "span12");

                };

                scope.expandRight = function () {

                  jq ('#leftPanel').hide ();
                  jq ('#expandRight').hide ();
                  jq ('#closeRight').show ();
                  jq ('#rightPanel').show ();
                  jq ('#rightPanel').attr ("class", "span12");

                };

                scope.expandBoth = function () {

                  jq ('#closeRight').hide ();
                  jq ('#closeLeft').hide ();
                  jq ('#expandRight').show ();
                  jq ('#expandLeft').show ();
                  jq ('#rightPanel').show ();
                  jq ('#leftPanel').show ();
                  jq ('#leftPanel').attr ("class", "span6");
                  jq ('#rightPanel').attr ("class", "span6");

                };

              }
            };
          })
          .directive ('menubar', [ 'analysisOptions', function (opts) {
            return {
              restrict : 'E',
              templateUrl : '/container/view/elements/menubar',
              link : function (scope) {
                scope.links = opts;
              }
            };
          } ])
          .directive ('expressionPanel', [ function () {
            return {
              restrict : 'A',
              templateUrl : '/container/view/elements/expressionPanel',
              link : function (scope) {

              }
            };
          } ])
          .directive ('analysisPanel', [ function () {
            return {
              restrict : 'A',
              templateUrl : '/container/view/elements/analysisPanel',
              link : function (scope) {

              }
            };
          } ])
          .directive ('bsprevanalysis', function () {

            return {

              restrict : 'C',
              scope : {

                bindid : '@',
                parentid : '@',
                header : '@',
                data : '@'

              }
            };

          })
          .directive ('bsTable', function () {

            return {
              scope : {
                data : "="
              },
              restrict : 'E',
              templateUrl : "/container/view/elements/table"

            };

          })
          .directive (
              'bsImgbutton',
              function () {

                return {
                  scope : {
                    icon : "@",
                    title : "@",
                    align : "@"
                  },
                  restrict : 'E',
                  template : "<button class='btn btn-success pull-{{align}}' "
                      + "title='{{title}}'>  "
                      + "<i class='icon-{{icon}}'></i> Download" + "</button>"

                };

              })
          .directive ('prevlimma', function () {

            return {

              restrict : 'C',
              templateUrl : "/container/view/elements/prevlimmashell"

            };

          })
          .directive ('bsmodal', [ '$compile', function ($compile) {

            return {

              restrict : 'E',
              scope : {

                bindid : '@',
                header : '@',
                test : '@',
                func : '&'

              },
              transclude : true,
              templateUrl : "/container/view/elements/modal"

            };

          } ])
          .directive ('modalHierarchical', function () {

            return {
              restrict : 'C',
              templateUrl : "/container/view/elements/hierarchicalbody",
              link : function (scope, elems, attrs) {
                scope.types = [ {
                  name : 'K-Means'
                }, {
                  name : 'K-Medians'
                } ];
              }

            };

          })
          .directive ('modalKmeans', function () {

            return {
              restrict : 'C',
              templateUrl : "/container/view/elements/kMeansBody"

            };

          })
          .directive ('modalLimma', function () {

            return {
              restrict : 'C',
              templateUrl : "/container/view/elements/limmaBody"

            };

          })
          .directive (
              'd3RadialTree',
              ['API', function (API) {

                return {
                  restrict : 'A',
                  scope : {
                    url : '@',
                    diameter : '@'

                  },
                  template : '<div id="vis"></div>', // requires
                  // css
                  // location
                  link : function (scope, elems, attr) {

                    
                    var cluster = API.hcl.radial.get("mock/cluster");
                    //cluster.then(function(data){
                    d3.text('heatmap/mock/cluster?format=newick', function(data){
                              
                              var r = scope.diameter / 2;

                              var cluster = d3.layout.cluster ().size (
                                  [ 360, 1 ]).sort (null).value (function (d) {
                                return d.length;
                              }).children (function (d) {
                                return d.branchset;
                              }).separation (function (a, b) {
                                return 1;
                              });

                              function project (d) {
                                var r = d.y, a = (d.x - 90) / 180 * Math.PI;
                                return [ r * Math.cos (a), r * Math.sin (a) ];
                              }

                              function cross (a, b) {
                                return a[0] * b[1] - a[1] * b[0];
                              }
                              function dot (a, b) {
                                return a[0] * b[0] + a[1] * b[1];
                              }

                              function step (d) {
                                var s = project (d.source), m = project ({
                                  x : d.target.x,
                                  y : d.source.y
                                }), t = project (d.target), r = d.source.y, sweep = d.target.x > d.source.x ? 1
                                    : 0;
                                return ("M" + s[0] + "," + s[1] + "A" + r + ","
                                    + r + " 0 0," + sweep + " " + m[0] + ","
                                    + m[1] + "L" + t[0] + "," + t[1]);
                              }
                              
                              var wrap = d3.select (elems[0])
                                  .append ("svg").attr ("width", r * 2).attr (
                                      "height", r * 2).style (
                                      "-webkit-backface-visibility", "hidden");

                              // Catch mouse events in Safari.
                              wrap.append ("rect").attr ("width", r * 2).attr (
                                  "height", r * 2).attr ("fill", "none");

                              var vis = wrap.append ("g").attr ("transform",
                                  "translate(" + r + "," + r + ")");

                              var start = null, rotate = 0, div = elems[0].childNodes[1];

                              function mouse (e) {
                                return [ e.pageX - div.offsetLeft - r,
                                    e.pageY - div.offsetTop - r ];
                              }

                              wrap.on ("mousedown", function () {
                                wrap.style ("cursor", "move");
                                start = mouse (d3.event);
                                d3.event.preventDefault ();
                              });
                              d3
                                  .select (window)
                                  .on (
                                      "mouseup",
                                      function () {
                                        if (start) {
                                          wrap.style ("cursor", "auto");
                                          var m = mouse (d3.event);
                                          var delta = Math.atan2 (cross (start,
                                              m), dot (start, m))
                                              * 180 / Math.PI;
                                          rotate += delta;
                                          if (rotate > 360)
                                            rotate %= 360;
                                          else if (rotate < 0)
                                            rotate = (360 + rotate) % 360;
                                          start = null;
                                          wrap
                                              .style ("-webkit-transform", null);
                                          vis
                                              .attr (
                                                  "transform",
                                                  "translate(" + r + "," + r
                                                      + ")rotate(" + rotate
                                                      + ")")
                                              .selectAll ("text")
                                              .attr (
                                                  "text-anchor",
                                                  function (d) {
                                                    return (d.x + rotate) % 360 < 180 ? "start"
                                                        : "end";
                                                  })
                                              .attr (
                                                  "transform",
                                                  function (d) {
                                                    return "rotate("
                                                        + (d.x - 90)
                                                        + ")translate("
                                                        + (r - 170 + 8)
                                                        + ")rotate("
                                                        + ((d.x + rotate) % 360 < 180 ? 0
                                                            : 180) + ")";
                                                  });
                                        }
                                      }).on (
                                      "mousemove",
                                      function () {
                                        if (start) {
                                          var m = mouse (d3.event);
                                          var delta = Math.atan2 (cross (start,
                                              m), dot (start, m))
                                              * 180 / Math.PI;
                                          wrap.style ("-webkit-transform",
                                              "rotateZ(" + delta + "deg)");
                                        }
                                      });

                              function phylo (n, offset) {
                                if (n.length != null)
                                  offset += n.length * 115;
                                n.y = offset;
                                if (n.children)
                                  n.children.forEach (function (n) {
                                    phylo (n, offset);
                                  });
                              }

                              var x = newick.parse (data);
                              var nodes = cluster.nodes (x);
                              phylo (nodes[0], 0);

                              var link = vis.selectAll ("path.link").data (
                                  cluster.links (nodes)).enter ().append (
                                  "path").attr ("class", "link").attr ("d",
                                  step);

                              var node = vis.selectAll ("g.node").data (
                                  nodes.filter (function (n) {
                                    return n.x !== undefined;
                                  })).enter ().append ("g").attr ("class",
                                  "node").attr (
                                  "transform",
                                  function (d) {
                                    return "rotate(" + (d.x - 90)
                                        + ")translate(" + d.y + ")";
                                  });

                              node.append ("circle").attr ("r", 2.5);

                              var label = vis.selectAll ("text").data (
                                  nodes.filter (function (d) {
                                    return d.x !== undefined && !d.children;
                                  })).enter ().append ("text").attr ("dy",
                                  ".31em").attr ("text-anchor", function (d) {
                                return d.x < 180 ? "start" : "end";
                              }).attr (
                                  "transform",
                                  function (d) {
                                    return "rotate(" + (d.x - 90)
                                        + ")translate(" + (r - 170 + 8)
                                        + ")rotate(" + (d.x < 180 ? 0 : 180)
                                        + ")";
                                  }).text (function (d) {
                                return d.name.replace (/_/g, ' ');
                              });

                            }); // end data watcher

                  } // end link
                };

              } ]).directive ('bsTable', function () {

            return {
              scope : {
                data : "="
              },
              restrict : 'E',
              templateUrl : "/container/view/elements/table.html"

            };

          }).directive ('visHeatmap', ['API', function (API) {

            return {

              restrict : 'A',
              //templateUrl : "/container/view/elements/visHeatmap",
              link: function(scope, elems, attr){
            	  
                
            	scope.width = 400;
            	scope.height = 700;
            	scope.marginleft = 20;
            	scope.marginright = 20;
            	scope.margintop = 20;
            	scope.marginbottom = 20;
            	  
            	API.heatmap.get('mock/data')
            	  .then(function(data){
            		  
            	    
            		  var cellwidth = 10;
            		  
            		  var margin = {
                              left: scope.marginleft,
                              right: scope.marginright,
                              top: scope.margintop,
                              bottom: scope.marginbottom
                      };
                      
                      var width = scope.width - margin.left - margin.right;
              
                      var height = scope.height - margin.top - margin.bottom;
            		  
            		  var window = d3.select(elems[0]);
            		  
            		  var cellXPosition = function(key) {
            			  return cellwidth * key;
            		  };
            		  
                      var cellYPosition = function(key) {
            			  return cellwidth * key;
            		  };
            		  
            		  var svg = window
                        .append("svg")
                        .attr("class", "chart")
                        //.attr("pointer-events", "all")
                        .attr("width", width + margin.left + margin.right)
                        .attr("height", height + margin.top + margin.bottom);
            		  
            		  var vis = svg.append("g")
                        .attr("class", "uncovered");
            		  
            		  var heatmapcells = vis.append("g")
                        .selectAll("rect")
                              .data(data)
                              .enter()
                              .append("rect");
                              
                      draw();
                      
                      function draw() {
                    	  
                    	  heatmapcells
                          .attr({
                                  "class": "cells",
                                  "height": function(d){
                                          return cellwidth ;
                                  },
                                  "width": function(d){
                                          return cellwidth ;
                                  },
                                  "x": function(d, i) { return cellXPosition( d.columnOrder ); },
                                  "y": function(d, i) { return cellYPosition( d.rowOrder ); },
                                  "fill": function(d) {
                                          return "rgb(0," + 255 * Math.floor(Math.sqrt( d.value*d.value ) ) + ",0)";
                                  },
                                  "value": function(d) { return d.value; },
                                  "index": function(d, i) { return i; },
                                  "row": function(d, i) { return d.rowOrder; },
                                  "column": function(d, i) { return d.columnOrder; },
                                  "rowKey": function(d, i) {return d.rowKey},
                                  "columnKey": function(d, i) {return d.columnKey},
                          }); 
                    	  
                      };
            		  
            		  
            	  });
              }

            };
          } ]);

    });