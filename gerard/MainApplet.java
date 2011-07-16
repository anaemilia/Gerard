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
import javax.swing.JApplet;

/**
 *
 * @author Kecia
 */
public class MainApplet extends JApplet {

    /**
     * Initialization method that will be called after the applet is loaded
     * into the browser.
     */
    public MainApplet(){

        GerardModelInterface model = new GerardModel();
        ControllerInterface controller = new GerardController(this,model);
        getContentPane().setLayout(new BorderLayout());
        this.getContentPane().add(controller.getJPanel(),BorderLayout.CENTER);
    }

    // TODO overwrite start(), stop() and destroy() methods

}
