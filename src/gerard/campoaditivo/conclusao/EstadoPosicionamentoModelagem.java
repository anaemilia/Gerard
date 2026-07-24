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
    private final Boolean valorCorrespondeAoCurado;

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

    /** Compatibilidade com testes e integrações anteriores: sem valor curado para conferir. */
    public EstadoPosicionamentoModelagem(String papelItem, String papelAlvo,
            String valorMatematico, boolean noDiagrama,
            boolean incognitaOriginal,
            boolean preenchidoPeloProtocoloMouseTexto) {
        this(papelItem, papelAlvo, valorMatematico, noDiagrama,
                incognitaOriginal, preenchidoPeloProtocoloMouseTexto, null);
    }

    /**
     * @param valorCorrespondeAoCurado só é relevante quando incognitaOriginal
     *        é true: null quando não há valor curado disponível para conferir
     *        (ex.: problema digitado livremente, sem situação curada
     *        carregada) — nesse caso a conclusão não é bloqueada por esse
     *        motivo, preservando o comportamento anterior a essa checagem.
     */
    public EstadoPosicionamentoModelagem(String papelItem, String papelAlvo,
            String valorMatematico, boolean noDiagrama,
            boolean incognitaOriginal,
            boolean preenchidoPeloProtocoloMouseTexto,
            Boolean valorCorrespondeAoCurado) {
        this.papelItem = limpar(papelItem);
        this.papelAlvo = limpar(papelAlvo);
        this.valorMatematico = limpar(valorMatematico);
        this.noDiagrama = noDiagrama;
        this.incognitaOriginal = incognitaOriginal;
        this.preenchidoPeloProtocoloMouseTexto =
                preenchidoPeloProtocoloMouseTexto;
        this.valorCorrespondeAoCurado = valorCorrespondeAoCurado;
    }

    public String getPapelItem() { return papelItem; }
    public String getPapelAlvo() { return papelAlvo; }
    public String getValorMatematico() { return valorMatematico; }
    public boolean isNoDiagrama() { return noDiagrama; }
    public boolean isIncognitaOriginal() { return incognitaOriginal; }
    public boolean isPreenchidoPeloProtocoloMouseTexto() {
        return preenchidoPeloProtocoloMouseTexto;
    }
    public Boolean getValorCorrespondeAoCurado() {
        return valorCorrespondeAoCurado;
    }

    private static String limpar(String valor) {
        return valor == null ? "" : valor.trim();
    }
}
