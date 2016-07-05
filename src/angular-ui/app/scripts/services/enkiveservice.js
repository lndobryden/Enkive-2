'use strict';

/**
 * @ngdoc service
 * @name angularApp.enkiveService
 * @description
 * # enkiveService
 * Service in the angularApp.
 */
angular.module('angularApp')
  .service('EnkiveService', function (SpringDataRestAdapter, $http) {
    // AngularJS will instantiate a singleton by calling "new" on this function

    this.getMessages = function() {

      var headers = {authorization : "Basic " + btoa("user:password")};

      var promise = $http.get("http://localhost:8080/", {headers : headers});
      return SpringDataRestAdapter.process(promise).then(function (response) {
        return response.enkiveMessages_embeddedItems;
      });
    }

  });
