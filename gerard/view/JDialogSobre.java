

package gerard.view;

import javax.swing.JDialog;


import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JPanel;

public class JDialogSobre extends javax.swing.JDialog {
    private Component component_pai = null;
    public JDialogSobre(Component componente_pai) {
        this.component_pai = componente_pai;
        this.setModal(true);
        initComponents();
        this.setLocationRelativeTo(component_pai);
        this.setVisible(true);
       
    }
  private void jButton_okActionPerformed(java.awt.event.ActionEvent evt) {
        this.dispose();
    }

    class AboutPanel extends JPanel {

        private Image img;

        public AboutPanel() {

            try {
                java.net.URL caminho = getClass().getResource("/gerard/imagens/sobre.png");

                img = javax.imageio.ImageIO.read(caminho);
            } catch (Exception e) {
            }
        }

        public void paintComponent(Graphics g) {
            super.paintComponent(g);

            if (img != null) {
                g.drawImage(img, 0, 0, 533, 338, this);
            } else {
                g.drawString("Aplicativo desenvolvido como Trabalho de Conclusão de Curso", 10, 10);
                g.drawString("Universidade Federal do Vale do São Francisco", 10, 15);
                g.drawString("Curso: Engenharia da Computação", 10, 20);
                g.drawString("Aluna: Kecia de Moura", 10, 25);
                g.drawString("kecia.de.moura@gmail.com", 10, 40);
                g.drawString("2011", 10, 45);

            }
        }
    }


    private void initComponents() {

        jPanel1 = new AboutPanel();
        jButton_ok = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Sobre o GERARD 1.0");
        setResizable(false);

        jButton_ok.setIcon(new javax.swing.ImageIcon(getClass().getResource("/gerard/imagens/botaoOK.png"))); // NOI18N
        jButton_ok.setToolTipText("");
        jButton_ok.setBorderPainted(false);
        jButton_ok.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButton_ok.setPreferredSize(new java.awt.Dimension(52, 50));
        jButton_ok.setRolloverIcon(new javax.swing.ImageIcon(getClass().getResource("/gerard/imagens/botaoOK_selecionado.png"))); // NOI18N
        jButton_ok.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton_okActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap(471, Short.MAX_VALUE)
                .addComponent(jButton_ok, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap(277, Short.MAX_VALUE)
                .addComponent(jButton_ok, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }

   
    
    private javax.swing.JButton jButton_ok;
    private javax.swing.JPanel jPanel1;
    // End of variables declaration
}
