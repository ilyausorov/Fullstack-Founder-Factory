app.controller('userExtendedEditCtrl', ['$scope', '$http', '$filter', 'userExtendedService', function($scope, $http, $filter, userExtendedService) {

    $scope.value = {};
    $scope.error = {};
    $scope.success = false;

    var id = null;

    /* lists */

    $scope.init = function(id_in) {
        id = id_in;
        load();
    };

    function load() {
        userExtendedService.detail(id).then(function(jdata) {
            $scope.value = jdata;
        });
    }

    $scope.delete = function(id) {
        if (confirm("wanna_delete")) {
            userExtendedService.delete(id).then(function(jdata) {
                // redirect
            });
        }
    };

    $scope.update = function() {
        var v = $scope.value;
        v.user_id = id;
        userExtendedService.update(v, id).then(function(jdata) {
                $scope.success = true;
                $scope.error = {};
                $scope.value.id = jdata.id;
            },
            function(jerror) {
                $scope.error = jerror;
            });
    };
}]);