package gerard.dominio.campoaditivo;

import gerard.semantica.numero.NumeroInteiro;

import java.util.Optional;

/**
 * Invariante operatório de Vergnaud para transformação de medidas:
 * EstadoFinal = EstadoInicial + Transformacao.
 *
 * Objeto coordenador de escopo fechado (só os três papéis deste esquema) —
 * a mesma justificativa de InvarianteOperatorio (piloto de Composição de
 * Medidas, já congelado): a regra cruza três conceitos, então não pertence
 * a nenhum PapelQuantitativo isolado.
 *
 * resolverIncognita nunca decide sozinho se um valor calculado é aceitável:
 * ele delega a decisão ao próprio papel-alvo via posicionar(...), que já
 * sabe seu domínio numérico. É assim que "uma transformação negativa não
 * pode produzir um EstadoFinal negativo" é garantido — não por uma
 * verificação duplicada aqui, mas porque EstadoFinal.posicionar(valor
 * negativo) já rejeita, com diagnóstico, sem que este objeto precise saber
 * por quê.
 */
public final class InvarianteOperatorioTransformacao {

    private InvarianteOperatorioTransformacao() { }

    public static InvarianteOperatorioTransformacao transformacaoDeMedidas() {
        return new InvarianteOperatorioTransformacao();
    }

    public String explicar() { return "EstadoFinal = EstadoInicial + Transformacao"; }

    /** Verifica EstadoFinal = EstadoInicial + Transformacao. Só decide algo se os três já estiverem preenchidos. */
    public boolean verificar(PapelQuantitativo estadoInicial, PapelQuantitativo transformacao,
                              PapelQuantitativo estadoFinal) {
        if (!estadoInicial.estaPreenchido() || !transformacao.estaPreenchido() || !estadoFinal.estaPreenchido()) {
            return false;
        }
        int ei = estadoInicial.valorAtual().valorOuNull();
        int tr = transformacao.valorAtual().valorOuNull();
        int ef = estadoFinal.valorAtual().valorOuNull();
        return ef == ei + tr;
    }

    /**
     * Resolve o único papel incógnito entre os três a partir dos outros
     * dois já preenchidos, e tenta posicioná-lo — o resultado (aceito ou
     * rejeitado, com diagnóstico) é sempre decidido pelo próprio papel-alvo,
     * nunca por este método.
     *
     * @throws IllegalStateException se não houver exatamente um papel incógnito
     */
    public Optional<DiagnosticoErroPapel> resolverIncognita(PapelQuantitativo estadoInicial,
                                                              PapelQuantitativo transformacao,
                                                              PapelQuantitativo estadoFinal) {
        int incognitas = 0;
        if (estadoInicial.ehIncognita()) incognitas++;
        if (transformacao.ehIncognita()) incognitas++;
        if (estadoFinal.ehIncognita()) incognitas++;
        if (incognitas != 1) {
            throw new IllegalStateException(
                    "resolverIncognita exige exatamente um papel incógnito entre os três; encontrados: " + incognitas);
        }

        if (estadoFinal.ehIncognita()) {
            int calculado = estadoInicial.valorAtual().valorOuNull() + transformacao.valorAtual().valorOuNull();
            return estadoFinal.posicionar(new NumeroInteiro(calculado));
        }
        if (estadoInicial.ehIncognita()) {
            int calculado = estadoFinal.valorAtual().valorOuNull() - transformacao.valorAtual().valorOuNull();
            return estadoInicial.posicionar(new NumeroInteiro(calculado));
        }
        int calculado = estadoFinal.valorAtual().valorOuNull() - estadoInicial.valorAtual().valorOuNull();
        return transformacao.posicionar(new NumeroInteiro(calculado));
    }
}
