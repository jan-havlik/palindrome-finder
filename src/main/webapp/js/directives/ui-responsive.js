define("directives/ui-responsive", [
	"app",
	"jquery"
], function(app, $) {

	app.factory("uiValuesService", [function() {
		return {
			uiValues: {},
			getValues: function() {
				return this.uiValues;
			},
			setValue: function(key, val) {
				this.uiValues[key] = val;
			}
		};
	}]);

	app.directive("uiResponsive", ['$rootScope', 'uiValuesService', function($rootScope, uiValuesService) {
		return {
			controller: function() {
				var self = this;
				$rootScope.ui = uiValuesService.getValues();

				this.setValue = function(key, val) {
					$rootScope.ui[key] = val;
				};
				this.getValue = function(key) {
					if($rootScope.ui[key]) {
						return $rootScope.ui[key];
					}
					return false;
				};
				this.toggleValue = function(key) {
					self.setValue(key, !self.getValue(key));
				};
			},
			link: function(scope, element, attrs, ctrl) {
				console.log("uiResponsive");
			}
		};

	}]);

	app.directive("uiToggle", function() {
		return {
			require: '^uiResponsive',
			link: function(scope, element, attrs, uiResponsive) {
				$(element[0]).click(function() {
					scope.$apply(function() {
						var key = attrs.uiToggle;
						uiResponsive.toggleValue(key);
					});
				});
			}
		};
	});

});