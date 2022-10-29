define("controllers/palindrome-results", [
    "app",
    "classes/palindrome-results",
    "classes/ui-order"
], function(app, PalindromeResults, UiOrder) {

    /**
     * zpetne zobrazeni vysledku analyzy palindromu
     *
     */
    app.controller("PalindromeResultsCtrl", function($http, $scope, $routeParams) {
        console.log("PalindromeResultsCtrl");

        PalindromeResults.initScopeVars($scope);

        if($routeParams.id) {
            //stazeni dat analyzy
            $http.get("rest/analyze/palindrome/" + $routeParams.id).success(function(data) {

                PalindromeResults.resetReportVars($scope);

                PalindromeResults.processPalindromes($scope, data);

                $scope.refreshRepetitions($scope.palindromes, true, false, false);

                //stazeni genomu
                $http.get("rest/genoms/" + data.genomeId).success(function(data) {
                    $scope.currentGenome = data;
                });
            });
        }

        PalindromeResults.extendsScope($scope);
        UiOrder.extendScope($scope);

    });

});