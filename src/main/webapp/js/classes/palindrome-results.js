define("classes/palindrome-results", [
    "classes/gene-visualizer"
], function(GeneVisualizer) {

    var gv = new GeneVisualizer();

    //analyza cetnosti
    var findRepetitions = function (data, matchSequence, matchSpacer, matchOpposite) {
        console.log("findRepetitions");
        var reps = [];
        var index = {};
        var findSequence = function (seq, opposite, spacer) {
            for (var i = 0; i < reps.length; i++) {
                var matching = true;
                if(matchSequence && reps[i].sequence != seq) { matching = false; }
                if(matchSpacer && reps[i].spacer != spacer) { matching = false; }
                if(matchOpposite && reps[i].opposite != opposite) { matching = false; }
                if(matching) {
                    return reps[i];
                }
            }
            return null;
        };
        for (var i = 0; i < data.length; i++) {
            var seq = findSequence(data[i].sequence, data[i].opposite, data[i].spacer);
            if (seq) {
                seq.pos.push(data[i].position);
                seq.count++;
            } else {
                seq = {
                    sequence: data[i].sequence,
                    opposite: data[i].opposite,
                    spacer: data[i].spacer,
                    count: 1,
                    pos: [data[i].position]
                };
                reps.push(seq);
            }
        }
        return reps;
    };

    //priprava statistik pro souhrn
    var calcGeneStats = function (data) {
        var stats = {
            'sizeCount': {},
            'mismatchCount': {},
            'spacerCount': {},
            'repetitions': {},
            'minStability': data.length > 0 ? parseFloat(data[0].stability_NNModel.delta) : 0,
            'maxStability': data.length > 0 ? parseFloat(data[0].stability_NNModel.delta) : 0
        };
        var addValue = function (k1, k2) {
            if (!stats[k1][k2]) {
                stats[k1][k2] = 0;
            }
            stats[k1][k2]++;
        };
        data.map(function (v) {
            addValue("sizeCount", v.sequence.length);
            addValue("mismatchCount", v.mismatches);
            addValue("spacerCount", v.spacer.length);
            v.stability_NNModel.delta = parseFloat(v.stability_NNModel.delta);
            v.stability_NNModel.linear = parseFloat(v.stability_NNModel.linear);
            v.stability_NNModel.cruciform = parseFloat(v.stability_NNModel.cruciform);
            if(stats.minStability > v.stability_NNModel.delta) {
                stats.minStability = v.stability_NNModel.delta;
            }
            if(stats.maxStability < v.stability_NNModel.delta) {
                stats.maxStability = v.stability_NNModel.delta;
            }
        });
        return stats;
    };

    var PalindromeResults = function() {

    };

    PalindromeResults.initScopeVars = function($scope) {
        $scope.uiOverview = true;            //rezim UI
        $scope.uiOrderOverview = "position";      //razeni v prehledu
        $scope.uiOrderSimilar = "-count";
        $scope.heatmapHighlight = [];   //zvyrazneni na heatmape
        $scope.filter = {};             //filtrace
        $scope.highlightSequence = "";  //zvyraznena sekvence
        $scope.minRepeat = 1;           //minimalni pocet opakovani
        $scope.matchRepSpacer = false;      //seskupovani i podle spaceru
        $scope.matchRepOpposite = false;    //seskupovani i podle opposite
        $scope.segment = null;          //zvyrazneny segment heatmapy
        $scope.currentGenome = null;   //aktualne analyzovany genom

        $scope.uiMatchRepSequence = true;
    };

    PalindromeResults.resetReportVars = function($scope) {
        $scope.info = null;
        $scope.currentGenome = null;
        $scope.chromosomeLength = 0;
    };

    PalindromeResults.extendsScope = function($scope) {

        const palindromeFilters = [
            function (filter, palindrome) {
                return filter.start && filter.end ? palindrome.position >= filter.start && palindrome.position < filter.end : true;
            },
            function (filter, palindrome) {
                return filter.size ? palindrome.sequence.length == filter.size : true;
            },
            function (filter, palindrome) {
                return filter.spacer ? palindrome.spacer.length == filter.spacer : true
            },
            function (filter, palindrome) {
                return filter.mismatch ? palindrome.mismatches == filter.mismatch : true;
            },
            function (filter, palindrome) {
                return filter.sequence ? palindrome.sequence == filter.sequence || palindrome.opposite == filter.sequence : true;
            }
        ];
        //filtrace jednotlivych palindromu v prehledu
        $scope.decideFilter = function (filter, palindrome) {
            if (filter) {
                for (var i = 0; i < palindromeFilters.length; i++) {
                    if (!palindromeFilters[i](filter, palindrome)) {
                        return false;
                    }
                }
            }
            return true;
        };

        //filtr podle pozice
        $scope.applyFilter = function (segment) {
            if (segment) {
                $scope.filter.start = segment.start;
                $scope.filter.end = segment.end
            } else {
                delete $scope.filter.start;
                delete $scope.filter.end;
            }
            if(segment) {
                $scope.browserPos = segment.start;
            }
        };

        $scope.isFilterEmpty = function () {
            if (!$scope.filter) {
                return true;
            }
            var filter = $scope.filter;
            return !filter.start && !filter.end && !filter.size && !filter.spacer && !filter.mismatch && !filter.sequence;
        };

        //na heatmape zvyrazni urcite pozice
        $scope.highlightPalindromes = function(positions) {
            $scope.heatmapHighlight = positions;
            //pokud zvyraznuju vic nez 1, tak ho i zamerim v browseru
            if(positions.length >= 1) {
                $scope.browserPos = positions[0];
            }
        };

        //vyfiltruje jen urcite sekvence ve vypisu
        $scope.filterSequence = function(sequence) {
            $scope.filter.sequence = sequence;
        };

        $scope.isSequenceFilter = function(sequence) {
            return $scope.filter && $scope.filter.sequence == sequence
        };

        $scope.cancelFilter = function() {
            $scope.filter = {};
            $scope.segment = null;
        };

        $scope.toggleDetails = function(palindrome) {
            palindrome.showDetails = !palindrome.showDetails;
            if(palindrome.showDetails && !palindrome.visual) {
                palindrome.visual = gv.renderGene(palindrome);
            }
        }

        //prepocitat opakovani repetic pro analyzu cetnosti
        $scope.refreshRepetitions = function(palindromes, matchSequence, matchSpacer, matchOpposite) {
            if(!matchSequence && !matchSpacer && !matchOpposite) {
                matchSequence = true;
            }
            $scope.repetitions = findRepetitions(palindromes, matchSequence, matchSpacer, matchOpposite);
            $scope.uiShowRepSequence = matchSequence;
            $scope.uiShowRepSpacer = matchSpacer;
            $scope.uiShowRepOpposite = matchOpposite;
        };

    };

    PalindromeResults.processPalindromes = function($scope, data) {
        //reset filtru, chyby a zvyrazneni
        $scope.heatmapHighlight = [];
        $scope.highlightSequence = "";
        $scope.filter = {};

        $scope.info = data;
        $scope.palindromes = data.palindromes;
        $scope.segmentCount = Math.min(80, data.size);
        $scope.chromosomeLength = data.size;
        $scope.stats = calcGeneStats(data.palindromes);
        $scope.browserPos = 0;
    };

    return PalindromeResults;

});