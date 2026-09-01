package gerard.dominio.campoaditivo;

import gerard.semantica.numero.ValorNumerico;
import java.util.Optional;

/** Relação ternária que possui autoridade para diagnosticar uma proposta. */
public interface RelacaoEstruturalDiagnosticavel extends RelacaoEstruturalAditiva {
    Optional<DiagnosticoErroPapel> diagnosticarValorProposto(
            PapelQuantitativo papel0, PapelQuantitativo papel1,
            PapelQuantitativo papel2, PapelQuantitativo papelAlvo,
            ValorNumerico valorProposto);
}
