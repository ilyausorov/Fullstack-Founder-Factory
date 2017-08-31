app.controller('userPasswordCtrl', ['$scope', '$http', '$filter', 'userService', function($scope, $http, $filter, userService) {

    $scope.error = {};
    $scope.success = false;
    $scope.successR = false;
    var id = null;

    $scope.init = function(id_in) {
        id = id_in;
    }

    $scope.update = function() {

        var jreq = {
            "new1": $scope.value.new1,
            "new2": $scope.value.new2
        }

        userService.changePassword(id, jreq).then(function(jdata) {
            $scope.error = {};
            $scope.value = {};
            $scope.success = true;

        }, function(jerror) {
            $scope.error = jerror;
        })
    }

    $scope.regenerate = function() {
        userService.generatePassword(id).then(function(jdata) {
            $scope.error = {};
            $scope.value = {};
            $scope.successR = true;

        })
    }
}]);