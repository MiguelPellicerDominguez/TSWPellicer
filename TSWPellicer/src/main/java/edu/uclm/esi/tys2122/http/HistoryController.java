package edu.uclm.esi.tys2122.http;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import edu.uclm.esi.tys2122.model.Historial;
import edu.uclm.esi.tys2122.model.User;
import edu.uclm.esi.tys2122.services.HistoryService;
import edu.uclm.esi.tys2122.services.UserService;

@RestController
@RequestMapping("history")
public class HistoryController extends CookiesController{

    @Autowired
	private UserService userService;

    @Autowired
	private HistoryService historyService;
	
    @GetMapping("/")
	public List<Historial> getHistory(HttpServletRequest request) {
    	//cogemos el usuario del cual se va ver el historial
    	User user = (User) request.getSession().getAttribute("user");
    	
    	if (userService.findUser(user.getId()) != null) {
    		List<Historial> lhistory = historyService.findHistorial(user.getName());
    		if (lhistory != null)
    	    	return lhistory;
    		else
    			throw new ResponseStatusException(HttpStatus.CONFLICT, "Error: No hay un historial existente .");
    	} else 
			throw new ResponseStatusException(HttpStatus.CONFLICT, "Error: usuario no registrado en nuestro sistema");
    }
}
