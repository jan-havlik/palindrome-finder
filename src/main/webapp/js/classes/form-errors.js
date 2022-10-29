define("classes/form-errors",
	["classes/promise"]
, function(Promise) {

	var FormErrorHelper = function() {
	};

	/**
	 * vynuluje promenne pro zobrazovani chyb a upozorneni kolem formularu
	 * 
	 * @returns {undefined}
	 */
	FormErrorHelper.prototype.reset = function(form) {
		form.errors = [];			//seznam chyb pro zobrazeni ve strance
		form.success = false;

		form.showError = false;	//zobrazit modal s hlaskou
		form.errorMessageID = false;

		form.showConfirm = false;	//zobrazi modal s potvrzenim
		form.confirmMessageID = false;

		form.submitting = false;	//formular se odesila
	};

	/**
	 * zobrazi modalni okno
	 *
	 * @param {type} errorMessageID
	 * @returns {undefined}
	 */
	FormErrorHelper.prototype.modal = function(form, errorMessageID) {
		form.success = false;
		form.showError = true;
		form.errorMessageID = errorMessageID;
	};

	/**
	 * zobrazi modalni okno pro potvrzeni
	 *
	 * @param {type} confirmMessageID
	 * @returns {undefined}
	 */
	FormErrorHelper.prototype.modalConfirm = function(form, confirmMessageID) {
		form.success = false;
		form.showConfirm = true;
		form.confirmMessageID = confirmMessageID;

		var promise = new Promise(function(resolve, reject) {
			form.modalConfirm = resolve;
			form.modalCancel = reject;
		});
		return promise;
	};

	FormErrorHelper.prototype.success = function(form) {
		form.success = true;
		form.$setPristine();
	};

	/**
	 * prida error hlasku
	 *
	 * @param {type} message
	 * @returns {undefined}
	 */
	FormErrorHelper.prototype.error = function(form, message) {
		form.success = false;
		form.errors.push(message);
	};

	FormErrorHelper.prototype.disable = function(form) {
		form.success = false;
		form.submitting = true;
	};

	FormErrorHelper.prototype.enable = function(form) {
		form.success = false;
		form.submitting = false;
	};

	return FormErrorHelper;
});