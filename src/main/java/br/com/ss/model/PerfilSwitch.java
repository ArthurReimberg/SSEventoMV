/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package br.com.ss.model;

import java.util.Date;

/**
 *
 * @author Arthur
 */
public class PerfilSwitch {

    private int id = 0;
    private String nome = "";
    private int portaEntrada = 60;
    private String ipA = "127.0.0.1";
    private String ipB = "127.0.0.1";
    private int portaA = 60;
    private int portaB = 60;
    private boolean eventosA = true;
    private boolean eventosB = true;
    private String pass;
    private int status=0;
    private int expire=0;
    private Date data;

    public PerfilSwitch(String nome) {
        this.nome=nome;
    }  

    public PerfilSwitch() {
    }
    
    
    
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }    
    
    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public int getPortaEntrada() {
        return portaEntrada;
    }

    public void setPortaEntrada(int portaEntrada) {
        this.portaEntrada = portaEntrada;
    }

    public String getIpA() {
        return ipA;
    }

    public void setIpA(String ipA) {
        this.ipA = ipA;
    }

    public String getIpB() {
        return ipB;
    }

    public void setIpB(String ipB) {
        this.ipB = ipB;
    }

    public int getPortaA() {
        return portaA;
    }

    public void setPortaA(int portaA) {
        this.portaA = portaA;
    }

    public int getPortaB() {
        return portaB;
    }

    public void setPortaB(int portaB) {
        this.portaB = portaB;
    }

    public boolean getEventosA() {
        return eventosA;
    }

    public void setEventosA(boolean eventosA) {
        this.eventosA = eventosA;
    }

    public boolean getEventosB() {
        return eventosB;
    }

    public void setEventosB(boolean eventosB) {
        this.eventosB = eventosB;
    }

     public String getPass() {
        return pass;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }
    
    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getExpire() {
        return expire;
    }

    public void setExpire(int expire) {
        this.expire = expire;
    }

    public Date getData() {
        return data;
    }

    public void setData(Date data) {
        this.data = data;
    }
    
    
}
