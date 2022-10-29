define("controllers/p53-predictor", [
    "app",
    "classes/p53-predictor",
    "classes/ui-order"
], function(app, P53Predictor, UiOrder) {

    var pred = new P53Predictor();

    app.controller("P53PredictorSingleCtrl", function($scope) {
        console.log("P53PredictorSingleCtrl");

        $scope.error = "";
        $scope.predictor = 0;
        $scope.difference = 0;

        $scope.calcPredictor = function(sequence) {
            $scope.error = "";
            $scope.predictor = 0;
            $scope.difference = 0;
            try {
                var info = pred.calc(sequence);
                $scope.predictor = info.predictor;
                $scope.difference = info.difference;
                $scope.relativeAffinity = info.relativeAffinity;
            } catch(e) {
                $scope.error = e;
            }
            $scope.length = pred.getLength();
        };

    });

    app.controller("P53PredictorMultiCtrl", function($scope) {
        console.log("P53PredictorMultiCtrl");

        $scope.error = "";
        $scope.results = [];

        $scope.calcPredictors = function(sequences) {
            $scope.uiOrderMultiple = "index";
            $scope.error = "";
            $scope.results = [];

            var seq = sequences.split("\n");
            for(var i = 0; i < seq.length; i++) {
                var sequence = seq[i].trim();
                var result = {};
                try {
                    result = pred.calc(sequence);
                    result.index = i + 1;
                } catch(e) {
                    result.error = e;
                }
                result.sequence = pred.getSequence();
                $scope.results.push(result);
            }
        };

        UiOrder.extendScope($scope);
    });

    app.controller("P53PredictorFindCtrl", function($scope) {
        console.log("P53PredictorFindCtrl");

        $scope.amount = 10;
        $scope.error = "";
        $scope.sequence = "";

        $scope.findBest = function(sequence) {
            $scope.error = "";
            if ($scope.sequence) {
                try {
                    $scope.results = pred.calcSequence($scope.sequence);
                    $scope.results.sort(function(a, b) {
                        if(a.difference < b.difference) {
                            return -1;
                        } else {
                            return 1;
                        }
                    });
                    $scope.results = $scope.results.slice(0, $scope.amount);
                } catch(e) {
                    $scope.error = e;
                }
            }
        };

    });

});