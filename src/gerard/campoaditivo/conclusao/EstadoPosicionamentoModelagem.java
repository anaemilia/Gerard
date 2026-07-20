package gerard.campoaditivo.conclusao;

/**
 * Retrato imutável do conteúdo matemático presente em um elemento do diagrama.
 * O conteúdo pode ter sido posicionado por arraste ou informado por teclado.
 */
public final class EstadoPosicionamentoModelagem {
    private final String papelItem;
    private final String papelAlvo;
    private final String valorMatematico;
    private final boolean noDiagrama;
    private final boolean incognitaOriginal;
    private final boolean preenchidoPeloProtocoloMouseTexto;

    /** Compatibilidade com estados antigos sem conteúdo. */
    public EstadoPosicionamentoModelagem(String papelItem, String papelAlvo,
            boolean noDiagrama) {
        this(papelItem, papelAlvo, "", noDiagrama, false, false);
    }

    /** Compatibilidade com testes e integrações anteriores. */
    public EstadoPosicionamentoModelagem(String papelItem, String papelAlvo,
            String valorMatematico, boolean noDiagrama) {
        this(papelItem, papelAlvo, valorMatematico, noDiagrama, false, false);
    }

    public EstadoPosicionamentoModelagem(String papelItem, String papelAlvo,
            String valorMatematico, boolean noDiagrama,
            boolean incognitaOriginal,
            boolean preenchidoPeloProtocoloMouseTexto) {
        this.papelItem = limpar(papelItem);
        this.papelAlvo = limpar(papelAlvo);
        this.valorMatematico = limpar(valorMatematico);
        this.noDiagrama = noDiagrama;
        this.incognitaOriginal = incognitaOriginal;
        this.preenchidoPeloProtocoloMouseTexto =
                preenchidoPeloProtocoloMouseTexto;
    }

    public String getPapelItem() { return papelItem; }
    public String getPapelAlvo() { return papelAlvo; }
    public String getValorMatematico() { return valorMatematico; }
    public boolean isNoDiagrama() { return noDiagrama; }
    public boolean isIncognitaOriginal() { return incognitaOriginal; }
    public boolean isPreenchidoPeloProtocoloMouseTexto() {
        return preenchidoPeloProtocoloMouseTexto;
    }

    private static String limpar(String valor) {
        return valor == null ? "" : valor.trim();
    }
}
