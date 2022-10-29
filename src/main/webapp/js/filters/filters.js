define("filters/filters", [
	"app"
], function(app) {

	app.filter("scut", [function() {
		/**
		 * oriznuti retezce na danou delku, reze od nejblizssi mezery
		 *
		 * @param {String} string
		 * @param {Number} maxLength
		 * @returns {@var;string}
		 */
		return function(string, maxLength) {
			if (string) {
				if(string.length > maxLength) {
					for(var i = maxLength; i < string.length; i++) {
						if(string.charAt(i) == " ") {
							string = string.substr(0, i);
						}
					}
					if(string.charAt(string.length - 1) == ".") {
						string += "..";
					} else {
						string += "...";
					}
				}
				return string;
			}
		};
	}]);

	app.filter('nl2br', [function() {
		return function(string) {
			if(string) {
				return string.split('\n').join('<br />');
			} else {
				return string;
			}
		}
	}]);

	app.filter('round', [function() {
		return function(string, decimals) {
			if(string) {
				var num = parseFloat(string);
				var val = Math.pow(10, decimals);
				return Math.round(num * val) / val;
			} else {
				return string;
			}
		}
	}]);

	app.filter('sqldatetime', [function() {
		return function(input) {
			if(input) {
				var t = String(input).split(/[- :]/);
				if(t.length == 6) {
					var d = new Date(t[0], t[1]-1, t[2], t[3], t[4], t[5]);
					return d.toLocaleDateString().replace(/ /g, "") + " " + d.toLocaleTimeString();
				} else {
					throw "Not valid date-time-string: " + input;
				}
			}
			return "";
		};
	}]);

	app.filter('sqldate', [function() {
		return function(input) {
			if(input) {
				var t = String(input).split(/[- :]/);
				if(t.length >= 3) {
					var d = new Date(t[0], t[1]-1, t[2]);
					return d.toLocaleDateString().replace(/ /g, "");
				} else {
					throw "Not valid date-string: " + input;
				}
			}
			return  "";
		};
	}]);

	app.filter('revsqldatetime', [function() {
		return function(input) {
			if(input) {
				var t = input.split(/[. :]/);
				return t[2] + "-" + t[1] + "-" + t[0] + " " + t[3] + ":" + t[4] + ":" + t[5];
			}
			return "";
		};
	}]);

	app.filter('revsqldate', [function() {
		function pad(input, padChar, finalLen) {
			input = String(input);
			while(input.length < finalLen) {
				input = padChar + input;
			}
			return input;
		}
		return function(input) {
			if(input) {
				if(input instanceof Date) {
					return input.getFullYear() + "-" + pad(input.getMonth() + 1, "0", 2) + "-" + pad(input.getDate(), "0", 2);
				} else {
					var t = input.split(/[.]/);
					return t[2] + "-" + t[1] + "-" + t[0];
				}
			}
			return  "";
		};
	}]);

	app.filter('localeDate', function() {
		return function(s) {
			var d = new Date(s);
			return d.toLocaleDateString();
		}
	})

	app.filter('localeDateTime', function() {
		return function(s) {
			var d = new Date(s);
			return d.toLocaleString();
		}
	});

});