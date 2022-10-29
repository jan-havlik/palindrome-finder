define("classes/promise", function() {

	/**
	 * vlastni jednoducha implementace Promise vzoru
	 *
	 * @param {type} callback
	 * @returns {promise_L1.Promise}
	 */
	var Promise = function(callback) {
		this._resolveCallback = null;
		this._rejectCallback = null;

		var self = this;
		var resolve = function() {
			if(self._resolveCallback) {
				self._resolveCallback.apply(this, arguments);
			}
		};
		var reject = function() {
			if(self._rejectCallback) {
				self._rejectCallback.apply(this, arguments);
			}
		};
		callback(resolve, reject);
	};

	Promise.prototype.then = function(resolve, reject) {
		this._resolveCallback = resolve;
		this._rejectCallback = reject;
	};

	return Promise;

});