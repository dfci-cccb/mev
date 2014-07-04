define ([ 'log' ], function (log) {
  describe ("log", function () {
    it ("should log to error", function () {
      log.error ("error");
    });

    it ("should log to warn", function () {
      log.warn ("warn");
    });

    it ("should log to info", function () {
      log.info ("info");
    });

    it ("should log to debug", function () {
      log.debug ("debug");
    });
  });
});