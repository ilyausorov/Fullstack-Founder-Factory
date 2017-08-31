app.controller('userEditCtrl', ['$scope', '$http', '$filter', 'userService', 'companyService', function($scope, $http, $filter, userService, companyService) {

    $scope.value = {};
    $scope.langs = [];
    $scope.status = [];
    $scope.companies = [];
    $scope.permissions = [];
    var id = null;

    /** success */
    $scope.success = false;
    $scope.successUsn = false;


    $scope.init = function(id_in) {
        id = id_in;
        load();
    }

    loadOptions();

    function load() {
        userService.detail(id).then(function(jdata) {
            $scope.value = jdata;
        });

        userService.permissions(id).then(function(jdata) {
            $scope.permissions = jdata;
            $.map($scope.permissions, function(x, i) {
                if (x.assigned) {
                    if (x.id == 5) {
                        $scope.value.permission_id = 1

                    } else {
                        $scope.value.permission_id = 0

                    }
                }
            })
        });
    }

    $scope.loginasuser = function(emp) {
        console.log(emp)
        if (confirm("Are you sure you want to login as this user?")) {
            window.location = jsRoutes.controllers.LoginCtrl.logincanemail(id, $scope.value.key, $scope.value.username, emp).url;
        }
    }


    $scope.sendciemail = function() {
        if (confirm("Are you sure you want to send this user a client invite email? This will reset their password.")) {
            $http({
                method: "GET",
                url: jsRoutes.controllers.EmployerCtrl.clientinvite(id, $scope.value.key, $scope.value.username).url
            }).success(function(jdata) {

                $scope.successemail = true;

            })
        }
    }

    function loadOptions() {
        userService.langs().then(function(jdata) {
            $scope.langs = jdata;
        });

        userService.status().then(function(jdata) {
            $scope.status = jdata;
        });

        companyService.list().then(function(jdata) {
            $scope.companies = jdata;
        });
    }

    $scope.delete = function(id) {
        if (confirm("wanna_delete")) {
            userService.delete(id).then(function(jdata) {
                //
            })
        }
    }

    $scope.update = function() {
        $scope.value.lang = "en"

        userService.update($scope.value, id).then(function(jdata) {
            $scope.success = true;
            $scope.error = {};
        }, function(jerror) {
            $scope.success = false;
            $scope.error = jerror;
        })
    }

    $scope.update2 = function() {
        $scope.value.lang = "en"
        $scope.value.status_id = 1;
        userService.update($scope.value, id).then(function(jdata) {

            console.log(jdata)

            $scope.success = true;
            $scope.error = {};

            window.location = "/admin/user/" + jdata.id + "/edit"


        }, function(jerror) {
            $scope.success = false;
            $scope.error = jerror;
        })
    }

    $scope.changeUsername = function() {

        var jreq = {
            username: $scope.value.username
        };

        userService.changeUsername(jreq).then(function(jdata) {
                $scope.successUsn = true;
            },
            function(jerror) {
                $scope.error = jerror;
            })
    }

    $scope.changePermission = function($index) {
        var permission = $scope.permissions[$index];
        console.log(permission)
        var jreq = {
            user_id: id,
            permission_id: permission.id
        };
        console.log(jreq)
        if (permission.assigned) {
            userService.permissionRevoke(jreq).then(function(jdata) {
                $scope.permissions[$index].assigned = !$scope.permissions[$index].assigned;
            })
        } else {
            userService.permissionAssign(jreq).then(function(jdata) {
                $scope.permissions[$index].assigned = !$scope.permissions[$index].assigned;
            })
        }
    }

}])