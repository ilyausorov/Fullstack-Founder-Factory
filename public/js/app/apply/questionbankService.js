var questionbank = angular.module('questionbank', []);

questionbank.service('questionbankService',['$http','$q', questionbankService]);

function questionbankService($http,$q){

this.question_types = function() {
    var deferred = $q.defer();
    var url = jsRoutes.controllers.ApplyCtrl.question_types()
    $http({
      method: url.method,
      url: url.url
    })
    .success(function (jdata) {
      deferred.resolve(jdata);
    })
    .error(function(jdata){
      deferred.reject(jdata);
    });

    return deferred.promise;
  };

this.application_headers = function() {
    var deferred = $q.defer();
    var url = jsRoutes.controllers.ApplyCtrl.application_headers()
    $http({
      method: url.method,
      url: url.url
    })
    .success(function (jdata) {
      deferred.resolve(jdata);
    })
    .error(function(jdata){
      deferred.reject(jdata);
    });

    return deferred.promise;
  };
	
this.question_list = function(name) {
    var deferred = $q.defer();
    var url = jsRoutes.controllers.ApplyCtrl.question_list(name)
    $http({
      method: url.method,
      url: url.url
    })
    .success(function (jdata) {
      deferred.resolve(jdata);
    })
    .error(function(jdata){
      deferred.reject(jdata);
    });

    return deferred.promise;
  };
}