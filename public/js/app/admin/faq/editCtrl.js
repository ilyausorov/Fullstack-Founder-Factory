app.controller('faqEditCtrl', ['$scope', '$http', '$filter', 'faqService', function($scope, $http, $filter, faqService) {

    /* init */
    var id = null;
    $scope.value = {
        category_id: 1
    };
    $scope.error = {};
    $scope.success = false;
    $scope.langs = [];
    load();

    $scope.init = function(id_in) {
        id = id_in;

        faqService.detail(id).then(function(jdata) {
            $scope.value = jdata;
        });
    }

    function load() {
        faqService.langs().then(function(jdata) {
            $scope.langs = jdata;
        })
    }

    $scope.update = function() {
        var v = $scope.value;

        faqService.update(v, id).then(function(jdata) {
            $scope.value.id = jdata.id;
            $scope.error = {};
            $scope.success = true;
            if (id == null) {
                window.location = jsRoutes.controllers.FaqCtrl.index().url;
            }
        }, function(jerror) {
            $scope.error = jerror;
        });
    }
}]);