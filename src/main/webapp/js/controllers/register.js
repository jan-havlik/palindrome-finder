define("controllers/register", [
	"app",
	"classes/form-errors"
], function(app, FormErrorHelper) {

	app.controller("RegisterCtrl", function($scope, $http, $filter) {
		console.log("RegisterCtrl");

		var fe = new FormErrorHelper();

		$scope.doRegister = function(form) {
			fe.reset(form);
			fe.disable(form);
			var match = $scope.captchaImage.match(/^(.+)\./i);
			if(match[1].toLowerCase() == $scope.captcha.toLowerCase()) {
				var regData = {
					email : $scope.email,
					password : $scope.pass1
				};
				$http.post("/rest/user", regData).success(function(response) {
					fe.enable(form);
					fe.success(form);
				}).error(function(response) {
					fe.enable(form);
					fe.error(form, response.message);
				});
			} else {
				fe.enable(form);
				fe.error(form, $filter("translate")("ERR_RETYPE"));
			}
		};

		$http.get("rest/captcha").success(function(data) {
			$scope.captchaImage = data.captcha;
		});
	});

});