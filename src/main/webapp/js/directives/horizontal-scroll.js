define("directives/horizontal-scroll", ["app"], function (app) {

    /**
     * horizontalni posuvnik
     */
    app.directive("horizontalScroll", function () {
        return {
            scope: {
                horizontalScroll: "@",
                initPos: "@",
                onPosChange: "&"
            },
            link: function (scope, element, attr) {
                var mover = $(".mover", element[0]);
                var bar = $(".navigation", element[0]);
                var moving = false;
                var prevPos = 0;    //predchozi x pozice mysi
                var moverPos = 0;   //pozice posuvniku
                var range = 0;
                var maxPos = bar.width() - mover.width();

                var updateMoverPos = function(newPos) {
                    moverPos = Math.min(Math.max(0, newPos), maxPos)
                    mover.css({left: moverPos + "px"});
                };

                mover.mousedown(function (ev) {
                    moving = true;
                    prevPos = ev.clientX;
                });

                bar.mousemove(function (ev) {
                    if (moving) {
                        var dx = ev.clientX - prevPos;
                        prevPos = ev.clientX;

                        updateMoverPos(moverPos + dx);

                        scope.$apply(function () {
                            scope.onPosChange({"pos": moverPos / maxPos * range});
                        });
                    }
                });

                $(document).mouseup(function () {
                    if (moving) {
                        moving = false;
                    }
                });

                $(window).resize(function () {
                    moverPos = 0;
                    prevPos = 0;
                    maxPos = bar.width() - mover.width();
                    mover.css({left: "0px"});
                });

                scope.$watch("horizontalScroll", function (newValue) {
                    if (newValue) {
                        maxPos = bar.width() - mover.width();
                        range = newValue;
                        prevPos = 0;
                        updateMoverPos(0);
                    }
                });

                scope.$watch("initPos", function (newVal) {
                    updateMoverPos(newVal / range * maxPos);
                });
            }
        };
    });
});