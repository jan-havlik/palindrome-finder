define("controllers/navbar", ["app"], function(app) {

    app.controller("NavbarCtrl", function($http, $scope) {
        $scope.version = "xxx";
        $http.get("/rest/app").success(function(data) {
            $scope.version = data.version;
        });
    });

});