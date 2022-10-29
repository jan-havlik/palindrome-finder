define("directives/modal", [
	"app"
], function(app) {

	/**
	 * vytvori konstruktor kontroleru pro komponentu vyberu v modalnim okne
	 *
	 * @param string modalTemplate cesta k sablone obsahu modal okna
	 * @param function setup moznost nastavit vlastni parametry na $scope
	 * @returns {Function}
	 */
	var makeGeneralModalController = function (modalTemplate, setup) {
		return ["$scope", "$uibModal", function ($scope, $uibModal) {
			var modalInstance = null;
			$scope.cancel = function () {
				if (modalInstance) {
					modalInstance.close();
				}
			};
			this.openModal = function () {
				modalInstance = $uibModal.open({
					animation: true,
					templateUrl: modalTemplate,
					size: "lg",
					scope: $scope
				});
				setup($scope, modalInstance);
				return modalInstance;
			};
		}];
	};

	var makeGeneralModalLink = function(watchVarName) {
		return function(scope, element, attr, controller) {
			var visible = false;
			scope.$watch(watchVarName, function(newValue) {
				if(newValue && !visible) {
					var modalInstance = controller.openModal();
					modalInstance.result.then(function (selectedItem) {
						scope[watchVarName] = false;
						visible = false;
					}, function () {
						scope[watchVarName] = false;
						visible = false;
					});
				}
			});
		};
	};

	app.directive("modal", [function() {
		return {
			scope: {
				message: "@",
				title: "@",
				modal: "=",
				onConfirm: "&"
			},
			restrict: "A",
			controller: makeGeneralModalController('partials/modal.html', function($scope, modalInstance) {
				$scope.confirm = function() {
					$scope.onConfirm();
					if (modalInstance) {
						modalInstance.close();
					}
				};
			}),
			link: makeGeneralModalLink("modal")
		};
	}]);

	app.directive("modalConfirm", [function() {
		return {
			scope: {
				message: "@",
				title: "@",
				modalConfirm: "=",
				onConfirm: "&",
				onCancel: "&"
			},
			restrict: "A",
			controller: makeGeneralModalController('partials/modal-confirm.html', function($scope, modalInstance) {
				$scope.confirm = function() {
					$scope.onConfirm();
					if (modalInstance) {
						modalInstance.close();
					}
				};
				$scope.cancel = function() {
					$scope.onCancel();
					if (modalInstance) {
						modalInstance.close();
					}
				};
			}),
			link: makeGeneralModalLink("modalConfirm")
		};
	}]);

});