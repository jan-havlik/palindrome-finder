define("directives/chromosome-browser", ["app"], function (app) {

    app.directive("chromosomeBrowser", function ($http, $timeout, $q, $sce) {
        var toLetterArray = function(letter) {
            return {
                letter: letter,
                layers: []
            }
        };
        /**
         * zobrazi nahled do chromozomu gen po genu
         */
        return {
            scope: {
                chromosomeBrowser: "@",
                chromosomeLength: "@",
                palindromes: "=",
                browserPos: "="
            },
            templateUrl: "partials/chromosome-browser.html",
            controller: function($scope) {
                var name = "";
                var chLen = 0;
                var currPos = 0;
                var loadOffset = 0; //pocet genu doleva a doprava
                var self = this;

                this.setInfo = function(n, l) {
                    name = n;
                    chLen = l;
                };

                this.update = function(p) {
                    this._loadChromosomePart(p);
                };

                this.setLoadOffset = function(lOffset) {
                    loadOffset = lOffset;
                };

                this._loadChromosomePart = function(p) {
                    if(loadOffset > 0 && loadOffset < 500) {
                        currPos = Math.max(0, Math.min(p, chLen - loadOffset));
                        $http.get("/rest/genoms/" + name + "?pos=" + currPos + "&len=" + Math.min(chLen, loadOffset)).success(function (data) {
                            self._highlightPalindromes(currPos, data.chromosome);
                        });
                    }
                };

                //vyresi prekryti palindromu
                this._findEmptyLayer = function(genes, pp) {
                    var found;
                    var layer = -1;
                    do {
                        found = true;
                        layer++;
                        for(var i = Math.max(0, pp.pos); i < pp.pos + pp.len * 2 + pp.splen && i < genes.length; i++) {
                            if(genes[i].layers[layer]) {
                                found = false;
                                break;
                            }
                        }
                    } while(!found);
                    return layer;
                };

                this._highlightPalindromes = function(p, chp) {
                    //vyber palindromu, ktere zobrazit
                    var palindromes = $scope.palindromes;
                    var positions = [];
                    for(var i in palindromes) {
                        var palindrome = palindromes[i];
                        if(palindrome.position >= p - chp.length && palindrome.position < p + chp.length) {
                            positions.push({
                                pos: palindrome.position - p,
                                len: palindrome.sequence.length,
                                splen: palindrome.spacer.length
                            });
                        }
                    }
                    var genes = chp.split("").map(toLetterArray);
                    for(var i = 0; i < positions.length; i++) {
                        var pp = positions[i];
                        var newLayer = this._findEmptyLayer(genes, pp);
                        for(var j = Math.max(0, pp.pos); j < pp.pos + pp.len && j < genes.length; j++) {
                            genes[j].layers[newLayer] = "sequence";
                        }
                        for(var j = Math.max(0, pp.pos + pp.len); j < pp.pos + pp.len + pp.splen && j < genes.length; j++) {
                            genes[j].layers[newLayer] = "spacer";
                        }
                        for(var j = Math.max(0, pp.pos + pp.len + pp.splen); j < pp.pos + pp.len * 2 + pp.splen && j < genes.length; j++) {
                            genes[j].layers[newLayer] = "opposite";
                        }
                    }
                    $scope.chromosomePart = genes;
                }
            },
            link: function (scope, element, attr, ctrl) {
                scope.pos = 0;  //pozice na scrollbaru
                scope.scrollChromosomeLength = 0;   //delka pro scrollbar
                scope.chromosomePart = [{letter: " "}]; //cast chromosomu (mezera pro zmereni sirky!)

                var maxLen = 0;
                var name = "";
                var p = 0;
                var step = 20;
                var loadOffset = 0;

                var calcLoadOffset = function() {
                    return $q(function(resolve) {
                        scope.chromosomePart = ["x"].map(toLetterArray); //cast chromosomu (X pro zmereni sirky, pokud neni chromozom!)
                        $timeout(function () {
                            var chromosomeCont = $(".chromosome .gene", element[0]);
                            var charWidth = Math.abs(chromosomeCont.width());
                            var contWidth = Math.abs($(".chromosome-container", element[0]).width());

                            var loadOffset = Math.max(step, Math.round(contWidth / charWidth)) - 1; //-1 kvuli paddingu

                            scope.scrollChromosomeLength = Math.max(0, maxLen - loadOffset);

                            resolve(loadOffset);
                        }, 100);
                    });
                };

                var init = function() {
                    calcLoadOffset().then(function(lo) {
                        loadOffset = lo;
                        //console.log("loadOffset", loadOffset);

                        ctrl.setLoadOffset(loadOffset);
                        ctrl.setInfo(name, maxLen);
                        ctrl.update(0);
                    });
                };

                var clear = function() {
                    scope.chromosomePart = [];
                };

                scope.$watchGroup(["chromosomeBrowser", "chromosomeLength"], function (newValues) {
                    name = newValues[0];
                    maxLen = newValues[1];

                    if (name && maxLen > 0) {
                        init();
                    } else {
                        clear();
                    }
                });

                scope.$watch("browserPos", function(newVal, oldVal) {
                    if(newVal != null && newVal != oldVal) {
                        ctrl.update(newVal);
                        p = newVal;
                        scope.pos = p;
                    }
                });

                //aktualizace polohy z posuvniku
                var loading = null;
                scope.updatePos = function(newPos) {
                    p = Math.round(newPos);
                    scope.chromosomePart = String("..." + p + "/" + maxLen + "...").split("").map(toLetterArray);
                    if(loading) {
                        $timeout.cancel(loading);
                    }
                    loading = $timeout(function() {
                        ctrl.update(p);
                    }, 500);
                };

                //sipka doprava
                scope.nextStep = function() {
                    p = Math.min(maxLen - loadOffset, p + step);
                    scope.pos = p;
                    ctrl.update(p);
                };

                //sipka doleva
                scope.prevStep = function() {
                    p = Math.max(0, p - step);
                    scope.pos = p;
                    ctrl.update(p);
                };

                $(window).resize(function () {
                    init();
                });

            }
        };

    });

});