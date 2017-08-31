var userExtended = angular.module('userExtended', []);

function userExtendedService($http, $q) {

    this.list = function() {
        var deferred = $q.defer();

        $http({
                method: "GET",
                url: jsRoutes.controllers.UserExtendedCtrl.listAdmin().url
            })
            .success(function(jdata) {
                deferred.resolve(jdata);
            })
            .error(function(jdata) {
                deferred.reject(jdata);
            });

        return deferred.promise;
    };

    this.detail = function(id) {

        var deferred = $q.defer();

        $http({
                method: "GET",
                url: jsRoutes.controllers.UserExtendedCtrl.detailAdmin(id).url
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

        $http({
                method: "GET",
                url: jsRoutes.controllers.UserExtendedCtrl.deleteAdmin(id).url
            })
            .success(function(jdata) {
                deferred.resolve(jdata);
            })
            .error(function(jdata) {
                deferred.reject(jdata);
            });

        return deferred.promise;
    };

    this.update = function(v, id) {

        var deferred = $q.defer();

        var url = jsRoutes.controllers.UserExtendedCtrl.insertAdmin().url;
        if (id) {
            url = jsRoutes.controllers.UserExtendedCtrl.updateAdmin(id).url;
        }

        $http({
                method: "POST",
                url: url,
                data: v
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

userExtended.service('userExtendedService', ['$http', '$q', userExtendedService]);