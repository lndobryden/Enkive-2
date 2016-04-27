'use strict';

describe('Service: enkiveService', function () {

  // load the service's module
  beforeEach(module('angularApp'));

  // instantiate service
  var enkiveService;
  beforeEach(inject(function (_enkiveService_) {
    enkiveService = _enkiveService_;
  }));

  it('should do something', function () {
    expect(!!enkiveService).toBe(true);
  });

});
