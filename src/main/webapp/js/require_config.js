require.config({
    baseUrl: 'js',
    paths: {
        angular: 'vendor/angular',
        angularRouter: 'vendor/angular-route',
		jquery: 'vendor/jquery',
		bootstrap: 'vendor/bootstrap',
        angularMessages: 'vendor/angular-messages',
        angularTranslate: 'vendor/angular-translate',
        angularBootstrap: 'vendor/angular-bootstrap'
    },
    shim: {
        angular: {
			exports: 'angular'
        },
        angularRouter: {
			deps: ["angular"]
        },
        angularMessages: {
            deps: ["angular"]
        },
        angularTranslate: {
            deps: ["angular"]
        },
        angularBootstrap: {
            deps: ["angular"]
        },
		bootstrap: {
			deps: ["jquery"]
		}
    }
});