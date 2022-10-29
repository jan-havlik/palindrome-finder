define("controllers/upload", [
    "app",
    "classes/form-errors"
], function(app, FormErrorHelper) {

    app.controller("UploadCtrl", function($scope, $filter) {
        console.log("UploadCtrl");

        var fe = new FormErrorHelper();

        $scope.format = "plain";

        $scope.upload = function(form) {
            var readyStateChange = function() {
                if(request.readyState == 4) {
                    if(request.status == 200) {
                        $scope.$apply(function() {
                            fe.enable(form);
                            fe.success(form);
                            $scope.name = "";
                        });
                    } else {
                        $scope.$apply(function() {
                            fe.enable(form);
                            fe.error(form, $filter("translate")("UPLOAD_FAILED") + request.message + "(" + request.status + ")");
                        });
                    }
                }
            };

            fe.reset(form);
            fe.disable(form);
            if(document.forms[form.$name].file.files.length > 0) {
                var fd = new FormData();
                fd.append("name", $scope.name);
                fd.append("format", $scope.format);
                fd.append("file", document.forms[form.$name].file.files[0], document.forms[form.$name].file.files[0].name);

                var request = new XMLHttpRequest();
                request.onreadystatechange = readyStateChange;
                request.open("POST", "/rest/genoms");
                request.send(fd);
            } else {
                fe.enable(form);
                fe.error(form, $filter("translate")("NO_FILE"));
            }
        };

    });

});