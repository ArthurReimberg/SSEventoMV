/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package br.com.ss.eventos.util;

/**
 *
 * @author Arthur
 */
public class CCPack {
    
    private String legenda="";
    private String lingua="";

    public CCPack(String legenda, String lingua) {
        this.lingua=lingua;
        this.legenda=legenda;
    }

    public CCPack() {
    }
    
    
    
    

    public String getLegenda() {
        return legenda;
    }

    public void setLegenda(String legenda) {
        this.legenda = legenda;
    }

    public String getLingua() {
        return lingua;
    }

    public void setLingua(String lingua) {
        this.lingua = lingua;
    }
    
    
}
