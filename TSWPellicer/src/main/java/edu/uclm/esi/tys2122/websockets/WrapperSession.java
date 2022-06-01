package edu.uclm.esi.tys2122.websockets;

import javax.servlet.http.HttpSession;

import org.springframework.web.socket.WebSocketSession;

import edu.uclm.esi.tys2122.model.User;

public class WrapperSession {
	
	private WebSocketSession wsSession;
	private HttpSession httpSession;

	public WrapperSession(WebSocketSession wsSession) {
		this.wsSession = wsSession;
	}

	public void setHttpSession(HttpSession httpSession) {
		this.httpSession = httpSession;
	}

	public HttpSession getHttpSession() {
		return httpSession;
	}
	
	public WebSocketSession getWsSession() {
		return wsSession;
	}

    public User getUser() {
        return (User) httpSession.getAttribute("user");
    }

}
