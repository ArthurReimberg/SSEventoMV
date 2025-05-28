/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package br.com.ss.eventos.dao;

import br.com.ss.eventos.util.ServerCom;
import br.com.ss.model.PerfilSwitch;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 *
 * @author Arthur
 */
public class LoginDAO {

    private ObjectMapper mapper = new ObjectMapper();

    public PerfilSwitch autenticar(String pass) { 
        try {
            return mapper.readValue(ServerCom.sendGET("/perfilSwitch/validate?pass=" + pass, ""),
                            PerfilSwitch.class);
        } catch (Exception e) {
            return new PerfilSwitch();
        }
    }

}
