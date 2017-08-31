app.controller('companyCtrl', ['$scope', '$http', '$filter', 'companyService', function($scope, $http, $filter, companyService) {

    var values = [];
    $scope.valuesS = [];
    $scope.valuesF = [];

    /* pagination */
    $scope.itemsPerPage = 50;
    $scope.currentPage = 1;

    /* sortBy */
    $scope.predicate = "name";
    $scope.reverse = false;
    $scope.showSearchP = false;

    load();

    function load() {
        companyService.list().then(function(jdata) {
            values = jdata;
            $scope.updateDisplayedData();
        })
    }

    $scope.delete = function(user) {
        if (confirm("wanna_delete")) {
            var idx = $scope.valuesF.indexOf(user);
            var id = user.id;

            companyService.delete(id).then(function(jdata) {
                $scope.valuesF.splice(idx, 1);
            })
        }
    }

    $scope.updateDisplayedData = function() {

        $scope.valuesS = $filter('orderBy')(
            $filter('filter')(values, $scope.search),
            $scope.predicate,
            $scope.reverse
        )

        var begin = (($scope.currentPage - 1) * $scope.itemsPerPage);
        var end = begin + $scope.itemsPerPage;
        $scope.valuesF = $scope.valuesS.slice(begin, end);

    };

    $scope.pageChanged = function() {
        $scope.updateDisplayedData();
    };

    $scope.showSearch = function() {
        $scope.showSearchP = !$scope.showSearchP;
    }

    $scope.sortBy = function(predicate) {
        if (predicate == $scope.predicate) {
            $scope.reverse = !$scope.reverse;
        }
        $scope.predicate = predicate;
        $scope.updateDisplayedData();
    }

}])