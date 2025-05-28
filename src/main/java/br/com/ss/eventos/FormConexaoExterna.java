/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.ss.eventos;

import br.com.ss.eventos.util.Arquivo;
import br.com.ss.eventos.util.CCPack;
import br.com.ss.model.PerfilSwitch;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Calendar;
import java.util.Date;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JColorChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JScrollBar;
import javax.swing.UIDefaults;
import javax.swing.UIManager;
import static javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.text.DefaultCaret;
import org.apache.commons.codec.binary.Hex;

/**
 *
 * @author Arthur
 */
public class FormConexaoExterna extends javax.swing.JFrame {

    private Font[] allFonts;
    private Dimension tamanhoInicial;
    private Color letra = Color.yellow;
    private Color fundo = Color.BLACK;
    private JFrame frame = this;

    private StringBuilder textoMemoria = new StringBuilder();
    private int cont = 1;

    private Socket accept;
    private BufferedReader br;
    private String aux_simple = "";

    private int maxCaracteres = 500;

    private MapaString mapaString = new MapaString();

    private FrmTelaoExterna frmTelaoExterna;

    private Date atual;

    private Date chegada;

    private long seconds;
    
    private PerfilSwitch perfilSwitch;

    public FormConexaoExterna(PerfilSwitch perfilSwitch) {
        this.perfilSwitch = perfilSwitch;

        initComponents();

        this.getContentPane().setBackground(new Color(153, 204, 255));

        Dimension windowSize = this.getSize();
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        Point centerPoint = ge.getCenterPoint();

        int dx = centerPoint.x - windowSize.width / 2;
        int dy = centerPoint.y - windowSize.height / 2;

        this.setLocation(dx, dy);

        this.setTitle("LiveCaption - ID " + perfilSwitch.getPass());

        Image image = Toolkit.getDefaultToolkit().getImage(getClass().getResource("/img/ss.jpg"));
        ImageIcon icon = new ImageIcon(image);
        this.setIconImage(icon.getImage());

        taSaida.setFont(new Font("Verdana", Font.PLAIN, new Integer(15)));

        allFonts = GraphicsEnvironment.getLocalGraphicsEnvironment().getAllFonts();
        for (int i = 0; i < allFonts.length; i++) {
            spFontes.addItem(allFonts[i].getFontName());
        }

        frmTelaoExterna = new FrmTelaoExterna(this);

        spEsquerda.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                frmTelaoExterna.setBorder(Integer.parseInt(spEsquerda.getValue().toString()),
                        Integer.parseInt(spDireita.getValue().toString()));

                taSaida.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(fundo),
                        BorderFactory.createEmptyBorder(0, Integer.parseInt(spEsquerda.getValue().toString()), 0, Integer.parseInt(spDireita.getValue().toString()))));
            }
        });

        spDireita.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                frmTelaoExterna.setBorder(Integer.parseInt(spEsquerda.getValue().toString()),
                        Integer.parseInt(spDireita.getValue().toString()));

                taSaida.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(fundo),
                        BorderFactory.createEmptyBorder(0, Integer.parseInt(spEsquerda.getValue().toString()), 0, Integer.parseInt(spDireita.getValue().toString()))));
            }
        });

        spLetra.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                taSaida.setFont(new Font(spFontes.getSelectedItem().toString(), Font.PLAIN, Integer.parseInt(spLetra.getValue().toString())));
                frmTelaoExterna.setTamanhoLetra(new Font(spFontes.getSelectedItem().toString(), Font.PLAIN, Integer.parseInt(spLetra.getValue().toString())));
            }
        });

        spFontes.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                taSaida.setFont(new Font(spFontes.getSelectedItem().toString(), Font.PLAIN, Integer.parseInt(spLetra.getValue().toString())));
                frmTelaoExterna.setTamanhoLetra(new Font(spFontes.getSelectedItem().toString(), Font.PLAIN, Integer.parseInt(spLetra.getValue().toString())));
            }
        });

        this.setVisible(true);

        try {
            spFontes.setSelectedItem("Arial");
        } catch (Exception e) {
        }

        this.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                try {
                    desconectar();
                    salvarStyle();
                    System.exit(0);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }

        });

        DefaultCaret caret = (DefaultCaret) taSaida.getCaret();
        caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);

        taSaida.setText("Teste de Closed Caption.\n"
                + ">>Por favor aguarde, iniciaremos\n"
                + "nossa transmissão em breve.\n");

        iniciar();
        threadTimeout();
        buscarInfos();

        UIDefaults defaults = new UIDefaults();
        defaults.put("EditorPane[Enabled].backgroundPainter", fundo);
        taSaida.putClientProperty("Nimbus.Overrides", defaults);
        taSaida.putClientProperty("Nimbus.Overrides.InheritDefaults", true);
        
    }
    String aux = "";

    private void addTexto(String text) {
        try {
            
            piscar();
            text = text.replaceAll("%-P", "\n");
            if (cbDuplo.isSelected()) {
                text = text.replaceAll(" ", "  ");
                char[] toCharArray = text.toCharArray();
                for (int i = 0; i < toCharArray.length; i++) {
                    aux += " " + toCharArray[i];
                }
                text = aux;
                aux = "";
            }

            if (cbLetra.getSelectedIndex() == 1) {
                text = text.toLowerCase();
            } else if (cbLetra.getSelectedIndex() == 2) {
                text = text.toUpperCase();
            }

            taSaida.setText(taSaida.getText() + text);

            if (cbCaracteres.isSelected() && taSaida.getText().length() > maxCaracteres) {
                taSaida.setText(taSaida.getText().substring(100));
                frmTelaoExterna.diminuirCaracteres();
            }

            frmTelaoExterna.addTexto(text);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void buscarInfos() {
        String[] split = Arquivo.ler().split("\n");
        if (split.length != 6) {
            return;
        }
        this.spDireita.setValue(Integer.valueOf(Integer.parseInt(split[0])));
        this.spEsquerda.setValue(Integer.valueOf(Integer.parseInt(split[1])));
        String fonte = split[4];
        for (int i = 0; i < this.allFonts.length; i++) {
            if (this.allFonts[i].getName().equals(fonte)) {
                this.spFontes.setSelectedIndex(i);
            }
        }
        this.spLetra.setValue(Integer.valueOf(Integer.parseInt(split[5])));
        this.fundo = new Color(Integer.parseInt(split[3]));
        this.letra = new Color(Integer.parseInt(split[2]));
        this.btnCorFundo.setBackground(this.fundo);
        this.taSaida.setBackground(this.fundo);
        this.frmTelaoExterna.setCorFundo(this.fundo);
        this.btnCorLetra.setBackground(this.letra);
        this.taSaida.setForeground(this.letra);
        this.frmTelaoExterna.setCorLetra(this.letra);
        this.taSaida.setFont(new Font(this.spFontes.getSelectedItem().toString(), 0, Integer.parseInt(this.spLetra.getValue().toString())));
    }

    private void salvarStyle() {
        try {
            String config = "";
            config = config + this.spDireita.getValue().toString() + "\n";
            config = config + this.spEsquerda.getValue().toString() + "\n";
            config = config + this.letra.getRGB() + "\n";
            config = config + this.fundo.getRGB() + "\n";
            config = config + this.spFontes.getSelectedItem().toString() + "\n";
            config = config + this.spLetra.getValue().toString() + "\n";
            Arquivo.salvarArquivo(config);
        } catch (Exception exception) {
        }
    }

    private void threadTimeout() {
        (new Thread(new Runnable() {
            public void run() {
                try {
                    while (true) {
                        if (chegada != null) {
                            atual = new Date(Calendar.getInstance().getTimeInMillis());
                            seconds = (atual.getTime() - chegada.getTime()) / 1000L;
                            if (seconds >= 15L) {
                                desconectar();
                                rodando = false;
                                piscar();
                            }
                        }
                        Thread.sleep(3000L);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    return;
                }
            }
        })).start();
    }

    private void piscar() {
        (new Thread(new Runnable() {
            public void run() {
                try {
                    FormConexaoExterna.this.chegada = new Date(Calendar.getInstance().getTimeInMillis());
                    FormConexaoExterna.this.lbDATA.setBackground(Color.GREEN);
                    Thread.sleep(100L);
                    FormConexaoExterna.this.lbDATA.setBackground(Color.red);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        })).start();
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        pConfigs = new javax.swing.JPanel();
        jLabel8 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jLabel7 = new javax.swing.JLabel();
        btnCorLetra = new javax.swing.JButton();
        jLabel6 = new javax.swing.JLabel();
        btnCorFundo = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        spEsquerda = new javax.swing.JSpinner();
        jLabel3 = new javax.swing.JLabel();
        spDireita = new javax.swing.JSpinner();
        jLabel1 = new javax.swing.JLabel();
        spLetra = new javax.swing.JSpinner();
        jLabel5 = new javax.swing.JLabel();
        spFontes = new javax.swing.JComboBox();
        jLabel4 = new javax.swing.JLabel();
        cbLetra = new javax.swing.JComboBox<>();
        jPanel3 = new javax.swing.JPanel();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        lbStatus = new javax.swing.JLabel();
        cbDuplo = new javax.swing.JCheckBox();
        jPanel1 = new javax.swing.JPanel();
        cbCaracteres = new javax.swing.JCheckBox();
        spCaracteres = new javax.swing.JSpinner();
        lbConfig = new javax.swing.JLabel();
        lbDATA = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        taSaida = new javax.swing.JEditorPane();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        pConfigs.setBackground(new java.awt.Color(255, 255, 255));
        pConfigs.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)), "Configurações"));

        jLabel8.setBackground(new java.awt.Color(255, 255, 255));
        jLabel8.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel8.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/LOGOSS.jpg"))); // NOI18N
        jLabel8.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jLabel8.setOpaque(true);
        jLabel8.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel8MouseClicked(evt);
            }
        });

        jPanel2.setBackground(new java.awt.Color(255, 255, 255));
        jPanel2.setLayout(new java.awt.GridLayout(7, 2, 3, 3));

        jLabel7.setText("Cor Letra");
        jPanel2.add(jLabel7);

        btnCorLetra.setText("Escolher");
        btnCorLetra.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCorLetraActionPerformed(evt);
            }
        });
        jPanel2.add(btnCorLetra);

        jLabel6.setText("Cor Fundo");
        jPanel2.add(jLabel6);

        btnCorFundo.setText("Escolher");
        btnCorFundo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCorFundoActionPerformed(evt);
            }
        });
        jPanel2.add(btnCorFundo);

        jLabel2.setText("Margem Esquerda");
        jPanel2.add(jLabel2);

        spEsquerda.setModel(new javax.swing.SpinnerNumberModel(0, 0, null, 2));
        jPanel2.add(spEsquerda);

        jLabel3.setText("Margem Direita");
        jPanel2.add(jLabel3);

        spDireita.setModel(new javax.swing.SpinnerNumberModel(0, 0, null, 2));
        jPanel2.add(spDireita);

        jLabel1.setText("Tamanho Letra");
        jPanel2.add(jLabel1);

        spLetra.setValue(20);
        spLetra.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                spLetraStateChanged(evt);
            }
        });
        jPanel2.add(spLetra);

        jLabel5.setText("Fonte");
        jPanel2.add(jLabel5);

        spFontes.setMaximumSize(new java.awt.Dimension(200, 32767));
        jPanel2.add(spFontes);

        jLabel4.setText("Estilo");
        jPanel2.add(jLabel4);

        cbLetra.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Normal", "Minuscula", "Maiuscula" }));
        jPanel2.add(cbLetra);

        jPanel3.setBackground(new java.awt.Color(255, 255, 255));
        jPanel3.setLayout(new java.awt.GridLayout(3, 2, 2, 2));

        jButton1.setText("+ LINHAS");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });
        jPanel3.add(jButton1);

        jButton2.setText("Reconectar");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });
        jPanel3.add(jButton2);

        lbStatus.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        lbStatus.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lbStatus.setText("STATUS");
        lbStatus.setOpaque(true);
        jPanel3.add(lbStatus);

        cbDuplo.setText("Espaços Duplos");
        jPanel3.add(cbDuplo);

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));
        jPanel1.setLayout(new java.awt.GridLayout(1, 0, 5, 5));

        cbCaracteres.setBackground(new java.awt.Color(255, 255, 255));
        cbCaracteres.setSelected(true);
        cbCaracteres.setText("MaxCaracteres");
        jPanel1.add(cbCaracteres);

        spCaracteres.setModel(new javax.swing.SpinnerNumberModel(500, 200, 2000, 50));
        spCaracteres.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                spCaracteresStateChanged(evt);
            }
        });
        jPanel1.add(spCaracteres);

        jPanel3.add(jPanel1);

        javax.swing.GroupLayout pConfigsLayout = new javax.swing.GroupLayout(pConfigs);
        pConfigs.setLayout(pConfigsLayout);
        pConfigsLayout.setHorizontalGroup(
            pConfigsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pConfigsLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, 277, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pConfigsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        pConfigsLayout.setVerticalGroup(
            pConfigsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pConfigsLayout.createSequentialGroup()
                .addContainerGap(13, Short.MAX_VALUE)
                .addGroup(pConfigsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, 175, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(pConfigsLayout.createSequentialGroup()
                        .addComponent(jLabel8)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, 67, javax.swing.GroupLayout.PREFERRED_SIZE))))
        );

        lbConfig.setBackground(new java.awt.Color(255, 255, 255));
        lbConfig.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/showMenor.png"))); // NOI18N
        lbConfig.setText("Esconder Configurações");
        lbConfig.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        lbConfig.setOpaque(true);
        lbConfig.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lbConfigMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                lbConfigMouseEntered(evt);
            }
        });

        lbDATA.setBackground(new java.awt.Color(255, 255, 255));
        lbDATA.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        lbDATA.setText(" DADOS ");
        lbDATA.setOpaque(true);

        jScrollPane2.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);

        taSaida.setBackground(new java.awt.Color(0, 0, 0));
        taSaida.setForeground(new java.awt.Color(255, 255, 51));
        jScrollPane2.setViewportView(taSaida);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                            .addComponent(lbConfig, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(lbDATA))
                        .addComponent(pConfigs, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addGap(0, 9, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 109, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lbDATA, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lbConfig))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(pConfigs, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jLabel8MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel8MouseClicked
        // TODO add your handling code here:
        int showConfirmDialog = JOptionPane.showConfirmDialog(null, "Deseja alterar o servidor?");
        if (showConfirmDialog == 0) {
            String ipNovo = JOptionPane.showInputDialog("IP para conexão:");
            if (!ipNovo.isEmpty()) {
               perfilSwitch.setIpA(ipNovo);
            }
        }
    }//GEN-LAST:event_jLabel8MouseClicked

    private void btnCorLetraActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCorLetraActionPerformed
        // TODO add your handling code here:
        try {
            letra = JColorChooser.showDialog(this, "Escolha a cor da letra", letra);
            if (letra == null) {
                letra = Color.yellow;
            }

            btnCorLetra.setBackground(letra);
            taSaida.setForeground(letra);
            frmTelaoExterna.setCorLetra(letra);

        } catch (Exception e) {
        }
    }//GEN-LAST:event_btnCorLetraActionPerformed

    private void btnCorFundoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCorFundoActionPerformed
        // TODO add your handling code here:
        try {
            fundo = JColorChooser.showDialog(this, "Escolha a cor do fundo", fundo);
            if (fundo == null) {
                fundo = Color.black;
            }

            btnCorFundo.setBackground(fundo);

            taSaida.setBackground(fundo);

            frmTelaoExterna.setCorFundo(fundo);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }//GEN-LAST:event_btnCorFundoActionPerformed

    private void spLetraStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_spLetraStateChanged
        // TODO add your handling code here:
    }//GEN-LAST:event_spLetraStateChanged

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        // TODO add your handling code here:
        frmTelaoExterna.setVisible(true);
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        // TODO add your handling code here:
        desconectar();
        rodando = false;
    }//GEN-LAST:event_jButton2ActionPerformed

    private void spCaracteresStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_spCaracteresStateChanged
        // TODO add your handling code here:
        maxCaracteres = Integer.parseInt(spCaracteres.getValue().toString());
    }//GEN-LAST:event_spCaracteresStateChanged

    private void lbConfigMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lbConfigMouseClicked
        // TODO add your handling code here:          
        if (showConfig) {
            esconder();
        } else {
            mostrar();
        }
    }//GEN-LAST:event_lbConfigMouseClicked

    private void lbConfigMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lbConfigMouseEntered
        // TODO add your handling code here:
    }//GEN-LAST:event_lbConfigMouseEntered

    private boolean showConfig = true;

    private void esconder() {
        try {
            pConfigs.setVisible(false);
            this.setSize(this.getWidth(), 191);
            lbConfig.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/hideMenor.png")));
            lbConfig.setText("Mostrar Configurações");
            showConfig = false;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void mostrar() {
        try {
            pConfigs.setVisible(true);
            this.setSize(this.getWidth(), 410);
            lbConfig.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/showMenor.png")));
            lbConfig.setText("Esconder Configurações");
            showConfig = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String tratarSimple(String text) {
        try {
            aux_simple += text + " ";
            if (text.equals("32")) {
                return traduzirSimpleEncoder(aux_simple);
            } else {
                return "";
            }
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
            return "";
        }
    }

    private void desconectar() {
        try {
            accept.close();
            br.close();

            lbStatus.setText("Desconectado " + perfilSwitch.getPass());
            lbStatus.setBackground(Color.red);

            rodando = false;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String traduzirSimpleEncoder(String array) {
        String traduzido = "";
        try {
            for (String key : mapaString.palavras.keySet()) {
                array = array.replaceAll(key, mapaString.palavras.get(key));
            }

            String[] split = array.split(" ");
            traduzido = new String();

            for (int j = 0; j < split.length; j++) {
                try {
                    traduzido = traduzido + new String(new byte[]{Byte.valueOf(split[j]).byteValue()});
                } catch (Exception e) {
                    //acentos que ja foram trocados
                    traduzido += split[j];
                }
            }
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }
        aux_simple = "";
        return traduzido;
    }

    private boolean rodando = false;
    private ObjectMapper mapper = new ObjectMapper();

    private void iniciar() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        rodando = true;

                        lbStatus.setText("Conectando " + perfilSwitch.getPass() + "...");
                        lbStatus.setBackground(Color.yellow);

                        accept = new Socket();
                        accept.connect(new InetSocketAddress(perfilSwitch.getIpA(), perfilSwitch.getPortaA()), 2000);

                        lbStatus.setText("Conectado " + perfilSwitch.getPass());
                        lbStatus.setBackground(Color.green);

                        br = new BufferedReader(new InputStreamReader(accept.getInputStream(), "windows-1252"));
                        
                        CCPack ccp=null;
                        
                        String linha=null;
                        while((linha=br.readLine())!=null){
                            ccp=mapper.readValue(linha, CCPack.class);
                            addTexto(ccp.getLegenda()+" ");
                        }
                        
            
                        
//                        StringWriter writer = new StringWriter();
//
//                        int byte_lido;
//                        while (((byte_lido = br.read()) != -1) && rodando) {
//                            try {
//                                if (byte_lido == 0) {
//                                    FormConexaoExterna.this.piscar();
//                                } else {
//                                    writer.write(byte_lido);
//                                    addTexto(tratarSimple(Byte.parseByte(Hex.encodeHexString(writer.toString().getBytes("windows-1252")), 16) + ""));
//                                    //addTexto(writer.toString());
//                                }
//
//                            } catch (Exception e) {
//                                //e.printStackTrace();
//                                addTexto(tratarSimple(byte_lido + ""));
//
//                            }
//
//                            writer = new StringWriter();
//                        }

                        desconectar();

                    } catch (Exception e) {
                        e.printStackTrace();
                        desconectar();
                        lbStatus.setText("Desconectado " + perfilSwitch.getPass());
                        lbStatus.setBackground(Color.red);

                        rodando = false;

                        try {
                            Thread.sleep(2000);
                        } catch (Exception ez) {
                        }
                    }

                }
            }
        }).start();
    }


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnCorFundo;
    private javax.swing.JButton btnCorLetra;
    private javax.swing.JCheckBox cbCaracteres;
    private javax.swing.JCheckBox cbDuplo;
    private javax.swing.JComboBox<String> cbLetra;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JLabel lbConfig;
    private javax.swing.JLabel lbDATA;
    private javax.swing.JLabel lbStatus;
    private javax.swing.JPanel pConfigs;
    private javax.swing.JSpinner spCaracteres;
    private javax.swing.JSpinner spDireita;
    private javax.swing.JSpinner spEsquerda;
    private javax.swing.JComboBox spFontes;
    private javax.swing.JSpinner spLetra;
    private javax.swing.JEditorPane taSaida;
    // End of variables declaration//GEN-END:variables
}
