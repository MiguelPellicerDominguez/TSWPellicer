package edu.uclm.esi.tys2122.http;

import java.security.SecureRandom;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import edu.uclm.esi.tys2122.dao.UserRepository;
import edu.uclm.esi.tys2122.model.Game;
import edu.uclm.esi.tys2122.model.Match;
import edu.uclm.esi.tys2122.model.User;
import edu.uclm.esi.tys2122.services.GamesService;
import edu.uclm.esi.tys2122.services.UserService;

@RestController
@RequestMapping("games")
public class GamesController extends CookiesController {
	@Autowired
	private GamesService gamesService;
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private UserRepository userRepo;
	
	@GetMapping("/getGames")
	public List<Game> getGames(HttpSession session) throws Exception {
		return gamesService.getGames();
	}
	
	
	@GetMapping("/getUsuarioConectado")
	public User getUsuarioConectado(HttpSession session) throws Exception {
		User user = null;
		if (session.getAttribute("userId") != null) {
			String userId = session.getAttribute("userId").toString();
			user = this.userService.findUser(userId);
		}
		return user;
	}
	

	@GetMapping("/joinGame/{gameName}")
	public Match joinGame(HttpSession session, @PathVariable String gameName) {
		User user;
		
		if (session.getAttribute("userId")!=null) {
			String userId = session.getAttribute("userId").toString();
			user = this.userService.findUser(userId);
		} else {
			user = new User();
			user.setName("anonimo" + new SecureRandom().nextInt(1000));
			user.setEmail(user.getName() + "@" + user.getName() + ".com");
			user.setPwd("1234");
			session.setAttribute("userId", user.getId());
			userRepo.save(user);
		}

		Game game = Manager.get().findGame(gameName);
		if (game==null)
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No se encuentra el juego " + gameName);
		
		Match match = getMatch(game);
		match.addPlayer(user);
		if (match.isReady()) {
			game.getPendingMatches().remove(match);
			game.getPlayingMatches().add(match);
		}
		gamesService.put(match);
		return match;
	}
	
	@PostMapping("/move")
	public Match move(HttpSession session, @RequestBody Map<String, Object> movement) {
		String userId = session.getAttribute("userId").toString();
		JSONObject jso = new JSONObject(movement);
		Match match = gamesService.getMatch(jso.getString("matchId"));
		try {
			match.move(userId, jso);
			return match;
		} catch (Exception e) {
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, e.getMessage());
		}
	}
	
	@GetMapping("/findMatch/{matchId}")
	public Match findMatch(@PathVariable String matchId) {
		return gamesService.getMatch(matchId);
	}

	
	
	@PostMapping("/disconnected")
	public Match partidaAbandonada(HttpSession session, @RequestBody Map<String, Object> response) {
		User user;
		String userId = session.getAttribute("userId").toString();
		user= this.userService.findUser(userId);
		JSONObject jso = new JSONObject(response);
		Match match = gamesService.getMatch(jso.getString("matchId"));
		match.setTerminada(true);
		match.setLooser(user);	
		return match;
		
	}
	
	
	
	private Match getMatch(Game game) {
		Match match;
		if (game.getPendingMatches().isEmpty()) {
			match = game.newMatch();
			game.getPendingMatches().add(match);
		} else {
			match = game.getPendingMatches().get(0);
		}
		return match;
	}
	
	
}
