/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package gerard;

import gerard.controller.ControllerInterface;
import gerard.controller.GerardController;
import gerard.model.GerardModel;
import gerard.model.GerardModelInterface;
import java.awt.BorderLayout;
import java.awt.Dimension;
import javax.swing.JFrame;
import java.awt.Toolkit;
import javax.swing.ImageIcon;
/**
 *
 * @author Kecia
 */
public class Main extends JFrame {

    public Main(String titulo){
        super(titulo);
        GerardModelInterface model = new GerardModel();
        ControllerInterface controller = new GerardController(this,model);
        getContentPane().setLayout(new BorderLayout());
       this.getContentPane().add(controller.getJPanel(),BorderLayout.CENTER);

    }
    public static void main(String[] args) {
        Main x = new Main("Gerard 1.0");
        Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
         int width = d.width-200;
         int height = d.height-200;
        x.setSize(810,600);
        x.setLocation(d.width/2 - width/2+100, (d.height/2 - height/2)-50);
        ImageIcon imageicon = new ImageIcon(x.getClass().getResource("imagens/iconeFrame.png"));
        x.setIconImage(imageicon.getImage());

       // x.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        x.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        x.setVisible(true);


    }

}
