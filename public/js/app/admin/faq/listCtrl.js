/**
 * 23.11 latest version
 * - array fixed
 * - todo in service?
 */
app.controller('faqCtrl', ['$scope', '$http', '$filter', 'faqService', function($scope, $http, $filter, faqService) {

    var values = [];
    $scope.langs = [];
    $scope.valuesF = [];
    $scope.valuesS = [];

    /* pagination */
    $scope.itemsPerPage = 50;
    $scope.currentPage = 1;

    /* sortBy */
    $scope.predicate = "question";
    $scope.reverse = false;

    $scope.showSearchP = false;

    load();

    function load() {
        faqService.list().then(function(jdata) {
            values = jdata;
            $scope.updateDisplayedData();
        })

        faqService.langs().then(function(jdata) {
            $scope.langs = jdata;
        })
    }

    $scope.delete = function(user) {
        if (confirm("wanna_delete")) {
            var idx = values.indexOf(user);
            var id = values[idx].id;

            faqService.delete(id).then(function(jdata) {
                $scope.valuesF.splice(idx, 1)
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