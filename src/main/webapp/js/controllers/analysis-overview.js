define("controllers/analysis-overview", [
    "app",
    "classes/ui-order"
], function(app, UiOrder) {

    /**
     * prehled analyz probehlych v minulosti + mazani
     */
    app.controller("AnalysisOverviewCtrl", function($http, $scope) {
        console.log("AnalysisOverviewCtrl");

        $scope.uiOrderResults = "-date";

        $http.get("rest/analyze/palindrome").success(function(data) {
            $scope.analyses = data;
        });

        $scope.deleteAnalysisConfirm = function() {
            var cnt = $scope.analyses.reduce(function(prev, current) {
                return prev + (current.selected ? 1 : 0);
            }, 0);
            if(cnt > 0) {
                $scope.confirmDeletion = true;
            }
        };

        $scope.deleteAnalyses = function() {
            $scope.analyses.forEach(function(analysis, index, arr) {
                if(analysis.selected) {
                    $http.delete("rest/analyze/palindrome/" + analysis.id).success(function() {
                        arr.splice(arr.indexOf(analysis), 1);
                    }).error(function(err) {
                        console.log(err);
                    });
                }
            });
        };

        $scope.toggleSelection = function() {
            $scope.analyses.forEach(function(analysis) {
                analysis.selected = !analysis.selected;
            });
        };

        UiOrder.extendScope($scope);

    });

});