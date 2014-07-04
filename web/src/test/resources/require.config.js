  baseUrl : "",
  paths : {
    jquery : [ "/webjars/jquery/2.1.0/jquery" ],
    jqueryUi : ["empty"],
    jasmineJquery : ["jasmine-jquery"],
    angular : [ "/webjars/angularjs/1.2.13/angular" ],
    angularRoute : [ "/webjars/angularjs/1.2.13/angular-route" ],
    angularResource : [ "/webjars/angularjs/1.2.13/angular-resource" ],
    angularAnimate : [ "/webjars/angularjs/1.2.13/angular-animate" ],
    angularMocks : [ "/webjars/angularjs/1.2.13/angular-mocks" ],
    bootstrap : [ "empty" ],
    uiBootstrapTpls : [ "ui-bootstrap-tpls-0.11.0-SNAPSHOT", "bootstrap-ui-tpls" ],
    d3 : [ "empty" ],
    retina : [ "/webjars/retinajs/0.0.2/retina" ],
    notific8 : [ "notific8.min" ],
    ngGrid : [ "empty" ],
    blob : [ "canvasToBlob/Blob" ],
    canvasToBlob : [ "canvasToBlob/canvas-toBlob" ],
    fileSaver : [ "fileSaver/FileSaver" ],
    qtip : [ "/webjars/qtip2/2.1.1/jquery.qtip" ],
    "css-loader" : ["css-test"] ,
    "console":["console"],
    'log4js' : [ '/webjars/log4javascript/1.4.5/log4javascript' ]
      // Added an empty file to point to since there is no head and css
      // can"t actually load from css/styles container
  },
  shim : {
    "angular" : {
      exports : "angular"

    },
    "angularRoute" : {
      exports : "ngRoute",
      deps : [ "angular" ]
    },
    "angularResource" : {
      exports : "ngResource",
      deps : [ "angular" ]
    },
    "angularMocks" : {
        exports : "ngMocks",
        deps : [ "angular" ]
      },
    "uiBootstrapTpls" : {
      deps : [ "angular", "bootstrap" ]
    },
    "d3" : {
      exports : "d3"
    },
    "bootstrap" : {
      deps : [ "jquery" ]
    },
    "css-loader" : {
      deps : [ "bootstrap" ]
    },
    "notific8" : {
      deps : [ "jquery" ],
      exports : "notific8"
    },
    "ngGrid" : {
      deps : [ "jquery", "angular", "uiBootstrapTpls" ]
    },
    "canvasToBlob" : {
      deps : [ "blob" ],
      exports : "canvasToBlob"
    },
    "fileSaver" : {
      deps : [ "canvasToBlob" ],
      exports : "fileSaver"
    },
    "jqueryUi": {
        deps : ["jquery", "css-loader"]
    },
    "jasmineJquery" : {
        deps : ["jquery"]
    },
    "qtip": {
      deps : ["jquery", "imagesloaded"],
      exports : "qtip"
    },
    'log4js' : {
      exports : 'log4javascript'
    }
  },
  packages : [ {
    name : "mainmenu",
    location : "mainmenu",
    main : "MainMenu.package"
  } ],
  waitSeconds : "2"