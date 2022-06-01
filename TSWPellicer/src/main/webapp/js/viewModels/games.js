define([ 'knockout', 'appController', 'ojs/ojmodule-element-utils', 'accUtils',
		'jquery' ], function(ko, app, moduleUtils, accUtils, $) {

	class MenuViewModel {
		constructor() {
			let self = this;

			self.games = ko.observableArray([]);
			self.matches = ko.observableArray([]);
			self.error = ko.observable(null);
/*			self.nombreUsuarioLogeado = ko.observable(nUsuarioLogeado());*/		
/*			self.idUser = ko.observable('Bienvenido');*/
			self.x = ko.observable(null);
			self.y = ko.observable(null);
						
						
/*			function nUsuarioLogeado(){
				var usuarioLogeado = localStorage.getItem("login");
				if (usuarioLogeado == null)
					return 'invitado';
				else
					return localStorage.getItem("nombre");
			}*/
						
						
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

		mover(match) {
			let self = this;

			let info = {
				x : self.x(),
				y : self.y(),
				matchId : match.id
			}

			let data = {
				type : "post",
				url : "/games/move",
				contentType : 'application/json',
				data : JSON.stringify(info),
				success : function(response) {

				},
				error : function(response) {

				}
			}
			$.ajax(data);
		}

		connected() {
			accUtils.announce('Juegos.');
			document.title = "Juegos";

			let self = this;

			let data = {
				type : "get",
				url : "/games/getGames",
				success : function(response) {
					self.games(response);
				},
				error : function(response) {
					console.error(response.responseJSON.message);
					self.error(response.responseJSON.message);
				}
			}
			$.ajax(data);
			
			
/*			let data2 = {
				type: "get",
				url: "/games/getUsuarioConectado",
				
				success: function (response){
					console.log(response);
					
					if (localStorage.getItem("nombre") != null && (response.name == localStorage.getItem("nombre"))){
						console.log(response.name);
						console.log(localStorage.getItem("nombre"));
						
						let btnVerHistorial = document.createElement("button");
						btnVerHistorial.innerHTML = "Historial Partidas";
						
						let btnCerrarSesion = document.createElement("button");
						btnCerrarSesion.innerHTML = "Cerrar Sesión";
						
						btnVerHistorial.onclick = function(){
							alert("Historial de Partidas del usuario: " + response.name);
							app.router.go({ path: "historial"});
						} 
						
						btnCerrarSesion.onclick = function(){
							alert("Sesion del Usuario " + response.name+ "Cerrada.");
							app.router.go({ path: "login"});
						}
						
						var btnHistorialPartidas = document.getElementById("btnHistorialPartidas");
						btnHistorialPartidas.innerHTML = "";
						btnHistorialPartidas.appendChild(btnVerHistorial);
						
						var btnSesionCerrar = document.getElementById("btnSesionCerrar");
						btnSesionCerrar.innerHTML = "";
						btnSesionCerrar.appendChild(btnCerrarSesion);
						
					}		
				},
				error: function (response) {
					console.error(response.responseJSON.message);
					self.error(response.responseJSON.message);
				}	
			}
			$.ajax(data2);	*/
		};

		joinGame(game) {
			let self = this;

			let data = {
				type : "get",
				url : "/games/joinGame/" + game.name,
				success : function(response) {
					let match;
					
					if (response.game == "TictactoeMatch") {
						match = new TictactoeMatch(response);
					}
					self.matches.push(match);
					console.log(JSON.stringify(response));
				},
				error : function(response) {
					console.error(response.responseJSON.message);
					self.error(response.responseJSON.message);
				}
			};
			$.ajax(data);
		}

		reload(match) {
			let self = this;

			let data = {
				type : "get",
				url : "/games/findMatch/" + match.id,
				success : function(response) {
					for (let i=0; i<self.matches().length; i++)
						if (self.matches()[i].id==match.id) 
							self.matches.splice(i, 1, response);
					console.log(JSON.stringify(response));
				},
				error : function(response) {
					console.error(response.responseJSON.message);
					self.error(response.responseJSON.message);
				}
			};
			$.ajax(data);
		}

		disconnected() {
			console.log("Abandonando partida..");
			app.router.go({ path: "login" });
		};

		transitionCompleted() {
			// Implement if needed
		};
	}

	return MenuViewModel;
});
