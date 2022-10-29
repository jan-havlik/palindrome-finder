define("classes/ui-order", function() {

    /**
     * prepinani smeru razeni
     *
     */

    UiOrder = {};

    UiOrder.extendScope = function($scope) {

        //prepnuti trideni
        $scope.toggleOrder = function (newOrder, orderVarName) {
            if(newOrder instanceof Array) {
                if ($scope[orderVarName][0] == newOrder[0]) {
                    newOrder[0] = "-" + newOrder[0];
                }
            } else {
                if ($scope[orderVarName] == newOrder) {
                    newOrder = "-" + newOrder;
                }
            }
            $scope[orderVarName] = newOrder;
        };

    };

    return UiOrder;

});
