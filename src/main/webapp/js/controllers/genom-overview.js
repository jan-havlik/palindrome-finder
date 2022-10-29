define("controllers/genom-overview", [
    "app",
    "classes/ui-order"
], function(app, UiOrder) {

    /**
     * prehled genomu nahranych nebo naimportovanych uzivatelem + mazani
     *
     */
    app.controller("GenomOverviewCtrl", function($scope, $http) {
        console.log("GenomOverviewCtrl");

        $scope.confirmDeletion = false;
        $scope.genoms = [];
        $scope.uiOrderSequences = "-date";

        $http.get("rest/genoms").success(function(data) {
            $scope.genoms = data;
        });

        $scope.deleteGenomesConfirm = function() {
            var cnt = $scope.genoms.reduce(function(prev, current) {
                return prev + (current.selected ? 1 : 0);
            }, 0);
            if(cnt > 0) {
                $scope.confirmDeletion = true;
            }
        };

        $scope.deleteGenomes = function() {
            $scope.genoms.forEach(function(gen, index, arr) {
                if(gen.selected) {
                    $http.delete("rest/genoms/" + gen.id).success(function() {
                        arr.splice(arr.indexOf(gen), 1);
                    }).error(function(err) {
                        console.log(err);
                    });
                }
            });
        };

        $scope.toggleSelection = function() {
            $scope.genoms.forEach(function(gen) {
                gen.selected = !gen.selected;
            });
        };

        UiOrder.extendScope($scope);

    });

});