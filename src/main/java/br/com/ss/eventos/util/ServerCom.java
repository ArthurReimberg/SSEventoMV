/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package br.com.ss.eventos.util;

import com.mashape.unirest.http.Unirest;

/**
 *
 * @author Arthur
 */
public class ServerCom {


    public static String sendPOST(String url, String body) {
        try {
            return Unirest.post("http://" + Preferencias.urlServidor + url)
                    .basicAuth("ssDataAPIUser", "ssDataAPIP@ss22")
                    .header("Content-Type", "application/json")
                    .body(body).asString().getBody();
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
            return "";
        }
    }

    public static String sendGET(String url, String body) {
        try {
            return Unirest.get("http://" + Preferencias.urlServidor + url)
                    .basicAuth("ssDataAPIUser", "ssDataAPIP@ss22")
                    .header("Content-Type", "application/json")
                    .asString().getBody();
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
            return "";
        }
    }

    public static void main(String args[]) {
        try {
            System.out.println(sendGET("/user/all", ""));

        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }
    }
}
