package gerard.ui;

/**
 * As duas opções de cor de sucesso descritas em prompt-claude-code-cores.md
 * (seção 2, 2026-09-30). {@link UITemaGerard} lê {@link #MODO_ATUAL} para
 * decidir COR_SUCESSO/COR_SUCESSO_FUNDO/COR_SUCESSO_TEXTO — trocar o modo
 * aqui é a única mudança necessária para alternar entre as duas opções.
 */
public enum FeedbackMode {
    /**
     * Opção A (aplicada): sucesso compartilha o azul institucional de ação
     * (COR_ACAO). Os elementos da representação (quadradinhos, números
     * destacados) não usam mais azul — ficam em INK/neutro — para não
     * confundir com o feedback de sucesso.
     */
    AZUL_UNIFICADO_COM_ACAO,

    /**
     * Opção B: sucesso em verde, separado do azul de ação/marca. O azul
     * permanece livre para os elementos da representação, se for o caso.
     */
    VERDE_SEPARADO_DA_ACAO;

    /** Modo em uso — trocar aqui para alternar entre as opções A e B. */
    public static final FeedbackMode MODO_ATUAL = AZUL_UNIFICADO_COM_ACAO;
}
