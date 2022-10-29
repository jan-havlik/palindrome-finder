define("directives/heat-map", ["app"], function(app) {

    /**
     * heat-mapa chromosomu
     */
    app.directive("heatMap", function() {
        return {
            scope: {
                heatMap: "=",
                segmentCount: "=",
                segment: "=",
                highlight: "=",
                segmentSize: "=",
                segmentSelected: "&"
            },
            templateUrl: "partials/heat-map.html",
            restrict: "A",
            link: function(scope, element, attr) {
                console.log("heatMap directive");

                var getPalindromeCount = function(start, end, palindromes) {
                    var ret = 0;
                    for(var i in palindromes) {
                        var p = palindromes[i];
                        if(p.position >= start && p.position < end) {
                            ret++;
                        }
                    }
                    return ret;
                };

                scope.clearFilter = function() {
                    scope.segment = null;
                    scope.current = null;
                    scope.highlight = [];
                    scope.segmentSelected({
                        segment: null,
                    });
                };

                scope.segmentClicked = function(segment) {
                    scope.segment = segment;
                    scope.current = segment;
                    scope.segmentSelected({
                        segment: segment,
                    });
                };

                scope.isHighlight = function(segment) {
                    for(var i in scope.highlight) {
                        var p = scope.highlight[i];
                        if(segment.start <= p && p < segment.end) {
                            return true;
                        }
                    }
                    return false;
                };

                scope.segmentSize = 0;

                scope.$watch("heatMap", function(heatMap) {
                    if(heatMap) {
                        var genes = heatMap.size;
                        var palindromes = heatMap.palindromes;
                        var genesPerSegment = Math.ceil(genes / scope.segmentCount);

                        scope.segmentSize = genesPerSegment;

                        var segments = [];
                        var maxPc = 1;
                        for(var i = 0; i < scope.segmentCount; i++) {
                            var s = i * genesPerSegment;
                            var e = s + genesPerSegment;
                            if(s <= genes) {
                                var pc = getPalindromeCount(s, e, palindromes);
                                if(pc > maxPc) {
                                    maxPc = pc;
                                }
                                segments.push({
                                    start: s,
                                    end: e,
                                    count: pc
                                });
                            }
                        }
                        for(var i in segments) {
                            segments[i].density = segments[i].count / maxPc;
                            segments[i].color = Math.round(segments[i].density * 255);
                        }

                        scope.segments = segments;
                    }
                });

                scope.$watch("segment", function(segment) {
                    if(!segment) {
                       scope.current = null;
                    }
                });

            }
        };
    });

});
