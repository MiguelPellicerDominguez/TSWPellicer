 package edu.uclm.esi.tys2122.http;

import java.io.IOException;
import java.util.Map;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import edu.uclm.esi.tys2122.dao.TokenRepository;
import edu.uclm.esi.tys2122.model.Email;
import edu.uclm.esi.tys2122.model.Token;
import edu.uclm.esi.tys2122.model.User;
import edu.uclm.esi.tys2122.services.UserService;

@RestController
@RequestMapping("user")
public class UserController extends CookiesController {
	
	@Autowired
	private UserService userService;
	
	@Autowired   
	private TokenRepository tokenDAO; 
	
	@PostMapping(value = "/login")
	public void login(HttpServletRequest request, HttpServletResponse response, @RequestBody Map<String, Object> credenciales) {
		JSONObject jso = new JSONObject(credenciales);
		String name = jso.getString("name");
		String pwd = jso.getString("pwd");
		String ip = request.getRemoteAddr();
		
		User user = userService.doLogin(name, pwd, ip);
		
		Cookie cookie = readOrCreateCookie(request, response);
		userService.insertLogin(user, ip, cookie);
		Manager.get().add(request.getSession()); //Se almacena la sesion 
		
		request.getSession().setAttribute("userId", user.getId());
		request.getSession().setAttribute("user", user);
	}

	@PutMapping("/register")
	@ResponseBody
	public String register(@RequestBody Map<String, Object> credenciales) {
		JSONObject jso = new JSONObject(credenciales);
		String userName = jso.optString("userName");
		String email = jso.optString("email");
		String pwd1 = jso.optString("pwd1");
		String pwd2 = jso.optString("pwd2");
		String picture = jso.optString("picture");		
		
		if (!pwd1.equals(pwd2))
			throw new ResponseStatusException(HttpStatus.CONFLICT, "Error: las contraseñas no coinciden");
		if (pwd1.length()<4)
			throw new ResponseStatusException(HttpStatus.CONFLICT, "Error: la contraseña debe tener al menos cuatro caracteres");
		
		//pwd1 = org.apache.commons.codec.digest.DigestUtils.sha512Hex(pwd1); //pasar hash por aquí
		
		Token token = new Token(email);
		tokenDAO.save(token);
		
		User user = new User();
		user.setName(userName);
		user.setEmail(email);
		user.setPwd(pwd1);
		user.setPicture(picture);
		
		if(jso.has("confirmationDate")) 
			user.setConfirmationDate(System.currentTimeMillis()); //ver si es la fecha correcta
		
		userService.save(user);
		
		Email sender = new Email();
		sender.send(email, "Confirmacion de cuenta", "Ya se encuentra registrado en nuestro sistema, y puede iniciar sesión en el siguiente enlace: http://localhost:8000/?ojr=login");
		
		return "Te hemos enviado un correo para confirmar tu registro";
	}
	
	@DeleteMapping("/remove/{userId}")
	public void remove(@PathVariable String userId) {
		System.out.println("Borrar el usuario con id " + userId);		
	}
	
	@GetMapping("/validateAccount/{tokenId}")
	public void validateAccount(HttpServletRequest request, HttpServletResponse response, @PathVariable String tokenId) {
		userService.validateToken(tokenId);
		System.out.println(tokenId);
		try {
			response.sendRedirect(Manager.get().getConfiguration().getString("validateAccount"));
		} catch (IOException e) {
			
		}
	}
	
	@PostMapping("/recoveryPassword")
	public String recoveryPassword(HttpServletRequest request, HttpServletResponse response, @RequestBody Map<String, Object> info) {
		
		JSONObject jso = new JSONObject(info);
		String email = jso.optString("email");
		
		if (userService.buscarEmail(email)) {
			Token token = new Token(email);
			tokenDAO.save(token);
			
			Email sender = new Email();
			sender.send(email, "Cambio de contraseña", "Haz clic en " + "http://localhost/user/recoveryPassword" + token.getId());
		
			return "Te he enviado un correo para recuperar tu contraseña";
		}
		else
			return "Usuario inexistente en el sistema.";
	}
}
