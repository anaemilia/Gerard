/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package gerard.view;

import gerard.banco.ModelBanco;
import gerard.controller.ControllerInterface;
import gerard.util.HistoricoObserver;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import javax.swing.JPanel;

/**
 *
 * @author Kecia
 */
public class JPanelHistorico extends JPanel implements HistoricoObserver {
    private ModelBanco modelBanco;
    private ControllerInterface controller;
    private Component component_pai;
    public JPanelHistorico(Component component_pai,ControllerInterface cont,ModelBanco modelBanco) {
        this.component_pai=component_pai;
        this.controller=cont;
        this.modelBanco=modelBanco;
        this.modelBanco.registerObserver((HistoricoObserver) this);
        initComponents();
        

        this.jButton_exibirMaisHistorico.addActionListener(new ActionListener(){

            public void actionPerformed(ActionEvent e) {
               controller.exibirMaisHistorico();

            }
        });
        this.jButton_sairHistorico.addActionListener(new ActionListener(){

            public void actionPerformed(ActionEvent e) {
                controller.sairHistorico();
            }
        });
    }

     public void updateHistorico() {
        String historico = this.modelBanco.getHistorico();
        this.jTextPane_historico.setText(historico);
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
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">
    private void initComponents() {

        jButton_exibirMaisHistorico = new javax.swing.JButton();
        jButton_sairHistorico = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTextPane_historico = new javax.swing.JTextPane();

        jButton_exibirMaisHistorico.setIcon(new javax.swing.ImageIcon(getClass().getResource("/gerard/imagens/botaoExibirMais.png"))); // NOI18N
        jButton_exibirMaisHistorico.setToolTipText("Exibir mais historico");
        jButton_exibirMaisHistorico.setBorderPainted(false);
        jButton_exibirMaisHistorico.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButton_exibirMaisHistorico.setPreferredSize(new java.awt.Dimension(104, 50));
        jButton_exibirMaisHistorico.setRolloverIcon(new javax.swing.ImageIcon(getClass().getResource("/gerard/imagens/botaoExibirMais_selecionado.png"))); // NOI18N

        jButton_sairHistorico.setIcon(new javax.swing.ImageIcon(getClass().getResource("/gerard/imagens/botaoVOLTAR.png"))); // NOI18N
        jButton_sairHistorico.setToolTipText("Exibir mais historico");
        jButton_sairHistorico.setBorderPainted(false);
        jButton_sairHistorico.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButton_sairHistorico.setPreferredSize(new java.awt.Dimension(104, 50));
        jButton_sairHistorico.setRolloverIcon(new javax.swing.ImageIcon(getClass().getResource("/gerard/imagens/botaoVOLTAR_selecionado.png"))); // NOI18N

        jTextPane_historico.setContentType("text/html");
        jTextPane_historico.setEditable(false);
        jTextPane_historico.setFont(new java.awt.Font("Times New Roman", 0, 12)); // NOI18N
        jTextPane_historico.setForeground(new java.awt.Color(0, 102, 0));
        jScrollPane1.setViewportView(jTextPane_historico);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(176, Short.MAX_VALUE)
                .addComponent(jButton_exibirMaisHistorico, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton_sairHistorico, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 380, Short.MAX_VALUE)
                    .addContainerGap()))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(239, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jButton_exibirMaisHistorico, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton_sairHistorico, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 225, Short.MAX_VALUE)
                    .addGap(64, 64, 64)))
        );
    }// </editor-fold>


    // Variables declaration - do not modify
    private javax.swing.JButton jButton_exibirMaisHistorico;
    private javax.swing.JButton jButton_sairHistorico;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextPane jTextPane_historico;
    // End of variables declaration

}
