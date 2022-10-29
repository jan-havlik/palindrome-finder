define("services/links", [
	"app"
], function(app) {
	console.log("links");

	/**
	 * sluzba pro stavbu odkazu v ramci aplikace
	 */
	app.factory('linksService', ['$location', 'routes', 'languagesService', function($location, routes, languagesService) {
		return {
			routes: routes,
			currentRoute: null,
			currentIdent: "",
			/**
			 * podle zadaneho identifikatoru vygeneruje URL
			 *
			 * @param String ident Identifikator routy
			 * @param Object rest Data
			 * @param String query Cast URL za ?
			 * @param String language (nepovinny) Jazyk
			 * @returns {String}
			 */
			buildLink: function(ident, rest, query, language) {
				for(var i in routes) {
					var route = routes[i];
					if(route.ident == ident) {
						language = language ? language : languagesService.getLang();
						var ret = "";
						if(!$location.$$html5) {
							ret += "#/";
						}
						ret += language;
						ret += "/" + route.url;
						if(rest) {
							ret += "/" + rest;
						}
						var sep = "?";
						for(var name in query) {
							ret += sep + name + "=" + query[name];
							sep = "&";
						}
						return ret;
					}
				}
				throw "Ident [" + ident + "] of route not found.";
			},
			getTemplate: function(ident) {
				for(var i in routes) {
					var route = routes[i];
					if(route.ident == ident) {
						return route.templateUrl;
					}
				}
				throw "Ident [" + ident + "] of route not found.";
			},
			navigate: function(ident, rest, query) {
				$location.path(this.buildLink(ident, rest, query));
			},
			isCurrent: function(ident) {
				if(this.currentRoute && this.currentRoute.ident == ident) {
					return true;
				}
				return false;
			}
		};
	}]);

});