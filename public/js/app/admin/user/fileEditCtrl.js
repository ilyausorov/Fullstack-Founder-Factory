app.controller('fileEditCtrl', ['$scope', '$http', '$filter', function($scope, $http, $filter) {

    /* init */
    var id = null;


    var dateFormat = null;

    $scope.init = function(i, df) {

        dateFormat = df;
        id = i;

        load();
    }

    function load() {

        var url = jsRoutes.controllers.UserFileCtrl.listing(id);

        $http({
                method: url.method,
                url: url.url
            })
            .success(function(jdata) {
                $scope.files = jdata;
            })
            .error(function(jdata) {})
    }

    $scope.delete = function(idx) {

        var fid = $scope.files[idx].id;
        var url = jsRoutes.controllers.UserFileCtrl.delete(fid, id);

        if (confirm("wanna_delete")) {
            $http({
                    method: url.method,
                    url: url.url
                })
                .success(function(jdata) {
                    $scope.files.splice(idx, 1)
                })
        }
    }
}])