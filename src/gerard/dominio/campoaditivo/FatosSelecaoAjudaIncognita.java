package gerard.dominio.campoaditivo;

import java.util.Objects;

/**
 * Fatos já produzidos pelos respectivos proprietários de conhecimento e
 * entregues à incógnita para uma decisão local de scaffolding.
 *
 * <p>Este objeto não conta rejeições e não reavalia o valor proposto. O
 * diagnóstico vem do papel ou da relação estrutural; o ordinal vem da
 * sequência da tentativa.</p>
 */
public final class FatosSelecaoAjudaIncognita {

    private final DiagnosticoErroPapel diagnostico;
    private final int ordinalRejeicao;

    public FatosSelecaoAjudaIncognita(
            DiagnosticoErroPapel diagnostico,
            int ordinalRejeicao) {
        this.diagnostico = Objects.requireNonNull(
                diagnostico, "diagnóstico factual não pode ser nulo");
        if (ordinalRejeicao <= 0) {
            throw new IllegalArgumentException(
                    "ordinal da rejeição deve ser positivo e já calculado pela tentativa");
        }
        this.ordinalRejeicao = ordinalRejeicao;
    }

    public DiagnosticoErroPapel getDiagnostico() { return diagnostico; }
    public int getOrdinalRejeicao() { return ordinalRejeicao; }

    public String descreverParaRastreabilidade() {
        return "diagnostico=" + diagnostico.getTipo().name()
                + ";ordem_rejeicao=" + ordinalRejeicao;
    }
}
