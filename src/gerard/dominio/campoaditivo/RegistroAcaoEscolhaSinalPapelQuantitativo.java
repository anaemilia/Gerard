package gerard.dominio.campoaditivo;

import gerard.campoaditivo.modelo.TipoSituacaoAditiva;
import gerard.dominio.atividade.ContextoAcaoInstrumental;
import gerard.dominio.atividade.RegistroFactualAcaoInstrumental;
import gerard.dominio.atividade.ResultadoAvaliacaoAcaoInstrumental;
import gerard.dominio.atividade.TarefaInteracao;
import gerard.semantica.numero.NumeroInteiro;
import gerard.semantica.numero.OpcaoSinalNumeroInteiro;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/** Registro factual produzido pelo papel ao avaliar a escolha de sinal. */
public final class RegistroAcaoEscolhaSinalPapelQuantitativo
        implements RegistroFactualAcaoInstrumental {

    public enum TipoDiagnostico {
        SINAL_DIVERGENTE_DO_PAPEL
    }

    private final String actionId;
    private final String tentativaId;
    private final String situacaoGrupoId;
    private final TipoSituacaoAditiva categoria;
    private final String chavePapel;
    private final NumeroInteiro numeroEsperado;
    private final OpcaoSinalNumeroInteiro sinalEscolhido;
    private final ResultadoAvaliacaoAcaoInstrumental resultado;
    private final TipoDiagnostico diagnostico;
    private final int rejeicoesConsecutivas;
    private final String rejectionSequenceId;
    private final ContextoAcaoInstrumental contexto;
    private final String regraSemantica;
    private final List<String> participantesSemanticos;

    RegistroAcaoEscolhaSinalPapelQuantitativo(
            String actionId,
            String tentativaId,
            String situacaoGrupoId,
            TipoSituacaoAditiva categoria,
            String chavePapel,
            NumeroInteiro numeroEsperado,
            OpcaoSinalNumeroInteiro sinalEscolhido,
            ResultadoAvaliacaoAcaoInstrumental resultado,
            TipoDiagnostico diagnostico,
            int rejeicoesConsecutivas,
            String rejectionSequenceId,
            ContextoAcaoInstrumental contexto,
            String regraSemantica) {
        this.actionId = obrigatorio(actionId, "action_id é obrigatório");
        this.tentativaId = obrigatorio(tentativaId, "tentativa é obrigatória");
        this.situacaoGrupoId = obrigatorio(
                situacaoGrupoId,
                "identidade conceitual da situação é obrigatória");
        this.chavePapel = obrigatorio(
                chavePapel, "papel semântico é obrigatório");
        if (categoria == null || numeroEsperado == null
                || sinalEscolhido == null || resultado == null
                || contexto == null) {
            throw new IllegalArgumentException(
                    "categoria, número, sinal, resultado e contexto são obrigatórios");
        }
        if (resultado == ResultadoAvaliacaoAcaoInstrumental.ERRADA
                && diagnostico == null) {
            throw new IllegalArgumentException("ação errada exige diagnóstico factual");
        }
        if (resultado != ResultadoAvaliacaoAcaoInstrumental.ERRADA
                && diagnostico != null) {
            throw new IllegalArgumentException(
                    "somente ação errada carrega diagnóstico");
        }
        this.categoria = categoria;
        this.numeroEsperado = numeroEsperado;
        this.sinalEscolhido = sinalEscolhido;
        this.resultado = resultado;
        this.diagnostico = diagnostico;
        this.rejeicoesConsecutivas = rejeicoesConsecutivas;
        this.rejectionSequenceId = limpar(rejectionSequenceId);
        this.contexto = contexto;
        this.regraSemantica = obrigatorio(
                regraSemantica, "regra semântica é obrigatória");

        Set<String> participantes = new LinkedHashSet<String>();
        participantes.add(this.chavePapel);
        participantes.add(getAlvoSemantico());
        participantes.add("tentativa.sinal." + this.tentativaId);
        participantes.add("situacao_grupo." + this.situacaoGrupoId);
        participantes.addAll(contexto.getParticipantesSemanticos());
        this.participantesSemanticos = Collections.unmodifiableList(
                new ArrayList<String>(participantes));
    }

    public String getActionId() { return actionId; }
    public OrigemAcao getOrigemAcao() { return OrigemAcao.ORIGEM_USUARIO; }
    public TarefaInteracao getTarefaInteracao() { return TarefaInteracao.SELECIONAR; }
    public TipoSituacaoAditiva getCategoria() { return categoria; }
    public String getProprietarioSemantico() { return chavePapel; }
    public String getAlvoSemantico() { return chavePapel + ".sinal"; }
    public ResultadoAvaliacaoAcaoInstrumental getResultado() { return resultado; }
    public String getTipoDiagnosticoFactual() {
        return diagnostico == null ? "" : diagnostico.name();
    }
    public String getValorPropostoFactual() { return sinalEscolhido.getSimbolo(); }
    public String getValorEsperadoFactual() {
        return numeroEsperado.sinalParaRepresentacaoBinaria().getSimbolo();
    }
    public String getRegraSemantica() { return regraSemantica; }
    public ContextoAcaoInstrumental getContexto() { return contexto; }
    public List<String> getParticipantesSemanticos() { return participantesSemanticos; }
    public String getRejectionSequenceId() {
        return rejectionSequenceId.length() == 0 ? null : rejectionSequenceId;
    }

    public String getTentativaId() { return tentativaId; }
    public String getSituacaoGrupoId() { return situacaoGrupoId; }
    public String getChavePapel() { return chavePapel; }
    public NumeroInteiro getNumeroEsperado() { return numeroEsperado; }
    public OpcaoSinalNumeroInteiro getSinalEscolhido() { return sinalEscolhido; }
    public TipoDiagnostico getDiagnostico() { return diagnostico; }
    public int getRejeicoesConsecutivas() { return rejeicoesConsecutivas; }
    public boolean foiCorreta() {
        return resultado == ResultadoAvaliacaoAcaoInstrumental.CORRETA;
    }
    public boolean foiErrada() {
        return resultado == ResultadoAvaliacaoAcaoInstrumental.ERRADA;
    }

    private static String obrigatorio(String valor, String mensagem) {
        String normalizado = limpar(valor);
        if (normalizado.length() == 0) {
            throw new IllegalArgumentException(mensagem);
        }
        return normalizado;
    }

    private static String limpar(String valor) {
        return valor == null ? "" : valor.trim();
    }
}
