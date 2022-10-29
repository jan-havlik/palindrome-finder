define("directives/form-validators", [
	"app",
	"jquery"
], function(app, $) {

	/**
	 * zabrani odeslani formular pri zmacknuti klavesy enter v text inputu
	 *
	 * hodi se pro formy, kde je vice buttonu, ktere nejsou disabled, aplikovat primo na <form>
	 */
	app.directive("preventEnter", [function() {
		return {
			link: function(scope, element, attr) {
				$(element).on("keypress", ":input:not(textarea)", function(event) {
					return event.keyCode != 13;
				});
			}
		};
	}]);

	/**
	 * validuje stejnou hodnotu proti jinemu modelu
	 */
	app.directive('sameAs', function () {
		return {
			scope: {
				sameAs: '='
			},
			require: 'ngModel',
			link: function (scope, elem, attr, ngModel) {

				ngModel.$validators.sameas = function(value) {
					if(value != "" && scope.sameAs != "") {
						return value == scope.sameAs;
					}
					return true;
				};

				scope.$watch("sameAs", function () {
					ngModel.$validate();
				});

			}
		};
	});

	/**
	 * validace data
	 */
	app.directive('date', function () {
		return {
			require: 'ngModel',
			link: function (scope, elem, attr, ngModel) {

				ngModel.$validators.date = function(value) {
					if(value) {
						var regExp = /^[0-9]{1,2}\.[0-9]{1,2}\.[0-9]{4}$/i;
						return regExp.test(value);
					}
					return true;
				};

			}
		};
	});

	/**
	 * validace data a casu v jednom inputu
	 */
	app.directive('dateTime', function () {
		return {
			require: 'ngModel',
			link: function (scope, elem, attr, ngModel) {

				ngModel.$validators.datetime = function(value) {
					if(value) {
						var regExpWithSec = /^[0-9]{1,2}\.[0-9]{1,2}\.[0-9]{4}\W+[0-9]{1,2}:[0-9]{2}:[0-9]{2}$/i;
						var regExpWithoutSec = /^[0-9]{1,2}\.[0-9]{1,2}\.[0-9]{4}\W+[0-9]{1,2}:[0-9]{2}(:[0-9]{2}){0,1}$/i;
						if(attr.seconds) {
							return regExpWithSec.test(value);
						} else {
							return regExpWithoutSec.test(value);
						}
					}
					return true;
				};

			}
		};
	});

	app.directive('palindromeSize', function() {
		return {
			scope: {
				mismatches: '=palindromeSize'
			},
			require: 'ngModel',
			link: function (scope, elem, attr, ngModel) {

				ngModel.$validators.palindromeSize = function(value) {
					if(value && scope.mismatches) {
						try {
							var lengthsPalindrome = extractRangeValues(value);
							var lengthsMismatches = extractRangeValues(scope.mismatches);

							var maxMismatch = Math.max.apply(null, lengthsMismatches);
							var minPalindrome = Math.min.apply(null, lengthsPalindrome);

							return maxMismatch / minPalindrome <= 0.5;
						} catch(e) {
							return true;
						}
					}
					return true;
				};

				scope.$watch("mismatches", function(newValue) {
					if(newValue) {
						ngModel.$validate();
					}
				});

			}
		};
	});

	app.directive('range', function() {
		return {
			require: 'ngModel',
			link: function (scope, elem, attr, ngModel) {

				ngModel.$validators.range = function(value) {
					if(value) {
						try {
							extractRangeValues(value);
							return true;
						} catch(e) {
							return false;
						}
					}
					return true;
				};

			}
		};
	});

	var extractRangeValues = function(s) {
		var tokens = s.split(',');
		var values = [];
		for(var i = 0; i < tokens.length; i++) {
			var token = tokens[i];
			if(token.indexOf('-') == -1) {
				values.push(parseInt(token));
			} else {
				var tmp = token.split('-');
				if(tmp.length == 2 && parseInt(tmp[0]) < parseInt(tmp[1])) {
					for (var j = parseInt(tmp[0]); j <= parseInt(tmp[1]); j++) {
						values.push(j);
					}
				} else {
					throw new RangeError("Values in range " + s + " are wrong.");
				}
			}
		}
		return values;
	};

});