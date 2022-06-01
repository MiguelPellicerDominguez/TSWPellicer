package edu.uclm.esi.tys2122.tictactoe;

import java.security.SecureRandom;

import org.json.JSONObject;
import org.springframework.http.HttpStatus;

import edu.uclm.esi.tys2122.model.Board;
import edu.uclm.esi.tys2122.model.Historial;
import edu.uclm.esi.tys2122.model.Match;
import edu.uclm.esi.tys2122.model.User;
import edu.uclm.esi.tys2122.services.HistoryService;

public class TictactoeMatch extends Match {
	
	private HistoryService history = new HistoryService();
	
	@Override
	protected Board newBoard() {
		return new TictactoeBoard();
	}

	@Override
	protected void checkReady() {
		this.ready = this.players.size()==2;
		if (this.ready)
			this.playerWithTurn = new SecureRandom().nextBoolean() ? this.players.get(0) : this.players.get(1);
	}

	public int getSquare(Integer x, Integer y) {
		TictactoeBoard board = (TictactoeBoard) this.getBoard();
		return board.getSquares()[x][y];
	}

	public void setSquare(Integer x, Integer y, int value) {
		TictactoeBoard board = (TictactoeBoard) this.getBoard();
		board.getSquares()[x][y]=value;
	}

	@Override
	public void move(String userId, JSONObject jsoMovimiento) throws Exception {
		if (this.filled())
			throw new Exception("La partida ya terminó");
		
		if (!this.getPlayerWithTurn().getId().equals(userId))
			throw new Exception("No es tu turno");
		
		Integer x = jsoMovimiento.getInt("x");
		Integer y = jsoMovimiento.getInt("y");
		
		if (this.getSquare(x, y)!=0)
			throw new Exception("Casilla ocupada");
		
		int value = this.getPlayerWithTurn()==this.getPlayers().get(0) ? 1 : 2;
		this.setSquare(x, y, value);
		
		checkWinner();
		
		if (this.filled() && this.winner==null) {
			this.draw = true;
		}
		else {
			this.playerWithTurn = this.getPlayerWithTurn()==this.getPlayers().get(0) ?
				this.getPlayers().get(1) : this.getPlayers().get(0);
		}
	}

	private boolean filled() {
		TictactoeBoard board = (TictactoeBoard) this.getBoard();
		int[][] squares = board.getSquares();
		for (int i=0; i<3; i++)
			for (int j=0; j<3; j++)
				if (squares[i][j]==0)
					return false;
		return true;
	}

	private void checkWinner() {
		TictactoeBoard board = (TictactoeBoard) this.getBoard();
		int[][] squares = board.getSquares();
		
		if (squares[0][0]!=0 && squares[0][0]==squares[0][1] && squares[0][1]==squares[0][2] ||
				squares[1][0]!=0 && squares[1][0]==squares[1][1] && squares[1][1]==squares[1][2] ||
				squares[2][0]!=0 && squares[2][0]==squares[2][1] && squares[2][1]==squares[2][2]) {
			this.winner = this.getPlayerWithTurn();
			
			anadirHistorialWinner();
			
			
		} else if (squares[0][0]!=0 && squares[0][0]==squares[1][0] && squares[1][0]==squares[2][0] ||
				squares[0][1]!=0 && squares[0][1]==squares[1][1] && squares[1][1]==squares[2][1] ||
				squares[1][2]==squares[1][2] && squares[2][1]==squares[2][2]) {
			this.winner = this.getPlayerWithTurn();
			
			anadirHistorialWinner();
			
			
		} else if (squares[0][0]!=0 && squares[0][0]==squares[1][1] && squares[1][1]==squares[2][2] ||
				squares[0][2]!=0 && squares[0][2]==squares[1][1] && squares[1][1]==squares[2][0]) {
			this.winner = this.getPlayerWithTurn();
			
			anadirHistorialWinner();
			
		}
		if (this.winner!=null) {
			this.looser = this.winner==this.players.get(0) ? this.players.get(1) : this.players.get(0);
			
			anadirHistorialLooser();
		}
	}
	
	private void anadirHistorialWinner() {
		Historial histoWinner = new Historial();
		histoWinner.setUsuario(this.winner.getName());
		histoWinner.setGame("Tres en raya");
		histoWinner.setWinner(true);
		histoWinner.setLooser(false);
		histoWinner.setEmpate(false);
		history.addHistorial(histoWinner);
	}
	
	private void anadirHistorialLooser() {
		Historial histoLooser = new Historial();
		histoLooser.setUsuario(this.looser.getName());
		histoLooser.setGame("Tres en raya");
		histoLooser.setWinner(false);
		histoLooser.setLooser(true);
		histoLooser.setEmpate(false);
		history.addHistorial(histoLooser);
	}

	public User getWinner() {
		return winner;
	}
	
	public User getLooser() {
		return looser;
	}
	
	public boolean isDraw() {
		return draw;
	}
}
