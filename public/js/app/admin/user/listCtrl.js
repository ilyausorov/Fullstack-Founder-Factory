/**
 * 23.11 latest version
 * - array fixed
 * - todo in service?
 */
app.controller('UserCtrl', ['$scope', '$http', '$filter', 'userService', function($scope, $http, $filter, userService) {

    var values = [];
    $scope.valuesF = [];
    $scope.valuesS = [];

    $scope.permissions = [{
        id: 1,
        name: "Admin"
    }, {
        id: 5,
        name: "Employer"
    }, {
        id: 6,
        name: "Candidate"
    }, {
        id: 7,
        name: "Reference Provider"
    }];

    /* pagination */
    $scope.itemsPerPage = 50;
    $scope.currentPage = 1;

    /* sortBy */
    $scope.predicate = "firstName";
    $scope.reverse = false;

    $scope.showSearchP = false;

    load();

    function load() {
        userService.list().then(function(jdata) {
            values = jdata;

            $scope.updateDisplayedData();
        })
    }

    $scope.delete = function(user) {
        if (confirm("wanna_delete")) {
            var idx = $scope.valuesF.indexOf(user);

            userService.delete(user.id).then(function(jdata) {
                $scope.valuesF.splice(idx, 1);
            })
        }
    }

    $scope.updateDisplayedData = function() {

        var v = $filter('filter')(values, $scope.search)
        $scope.valuesS = $filter('orderBy')(v, $scope.predicate, $scope.reverse)

        var begin = (($scope.currentPage - 1) * $scope.itemsPerPage);
        var end = begin + $scope.itemsPerPage;
        $scope.valuesF = $scope.valuesS.slice(begin, end);

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
    }
}])