var company = angular.module('company', []);

function companyService($http, $q) {

    this.list = function() {
        var deferred = $q.defer();
        var url = jsRoutes.controllers.CompanyCtrl.listAdmin()
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

    this.detail = function(id) {
        var deferred = $q.defer();
        var url = jsRoutes.controllers.CompanyCtrl.detailAdmin(id)
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

        var d = new Date();
        var c = d.getMonth() + 1
        v.date_added = d.getFullYear() + "-" + c + "-" + d.getDate();

        var url = jsRoutes.controllers.CompanyCtrl.insertAdmin();
        if (v.id) {
            var url = jsRoutes.controllers.CompanyCtrl.updateAdmin(id);
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

        var url = jsRoutes.controllers.CompanyCtrl.deleteAdmin(id);

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

company.service('companyService', ['$http', '$q', companyService]);