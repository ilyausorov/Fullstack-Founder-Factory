app.controller('userExtendedCtrl', ['$scope', '$http', '$filter', 'userExtendedService', function($scope, $http, $filter, userExtendedService) {

    $scope.values = [];
    $scope.valuesF = [];

    /* pagination */
    $scope.itemsPerPage = 50;
    $scope.currentPage = 1;

    /* sortBy */
    $scope.predicate = "name";
    $scope.reverse = false;

    $scope.showSearchP = false;

    /* lists */

    load();

    function load() {

        userExtendedService.list().then(function(jdata) {
            $scope.values = jdata;
            $scope.updateDisplayedData();
        });

        /** load lists */
    }

    $scope.delete = function(id, idx) {
        if (confirm("wanna_delete")) {
            var obs = $scope.values.filter(function(v) {
                if (v.id == id) {
                    return v;
                }
            });

            if (obs && obs[0] && obs[0].id) {
                userExtendedService.delete(obs[0].id).then(function(jdata) {
                    $scope.valuesF.splice(idx, 1);
                });
            }
        }
    };

    $scope.updateDisplayedData = function() {


        var v = $filter('filter')($scope.values, $scope.search);
        v = $filter('orderBy')(v, $scope.predicate, $scope.reverse);

        var begin = (($scope.currentPage - 1) * $scope.itemsPerPage);
        var end = begin + $scope.itemsPerPage;
        $scope.valuesF = v.slice(begin, end);

    };

    $scope.pageChanged = function() {
        $scope.updateDisplayedData();
    };

    $scope.showSearch = function() {
        $scope.showSearchP = !$scope.showSearchP;
    };

    $scope.sortBy = function(predicate) {
        if (predicate == $scope.predicate) {
            $scope.reverse = !$scope.reverse;
        }
        $scope.predicate = predicate;

        $scope.updateDisplayedData();
    };

}]);