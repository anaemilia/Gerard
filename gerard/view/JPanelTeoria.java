/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * JPanelTeoriaProjeto.java
 *
 * Created on 13/06/2011, 10:55:47
 */
package gerard.view;

import gerard.controller.ControllerInterface;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JViewport;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLFrameHyperlinkEvent;

/**
 *
 * @author Kecia
 */
public class JPanelTeoria extends javax.swing.JPanel {

    private Component component_pai;
     private ControllerInterface controller;
     private URL url;
    public JPanelTeoria(Component component_pai,ControllerInterface cont) {
     this.component_pai=component_pai;
     this.controller=cont;
      html();
        initComponents();
        this.jButton_voltar.addActionListener(new ActionListener(){

            public void actionPerformed(ActionEvent e) {
                controller.cancelarEditarCadastro();
            }
        });

    }
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        int width = component_pai.getWidth();
        int height = component_pai.getHeight();

        java.net.URL caminho = getClass().getResource("/gerard/imagens/telaLogin.png");
 
        Image img = null;
        try {
            img = javax.imageio.ImageIO.read(caminho);
        } catch (IOException ex) {
            // Logger.getLogger(JPanelLogin.class.getName()).log(Level.SEVERE, null, ex);
        }
        if (img != null) {
            g.drawImage(img, 0, 0, width, height, this);
        }
    }
    public void  html() {
            url = null;
            String path = null;
            try {
                url = getClass().getResource("/gerard/html/index.html");

            } catch (Exception e) {
                System.err.println("Failed to open " + path);
                url = null;
            }
           
    }

    private HyperlinkListener createHyperLinkListener() {
        return new HyperlinkListener() {
            public void hyperlinkUpdate(HyperlinkEvent e) {
                if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
                    if (e instanceof HTMLFrameHyperlinkEvent) {
                        ((HTMLDocument) jEditorPane1.getDocument()).processHTMLFrameHyperlinkEvent(
                                (HTMLFrameHyperlinkEvent) e);
                    } else {
                        try {
                            jEditorPane1.setPage(e.getURL());
                        } catch (IOException ioe) {
                            System.out.println("IOE: " + ioe);
                        }
                    }
                }
            }
        };
    }

    void updateDragEnabled(boolean dragEnabled) {
        jEditorPane1.setDragEnabled(dragEnabled);
    }

    // <editor-fold defaultstate="collapsed" desc="Generated Code">
    private void initComponents() {

        jLabel_titulo = new javax.swing.JLabel();
        jButton_voltar = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        if(this.url!=null)
        try {
            jEditorPane1 = new javax.swing.JEditorPane(this.url);
             this.jEditorPane1.addHyperlinkListener(createHyperLinkListener());
            JViewport vp = this.jScrollPane1.getViewport();
               vp.add(this.jEditorPane1);
        } catch (IOException ex) {
            //Logger.getLogger(JPanelTeoria.class.getName()).log(Level.SEVERE, null, ex);
            jEditorPane1 = new javax.swing.JEditorPane();

        }


        jLabel_titulo.setFont(new java.awt.Font("Lucida Calligraphy", 0, 18)); // NOI18N
        jLabel_titulo.setForeground(new java.awt.Color(0, 102, 0));
        jLabel_titulo.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel_titulo.setText("Teoria por trás do GERARD");

        jButton_voltar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/gerard/imagens/botaoVOLTAR.png"))); // NOI18N
        jButton_voltar.setToolTipText("voltar");
        jButton_voltar.setBorderPainted(false);
        jButton_voltar.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButton_voltar.setPreferredSize(new java.awt.Dimension(104, 50));
        jButton_voltar.setRolloverIcon(new javax.swing.ImageIcon(getClass().getResource("/gerard/imagens/botaoVOLTAR_selecionado.png"))); // NOI18N

        jEditorPane1.setEditable(false);
        jScrollPane1.setViewportView(jEditorPane1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel_titulo, javax.swing.GroupLayout.DEFAULT_SIZE, 380, Short.MAX_VALUE)
                    .addComponent(jButton_voltar, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 380, Short.MAX_VALUE)
                    .addContainerGap()))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel_titulo)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 203, Short.MAX_VALUE)
                .addComponent(jButton_voltar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addGap(42, 42, 42)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 194, Short.MAX_VALUE)
                    .addGap(64, 64, 64)))
        );
    }// </editor-fold>
    // Variables declaration - do not modify
    private javax.swing.JButton jButton_voltar;
    private javax.swing.JEditorPane jEditorPane1;
    private javax.swing.JLabel jLabel_titulo;
    private javax.swing.JScrollPane jScrollPane1;
    // End of variables declaration
}
