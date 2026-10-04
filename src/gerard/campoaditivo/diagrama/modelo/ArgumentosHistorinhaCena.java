package gerard.campoaditivo.diagrama.modelo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Textos que a cena entrega como argumento à historinha, já no idioma da situação em curso, e o
 * cronograma em que aparecem DENTRO da animação. O idioma nunca decide se há historinha nem qual
 * ilustração: só determina estes textos. Valor imutável; quem o produz é o renderizador da categoria
 * dentro do criador de cena. A regra do cronograma mora aqui, num lugar só: os trechos curados, na
 * ordem do enunciado, dividem igualmente a duração da animação; os clientes (Swing e React) apenas
 * mostram a legenda cujo intervalo contém o instante atual do laço da animação.
 */
public final class ArgumentosHistorinhaCena {
    public static final ArgumentosHistorinhaCena VAZIO =
            new ArgumentosHistorinhaCena("", Collections.<String>emptyList(), 0.0);

    /** Legenda visível entre {@code inicioSegundos} (inclusive) e {@code fimSegundos} (exclusive) do laço. */
    public static final class Legenda {
        private final String texto;
        private final double inicioSegundos;
        private final double fimSegundos;

        Legenda(String texto, double inicioSegundos, double fimSegundos) {
            this.texto = texto;
            this.inicioSegundos = inicioSegundos;
            this.fimSegundos = fimSegundos;
        }

        public String getTexto() { return texto; }
        public double getInicioSegundos() { return inicioSegundos; }
        public double getFimSegundos() { return fimSegundos; }
    }

    private final String idioma;
    private final List<String> trechos;
    private final double duracaoSegundos;
    private final List<Legenda> legendas;

    public ArgumentosHistorinhaCena(String idioma, List<String> trechos) {
        this(idioma, trechos, 0.0);
    }

    private ArgumentosHistorinhaCena(String idioma, List<String> trechos, double duracaoSegundos) {
        this.idioma = idioma == null ? "" : idioma;
        this.trechos = trechos == null ? Collections.<String>emptyList()
                : Collections.unmodifiableList(new ArrayList<String>(trechos));
        this.duracaoSegundos = Math.max(0.0, duracaoSegundos);
        this.legendas = Collections.unmodifiableList(dividir(this.trechos, this.duracaoSegundos));
    }

    private static List<Legenda> dividir(List<String> trechos, double duracao) {
        List<Legenda> resultado = new ArrayList<Legenda>();
        if (trechos.isEmpty() || duracao <= 0.0) {
            return resultado;
        }
        double fatia = duracao / trechos.size();
        for (int i = 0; i < trechos.size(); i++) {
            double inicio = i * fatia;
            double fim = i == trechos.size() - 1 ? duracao : (i + 1) * fatia;
            resultado.add(new Legenda(trechos.get(i), inicio, fim));
        }
        return resultado;
    }

    /** Mesmos textos, agora com o cronograma sobre uma animação de {@code duracaoSegundos}. */
    public ArgumentosHistorinhaCena comDuracao(double duracaoSegundos) {
        return new ArgumentosHistorinhaCena(idioma, trechos, duracaoSegundos);
    }

    public String getIdioma() { return idioma; }

    public List<String> getTrechos() { return trechos; }

    public boolean possuiTrechos() { return !trechos.isEmpty(); }

    public double getDuracaoSegundos() { return duracaoSegundos; }

    /** Legendas temporizadas; vazia quando não há trecho curado ou a duração da animação é desconhecida. */
    public List<Legenda> getLegendas() { return legendas; }
}
