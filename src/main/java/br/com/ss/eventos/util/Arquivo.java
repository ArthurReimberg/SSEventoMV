/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package br.com.ss.eventos.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.PrintStream;

public class Arquivo {

    public static void salvarArquivo(String config) {
        try {
            String nomeArquivo = "config.ss";
            File arquivoTXT = new File(System.getProperty("user.home") + "/LEGENDA/" + nomeArquivo);
            arquivoTXT.getParentFile().mkdirs();
            arquivoTXT.createNewFile();
            PrintStream saida = new PrintStream(arquivoTXT, "windows-1252");
            saida.println(config);
            saida.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String ler() {
        try {
            String nomeArquivo = "config.ss";
            File arquivoTXT = new File(System.getProperty("user.home") + "/LEGENDA/" + nomeArquivo);
            BufferedReader br = new BufferedReader(new FileReader(arquivoTXT));
            String aux = null;
            String total = "";
            while ((aux = br.readLine()) != null) {
                total = total + aux + "\n";
            }
            br.close();
            return total;
        } catch (Exception e) {
            return "error";
        }
    }
}
