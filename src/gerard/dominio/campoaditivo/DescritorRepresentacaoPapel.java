package gerard.dominio.campoaditivo;

/**
 * Descritor abstrato de como um PapelQuantitativo se apresenta — forma
 * conceitual, símbolo e chave de rótulo, nunca pixels, cor, fonte ou
 * qualquer tipo pertencente à camada de diagrama/renderização.
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
 * que símbolo, que rótulo), mas nunca COMO desenhá-la.
 */
public final class DescritorRepresentacaoPapel {

    private final TipoRepresentacaoAbstrata forma;
    private final String simbolo;
    private final String chaveRotulo;

    public DescritorRepresentacaoPapel(TipoRepresentacaoAbstrata forma, String simbolo, String chaveRotulo) {
        if (forma == null) {
            throw new IllegalArgumentException("forma não pode ser nula");
        }
        this.forma = forma;
        this.simbolo = simbolo == null ? "" : simbolo;
        this.chaveRotulo = chaveRotulo == null ? "" : chaveRotulo;
    }

    public DescritorRepresentacaoPapel(TipoRepresentacaoAbstrata forma, String chaveRotulo) {
        this(forma, "", chaveRotulo);
    }

    public TipoRepresentacaoAbstrata getForma() { return forma; }
    public String getSimbolo() { return simbolo; }
    public String getChaveRotulo() { return chaveRotulo; }
}
