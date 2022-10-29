define("controllers/login", [
	"app",
	"classes/form-errors"
], function(app, FormErrorHelper) {
	
	app.controller("LoginCtrl", function($scope, $rootScope, $http, authService, linksService) {
		console.log("LoginCtrl");

		var fe = new FormErrorHelper();
		
		$scope.doLogin = function(form) {
			fe.reset(form);
			fe.disable(form);

			var credentials = btoa($scope.email + ":" + $scope.pass);

			$http.put("/rest/user/login", {}, {headers: { Authorization: "Basic " + credentials }}).success(function(response) {
				fe.enable(form);
				fe.success(form);

				var user = {
					email: $scope.email
				};
                authService.reset();
				authService.setUser(user);

                $rootScope.user = user;

				linksService.navigate("index");
			}).error(function(response) {
				fe.enable(form);
				fe.error(form, response.message);
			});
		};
		
	});
	
});