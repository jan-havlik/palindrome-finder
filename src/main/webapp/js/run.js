require([
    "app",

    "classes/form-errors",

    "controllers/index",
    "controllers/palindrome",
    "controllers/upload",
    "controllers/p53-predictor",
    "controllers/register",
    "controllers/login",
    "controllers/logout",
    "controllers/genom-overview",
    "controllers/ncbi",
    "controllers/analysis-overview",
    "controllers/palindrome-results",
    "controllers/reset-pass",
    "controllers/change-pass",
    "controllers/navbar",
    "controllers/text-import",

    "services/links",
    "services/languages",
    "services/auth",
    "services/analytics",

    "filters/filters",

    "directives/heat-map",
    "directives/ui-responsive",
    "directives/chromosome-browser",
    "directives/order-direction",
    "directives/horizontal-scroll",
    "directives/form-validators",
    "directives/modal"
], function(app) {

    app.run(function($rootScope, $filter, linksService, appConfig, authService, analyticsService) {
        $rootScope.linksService = linksService;
        $rootScope.buildLink = linksService.buildLink;
        $rootScope.appConfig = appConfig;

        authService.init();
        authService.isLoggedIn().then(function(user) {
            $rootScope.user = user;
        });

        $rootScope.$on('$routeChangeSuccess', function(event, newRoute, oldRoute) {
            var title = appConfig.pageTitleSuffix;
            if(newRoute.$$route && newRoute.$$route.pageTitle) {
                title = $filter('translate')(newRoute.$$route.pageTitle) + " - " + appConfig.pageTitleSuffix;

                linksService.currentRoute = newRoute.$$route;
                linksService.currentIdent = newRoute.$$route.ident;
            } else {
                linksService.currentRoute = null;
                linksService.currentIdent = "";
            }
            $rootScope.pageTitle = title;
        });

        $rootScope.$on('$routeChangeStart', function(event, newRoute, oldRoute) {
            if(newRoute.$$route) {
                var isPublic = Boolean(newRoute.$$route.public);
                if(!isPublic) {
                    newRoute.$$route.resolve = {waitForUserAuth: function() { return authService.isLoggedIn(); }};
                }
            }
        });

        $rootScope.$on('$routeChangeError', function() {
            linksService.navigate("index");
        });

        analyticsService.init(appConfig.analyticsIdent, appConfig.analyticsDomain);
        $rootScope.$on('$viewContentLoaded', function() {
            analyticsService.report();
        });

    });

    angular.bootstrap(document, ['app']);

});
