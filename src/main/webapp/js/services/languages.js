define("services/languages", [
	"app"
], function(app) {
	console.log("languages");

	/**
	 * drzi aktualni jazyk aplikace
	 */
	app.factory('languagesService', ['appConfig', function(appConfig) {
		return {
			currentLang: appConfig.defaultLang,
			setLang: function(l) {
				this.currentLang = l;
			},
			getLang: function() {
				return this.currentLang;
			}
		};
	}]);

});