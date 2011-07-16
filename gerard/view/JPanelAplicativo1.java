package gerard.view;

import gerard.util.Aplicativo1.ControllerAplicativo1;
import gerard.util.ComponenteDoDiagrama;
import gerard.util.desenhos.Desenho;
import gerard.util.desenhos.DesenhoEmBranco;
import gerard.util.desenhos.DiagramaComparacaoDesenho;
import gerard.util.desenhos.DiagramaComposicaoDesenho;
import gerard.util.desenhos.DiagramaTransformacaoDesenho;
import gerard.util.desenhos.ValorDesenho;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.ArrayList;
import javax.swing.JPanel;

public class JPanelAplicativo1 extends javax.swing.JPanel {

    private Component component_pai;
    private ControllerAplicativo1 controllerAplicativo1;
    private Desenho diagramaDesenho;

    public JPanelAplicativo1(Component component_pai, ControllerAplicativo1 cont) {
        this.component_pai = component_pai;
        this.controllerAplicativo1 = cont;
        initComponents();
        addBotoesListener();

    }

    private void addBotoesListener() {

     this.jButton_composicao.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                //diagrama composicao
                int num = (int)(10*Math.random())%3+1;
                int tipo_problema =1;
                 controllerAplicativo1.setTipoEPosicaoPergunta(tipo_problema, num);
                panelGrafico.repaint();
            }
        });

        this.jButton_trasnformacao.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                //diagrama trasnformacao
                int num = (int)(10*Math.random())%3+1;
                int tipo_problema =2;
                 controllerAplicativo1.setTipoEPosicaoPergunta(tipo_problema, num);
                 panelGrafico.repaint();
            }
        });
            this.jButton_comparacao.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                //diagrama comparacao
                int num = (int)(10*Math.random())%3+1;
                int tipo_problema =3;
                 controllerAplicativo1.setTipoEPosicaoPergunta(tipo_problema, num);
                 panelGrafico.repaint();
            }
        });


        this.jButton_validar.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                controllerAplicativo1.validar(jTextField_textoDigitado.getText());

            }
        });

        this.jButton_limparTexto.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                    jTextField_textoDigitado.setText("");
            }
        });
        this.jButton_voltar.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                    setInicializar();
                    controllerAplicativo1.voltar();
            }
        });

    }

    private void setDesenharValores(Graphics2D g2d, String[] valores) {
        ComponenteDoDiagrama componente = (ComponenteDoDiagrama) this.diagramaDesenho;
        ArrayList<Shape> partes = componente.getShapes();
        int tamanho = partes.size();
        Font font = new Font("SanSerif", Font.PLAIN, 18);
        g2d.setFont(font);
        FontMetrics fm = g2d.getFontMetrics(font);
        int posX, posY, width, height, wTexto, hTexto;
        for (int i = 0; i < tamanho; i++) {
            Shape parteDiagrama = partes.get(i);
            posX = parteDiagrama.getBounds().x;
            posY = parteDiagrama.getBounds().y;
            width = parteDiagrama.getBounds().width;
            height = parteDiagrama.getBounds().height;
            wTexto = fm.stringWidth(valores[i]);
            hTexto = fm.getHeight();
            posX = posX + (width - wTexto) / 2;
            posY = posY - 4 + (height + hTexto) / 2;
            this.diagramaDesenho = new ValorDesenho(diagramaDesenho, valores[i], posX, posY, wTexto, hTexto, new Color(0, 102, 0));
        }

    }

    private class JPanelDiagrama extends JPanel {

        private Desenho desenho = null;
        public JPanelDiagrama(){

        }
        @Override
        public void paintComponent(Graphics g) {
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);
            this.desenho = criarDiagramaDesenho(g2d);
            desenho.draw(g2d);
        }
    }

    protected Desenho criarDiagramaDesenho(Graphics2D g2d) {
        this.diagramaDesenho = new DesenhoEmBranco();
        int tipoProblema = this.controllerAplicativo1.getTipoProblema();
        String valores[] = this.controllerAplicativo1.getValores();
        switch (tipoProblema) {
            case 1: //composicao
                this.diagramaDesenho = new DiagramaComposicaoDesenho(this.diagramaDesenho);
                this.diagramaDesenho.centralizar(this.panelGrafico.getSize());
                setDesenharValores(g2d, valores);
                break;
            case 2: //transformacao
                this.diagramaDesenho = new DiagramaTransformacaoDesenho(this.diagramaDesenho);
                this.diagramaDesenho.centralizar(this.panelGrafico.getSize());
                String aux = valores[1];
                valores[1] = valores[2];
                valores[2] = aux;
                setDesenharValores(g2d, valores);
                break;
            default: //comparacao
                this.diagramaDesenho = new DiagramaComparacaoDesenho(this.diagramaDesenho);
                this.diagramaDesenho.centralizar(this.panelGrafico.getSize());
                aux = valores[1];
                valores[1] = valores[2];
                valores[2] = aux;
                setDesenharValores(g2d, valores);


        }
        return this.diagramaDesenho;
    }
    public void setTelaAguarde(boolean valor){
        
        this.jLabel_textoAguarde1.setVisible(valor);
        this.jLabel_textoAguarde2.setVisible(valor);
    }

    public void setInicializar(){
        jTextField_textoDigitado.setText("Eu tenho 2 petecas e Pedro tem 5. Quantas petecas temos juntos?");
       // jComboBox_tipoProblema.setSelectedIndex(2);
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
        }
        if (img != null) {
            g.drawImage(img, 0, 0, width, height, this);
        }
    }

   
   // <editor-fold defaultstate="collapsed" desc="Generated Code">
    private void initComponents() {

        panelGrafico = new JPanelDiagrama();
        jLabel_digitar = new javax.swing.JLabel();
        jTextField_textoDigitado = new javax.swing.JTextField();
        jPanel_botoesDiagramas = new javax.swing.JPanel();
        jButton_composicao = new javax.swing.JButton();
        jButton_trasnformacao = new javax.swing.JButton();
        jButton_comparacao = new javax.swing.JButton();
        jButton_voltar = new javax.swing.JButton();
        jButton_validar = new javax.swing.JButton();
        jButton_limparTexto = new javax.swing.JButton();
        jLabel_textoAguarde1 = new javax.swing.JLabel();
        jLabel_textoAguarde2 = new javax.swing.JLabel();

        panelGrafico.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Diagrama", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Lucida Calligraphy", 0, 12), new java.awt.Color(0, 102, 0))); // NOI18N
        panelGrafico.setOpaque(false);

        javax.swing.GroupLayout panelGraficoLayout = new javax.swing.GroupLayout(panelGrafico);
        panelGrafico.setLayout(panelGraficoLayout);
        panelGraficoLayout.setHorizontalGroup(
            panelGraficoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 501, Short.MAX_VALUE)
        );
        panelGraficoLayout.setVerticalGroup(
            panelGraficoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 72, Short.MAX_VALUE)
        );

        jLabel_digitar.setFont(new java.awt.Font("Lucida Calligraphy", 0, 14));
        jLabel_digitar.setText("Digite o texto correspondente ao diagrama");

        jTextField_textoDigitado.setFont(new java.awt.Font("Tahoma", 0, 12));
        jTextField_textoDigitado.setText("Eu tenho 2 petecas e Pedro tem 5. Quantas petecas temos juntos?");

        jPanel_botoesDiagramas.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Escolha o tipo de situção-problema:", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Lucida Calligraphy", 0, 12), new java.awt.Color(0, 124, 0))); // NOI18N
        jPanel_botoesDiagramas.setOpaque(false);

        jButton_composicao.setIcon(new javax.swing.ImageIcon(getClass().getResource("/gerard/imagens/composicaoA1.png"))); // NOI18N
        jButton_composicao.setToolTipText("Diagrama de Composição");
        jButton_composicao.setBorderPainted(false);
        jButton_composicao.setMaximumSize(new java.awt.Dimension(80, 70));
        jButton_composicao.setMinimumSize(new java.awt.Dimension(80, 70));
        jButton_composicao.setPreferredSize(new java.awt.Dimension(80, 70));
        jButton_composicao.setRolloverIcon(new javax.swing.ImageIcon(getClass().getResource("/gerard/imagens/composicaoA1_selecionado.png"))); // NOI18N
        jPanel_botoesDiagramas.add(jButton_composicao);

        jButton_trasnformacao.setIcon(new javax.swing.ImageIcon(getClass().getResource("/gerard/imagens/trasnformacaoA1.png"))); // NOI18N
        jButton_trasnformacao.setToolTipText("Diagrama de Composição");
        jButton_trasnformacao.setBorderPainted(false);
        jButton_trasnformacao.setMaximumSize(new java.awt.Dimension(80, 70));
        jButton_trasnformacao.setMinimumSize(new java.awt.Dimension(80, 70));
        jButton_trasnformacao.setPreferredSize(new java.awt.Dimension(80, 70));
        jButton_trasnformacao.setRolloverIcon(new javax.swing.ImageIcon(getClass().getResource("/gerard/imagens/trasnformacaoA1_selecionado.png"))); // NOI18N
        jPanel_botoesDiagramas.add(jButton_trasnformacao);

        jButton_comparacao.setIcon(new javax.swing.ImageIcon(getClass().getResource("/gerard/imagens/comparacaoA1.png"))); // NOI18N
        jButton_comparacao.setToolTipText("Diagrama de Composição");
        jButton_comparacao.setBorderPainted(false);
        jButton_comparacao.setMaximumSize(new java.awt.Dimension(80, 70));
        jButton_comparacao.setMinimumSize(new java.awt.Dimension(80, 70));
        jButton_comparacao.setPreferredSize(new java.awt.Dimension(80, 70));
        jButton_comparacao.setRolloverIcon(new javax.swing.ImageIcon(getClass().getResource("/gerard/imagens/comparacaoA1_selecionado.png"))); // NOI18N
        jPanel_botoesDiagramas.add(jButton_comparacao);

        jButton_voltar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/gerard/imagens/botaoVOLTARpequeno.png"))); // NOI18N
        jButton_voltar.setBorderPainted(false);
        jButton_voltar.setRolloverIcon(new javax.swing.ImageIcon(getClass().getResource("/gerard/imagens/botaoVOLTARpequeno_selecionado.png"))); // NOI18N

        jButton_validar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/gerard/imagens/botaoVALIDAR.png"))); // NOI18N
        jButton_validar.setToolTipText("validar situação-problema..");
        jButton_validar.setBorderPainted(false);
        jButton_validar.setRolloverIcon(new javax.swing.ImageIcon(getClass().getResource("/gerard/imagens/botaoVALIDAR_selecionado.png"))); // NOI18N

        jButton_limparTexto.setIcon(new javax.swing.ImageIcon(getClass().getResource("/gerard/imagens/botaoLIMPAR.png"))); // NOI18N
        jButton_limparTexto.setToolTipText("validar situação-problema..");
        jButton_limparTexto.setBorderPainted(false);
        jButton_limparTexto.setRolloverIcon(new javax.swing.ImageIcon(getClass().getResource("/gerard/imagens/botaoLIMPAR_selecionado.png"))); // NOI18N

        jLabel_textoAguarde1.setFont(new java.awt.Font("Lucida Calligraphy", 0, 14)); // NOI18N
        jLabel_textoAguarde1.setForeground(new java.awt.Color(255, 0, 0));
        jLabel_textoAguarde1.setText("Aguarde,");
jLabel_textoAguarde1.setVisible(false);
        jLabel_textoAguarde2.setFont(new java.awt.Font("Lucida Calligraphy", 0, 14));
        jLabel_textoAguarde2.setForeground(new java.awt.Color(255, 0, 0));
        jLabel_textoAguarde2.setText("validando Situação-Problema...");
jLabel_textoAguarde2.setVisible(false);
        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(panelGrafico, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addContainerGap())
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel_digitar, javax.swing.GroupLayout.DEFAULT_SIZE, 384, Short.MAX_VALUE)
                        .addGap(139, 139, 139))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jPanel_botoesDiagramas, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 513, Short.MAX_VALUE)
                            .addComponent(jTextField_textoDigitado, javax.swing.GroupLayout.DEFAULT_SIZE, 513, Short.MAX_VALUE))
                        .addContainerGap())
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(jButton_voltar, javax.swing.GroupLayout.PREFERRED_SIZE, 78, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap())
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel_textoAguarde1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addGap(191, 191, 191))
                            .addGroup(layout.createSequentialGroup()
                                .addGap(32, 32, 32)
                                .addComponent(jLabel_textoAguarde2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                        .addGap(83, 83, 83)
                        .addComponent(jButton_limparTexto, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton_validar, javax.swing.GroupLayout.PREFERRED_SIZE, 82, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap())))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel_botoesDiagramas, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel_digitar)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jTextField_textoDigitado, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addComponent(jButton_validar, 0, 0, Short.MAX_VALUE)
                        .addComponent(jButton_limparTexto, javax.swing.GroupLayout.PREFERRED_SIZE, 51, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel_textoAguarde1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel_textoAguarde2)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(panelGrafico, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton_voltar, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
    }// </editor-fold>
   private javax.swing.JButton jButton_comparacao;
    private javax.swing.JButton jButton_composicao;
    private javax.swing.JButton jButton_limparTexto;
    private javax.swing.JButton jButton_trasnformacao;
    private javax.swing.JButton jButton_validar;
    private javax.swing.JButton jButton_voltar;
    private javax.swing.JLabel jLabel_digitar;
    private javax.swing.JLabel jLabel_textoAguarde1;
    private javax.swing.JLabel jLabel_textoAguarde2;
    private javax.swing.JPanel jPanel_botoesDiagramas;
    private javax.swing.JTextField jTextField_textoDigitado;
    private javax.swing.JPanel panelGrafico;
    // End of variables declaration
}
