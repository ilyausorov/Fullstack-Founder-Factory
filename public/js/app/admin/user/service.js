var user = angular.module('user', []);

function userService($http, $q) {

    this.list = function() {
        var deferred = $q.defer();
        var url = jsRoutes.controllers.UserExtendedCtrl.listAdmin();

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
        var url = jsRoutes.controllers.UserCtrl.detailAdmin(id);

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

    this.delete = function(id, user_id) {

        var deferred = $q.defer();
        var url = jsRoutes.controllers.UserCtrl.deleteAdmin(id, user_id)

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

    this.update = function(v_in, user_id) {

        var v = v_in;

        //setting varibale to be converted to AngularJS date/time
        var d2c = v.date_added

        if (!d2c) {
            d2c = new Date();
        }

        console.log(d2c)
        //all the processing  
        var d = new Date(d2c);
        console.log(d)
        var curr_date = d.getDate();
        var curr_month = d.getMonth();
        curr_month++;
        var curr_year = d.getFullYear();
        var curr_hour = d.getHours();
        var curr_minute = d.getMinutes();
        var curr_second = d.getSeconds();
        var z = curr_year + "-" + curr_month + "-" + curr_date
        //assign it back	
        v.date_added = z;
        console.log(v.date_added)

        //all the processing  
        var d = new Date();
        var curr_date = d.getDate();
        var curr_month = d.getMonth();
        curr_month++;
        var curr_year = d.getFullYear();
        var curr_hour = d.getHours();
        var curr_minute = d.getMinutes();
        var curr_second = d.getSeconds();
        var z = curr_year + "-" + curr_month + "-" + curr_date
        //assign it back	
        v.date_edited = z;
        console.log(v.date_edited)



        var deferred = $q.defer();

        var url = jsRoutes.controllers.UserCtrl.insertAdmin();

        if (user_id) {
            url = jsRoutes.user_management.user.controllers.UserManagementCtrl.updateAdmin(user_id);
        } else {
            v.bcryptedPassword = "-";
            v.md5Password = "-";
            v.key = "-";
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

    this.updateExtended = function(v_in, user_id) {

        var v = v_in;
        delete(v.date_added);
        delete(v.date_edited);


        var deferred = $q.defer();
        var url = routes.javascript.UserExtendedCtrl.update(user_id);

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

    this.resetPassword = function(resetRequest, forWebservice) {

        var deferred = $q.defer();
        var url = jsRoutes.user_management.user.controllers.UserManagementCtrl.resetPassword();

        if (forWebservice) {
            url = jsRoutes.user_management.user.controllers.UserManagementCtrl.resetWebServicePassword();
        }

        $http({
                method: url.method,
                url: url.url,
                data: resetRequest
            })
            .success(function(jdata) {
                deferred.resolve(jdata);
            })
            .error(function(jdata) {
                deferred.reject(jdata);
            });

        return deferred.promise;
    };

    this.detail = function(id, user_id) {

        var deferred = $q.defer();
        var url = jsRoutes.controllers.UserCtrl.detailAdmin(id, user_id);

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
        var url = jsRoutes.controllers.Application.lang()

        $http({
                method: url.method,
                url: url.url
            })
            .success(function(jdata) {
                deferred.resolve(jdata.map(function(a) {
                    a.id = a.name;
                    a.name = a.name;
                    return a;
                }));
            })
            .error(function(jdata) {
                deferred.reject(jdata);
            });

        return deferred.promise;
    };

    this.status = function() {

        var deferred = $q.defer();
        var url = jsRoutes.controllers.Application.status();

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

    this.permissions = function(id) {

        var deferred = $q.defer();
        var url = jsRoutes.user_management.user.controllers.PermissionCtrl.listWithAssign(id);

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

    this.permissionRevoke = function(jreq) {

        var deferred = $q.defer();
        var url = jsRoutes.user_management.user.controllers.PermissionCtrl.revoke();

        $http({
                method: url.method,
                url: url.url,
                data: jreq
            })
            .success(function(jdata) {
                deferred.resolve(jdata);
            })
            .error(function(jdata) {
                deferred.reject(jdata);
            });

        return deferred.promise;
    };

    this.permissionAssign = function(jreq) {

        var deferred = $q.defer();
        var url = jsRoutes.user_management.user.controllers.PermissionCtrl.assign();

        $http({
                method: url.method,
                url: url.url,
                data: jreq
            })
            .success(function(jdata) {
                deferred.resolve(jdata);
            })
            .error(function(jdata) {
                deferred.reject(jdata);
            });

        return deferred.promise;
    };

    this.changeUsername = function(jreq) {

        var deferred = $q.defer();
        var url = jsRoutes.user_management.user.controllers.UserManagementCtrl.changeUserName();

        $http({
                method: url.method,
                url: url.url,
                data: jreq
            })
            .success(function(jdata) {
                deferred.resolve(jdata);
            })
            .error(function(jdata) {
                deferred.reject(jdata);
            });

        return deferred.promise;
    };

    this.changePassword = function(id, jreq) {

        var deferred = $q.defer();
        var url = jsRoutes.user_management.user.controllers.UserManagementCtrl.changePasswordAdmin(id);

        $http({
                method: url.method,
                url: url.url,
                data: jreq
            })
            .success(function(jdata) {
                deferred.resolve(jdata);
            })
            .error(function(jdata) {
                deferred.reject(jdata);
            });

        return deferred.promise;
    };

    this.generatePassword = function(id) {

        var deferred = $q.defer();
        var url = jsRoutes.controllers.UserCtrl.generatePasswordAdmin(id);

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

user.service('userService', ['$http', '$q', userService]);