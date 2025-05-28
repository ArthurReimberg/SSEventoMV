package br.com.ss.eventos;

import java.awt.Color;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.WindowEvent;
import java.awt.event.WindowStateListener;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JFrame;

/**
 *
 * @author Arthur
 */
public class FrmTelaoExterna extends javax.swing.JFrame {

    private String texto_evento = new String();
    private FormConexaoExterna monitoracao;

    private int numeroLinhas = 0;
    private String aux = "";

    public FrmTelaoExterna(final FormConexaoExterna monitoracao) {
        this.monitoracao = monitoracao;

        initComponents();

        this.setTitle("LiveCaption");

        Image image = Toolkit.getDefaultToolkit().getImage(getClass().getResource("/img/ss.jpg"));
        ImageIcon icon = new ImageIcon(image);
        this.setIconImage(icon.getImage());

        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
      
        tpTexto.getCaret().setBlinkRate(0);

        this.getContentPane().setBackground(Color.BLACK);

    }

    public void addTexto(String texto) {
        try {

            tpTexto.setText(tpTexto.getText() + texto);

            tpTexto.setCaretPosition(tpTexto.getDocument().getLength());

        } catch (Exception e) {
            tpTexto.setText(tpTexto.getText() + texto);
        }

    }

    public void diminuirCaracteres() {
        tpTexto.setText(tpTexto.getText().substring(100));
    }

    public void limparTela() {
        tpTexto.setText("");
    }

    public void setTamanhoLetra(Font f) {
        tpTexto.setFont(f);
    }

    public void setCorLetra(Color c) {
        tpTexto.setForeground(c);
    }

    public void setCorFundo(Color c) {
        tpTexto.setBackground(c);
        this.getContentPane().setBackground(c);
    }

    public void setBorder(int esquerda, int direita) {
        tpTexto.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(Color.BLACK),
                BorderFactory.createEmptyBorder(0, esquerda, 0, direita)));
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane2 = new javax.swing.JScrollPane();
        tpTexto = new javax.swing.JTextArea();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        tpTexto.setBackground(new java.awt.Color(1, 1, 1));
        tpTexto.setColumns(20);
        tpTexto.setForeground(new java.awt.Color(255, 253, 0));
        tpTexto.setLineWrap(true);
        tpTexto.setRows(5);
        jScrollPane2.setViewportView(tpTexto);

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jScrollPane2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 626, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jScrollPane2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 463, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTextArea tpTexto;
    // End of variables declaration//GEN-END:variables
}
