package gerard.campoaditivo.diagrama.elementos;

import java.awt.*;
import java.awt.geom.QuadCurve2D;

public class FragmentoAnotacao {
    public String texto;
    public boolean negrito;

    public FragmentoAnotacao(String texto, boolean negrito) {
        this.texto = texto == null ? "" : texto;
        this.negrito = negrito;
    }
}
