package gerard.interacao.texto;

import gerard.campoaditivo.diagrama.elementos.ItemTextoArrastavel;
import gerard.campoaditivo.diagrama.elementos.MarcadorTexto;

/** Resultado portátil do pressionamento sobre um marcador do enunciado. */
public final class ResultadoSelecaoMarcadorTexto {

    public enum Desfecho {
        JA_POSICIONADO,
        SEM_PROXY,
        INICIADO
    }

    private final Desfecho desfecho;
    private final MarcadorTexto marcador;
    private final ItemTextoArrastavel item;

    ResultadoSelecaoMarcadorTexto(Desfecho desfecho,
            MarcadorTexto marcador, ItemTextoArrastavel item) {
        this.desfecho = desfecho;
        this.marcador = marcador;
        this.item = item;
    }

    public Desfecho getDesfecho() {
        return desfecho;
    }

    public MarcadorTexto getMarcador() {
        return marcador;
    }

    public ItemTextoArrastavel getItem() {
        return item;
    }
}
