package edu.uclm.esi.tys2122.services;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import edu.uclm.esi.tys2122.http.Manager;
import edu.uclm.esi.tys2122.model.Historial;


@Service
public class HistoryService {
	
	public HistoryService() {
		
	}

	public void addHistorial(Historial history){
		//Obtener el historial antes de savearlo dentro del repository de los historial 
		//para que lo pueda redirigirlo a la BBDD 
		Manager.get().getHistoryRepo().save(history);
	}
	
    public List<Historial> findHistorial(String userName) {
    	//mostrar el id del username que le corresponde 
        List<Historial> lhistory = new ArrayList<Historial>();
        for (Historial historial : Manager.get().getHistoryRepo().findAll()) {
            if(historial.getUsuario().equals(userName)){
                lhistory.add(historial);
            }
        }
        return lhistory;
    }
	
}
