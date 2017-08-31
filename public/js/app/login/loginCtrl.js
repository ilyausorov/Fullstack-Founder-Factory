app.controller('loginCtrl', ['$scope', '$http', '$filter', '$timeout', function($scope, $http, $filter, $timeout) {

    $scope.value = {};
    $scope.success = false;
    $scope.error = null;
    $scope.emailError = null;
    var id = null
    var key = null
    var url = {update: jsRoutes.controllers.LoginCtrl.login().url, success: '', forgotPasswordAsk: ''}

    $scope.init = function(successUrl) {
        url.success = successUrl;
    }

    $scope.initEmail = function(idin, keyin) {
        id = idin;
        key = keyin;
        loadEmail();
    }

    function loadEmail() {
        var url = jsRoutes.user_management.user.controllers.UserManagementCtrl.findRestrictedUserByKey(id, key);
        $http({
            method: url.method,
            url: url.url
        }).success(function(jdata) {
            $scope.value.email = jdata.email
        }).error(function(jdata) {
            $scope.emailError = jdata
            $timeout(function() {
                window.location = "/emaillogin"
            }, 2000)
        })
    }

    $scope.updatePassUpdate = function(id, keyy) {
        $scope.error = null;
        $scope.success = false;
        var url = jsRoutes.user_management.user.controllers.UserManagementCtrl.reinitPassword(id, keyy);

        $http({
            method: url.method,
            url: url.url,
            data: $scope.value.new
        }).success(function(jdata) {

            $scope.success = true;
            $timeout(function() {
                window.location = "/emaillogin"
            }, 1000)
        }).error(function(jdata) {
            $scope.error = jdata
        })
    }


    $scope.logincheck = function() {
        var url = jsRoutes.controllers.LoginCtrl.loginPostAction();
        $http({
                method: url.method,
                url: url.url,
            })
            .success(function(jdata) {
                $scope.success = true;
                $scope.error = null;
                if (jdata.url) {
                    window.location = jdata.url;
                } else {
                    $('#signInModal').modal();
                }
            })
            .error(function(jdata) {
                $('#signInModal').modal();
            })
    }

    $scope.passwordReset = function() {
        if (confirm('Press Ok to request a password reset email')) {
            alert('If we have this email address on record, you will recieve an email.')
            var url = jsRoutes.controllers.LoginCtrl.askPasswordReset();
            $http({
                method: url.method,
                url: url.url,
                data: $scope.value
            })
        }
    }

    $scope.update = function() {
        $http({
                method: "POST",
                url: url.update,
                data: $scope.value
            })
            .success(function(jdata) {
                $scope.success = true;
                $scope.error = null;
                if (jdata.url) {
                    window.location = jdata.url;
                } else {
                    window.location = url.success;
                }
            })
            .error(function(jdata) {
                $scope.error = jdata
                if (!$scope.error) {
                    $scope.error = {
                        'NIC': "No internet connection. Please reconnect to the internet and try again."
                    }
                }
                console.log($scope.error)
            })
    }
}]);