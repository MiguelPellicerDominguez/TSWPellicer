package edu.uclm.esi.tys2122.model;

import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;

@Entity
@Table(name = "historial")
public class Historial {
    
    @Id
    @Column(length = 36)
    private String id;

    @NotBlank
    private String usuario;
    
    private String game;

    private boolean winner;

    private boolean looser;
    
    private boolean empate;
    
    public Historial() {
        this.id = UUID.randomUUID().toString();
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public String getGame() {
        return game;
    }

    public void setGame(String game) {
        this.game = game;
    }

    public boolean isEmpate() {
        return empate;
    }

    public void setEmpate(boolean empate) {
        this.empate = empate;
    }

    public boolean isLooser() {
        return looser;
    }

    public void setLooser(boolean looser2) {
        this.looser = looser2;
    }

    public boolean isWinner() {
        return winner;
    }

    public void setWinner(boolean winner) {
        this.winner = winner;
    }

    public String getId() {
        return id;
    }

}
