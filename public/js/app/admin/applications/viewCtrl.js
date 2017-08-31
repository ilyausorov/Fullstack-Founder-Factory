app.controller('viewApplicationCtrl', ['$scope', '$http', '$filter', 'applicationsService', 'questionbankService', function ($scope, $http, $filter, applicationsService, questionbankService) {

  
  $scope.values       = [];
  $scope.valuesF      = [];
  $scope.applicationAnswers      = {};
  $scope.applicationQuestions    = {};
  
  /* pagination */
  $scope.itemsPerPage = 50;
  $scope.currentPage  = 1;
  $scope.application = {};
  
  /* sortBy */
  $scope.predicate    = "name";
  $scope.reverse      = false;

  $scope.showSearchP  = false;
  $scope.selectAllInit= false;	

  var values = [];
  var application_id    = null;

  /* lists */

  $scope.init = function(id){
    application_id = id		
    load(application_id);	
  }
  
function load(application_id){
	
	applicationsService.detailApplication(application_id).then(function(jdata){
		
		$scope.application = jdata;
		
		if($scope.application.read_id == 0){
			applicationsService.updateRead(application_id)
		}
		
		questionbankService.question_list("application").then(function(jdata) {
			$scope.applicationQuestions = jdata;	
		})
		
		applicationsService.listAnswers(application_id).then(function(jdata){
			$scope.applicationAnswers = jdata;
		})
	})
};
  
  $scope.updateDisplayedData = function() {
    var v = $filter('filter')($scope.values, $scope.search);
    v = $filter('orderBy')(v, $scope.predicate, $scope.reverse);

    var begin      = (($scope.currentPage - 1) * $scope.itemsPerPage);
    var end        = begin + $scope.itemsPerPage;
    $scope.valuesF = v.slice(begin, end);
  };

  $scope.pageChanged = function() {
    $scope.updateDisplayedData();
  };

  $scope.showSearch = function(){
    $scope.showSearchP = !$scope.showSearchP;
  };

  $scope.sortBy = function(predicate){
    if(predicate == $scope.predicate){
      $scope.reverse=!$scope.reverse;
    }
    $scope.predicate=predicate;
    
    $scope.updateDisplayedData();
  };

}]);