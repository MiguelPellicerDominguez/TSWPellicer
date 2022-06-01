package edu.uclm.esi.tys2122.http;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.http.HttpSession;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

import com.github.bhlangonijr.chesslib.game.Player;

import edu.uclm.esi.tys2122.dao.HistorialRepository;
import edu.uclm.esi.tys2122.model.Game;
import edu.uclm.esi.tys2122.model.Match;
import edu.uclm.esi.tys2122.model.User;
import edu.uclm.esi.tys2122.websockets.WrapperSession;
import edu.uclm.esi.tys2122.tictactoe.TictactoeMatch;

@Component
public class Manager {
	
	@Autowired
    private HistorialRepository historyRepo;
	
	private Vector<Game> games;
	private JSONObject configuration;
	private ConcurrentHashMap<String, HttpSession> httpSessions; 
	private ConcurrentHashMap<String, WrapperSession> sessionsPorHttp;
	private ConcurrentHashMap<String, WrapperSession> sessionsPorWs;
	private ConcurrentHashMap<String, User> usersMap;

	private Manager() {
		this.games = new Vector<>();
		this.httpSessions = new ConcurrentHashMap<>();
		this.sessionsPorHttp = new ConcurrentHashMap<>();
		this.sessionsPorWs = new ConcurrentHashMap<>();
		try {
			loadParameters();
		} catch (Exception e) {
			System.err.println("Error al leer el fichero parametros.txt: " + e.getMessage());
			System.exit(-1);
		}
	}
	
	private static class ManagerHolder {
		static Manager singleton=new Manager();
	}
	
	@Bean
	public static Manager get() {
		return ManagerHolder.singleton;
	}

	public JSONObject getConfiguration() {
		return configuration;
	}
	
	private void loadParameters() throws IOException {
		this.configuration = read("./parametros.txt");
	}
	
	private JSONObject read(String fileName) throws IOException {
		 ClassLoader classLoader = getClass().getClassLoader();
		 try (InputStream fis = classLoader.getResourceAsStream(fileName)) {
			byte[] b = new byte[fis.available()];
			fis.read(b);
			String s = new String(b);
			return new JSONObject(s);
		 }
	}
	
	public JSONArray readFileAsJSONArray(String fileName) throws IOException {
		ClassLoader classLoader = getClass().getClassLoader();
		 try (InputStream fis = classLoader.getResourceAsStream(fileName)) {
			byte[] b = new byte[fis.available()];
			fis.read(b);
			String s = new String(b);
			return new JSONArray(s);
		 }
	}

	public void clearGames() {
		this.games.clear();
	}

	public void add(Game game) {
		this.games.add(game);
	}
	
	public Vector<Game> getGames() {
		return games;
	}

	public HistorialRepository getHistoryRepo() {
		return historyRepo;
	}
	
	public Game findGame(String gameName) {
		for (Game game : this.games)
			if (game.getName().equals(gameName))
				return game;
		return null;
	}
	
	public void add(HttpSession session) {
		this.httpSessions.put(session.getId(), session);
	}
	
	public void add(WrapperSession wrapperSession, String httpSessionId) {
		HttpSession httpSession = this.httpSessions.get(httpSessionId);
		User user = (User) httpSession.getAttribute("user");
		user.setSession(wrapperSession);
		wrapperSession.setHttpSession(httpSession);
		this.sessionsPorHttp.put(httpSessionId, wrapperSession);
		this.sessionsPorWs.put(wrapperSession.getWsSession().getId(), wrapperSession);	
	}
	
	public void desconexionPartida(WrapperSession wrapperSession, String httpSessionId) {
		HttpSession httpSession = this.httpSessions.get(httpSessionId);
		User user = (User) httpSession.getAttribute("user");
		Game game = (Game) httpSession.getAttribute("game");
		if (user.getSession() == wrapperSession && wrapperSession.getHttpSession() == httpSession) {
			Collection<Match> partidas = game.getPlayingMatches();
			
			if(game.getName().contains("Tres en raya")) {
				List<TictactoeMatch> auxTictactoeMatchs = new ArrayList<TictactoeMatch>();
				for (Match match : partidas) {
					auxTictactoeMatchs.add((TictactoeMatch) match);
				}
				for (TictactoeMatch matchTictactoe : auxTictactoeMatchs) {
					Vector<User> players = matchTictactoe.getPlayers();
					if (players.get(1) == user && matchTictactoe.getWinner() == null) {
						matchTictactoe.setWinner(players.get(1));
						matchTictactoe.setLooser(players.get(0));
						this.sessionsPorHttp.remove(httpSessionId, wrapperSession);
						this.sessionsPorWs.remove(wrapperSession.getWsSession().getId(), wrapperSession);
					}
				}
			}
		}
	}
	
	
	public WrapperSession findWrapperSession(WebSocketSession session) {
		return this.sessionsPorWs.get(session.getId());
	}
}
