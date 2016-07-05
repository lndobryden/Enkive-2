'use strict';

/**
 * @ngdoc function
 * @name angularApp.controller:SearchCtrl
 * @description
 * # SearchCtrl
 * Controller of the angularApp
 */
angular.module('angularApp')
  .controller('SearchCtrl', function (EnkiveService) {
    this.messages = EnkiveService.getMessages();
  });
