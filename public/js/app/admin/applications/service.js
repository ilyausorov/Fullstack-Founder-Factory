var applications = angular.module('applications', []);

function applicationsService($http, $q) {

    this.list = function() {
        var deferred = $q.defer();
        var url = jsRoutes.controllers.ApplyCtrl.listAdmin()
        $http({
                method: url.method,
                url: url.url
            })
            .success(function(jdata) {
                deferred.resolve(jdata);
            })
            .error(function(jdata) {
                deferred.reject(jdata);
            });
        return deferred.promise;
    };

    this.delete = function(id) {
        var deferred = $q.defer();
        var url = jsRoutes.controllers.ApplyCtrl.deleteAdmin(id);
        $http({
                method: url.method,
                url: url.url
            })
            .success(function(jdata) {
                deferred.resolve(jdata);
            })
            .error(function(jdata) {
                deferred.reject(jdata);
            });
        return deferred.promise;
    };
	
	this.detailApplication = function(id) {
        var deferred = $q.defer();
        var url = jsRoutes.controllers.ApplyCtrl.detailApplication(id);
        $http({
                method: url.method,
                url: url.url
            })
            .success(function(jdata) {
                deferred.resolve(jdata);
            })
            .error(function(jdata) {
                deferred.reject(jdata);
            });

        return deferred.promise;
    };
	
	this.updateRead = function(id) {
        var deferred = $q.defer();
        var url = jsRoutes.controllers.ApplyCtrl.updateRead(id);
        $http({
                method: url.method,
                url: url.url
            })
            .success(function(jdata) {
                deferred.resolve(jdata);
            })
            .error(function(jdata) {
                deferred.reject(jdata);
            });

        return deferred.promise;
    };
	
	this.listAnswers = function(id) {
        var deferred = $q.defer();
        var url = jsRoutes.controllers.ApplyCtrl.listAnswers(id);
        $http({
                method: url.method,
                url: url.url
            })
            .success(function(jdata) {
                deferred.resolve(jdata);
            })
            .error(function(jdata) {
                deferred.reject(jdata);
            });

        return deferred.promise;
    };
}

applications.service('applicationsService', ['$http', '$q', applicationsService]);