app.controller('companyEditCtrl', ['$scope', '$http', '$filter', 'companyService', function($scope, $http, $filter, companyService) {

    /* init */
    var id = null;
    $scope.value = {};
    $scope.error = {};
    $scope.success = false;

    $scope.init = function(id_in) {
        id = id_in;

        companyService.detail(id).then(function(jdata) {
            $scope.value = jdata;
        });
    }

    $scope.update = function() {
        var v = $scope.value;

        companyService.update(v, id).then(function(jdata) {
            $scope.value.id = jdata.id;
            $scope.error = {};
            $scope.success = true;
            if (id == null) {
                window.location = jsRoutes.controllers.CompanyCtrl.index().url;
            }
        }, function(jerror) {
            $scope.error = jerror;
        });
    }
}]);

app.controller('userCompanyCtrl', ['$scope', '$http', '$filter', 'userCompanyService', 'userService', function($scope, $http, $filter, userCompanyService, userService) {
    /* init */
    $scope.values = [];
    var id = null;

    $scope.init = function(id_in) {
        id = id_in;

        userCompanyService.list(id).then(function(jdata) {
            jdata.map(function(x, y) {
                userService.detail(x.user_id).then(function(jdata2) {
                    x.user = jdata2
                })
            })
            $scope.values = jdata
        })
    }
}]);