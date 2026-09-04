package gerard.interpretacao.modelo;

/**
 * Um token do enunciado (palavra ou "?"), com o vínculo semântico opcional
 * que o liga a um papel do problema — equivalente portátil de
 * {@code gerard.campoaditivo.diagrama.elementos.ElementoTextoMovel} em
 * {@code Main.java}, sem nenhum campo de geometria/pixel (x, y, largura,
 * altura, arraste): aqui só a semântica (o quê e onde no texto), nunca a
 * apresentação. Ver {@link SegmentadorTextoSemantico}.
 */
public final class SegmentoTextoSemantico {
    private final String valor;
    private final int posicaoInicial;
    private String chavePapelSemantico;
    private int inicioSemanticoLocal = -1;
    private int fimSemanticoLocal = -1;
    private String valorSemanticoOriginal = "";

    SegmentoTextoSemantico(String valor, int posicaoInicial) {
        this.valor = valor;
        this.posicaoInicial = posicaoInicial;
    }

    void vincularSemantica(String chavePapel, int inicioLocal, int fimLocal, String valorOriginalDoPapel) {
        if (chavePapel == null || chavePapel.trim().length() == 0
                || inicioLocal < 0 || fimLocal <= inicioLocal || fimLocal > valor.length()) {
            return;
        }
        this.chavePapelSemantico = chavePapel;
        this.inicioSemanticoLocal = inicioLocal;
        this.fimSemanticoLocal = fimLocal;
        this.valorSemanticoOriginal = valorOriginalDoPapel == null
                ? valor.substring(inicioLocal, fimLocal) : valorOriginalDoPapel;
    }

    public String getValor() {
        return valor;
    }

    public int getPosicaoInicial() {
        return posicaoInicial;
    }

    public String getChavePapelSemantico() {
        return chavePapelSemantico;
    }

    public boolean possuiVinculoSemantico() {
        return chavePapelSemantico != null
                && inicioSemanticoLocal >= 0 && fimSemanticoLocal > inicioSemanticoLocal;
    }

    public int getInicioSemanticoLocal() {
        return inicioSemanticoLocal;
    }

    public int getFimSemanticoLocal() {
        return fimSemanticoLocal;
    }

    public boolean representaIncognitaOriginal() {
        return "?".equals(valorSemanticoOriginal == null ? "" : valorSemanticoOriginal.trim());
    }
}
