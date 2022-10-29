define("controllers/text-import", [
    "app",
    "classes/form-errors"
], function(app, FormErrorHelper) {

    app.controller("TextImportCtrl", function($scope, $http, $timeout) {
        console.log("TextImportCtrl");

        var fe = new FormErrorHelper();

        $timeout(function() {
            $scope.upload = function(form) {
                fe.reset(form);
                fe.disable(form);

                var data = {
                    name: $scope.name,
                    sequence: $scope.sequence
                };

                $http.post("/rest/genoms", data).success(function() {
                    fe.enable(form);
                    fe.success(form);
                }).error(function(err) {
                    fe.enable(form);
                    fe.error(form, err.message);
                });

            };
        });

    });

});