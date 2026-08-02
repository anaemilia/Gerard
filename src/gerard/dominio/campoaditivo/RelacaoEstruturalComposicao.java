package gerard.dominio.campoaditivo;

/**
 * Relação estrutural formal do esquema Composição de Medidas:
 * Todo = Parte1 + Parte2.
 *
 * DISTINÇÃO CONCEITUAL OBRIGATÓRIA (ver relatório técnico do baseline v2,
 * seção "Situações, Invariantes Operatórios e Representações" — tripé
 * C=(S,I,R) de Vergnaud): esta classe NÃO representa um invariante
 * operatório. Um invariante operatório é uma proposição sobre o mundo
 * mobilizada pelo sujeito durante sua atividade — pode emergir
 * implicitamente por meio de ações, ser inferido a partir de uma sequência
 * de ações, ou ser verbalizado pelo estudante em qualquer momento da
 * tentativa. Não pertence a Parte, não pertence a Todo, não pertence a
 * nenhum objeto gráfico específico, e não deve ser modelado como uma regra
 * formal possuída por um elemento do diagrama.
 *
 * O que esta classe modela é a organização matemática formal que relaciona
 * os papéis Parte1/Parte2/Todo dentro de UMA representação — pertence ao R
 * do tripé (representações), nunca ao I (invariantes operatórios). O
 * invariante operatório em si — a compreensão de que "juntar duas
 * quantidades produz uma terceira maior" — não é e não pode ser modelado
 * por nenhuma classe deste pacote: ele é mobilizado pelo estudante, não
 * pelo sistema.
 *
 * Objeto coordenador de escopo fechado (só os três papéis deste esquema):
 * a regra cruza três conceitos, então não pertence a nenhum
 * PapelQuantitativo isolado — a mesma regra de revisão da skill
 * KnowledgeLocalityPrinciple que já justificava esta classe antes de ser
 * renomeada continua valendo.
 */
public final class RelacaoEstruturalComposicao {

    private RelacaoEstruturalComposicao() { }

    public static RelacaoEstruturalComposicao composicaoDeMedidas() {
        return new RelacaoEstruturalComposicao();
    }

    /** Descreve a relação estrutural em notação simbólica. Não é uma verbalização de invariante operatório. */
    public String descreverRelacao() { return "Todo = Parte1 + Parte2"; }

    /**
     * Avalia a relação estrutural formal contra os três papéis.
     * REPRESENTACAO_INCOMPLETA quando nem todos estão preenchidos ainda —
     * isso é o estado normal de uma atividade em andamento, não um erro.
     */
    public EstadoConsistencia verificarConsistencia(PapelQuantitativo parte1, PapelQuantitativo parte2,
                                                      PapelQuantitativo todo) {
        exigirNaoNulo(parte1, "parte1");
        exigirNaoNulo(parte2, "parte2");
        exigirNaoNulo(todo, "todo");
        if (!parte1.estaPreenchido() || !parte2.estaPreenchido() || !todo.estaPreenchido()) {
            return EstadoConsistencia.REPRESENTACAO_INCOMPLETA;
        }
        int p1 = parte1.valorAtual().valorOuNull();
        int p2 = parte2.valorAtual().valorOuNull();
        int t = todo.valorAtual().valorOuNull();
        return (t == p1 + p2) ? EstadoConsistencia.CONSISTENTE : EstadoConsistencia.REPRESENTACAO_INCONSISTENTE;
    }

    private static void exigirNaoNulo(PapelQuantitativo papel, String nomeParametro) {
        if (papel == null) {
            throw new IllegalArgumentException(nomeParametro + " não pode ser nulo — violação de contrato de programação");
        }
    }
}
