/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package gerard.util;

import java.awt.Shape;
import java.util.ArrayList;

public interface ComponenteDoDiagrama {
    //usado como forma de verificar se o usuario cometeu erro de posicionamento
    public ArrayList<Shape> getShapes();
    public Shape getShapeAt(int cont);
}
