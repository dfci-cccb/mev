paths : {
  mev : [ '/classpath/META-INF/resources/javascript/mev' ],
  underscore : [ '/webjar/underscore' ],
  angular : [ '/webjar/angular' ],
  angularResource : [ '/webjar/angular-resource' ],
  angularRoute : [ '/webjar/angular-route' ]
},
shim : {
  'angular' : {
    exports : 'angular'
  },
  'angularRoute' : {
    exports : 'ngRoute',
    deps: [ 'angular' ]
  },
  'angularResource' : {
    exports : 'ngResource',
    deps: [ 'angular' ]
  }
}