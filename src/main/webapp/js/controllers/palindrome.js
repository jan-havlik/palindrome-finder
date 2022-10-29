define("controllers/palindrome", [
    "app",
    "classes/form-errors",
    "classes/palindrome-results",
    "classes/ui-order"
], function(app, FormErrorHelper, PalindromeResults, UiOrder) {

    app.controller("PalindromeCtrl", function($scope, $http, $timeout, $filter, $routeParams) {
        console.log("PalindromeCtrl");

        var fe = new FormErrorHelper();

        //vychozi hodnoty formulare
        $scope.size = "6-30";
        //$scope.size = "4-6";            //debug
        $scope.spacer = "0-10";
        $scope.mismatches = "0,1";
        $scope.storeResult = false; //ulozeni vysledku
        $scope.cycle = false;   //cyklicky
        $scope.genome = "text";

        $scope.ncbiId = ""; //zadane NCBI ID
        $scope.rawChromosome = "";  //zadani jako text
        $scope.dinucleotide = false;    //zahrnout ATATAT... palindromy

        PalindromeResults.initScopeVars($scope);

        const loadGenoms = function(overrideID) {
            $http.get("/rest/genoms").success(function (data) {
                $scope.genoms = data;

                //prednastavit genom
                if(overrideID) {
                    $scope.currentGenome = $scope.genoms.find(findGenomeById(overrideID));
                    $scope.genome = overrideID;
                } else if($routeParams.id) {
                    $scope.currentGenome = $scope.genoms.find(findGenomeById($routeParams.id));
                    $scope.genome = $scope.currentGenome.id;
                }
            });
        };

        //funkce pro hledani genomu podle id
        const findGenomeById = function(id) {
            return function(item) { return item.id == id; }
        };

        const launchAnalysis = function(form, url, data) {
            var start = new Date().getTime();

            fe.reset(form);
            fe.disable(form);
            $timeout(function() {
                $http.post(url, data).success(function (data) {
                    var end = new Date().getTime();

                    PalindromeResults.processPalindromes($scope, data);

                    $scope.refreshRepetitions($scope.palindromes, true, false, false);

                    $scope.duration = (end - start) / 1000;

                    fe.enable(form);
                    fe.success(form);
                }).error(function (data) {
                    fe.enable(form);
                    fe.error(form, data.message);
                });
            });
        };

        const downloadGenomeAndLaunch = function(form, url, data) {
            fe.reset(form);
            fe.disable(form);
            $timeout(function() {
                var ncbiId = String($scope.ncbiId).trim();
                $http.post("/rest/ncbi/" + ncbiId).success(function(response) {
                    loadGenoms(response.id);
                    url = "/rest/analyze/palindrome/" + response.id;
                    if($scope.storeResult) {
                        data.storeResult = true;
                    }
                    launchAnalysis(form, url, data);
                }).error(function() {
                    fe.reset(form);
                    fe.error(form, $filter("translate")("ERR_NOT_FOUND"));
                });
            });
        };

        $scope.analyzePalindrome = function (form) {
            console.log("analyzePalindrome");

            var data = {
                size: $scope.size,
                spacer: $scope.spacer,
                mismatches: $scope.mismatches,
                cycle: $scope.cycle,
                dinucleotide: $scope.dinucleotide
            };

            PalindromeResults.resetReportVars($scope);

            var url = "";
            if($scope.rawChromosome && $scope.genome == "text") {
                //analyza z textu
                url = "/rest/analyze/palindrome";
                data.sequence = $scope.rawChromosome;
                launchAnalysis(form, url, data);
            } else if($scope.ncbiId && $scope.genome == "ncbi") {
                //analyza z NCBI
                downloadGenomeAndLaunch(form, url, data);
            } else if($scope.genome) {
                //analyza nad existujicim
                url = "/rest/analyze/palindrome/" + $scope.genome;
                $scope.currentGenome = $scope.genoms.find(findGenomeById($scope.genome));
                if($scope.storeResult) {
                    data.storeResult = true;
                }
                launchAnalysis(form, url, data);
            } else {
                fe.reset(form);
                fe.error(form, $filter("translate")("ERR_FILL_ALL"));
                return;
            }
        };

        loadGenoms();
        PalindromeResults.extendsScope($scope);
        UiOrder.extendScope($scope);

    });

});