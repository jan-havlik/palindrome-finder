define("controllers/index", ["app"], function(app) {

    app.controller("IndexCtrl", function($scope) {
        console.log("IndexCtrl");

        $scope.date = new Date();
    });

});
