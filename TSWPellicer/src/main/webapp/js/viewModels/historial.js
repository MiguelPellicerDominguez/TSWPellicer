define([ 'knockout', 'appController', 'ojs/ojmodule-element-utils', 'accUtils',
		'jquery' ], function(ko, app, moduleUtils, accUtils, $) {

	class MenuViewModel {
		constructor() {
			var self = this;

            self.ishistorial = ko.observable(null);
            self.historiales = ko.observableArray([]);

			self.error = ko.observable(null);
						
			// Header Config
			self.headerConfig = ko.observable({
				'view' : [],
				'viewModel' : null
			});
			moduleUtils.createView({
				'viewPath' : 'views/header.html'
			}).then(function(view) {
				self.headerConfig({
					'view' : view,
					'viewModel' : app.getHeaderModel()
				})
			});
		}

		connected() {
			this.conseguirHistoriales();
		};

		conseguirHistoriales() {
			let self = this;

			let data = {
				type : "get",
				url : "history/",
				success : function(response) {
					console.log(JSON.stringify(response));	
					self.historiales(response);
                    self.ishistorial(true);
				},
				error : function(response) {
					console.error(response.responseJSON.message);
					
					if (response.responseJSON.message == "No message available") {
                        self.error("El usuario no se encuentra logeado");
                    } else {
                        self.error(response.responseJSON.message);
                    }

 					setTimeout(() => {
                        self.error("");
                    }, 5000);
				}
			};
			$.ajax(data);
		}

		disconnected() {
			// Implement if needed
		};

		transitionCompleted() {
			// Implement if needed
		};
	}

	return MenuViewModel;
});
