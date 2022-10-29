define("app", [
    "angular",
    "translations",
    "routes",
    "angularRouter",
    "angularMessages",
    "angularTranslate",
    "angularBootstrap",
    "jquery",
    "bootstrap",
    "translations",
    "routes"
], function(angular, translations, routes) {

    var app = angular.module("app", ["ngRoute", "ngMessages", "pascalprecht.translate", "ui.bootstrap"]);

    app.constant("routes", routes);

    app.constant('appConfig', {
        appTitle: 'DNA analyzer',
        analyticsIdent: 'UA-74932754-1',
        analyticsDomain: 'auto',
        pageTitleSuffix: 'DNA analyzer',
        languages: ['cs', 'en'],	//jazyky, ve kterych je aplikace provozovana
        defaultLang: 'en'	//vychozi jazyk
    });

    app.config(['$translateProvider', function($translateProvider) {
        $translateProvider.translations('cs', translations.cz);
        $translateProvider.translations('en', translations.en);
        $translateProvider.preferredLanguage('en');
        $translateProvider.useSanitizeValueStrategy('escape');
    }]);

    app.config(['$routeProvider', '$locationProvider', 'routes', 'appConfig', function($routeProvider, $locationProvider, routes, appConfig) {
        for(var i in routes) {
            var route = routes[i];
            if(!route.url) {
                route.url = route.ident;
            }
            var path =  route.route ? route.route : ":lang/" + route.url;
            $routeProvider.when("/" + path, {
                templateUrl: route.templateUrl,
                controller: route.controller,
                pageTitle: route.pageTitle,
                ident: route.ident,
                public: route.public
            });
        }
        $routeProvider.otherwise({
            redirectTo: appConfig.defaultLang + "/index"
        });
        //$locationProvider.html5Mode(true);
    }]);

    return app;

});