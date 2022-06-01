class Match {
	constructor(response) {
		this.response = response;
		this.id = response.id;
		this.ready = response.ready;
		this.players = response.players;
		this.playerWithTurn = response.playerWithTurn;
		this.winner = response.winner;
		this.looser = response.looser;
		this.draw = response.draw;
		this.board = response.board;
		this.game = response.game;
	}
}