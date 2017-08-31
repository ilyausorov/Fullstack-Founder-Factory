app.controller('applicationsCtrl', ['$scope', '$http', '$filter', 'applicationsService', function($scope, $http, $filter, applicationsService) {

    var values = [];
    $scope.valuesS = [];
    $scope.valuesF = [];

	$scope.itemStatus = [{id: 1, name: "Not submitted"}, {id: 2, name: "Completed"}]
	
	$scope.readStatus = [{id: 0, name: "Not read"}, {id: 1, name: "Read"}]
	
    /* pagination */
    $scope.itemsPerPage = 50;
    $scope.currentPage = 1;

    /* sortBy */
    $scope.predicate = "name";
    $scope.reverse = false;
    $scope.showSearchP = false;

    load();

    function load() {
        applicationsService.list().then(function(jdata) {
            values = jdata;
            $scope.updateDisplayedData();
        })
    }

    $scope.delete = function(application) {
        if (confirm("Are you sure you want to delete this application?")) {
            var idx = $scope.valuesF.indexOf(application);
            var id = application.id;

            applicationsService.delete(id).then(function(jdata) {
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