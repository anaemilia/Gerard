package gerard.dominio.campoaditivo;

import gerard.semantica.numero.ValorNumerico;

import java.util.Objects;

/**
 * Resultado de um cálculo realizado por uma relação estrutural formal —
 * nunca aplicado automaticamente ao papel calculado. A relação estrutural
 * calcula um valor candidato e devolve este objeto; quem decide se o valor
 * será apresentado, sugerido, validado ou efetivamente posicionado é quem
 * chama calcularValorAusente(...), nunca a própria relação (ver
 * RelacaoEstruturalTransformacao.aplicar(...)).
 *
 * Esta separação existe para que um valor calculado pelo sistema nunca seja
 * registrado com a mesma origem de um valor digitado pelo estudante — ver
 * OrigemAcao e o relatório técnico, seção "Origem das ações".
 */
public final class ResultadoCalculo {

    private final PapelQuantitativo papelCalculado;
    private final ValorNumerico valorCalculado; // null quando estadoConsistencia != CONSISTENTE
    private final String relacaoEstruturalUtilizada;
    private final EstadoConsistencia estadoConsistencia;
    private final String justificativa;
    private final OrigemAcao origem;

    ResultadoCalculo(PapelQuantitativo papelCalculado, ValorNumerico valorCalculado,
                      String relacaoEstruturalUtilizada, EstadoConsistencia estadoConsistencia,
                      String justificativa, OrigemAcao origem) {
        this.papelCalculado = papelCalculado;
        this.valorCalculado = valorCalculado;
        this.relacaoEstruturalUtilizada = Objects.requireNonNull(relacaoEstruturalUtilizada,
                "relacaoEstruturalUtilizada não pode ser nula");
        this.estadoConsistencia = Objects.requireNonNull(estadoConsistencia, "estadoConsistencia não pode ser nulo");
        this.justificativa = justificativa;
        this.origem = Objects.requireNonNull(origem, "origem não pode ser nula");
    }

    public PapelQuantitativo getPapelCalculado() { return papelCalculado; }
    public ValorNumerico getValorCalculado() { return valorCalculado; }
    public String getRelacaoEstruturalUtilizada() { return relacaoEstruturalUtilizada; }
    public EstadoConsistencia getEstadoConsistencia() { return estadoConsistencia; }
    public String getJustificativa() { return justificativa; }
    public OrigemAcao getOrigem() { return origem; }

    public boolean temValorCalculavel() {
        return estadoConsistencia == EstadoConsistencia.CONSISTENTE && valorCalculado != null;
    }

    @Override
    public String toString() {
        return estadoConsistencia + (valorCalculado != null ? " valor=" + valorCalculado.formatar(true) : "")
                + " origem=" + origem;
    }
}
