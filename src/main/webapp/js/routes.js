define("routes", function() {

    return [
        {
            ident: "index",	//identifikator pro stavbu odkazu
            controller: "IndexCtrl",	//objekt kontroleru
            url: "index",	//url - zaklad
            route: ":lang/index",	//routa
            templateUrl: "views/index.html",	//cesta k sablone
            pageTitle: "PAGE_INDEX",	//title stranky
            public: true    //stranka je verejna a neni potreba se prihlasovat
        },
        {   //analyza palindromu
            ident: "palindrome",
            controller: "PalindromeCtrl",
            url: "palindrome",
            route: ":lang/palindrome/:id?",
            templateUrl: "views/palindrome.html",
            pageTitle: "PAGE_PALINDROME",
            public: true
        },
        {   //prediktor
            ident: "p53-predictor",
            url: "p53-predictor",
            route: ":lang/p53-predictor",
            templateUrl: "views/p53-predictor.html",
            pageTitle: "PAGE_PREDICTOR",
            public: true
        },
        {   //upload
            ident: "import",
            url: "import",
            route: ":lang/import",
            templateUrl: "views/import.html",
            pageTitle: "PAGE_IMPORT"
        },
        {   //prehled genomu
            ident: "genom-overview",
            controller: "GenomOverviewCtrl",
            url: "genom-overview",
            route: ":lang/genom-overview",
            templateUrl: "views/genom-overview.html",
            pageTitle: "PAGE_GENOMS_OVERVIEW",
        },
        {   //prehled analyz
            ident: "analysis-overview",
            controller: "AnalysisOverviewCtrl",
            url: "analysis-overview",
            route: ":lang/analysis-overview",
            templateUrl: "views/analysis-overview.html",
            pageTitle: "PAGE_ANALYSIS_OVERVIEW",
        },
        {   //ulozene vysledky analyzy
            ident: "palindrome-results",
            controller: "PalindromeResultsCtrl",
            url: "palindrome-results",
            route: ":lang/palindrome-results/:id",
            templateUrl: "views/palindrome-results.html",
            pageTitle: "PAGE_ANALYSIS_OVERVIEW",
        },
        //servis
        {
            ident: "login",
            controller: "LoginCtrl",
            url: "login",
            route: ":lang/login",
            templateUrl: "views/login.html",
            pageTitle: "PAGE_LOGIN",
            public: true
        },
        {
            ident: "register",
            controller: "RegisterCtrl",
            url: "register",
            route: ":lang/register",
            templateUrl: "views/register.html",
            pageTitle: "PAGE_REGISTER",
            public: true
        },
        {
            ident: "reset-pass",
            controller: "ResetPassCtrl",
            url: "reset-pass",
            route: ":lang/reset-pass",
            templateUrl: "views/reset-pass.html",
            pageTitle: "PAGE_RESET_PASS",
            public: true
        },
        {
            ident: "change-pass",
            controller: "ChangePassCtrl",
            url: "change-pass",
            route: ":lang/change-pass",
            templateUrl: "views/change-pass.html",
            pageTitle: "PAGE_CHANGE_PASS"
        },
        {
            ident: "logout",
            controller: "LogoutCtrl",
            url: "logout",
            route: ":lang/logout",
            templateUrl: "views/logout.html",
            pageTitle: "PAGE_LOGOUT",
            public: true
        },

        //napoveda
        {
            ident: "help-palindrome",
            templateUrl: "views/help/palindrome.html",
            pageTitle: "PAGE_PALINDROME",
            public: true
        },
        {
            ident: "help-p53-predictor",
            templateUrl: "views/help/p53-predictor.html",
            pageTitle: "PAGE_PREDICTOR",
            public: true
        },
        {
            ident: "help-about",
            templateUrl: "views/help/about.html",
            pageTitle: "PAGE_ABOUT",
            public: true
        },
        {
            ident: "help-import-upload",
            templateUrl: "views/help/import-upload.html",
            pageTitle: "PAGE_IMPORT_UPLOAD",
            public: true
        },
        {
            ident: "help-faq",
            templateUrl: "views/help/faq.html",
            pageTitle: "PAGE_FAQ",
            public: true
        }
    ];

});