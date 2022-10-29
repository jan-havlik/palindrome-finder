define("controllers/change-pass", [
    "app",
    "classes/form-errors"
], function(app, FormErrorHelper) {

    app.controller("ChangePassCtrl", function($scope, $http) {
        console.log("ChangePassCtrl");

        var fe = new FormErrorHelper();

        $scope.changePass = function(form) {
            fe.reset(form);
            fe.disable(form);
            $http.put("/rest/user/password", {
                oldPassword: $scope.passwordOld,
                newPassword: $scope.password1
            }).success(function() {
                fe.enable(form);
                fe.success(form);
            }).error(function(response) {
                fe.enable(form);
                fe.error(form, response.message);
            });
        };

    });

})