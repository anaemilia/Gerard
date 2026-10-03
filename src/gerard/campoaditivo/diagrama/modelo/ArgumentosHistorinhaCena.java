package gerard.campoaditivo.diagrama.modelo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Textos que a cena entrega como argumento à historinha, já no idioma da situação em curso.
 * O idioma nunca decide se há historinha nem qual ilustração: só determina estes textos.
 * Valor imutável; quem o produz é o renderizador da categoria dentro do criador de cena.
 */
public final class ArgumentosHistorinhaCena {
    public static final ArgumentosHistorinhaCena VAZIO =
            new ArgumentosHistorinhaCena("", Collections.<String>emptyList());

    private final String idioma;
    private final List<String> trechos;

    public ArgumentosHistorinhaCena(String idioma, List<String> trechos) {
        this.idioma = idioma == null ? "" : idioma;
        this.trechos = trechos == null ? Collections.<String>emptyList()
                : Collections.unmodifiableList(new ArrayList<String>(trechos));
    }

    public String getIdioma() { return idioma; }

    public List<String> getTrechos() { return trechos; }

    public boolean possuiTrechos() { return !trechos.isEmpty(); }
}
