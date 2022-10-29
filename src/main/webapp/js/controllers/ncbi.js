define("controllers/ncbi", [
    "app",
    "classes/form-errors"
], function(app, FormErrorHelper) {

    /**
     * kontroler pro komunikaci s NCBI databazi pres vlastni backend
     *
     */
    app.controller("NCBICtrl", function($scope, $http, $timeout) {
        console.log("NCBICtrl");

        var fe = new FormErrorHelper();

        $timeout(function() {
            $scope.NCBILoad = function(form) {
                fe.reset(form);
                fe.disable(form);

                var id = String($scope.id).trim();

                $http.post("/rest/ncbi/" + id).success(function(response) {
                    fe.enable(form);
                    fe.success(form);
                }).error(function(response) {
                    fe.enable(form);
                    fe.error(form, response.message);
                });
            };
        });

    });

});