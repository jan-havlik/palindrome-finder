define("directives/order-direction", ["app"], function(app) {

    app.directive("orderDirection", function() {
        return {
            scope: {
                orderDirection: '=',
                column: '@'
            },
            template: "",
            restrict: "A",
            link: function(scope, element, attr) {
                scope.$watch("orderDirection", function(val) {
                    if(val) {
                        var cls = "";
                        if(val == scope.column) {
                            cls += "glyphicon glyphicon-arrow-down";
                        } else if(val == "-" + scope.column) {
                            cls += "glyphicon glyphicon-arrow-up";
                        }
                        $(element).removeClass();
                        $(element).addClass(cls);
                    }
                });
            }
        };
    });

});
