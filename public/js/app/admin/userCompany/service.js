var userCompany = angular.module('userCompany', []);

function userCompanyService($http, $q) {

    this.list = function(id) {
        var deferred = $q.defer();
        var url = jsRoutes.controllers.UserCompanyCtrl.listing(id)
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

    this.update = function(v, id) {

        var deferred = $q.defer();

        var url = jsRoutes.controllers.UserCompanyCtrl.insert();
        if (v.id) {
            var url = jsRoutes.controllers.UserCompanyCtrl.update(id);
        }

        $http({
                method: url.method,
                url: url.url,
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

    this.delete = function(id) {

        var deferred = $q.defer();

        var url = jsRoutes.controllers.UserCompanyCtrl.delete(id);

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

userCompany.service('userCompanyService', ['$http', '$q', userCompanyService]);