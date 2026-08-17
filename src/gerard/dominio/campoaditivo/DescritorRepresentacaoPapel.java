package gerard.dominio.campoaditivo;

/**
 * Descritor abstrato de como um PapelQuantitativo se apresenta — forma
 * conceitual, símbolo, chave de rótulo e chave de explicação conceitual,
 * nunca pixels, cor, fonte ou qualquer tipo pertencente à camada de
 * diagrama/renderização.
 *
 * Corrige a dependência anterior de
 * gerard.campoaditivo.diagrama.modelo.TipoFiguraDiagrama: mesmo essa classe
 * já sendo livre de AWT/Swing, ela pertence conceitualmente à camada do
 * diagrama, não ao domínio — o domínio não deveria conhecer um tipo cujo
 * pacote e razão de existir são "diagrama". TipoRepresentacaoAbstrata,
 * definido neste mesmo pacote, é o vocabulário que o domínio de fato
 * possui; mapear esse vocabulário para TipoFiguraDiagrama (ou qualquer
 * outra tecnologia visual) é responsabilidade da camada de interface,
 * ainda não implementada nesta etapa isolada.
 *
 * O domínio sabe o SIGNIFICADO de sua representação (que forma conceitual,
 * que símbolo, que rótulo, que explicação conceitual), mas nunca COMO
 * desenhá-la.
 *
 * {@code chaveExplicacaoConceitual} é uma CHAVE de mensagem i18n (nunca
 * texto final embutido no domínio — mesma regra já aplicada a
 * chaveRotulo), usada por AG_EME (levantamento de pendências de
 * 2026-08-11, item 1) para explicar ao participante o que o papel
 * manipulado via protocolo de mouse significa conceitualmente, além da
 * instrução operacional genérica já existente
 * ({@code ui.hint.chooseOperation}).
 */
public final class DescritorRepresentacaoPapel {

    private final TipoRepresentacaoAbstrata forma;
    private final String simbolo;
    private final String chaveRotulo;
    private final String chaveExplicacaoConceitual;

    public DescritorRepresentacaoPapel(TipoRepresentacaoAbstrata forma, String simbolo, String chaveRotulo,
                                        String chaveExplicacaoConceitual) {
        if (forma == null) {
            throw new IllegalArgumentException("forma não pode ser nula");
        }
        this.forma = forma;
        this.simbolo = simbolo == null ? "" : simbolo;
        this.chaveRotulo = chaveRotulo == null ? "" : chaveRotulo;
        this.chaveExplicacaoConceitual = chaveExplicacaoConceitual == null ? "" : chaveExplicacaoConceitual;
    }

    /** Compatibilidade: sem explicação conceitual (chamador antigo, ou papel sem explicação decidida ainda). */
    public DescritorRepresentacaoPapel(TipoRepresentacaoAbstrata forma, String simbolo, String chaveRotulo) {
        this(forma, simbolo, chaveRotulo, "");
    }

    public DescritorRepresentacaoPapel(TipoRepresentacaoAbstrata forma, String chaveRotulo) {
        this(forma, "", chaveRotulo, "");
    }

    public TipoRepresentacaoAbstrata getForma() { return forma; }
    public String getSimbolo() { return simbolo; }
    public String getChaveRotulo() { return chaveRotulo; }
    public String getChaveExplicacaoConceitual() { return chaveExplicacaoConceitual; }
}
