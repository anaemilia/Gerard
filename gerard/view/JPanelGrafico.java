/*
 * Esta classe desenha toda parte grafica do aplicativo utilizando Java2D
 */
package gerard.view;

import gerard.controller.ControllerInterface;
import gerard.model.GerardModelInterface;
import gerard.util.DesenharObserver;
import gerard.util.SituacaoProblema;
import gerard.util.desenhos.Desenho;
import gerard.util.desenhos.FaixaDesenho;
import gerard.util.desenhos.SituacaoProblemaDesenho;
import gerard.util.desenhos.ValorDesenho;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.LayoutManager;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Rectangle2D;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SpringLayout;

/**
 *
 * @author Kecia
 */
public class JPanelGrafico extends JPanel implements DesenharObserver {

    private GerardModelInterface model;
    private ControllerInterface controller;
    //private SituacaoProblema sp;
    // private DiagramaGraficoInterface diagrama;
    // private DiagramaDesenhoInterface diagrama;
    private Desenho desenho;
    private JTextField jTextField;
    private SpringLayout layout = new SpringLayout();
    private boolean flagPosicionamento = true;
    private Rectangle2D.Double quadradoSelecao = null;//new Rectangle2D.Double(50, 50, 50, 50);
    private ValorDesenho valorSelecionado;
    private SituacaoProblemaDesenho situacaoProblemaDesenho;
    private SituacaoProblema SP;
    private FaixaDesenho faixaDesenho;
    private boolean flagModel = false;
    private final MouseHandler handler;

    public JPanelGrafico(GerardModelInterface model, ControllerInterface cont) {
        this.model = model;
        this.controller = cont;
        model.registerObserver((DesenharObserver) this);
        desenho = null;
        handler = new MouseHandler();
        this.addMouseMotionListener((MouseMotionListener) handler);
        this.addMouseListener((MouseListener) handler);
        this.setLayout(layout);
        this.jTextField = new JTextField("aqui");
        this.jTextField.setFont(new Font("SanSerif", Font.PLAIN, 18));
        this.add(this.jTextField);
        this.jTextField.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                controller.veriricarResultado(jTextField.getText());
            }
        });

    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g.create();


        if (flagModel) {
            if (desenho == null) {//primeira tela

                this.SP = model.getSituacaoProblema();
                this.situacaoProblemaDesenho = new SituacaoProblemaDesenho(this.getSize(), 10, 10, this.SP);
                this.desenho = situacaoProblemaDesenho;
                this.desenho.draw(g2d);
                controller.setSP(situacaoProblemaDesenho.getSP());
                this.faixaDesenho = new FaixaDesenho(this.desenho, 0, this.getSize().width, situacaoProblemaDesenho.alturaTexto());
                this.desenho = this.faixaDesenho;
                controller.setDesenho(this.desenho);
                model.setGraphics2D(g2d,this.getSize());//para auxiliar na função abrir estado salvo

            } else {
                //quando a tela eh maximinizada o texto eh redesenhado e eh necessario
                //atualizar os dados no model
                this.situacaoProblemaDesenho.setLocal(this.getSize(), 10, 10);
                controller.setSP(this.situacaoProblemaDesenho.getSP());
                this.faixaDesenho.setLocal(0, this.getSize().width, situacaoProblemaDesenho.alturaTexto());
                this.desenho.setDistacia_Y(situacaoProblemaDesenho.alturaTexto() + 80);
                this.desenho.centralizar(this.getSize());
            }


            this.desenho.draw(g2d);
        }
        g2d.dispose();
    }//fim do metodo paintComponent

    public void setLocalJTextField(int x, int y) {

        //JTextField responsável por receber o resultado digitado pelo usuário
        layout.putConstraint(SpringLayout.WEST, jTextField,
                x - 8,
                SpringLayout.WEST, this);
        layout.putConstraint(SpringLayout.NORTH, jTextField,
                y - 6,
                SpringLayout.NORTH, this);
        this.jTextField.setOpaque(false);
        jTextField.setBorder(null);

        jTextField.setVisible(true);

        ((LayoutManager) layout).layoutContainer(this);

        //jTextField.setFocusable(true);
    }

    public void updateDesenhar() {
        desenho = model.getDesenho();
        this.repaint();

    }

    public void updateDigitarResultado() {
        Point point = model.getPontoResultado();
        this.setLocalJTextField(point.x, point.y);
       setMouseListener(false);

    }

    public void updateInicio() {
        setMouseListener(true);
        this.desenho = null;
        this.jTextField.setText("aqui");
        this.jTextField.setVisible(false);
        this.flagModel = true;
        this.repaint();
    }

    public void updateDesabilitarJtextfile() {

       
        this.jTextField.setVisible(false);
        this.repaint();
    }

    public void setMouseListener(boolean setar) {
     
        this.flagPosicionamento=setar;

    }

    public void atualizarVariaveis() {
       this.situacaoProblemaDesenho = model.getSituacaoProblemaDesenho();
       this.faixaDesenho= model.getFaixaDesenho();
    }

    public class MouseHandler implements MouseListener, MouseMotionListener {

        private boolean mouseDownOnQuad = false;
        // True if the user pressed, dragged or released the mouse outside of the rectangle; false otherwise.
        boolean pressOut = false;
        // int teste=0;
        int last_x, last_y;

        @Override
        public void mousePressed(MouseEvent e) {
            if ((quadradoSelecao = model.getQuadradoSelecao()) != null) {
                valorSelecionado = model.getValorSelecionado();
                last_x = (int) quadradoSelecao.x - e.getX();
                last_y = (int) quadradoSelecao.y - e.getY();

                // Checks whether or not the cursor is inside of the rectangle while the user is pressing the mouse.
                if (quadradoSelecao.contains(e.getX(), e.getY())) {
                    updateLocation(e);
                } else {
                   
                    pressOut = true;
                }
            }
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            if (quadradoSelecao != null) {
                if (quadradoSelecao.contains(e.getX(), e.getY()) == false) {
                   
                    pressOut = false;
                } else if (flagPosicionamento) {
                    controller.verificarPosicionamento(e.getX(), e.getY());
                }
            }
        }

        @Override
        public void mouseDragged(MouseEvent e) {
            if (quadradoSelecao != null) {
                if (pressOut == false) {
                    updateLocation(e);
                }
            }

        }

        public void mouseClicked(MouseEvent e) {
        }

        public void mouseEntered(MouseEvent e) {
        }

        public void mouseExited(MouseEvent e) {
        }

        public void mouseMoved(MouseEvent e) {
        }

        private void updateLocation(MouseEvent e) {
            //rect.setLocation(last_x + e.getX(), last_y + e.getY());
            int x = last_x + e.getX();
            int y = last_y + e.getY();
            quadradoSelecao.x = x;
            quadradoSelecao.y = y;
            valorSelecionado.setLocal(x, y + 18);
            // teste=1;
            repaint();

        }
    }
}
