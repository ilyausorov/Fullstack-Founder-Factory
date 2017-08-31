app.controller('signupCtrl', ['$scope', '$http', '$filter', function($scope, $http, $filter) {

    $scope.value = {
        login: {}
    };
    $scope.success = false;
    $scope.error = false;
    var url = {signup: "", success: ""};

    $scope.init = function(signupUrl, successUrl) {
        url = {
            signup: signupUrl,
            success: successUrl
        };
    }

    $scope.update = function() {
        $scope.value.passwords.new2 = $scope.value.passwords.new1
        $scope.value.login.phone = "000"

        $http({
                method: "POST",
                url: url.signup,
                data: $scope.value
            })
            .success(function(jdata) {
                $scope.success = true;
                window.location = url.success;
            })
            .error(function(jdata) {
                $scope.error = jdata;
            })
    }
	
}])