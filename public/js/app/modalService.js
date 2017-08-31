var modal = angular.module('modal', []);

// NB: For Angular-bootstrap 0.14.0 or later, use $uibModal above instead of $modal
function modalService($uibModal) {

    var modalDefaults = {
        backdrop: true,
        keyboard: true,
        modalFade: true,
        templateUrl: jsRoutes.controllers.PublicCtrl.modalTemplate().url
    };

    var modalOptions = {
        closeButtonText: 'Close',
        actionButtonText: 'OK',
        headerText: 'Proceed?',
        bodyText: 'Perform this action?'
    };

    this.showModal = function(customModalDefaults, customModalOptions) {
        if (!customModalDefaults) customModalDefaults = {};
        customModalDefaults.backdrop = 'static';
        return this.show(customModalDefaults, customModalOptions);
    };

    this.show = function(customModalDefaults, customModalOptions) {
        //Create temp objects to work with since we're in a singleton service
        var tempModalDefaults = {};
        var tempModalOptions = {};

        //Map angular-ui modal custom defaults to modal defaults defined in service
        angular.extend(tempModalDefaults, modalDefaults, customModalDefaults);

        //Map modal.html $scope custom properties to defaults defined in service
        angular.extend(tempModalOptions, modalOptions, customModalOptions);

        if (!tempModalDefaults.controller) {
            tempModalDefaults.controller = function($scope, $uibModalInstance) {
                $scope.modalOptions = tempModalOptions;
                $scope.modalOptions.ok = function(result) {
                    $uibModalInstance.close(result);
                };
                $scope.modalOptions.close = function(result) {
                    $uibModalInstance.dismiss('cancel');
                };
            };
        }

        return $uibModal.open(tempModalDefaults).result;
    };


    var alertDefaults = {
        backdrop: true,
        keyboard: true,
        modalFade: true,
        templateUrl: jsRoutes.controllers.PublicCtrl.alertTemplate().url
    };

    var alertOptions = {
        actionButtonText: 'OK',
        headerText: 'Proceed?',
        bodyText: 'Perform this action?'
    };

    this.showAlert = function(customAlertDefaults, customAlertOptions) {
        if (!customAlertDefaults) customAlertDefaults = {};
        customAlertDefaults.backdrop = 'static';

        return this.showA(customAlertDefaults, customAlertOptions);
    };

    this.showA = function(customAlertDefaults, customAlertOptions) {
        //Create temp objects to work with since we're in a singleton service
        var tempAlertDefaults = {};
        var tempAlertOptions = {};

        //Map angular-ui Alert custom defaults to Alert defaults defined in service
        angular.extend(tempAlertDefaults, alertDefaults, customAlertDefaults);

        //Map Alert.html $scope custom properties to defaults defined in service
        angular.extend(tempAlertOptions, alertOptions, customAlertOptions);


        if (!tempAlertDefaults.controller) {

            tempAlertDefaults.controller = function($scope, $uibModalInstance) {
                $scope.AlertOptions = tempAlertOptions;
                $scope.AlertOptions.ok = function(result) {
                    $uibModalInstance.close(result);
                };
                $scope.AlertOptions.close = function(result) {
                    $uibModalInstance.dismiss('cancel');
                };
            };
        }

        return $uibModal.open(tempAlertDefaults).result;
    };


    //	----------------------------


    var PromptDefaults = {
        backdrop: true,
        keyboard: true,
        modalFade: true,
        templateUrl: jsRoutes.controllers.PublicCtrl.promptTemplate().url
    };

    var PromptOptions = {
        actionButtonText: 'OK',
        headerText: 'Proceed?',
        bodyText: 'Perform this action?'
    };

    this.showPrompt = function(customPromptDefaults, customPromptOptions) {
        if (!customPromptDefaults) customPromptDefaults = {};
        customPromptDefaults.backdrop = 'static';

        return this.showB(customPromptDefaults, customPromptOptions);
    };

    this.showB = function(customPromptDefaults, customPromptOptions) {
        //Create temp objects to work with since we're in a singleton service
        var tempPromptDefaults = {};
        var tempPromptOptions = {};

        //Map angular-ui Prompt custom defaults to Prompt defaults defined in service
        angular.extend(tempPromptDefaults, PromptDefaults, customPromptDefaults);

        //Map Prompt.html $scope custom properties to defaults defined in service
        angular.extend(tempPromptOptions, PromptOptions, customPromptOptions);


        if (!tempPromptDefaults.controller) {

            tempPromptDefaults.controller = function($scope, $uibModalInstance) {
                $scope.PromptOptions = tempPromptOptions;
                $scope.PromptOptions.ok = function(result) {
                    $uibModalInstance.close($scope.PromptOptions.inputText);
                };
                $scope.PromptOptions.close = function(result) {
                    $uibModalInstance.dismiss('cancel');
                };
            };
        }

        return $uibModal.open(tempPromptDefaults).result;
    };
}

modal.service('modalService', ['$uibModal', modalService]);