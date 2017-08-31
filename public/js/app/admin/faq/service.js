var faq = angular.module('faq', []);

function faqService($http, $q) {

    this.list = function() {
        var deferred = $q.defer();
        var url = jsRoutes.controllers.FaqCtrl.listing()
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
        var url = jsRoutes.controllers.FaqCtrl.detail(id)
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

        var url = jsRoutes.controllers.FaqCtrl.insert();
        if (v.id) {
            var url = jsRoutes.controllers.FaqCtrl.update(id);
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

        var url = jsRoutes.controllers.FaqCtrl.delete(id);

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

    this.langs = function() {
        var deferred = $q.defer();
        var url = jsRoutes.controllers.Application.lang();

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
    }
}

faq.service('faqService', ['$http', '$q', faqService]);