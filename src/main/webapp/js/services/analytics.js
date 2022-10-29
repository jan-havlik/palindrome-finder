define("services/analytics", ["app"], function(app) {
	console.log("analytics");

	app.factory("analyticsService", ['$location', function($location) {
		return {
			/**
			 * inicializace google analytics
			 * @param String ident - napr. UA-5190...
			 * @param String domain - napr. domena.cz
			 */
			init: function(ident, domain) {
				(function(i,s,o,g,r,a,m){i['GoogleAnalyticsObject']=r;i[r]=i[r]||function(){
				(i[r].q=i[r].q||[]).push(arguments)},i[r].l=1*new Date();a=s.createElement(o),
				m=s.getElementsByTagName(o)[0];a.async=1;a.src=g;m.parentNode.insertBefore(a,m)
				})(window,document,'script','//www.google-analytics.com/analytics.js','ga');

				ga('create', ident, domain);
				ga('send', 'pageview');
			},
			report: function() {
				ga('send', 'pageview', {'page': $location.path()});
			}
		}
	}]);

});
