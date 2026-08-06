package gerard.dominio.campoaditivo;

/**
 * Tipos de erro que um PapelQuantitativo, ou uma RelacaoEstruturalX ao
 * avaliar um valor proposto para um de seus papéis, sabe diagnosticar.
 *
 * OPERACAO_INVERTIDA e VALOR_INCORRETO (2026-08-06) vêm de
 * diagnosticarValorProposto — método que avalia um valor PROPOSTO para o
 * papel-alvo contra o valor correto calculado a partir dos outros dois
 * papéis, distinção que calcularValorAusente sozinho não faz (ele só
 * preenche o que falta, nunca avalia o que o estudante digitou).
 */
public enum TipoErroPapel {
    /** O valor proposto não é aceito pelo domínio numérico do papel (ex.: negativo onde só se aceita natural). */
    VALOR_FORA_DO_DOMINIO,
    /**
     * O valor proposto bate exatamente com o resultado de usar a operação
     * oposta à correta (ex.: subtrair quando a relação pede somar, ou
     * vice-versa). Categoria de erro descrita na literatura de Vergnaud
     * sobre situações aditivas como escolha equivocada de operação — mais
     * específica e mais acionável pedagogicamente do que "valor errado
     * genérico": o estudante entendeu que os três papéis se relacionam,
     * mas escolheu a operação inversa da que a relação exige.
     */
    OPERACAO_INVERTIDA,
    /** O valor proposto está no domínio aceito, não bate com o correto, e não reconhece o padrão de OPERACAO_INVERTIDA. */
    VALOR_INCORRETO
}
