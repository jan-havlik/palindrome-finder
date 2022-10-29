define("services/auth", ["app"], function(app) {
	console.log("auth");

	app.factory('authService', ['$http', '$q', 'appConfig', function($http, $q, appConfig) {
		return {
			_user: null,
			_storage: "session",	//session nebo local
			_sessionAuth: true,		//je mozne, ze ma nastaveny session ID v cookie
			_storeUser: function(storage, user) {
				storage.setItem(appConfig.appTitle + '_user', JSON.stringify(user));
			},
            _loginPromise: $q.defer(),
			_loadUser: function(storage) {
				var userText = storage.getItem(appConfig.appTitle + '_user');
				if(userText) {
					try {
						var user = JSON.parse(userText);
						if(user && user.email) {
							this._sessionAuthUser(user);
							return true;
						} else {
							return false;
						}
					} catch(e) {
						console.log(userText, e);
					}
				}
				return false;
			},
			_sessionAuthUser: function(user) {
				var self = this;
				$http.get("/rest/user/auth").success(function(authUser) {
					if(!user) {
						self.setUser(authUser);
					} else if(user.email == authUser.email) {
						self.setUser(authUser);
					} else {
						self.logout();
					}
				}).error(function(error) {
					console.log(error);
                    self._loginPromise.reject();
				});
			},
			setUser: function(user) {
				this._user = user;
				if(this._storage == "local" && window.localStorage) {
					this._storeUser(localStorage, user);
				}
				if(this._storage == "session" && window.sessionStorage) {
					this._storeUser(sessionStorage, user);
				}
                if(user && this._loginPromise) {
                    this._loginPromise.resolve(user);
                } else if(this._loginPromise) {
                    this._loginPromise.reject();
                }
			},
			logout: function() {
                this.reset();
				this.setUser(null);
			},
			getUser: function() {
				return this._user;
			},
            isLoggedIn: function() {
                return this._loginPromise.promise;
            },
            reset: function() {
                this._loginPromise = $q.defer();
            },
			init: function() {
                this.reset();

				var userInSession = false;
				if(this._storage == "local" && window.localStorage) {
					if(this._loadUser(localStorage)) {
						userInSession = true;
					}
				} else if(this._storage == "session" && window.sessionStorage) {
					if(this._loadUser(sessionStorage)) {
						userInSession = true;
					}
				}
				if(this._sessionAuth && !userInSession) {
					this._sessionAuthUser();
				}
			}
		};
	}]);

});