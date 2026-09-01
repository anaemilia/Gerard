package gerard.dominio.campoaditivo;


/**
 * Contrato comum das relações estruturais ternárias do campo aditivo.
 *
 * A interface uniformiza apenas a operação das relações. Cada implementação
 * continua sendo a autoridade sobre sua própria regra matemática.
 */
public interface RelacaoEstruturalAditiva {
    String descreverRelacao();

    EstadoConsistencia verificarConsistencia(PapelQuantitativo papel0,
            PapelQuantitativo papel1, PapelQuantitativo papel2);

    ResultadoCalculo calcularValorAusente(PapelQuantitativo papel0,
            PapelQuantitativo papel1, PapelQuantitativo papel2);

    ResultadoCalculo recalcularParaConsistencia(PapelQuantitativo papel0,
            PapelQuantitativo papel1, PapelQuantitativo papel2,
            PapelQuantitativo papelAlterado);

}
