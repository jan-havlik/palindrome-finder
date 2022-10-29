define("controllers/reset-pass", [
    "app",
    "classes/form-errors"
], function(app, FormErrorHelper) {

    app.controller("ResetPassCtrl", function($scope, $http) {
        console.log("ResetPassCtrl");

        var fe = new FormErrorHelper();

        $scope.resetPass = function(form) {
            fe.reset(form);
            fe.disable(form);
            $http.post("/rest/user/password", {
                email: $scope.email
            }).success(function(response) {
                fe.enable(form);
                fe.success(form);
            }).error(function(response) {
                fe.enable(form);
                fe.error(form, response.message);
            });
        };

    });

});