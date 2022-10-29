define("controllers/logout", ["app"], function(app) {

    app.controller("LogoutCtrl", function($http, $rootScope, authService, linksService) {
        $http.put("/rest/user/logout").success(function () {
            $rootScope.user = null;

            authService.logout();
            linksService.navigate("index");
        });
    });

});