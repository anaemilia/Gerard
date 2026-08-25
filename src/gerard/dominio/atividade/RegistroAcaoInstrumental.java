package gerard.dominio.atividade;

import gerard.campoaditivo.modelo.TipoSituacaoAditiva;
import gerard.dominio.campoaditivo.DiagnosticoErroPapel;
import gerard.dominio.campoaditivo.IdentidadeAcaoInstrumentalPapel;
import gerard.dominio.campoaditivo.OrigemAcao;
import gerard.dominio.campoaditivo.ResultadoRegistroTentativaPapel;
import gerard.semantica.numero.ValorNumerico;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * Registro factual único produzido pelo proprietário semântico da ação.
 * Participantes adicionais são referências no mesmo {@code action_id}.
 */
public final class RegistroAcaoInstrumental implements RegistroFactualAcaoInstrumental {
    private final IdentidadeAcaoInstrumentalPapel identidade;
    private final TarefaInteracao tarefaInteracao;
    private final TipoSituacaoAditiva categoria;
    private final String proprietarioSemantico;
    private final String alvoSemantico;
    private final ResultadoAvaliacaoAcaoInstrumental resultado;
    private final DiagnosticoErroPapel diagnostico;
    private final ValorNumerico valorProposto;
    private final ValorNumerico valorEsperado;
    private final String regraSemantica;
    private final ContextoAcaoInstrumental contexto;
    private final ResultadoRegistroTentativaPapel resultadoTentativa;
    private final List<String> participantesSemanticos;

    public RegistroAcaoInstrumental(
            IdentidadeAcaoInstrumentalPapel identidade,
            TarefaInteracao tarefaInteracao,
            TipoSituacaoAditiva categoria,
            String proprietarioSemantico,
            String alvoSemantico,
            ResultadoAvaliacaoAcaoInstrumental resultado,
            DiagnosticoErroPapel diagnostico,
            ValorNumerico valorProposto,
            ValorNumerico valorEsperado,
            String regraSemantica,
            ContextoAcaoInstrumental contexto,
            ResultadoRegistroTentativaPapel resultadoTentativa) {
        if (identidade == null || tarefaInteracao == null || resultado == null
                || contexto == null) {
            throw new IllegalArgumentException(
                    "identidade, protocolo, resultado e contexto são obrigatórios");
        }
        this.identidade = identidade;
        this.tarefaInteracao = tarefaInteracao;
        this.categoria = categoria;
        this.proprietarioSemantico = obrigatorio(proprietarioSemantico,
                "proprietário semântico é obrigatório");
        this.alvoSemantico = obrigatorio(alvoSemantico,
                "alvo semântico é obrigatório");
        if (resultado == ResultadoAvaliacaoAcaoInstrumental.ERRADA
                && diagnostico == null) {
            throw new IllegalArgumentException("ação errada exige diagnóstico factual");
        }
        if (resultado != ResultadoAvaliacaoAcaoInstrumental.ERRADA
                && diagnostico != null) {
            throw new IllegalArgumentException("somente ação errada carrega diagnóstico");
        }
        if (resultado == ResultadoAvaliacaoAcaoInstrumental.NAO_APLICAVEL
                && resultadoTentativa != null) {
            throw new IllegalArgumentException(
                    "ação sem critério não pode alterar a sequência de rejeições");
        }
        this.resultado = resultado;
        this.diagnostico = diagnostico;
        this.valorProposto = valorProposto;
        this.valorEsperado = valorEsperado;
        this.regraSemantica = obrigatorio(regraSemantica,
                "regra semântica é obrigatória");
        this.contexto = contexto;
        this.resultadoTentativa = resultadoTentativa;

        Set<String> participantes = new LinkedHashSet<String>();
        participantes.add(this.proprietarioSemantico);
        participantes.add(this.alvoSemantico);
        participantes.addAll(contexto.getParticipantesSemanticos());
        this.participantesSemanticos = Collections.unmodifiableList(
                new ArrayList<String>(participantes));
    }

    public IdentidadeAcaoInstrumentalPapel getIdentidade() { return identidade; }
    public String getActionId() { return identidade.getActionId(); }
    public OrigemAcao getOrigemAcao() { return identidade.getOrigem(); }
    public TarefaInteracao getTarefaInteracao() { return tarefaInteracao; }
    public TipoSituacaoAditiva getCategoria() { return categoria; }
    public String getProprietarioSemantico() { return proprietarioSemantico; }
    public String getAlvoSemantico() { return alvoSemantico; }
    public ResultadoAvaliacaoAcaoInstrumental getResultado() { return resultado; }
    public Optional<DiagnosticoErroPapel> getDiagnostico() {
        return Optional.ofNullable(diagnostico);
    }
    public String getTipoDiagnosticoFactual() {
        return diagnostico == null ? "" : diagnostico.getTipo().name();
    }
    public ValorNumerico getValorProposto() { return valorProposto; }
    public ValorNumerico getValorEsperado() { return valorEsperado; }
    public String getValorPropostoFactual() {
        return valorProposto == null ? "" : valorProposto.formatar(true);
    }
    public String getValorEsperadoFactual() {
        return valorEsperado == null ? "" : valorEsperado.formatar(true);
    }
    public String getRegraSemantica() { return regraSemantica; }
    public ContextoAcaoInstrumental getContexto() { return contexto; }
    public Optional<ResultadoRegistroTentativaPapel> getResultadoTentativa() {
        return Optional.ofNullable(resultadoTentativa);
    }
    public List<String> getParticipantesSemanticos() { return participantesSemanticos; }
    public boolean foiCorreta() {
        return resultado == ResultadoAvaliacaoAcaoInstrumental.CORRETA;
    }
    public boolean foiErrada() {
        return resultado == ResultadoAvaliacaoAcaoInstrumental.ERRADA;
    }
    public boolean possuiCriterioAplicavel() {
        return resultado != ResultadoAvaliacaoAcaoInstrumental.NAO_APLICAVEL;
    }
    public String getRejectionSequenceId() {
        return resultadoTentativa == null
                ? null : resultadoTentativa.getRejectionSequenceId();
    }

    private static String obrigatorio(String valor, String mensagem) {
        String normalizado = valor == null ? "" : valor.trim();
        if (normalizado.length() == 0) {
            throw new IllegalArgumentException(mensagem);
        }
        return normalizado;
    }
}
