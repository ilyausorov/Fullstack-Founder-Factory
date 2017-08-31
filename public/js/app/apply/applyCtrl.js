app.controller('applyCtrl', ['$scope', '$http', '$filter', 'modalService', 'questionbankService','$timeout', function($scope, $http, $filter, modalService, questionbankService, $timeout) {

	$scope.value = {
		intQs: {}
	};

	$scope.error = {};
	$scope.success = false;

	$scope.step = 1;
	$scope.maxstep = 0;

	$scope.sort = 1;
	
	$scope.init = function() {
		load();
	}

	function load() {

		$('#loadingModal').modal({backdrop:'static',keyboard: false});
		
		questionbankService.application_headers().then(function(jdata) {
			$scope.application_headers = jdata.application_headers;
			var x = $.map($scope.application_headers, function(value, index) {
				if (value.collection_id == "application") {
					$scope.maxstep += 1;
				}
			})
		})
											
		questionbankService.question_list("application").then(function(jdata) {
			$scope.questions = jdata;
		})
		
		$('#loadingModal').modal('hide');
	}	
	
	$scope.changeStep = function(i) {
		document.getElementById("top").scrollIntoView()
		$scope.step = i;
	}
	
	$scope.update = function(abc) {
		$('#loadingModal').modal({backdrop:'static',keyboard: false});
		var v = angular.copy($scope.value);
		
		var ints = $.map(v.intQs, function(value, index) {
			
			if(index == 1){
				v.first_name = value;
				$scope.value.first_name = value;
			}
			if(index == 2){
				v.last_name = value;
				$scope.value.last_name = value;
			}
			if(index==3){
				v.email = value;
				$scope.value.email = value;
			}
			
			if (value) {
				if (typeof(value) == "string") {
					return [{
						qid: index,
						val: 0,
						olabel: value
					}];
				}
				else {
					return [{
						qid: index,
						val: value.val,
						olabel: value.olabel
					}];
				}
			}
		});
		
		v.questions = ints;
		delete(v.intQs)
		
		
		
		if(!v.first_name || !v.last_name || !v.email){
			var alertOptions = {
				headerText: 'Looks like something is missing!',
				bodyText: 'Woah there...! I have to know who you are before you get further into the application.'
			};
			modalService.showAlert({}, alertOptions)
		}else{
			if ($scope.step == $scope.maxstep) {
				if (abc == 1) {
					$('#loadingModal').modal('hide');
					var modalOptions = {
						closeButtonText: 'Cancel',
						actionButtonText: 'Submit',
						headerText: 'Submit application',
						bodyText: 'Are you sure that you want to submit the application? If you skipped any questions, this is the last chance to go back and respond. You cannot edit your responses after your submit them.'
					};
					modalService.showModal({}, modalOptions).then(function(result) {
						$('#loadingModal').modal({backdrop:'static',keyboard: false});
						// this is what happens if they confirm they're done
						v.status_id = 2;
						var url = jsRoutes.controllers.ApplyCtrl.update();
						$http({
							method: url.method,
							url: url.url,
							data: v
						}).success(function(jdata) {
							var url5 = jsRoutes.controllers.ApplyCtrl.thankyouemail($scope.value.email, $scope.value.first_name);
							$http({
								method: url5.method,
								url: url5.url
							}).success(function(jdata) {
								$('#loadingModal').modal('hide');
								var alertOptions = {
									headerText: 'Thanks for applying!',
									bodyText: "Thanks for submitting your application to the Fullstack Founder Factory program. Your application has been received. Check your email for a confirmation email. You're going to be redirected to the landing page now."
								};

								modalService.showAlert({}, alertOptions).then(function(result){
									window.location = jsRoutes.controllers.PublicCtrl.index().url;		
								}, function(failure){
									window.location = jsRoutes.controllers.PublicCtrl.index().url;
								})
							}).error(function(jerror){
								var alertOptions = {
									headerText: 'Uh oh!',
									bodyText: "Woops... Looks like something went wrong. Try pressing the button again."
								};
								modalService.showAlert({}, alertOptions)
							})
						}).error(function(jerror){
							var alertOptions = {
								headerText: 'Uh oh!',
								bodyText: "Woops... Looks like something went wrong. Try pressing the button again."
							};
							modalService.showAlert({}, alertOptions)
						})
					})
				} else {
					//this is what happens if it's step 4 but going back
					var url = jsRoutes.controllers.ApplyCtrl.update();
					$http({
						method: url.method,
						url: url.url,
						data: v
					}).success(function(jdata) {

						$scope.value.application_id = jdata.id;

						if (abc == 1) {
							$scope.step += 1;
							document.getElementById("top").scrollIntoView();

							$('#loadingModal').modal('hide');
						}
						if (abc == 0) {
							$scope.step -= 1;
							document.getElementById("top").scrollIntoView();

							$('#loadingModal').modal('hide');
						}
					}).error(function(jerror){
						var alertOptions = {
							headerText: 'Uh oh!',
							bodyText: "Woops... Looks like something went wrong. Try pressing the button again."
						};
						modalService.showAlert({}, alertOptions)
					})
				}
		} else {
			//this is what happens if it's not step 4
			var url = jsRoutes.controllers.ApplyCtrl.update();
			$http({
				method: url.method,
				url: url.url,
				data: v
			}).success(function(jdata) {
				$scope.value.application_id = jdata.id;
				if (abc == 1) {
					$scope.step += 1;
					document.getElementById("top").scrollIntoView();
					$('#loadingModal').modal('hide');
				}
				if (abc == 0) {
					$scope.step -= 1;
					document.getElementById("top").scrollIntoView();
					$('#loadingModal').modal('hide');
				}
			}).error(function(jerror){
			
				if(jerror.DETAIL == "internal server error"){
					var modalOptions = {
						closeButtonText: 'Cancel',
						actionButtonText: 'Yes, I do',
						headerText: 'Hold on a sec!',
						bodyText: "Looks like you (or someone else) has already used this email address in an application before. Do you want to delete the old application and start a new application under this email address? If not, then please use a different email address."
					};

					modalService.showModal({}, modalOptions)
						.then(function (result) {
							var url = jsRoutes.controllers.ApplyCtrl.find_application_id(v.email);
							$http({
								method: url.method,
								url: url.url
							}).success(function(jdata){
								$scope.value.application_id = jdata.id;
								$scope.update(1);
							})		
					})
				}else{
					var alertOptions = {
						headerText: 'Hold on a sec!',
						bodyText: "That email address doesn't quite look like an email address. Could you check that again?"
					};

					modalService.showAlert({}, alertOptions)
				}
				
			});
		}
	}
		
		$('#loadingModal').modal('hide');
	}
	
	
$scope.warningback = function(){
		var modalOptions = {
        closeButtonText: 'Cancel',
        actionButtonText: 'Yes, I do',
        headerText: 'Warning',
        bodyText: "If you go back or refresh this page, any progress you made will be lost. Are you sure you want to go back?"
    };

    modalService.showModal({}, modalOptions)
        .then(function (result) {
			window.location=jsRoutes.controllers.PublicCtrl.index().url;
		})
}	


}]);