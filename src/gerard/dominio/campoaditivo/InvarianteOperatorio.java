package gerard.dominio.campoaditivo;

/**
 * Invariante operatório de Vergnaud para composição de medidas:
 * Todo = Parte1 + Parte2.
 *
 * É deliberadamente um objeto à parte, não um método dentro de
 * PapelQuantitativo: a regra envolve TRÊS papéis simultaneamente, e a
 * própria regra de revisão da skill KnowledgeLocalityPrinciple só autoriza
 * um objeto coordenador quando a responsabilidade cruza mais de um
 * conceito — este é exatamente esse caso, não uma exceção ao princípio.
 */
public final class InvarianteOperatorio {

    private final String expressao;

    private InvarianteOperatorio(String expressao) {
        this.expressao = expressao;
    }

    public static InvarianteOperatorio composicaoDeMedidas() {
        return new InvarianteOperatorio("Todo = Parte1 + Parte2");
    }

    public String explicar() { return expressao; }

    /** Verifica Todo = Parte1 + Parte2. Só decide algo se os três papéis já estiverem preenchidos. */
    public boolean verificar(PapelQuantitativo parte1, PapelQuantitativo parte2, PapelQuantitativo todo) {
        if (!parte1.estaPreenchido() || !parte2.estaPreenchido() || !todo.estaPreenchido()) {
            return false;
        }
        int p1 = parte1.valorAtual().valorOuNull();
        int p2 = parte2.valorAtual().valorOuNull();
        int t = todo.valorAtual().valorOuNull();
        return t == p1 + p2;
    }
}
