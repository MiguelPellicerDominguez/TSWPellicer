class ChessMatch extends Match {
	constructor(response) {
		super(response);
	}
	
	colocarPiezas() {
		let color = "brown";
		for (let i=0; i<this.board.squares.length; i++) {
			for (let j=0; j<this.board.squares.length; j++) {
				let square = this.board.squares[i][j];
				
				this.board.squares[i][j] = {
					color : color,
					valor : square,
					imagen : null
				};
				
				color = color=="white" ? "brown" : "white";
				switch (this.board.squares[i][j].valor) {
					case 1 : 
						this.board.squares[i][j].imagen = "css/images/tb.png";
						break;
					case 2 : 
						this.board.squares[i][j].imagen = "css/images/cb.png";
						break;
					case -1 : 
						this.board.squares[i][j].imagen = "css/images/tn.png";
						break;
					case -2 : 
						this.board.squares[i][j].imagen = "css/images/cn.png";
					default:
						this.board.squares[i][j].imagen = null;
				}
			}
		}		
	}
}