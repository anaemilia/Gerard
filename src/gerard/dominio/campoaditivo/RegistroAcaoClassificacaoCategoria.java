package gerard.dominio.campoaditivo;

import gerard.campoaditivo.modelo.TipoSituacaoAditiva;
import gerard.dominio.atividade.ContextoAcaoInstrumental;
import gerard.dominio.atividade.RegistroFactualAcaoInstrumental;
import gerard.dominio.atividade.ResultadoAvaliacaoAcaoInstrumental;
import gerard.dominio.atividade.TarefaInteracao;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/** Registro factual de uma ação da tentativa de classificar a situação. */
public final class RegistroAcaoClassificacaoCategoria
        implements RegistroFactualAcaoInstrumental {

    public enum TipoAcao {
        ESCOLHA_CATEGORIA,
        CONFIRMACAO_CATEGORIA_DIVERGENTE
    }

    public enum TipoDiagnostico {
        CATEGORIA_DIVERGENTE,
        CONCORDANCIA_COM_CATEGORIA_DIVERGENTE
    }

    public enum Desfecho {
        ACEITAR_CATEGORIA,
        QUESTIONAR_CATEGORIA_ESCOLHIDA,
        CONTINUAR_CLASSIFICACAO,
        REEXPLICAR_CATEGORIA_APOS_LIMITE
    }

    private final String actionId;
    private final String tentativaId;
    private final String situacaoId;
    private final TipoAcao tipoAcao;
    private final TipoSituacaoAditiva categoriaEsperada;
    private final TipoSituacaoAditiva categoriaEscolhida;
    private final Boolean concordouComCategoriaDivergente;
    private final ResultadoAvaliacaoAcaoInstrumental resultado;
    private final TipoDiagnostico diagnostico;
    private final Desfecho desfecho;
    private final int rejeicoesConsecutivas;
    private final String rejectionSequenceId;
    private final ContextoAcaoInstrumental contexto;
    private final String regraSemantica;
    private final List<String> participantesSemanticos;

    RegistroAcaoClassificacaoCategoria(
            String actionId,
            String tentativaId,
            String situacaoId,
            TipoAcao tipoAcao,
            TipoSituacaoAditiva categoriaEsperada,
            TipoSituacaoAditiva categoriaEscolhida,
            Boolean concordouComCategoriaDivergente,
            ResultadoAvaliacaoAcaoInstrumental resultado,
            TipoDiagnostico diagnostico,
            Desfecho desfecho,
            int rejeicoesConsecutivas,
            String rejectionSequenceId,
            ContextoAcaoInstrumental contexto,
            String regraSemantica) {
        this.actionId = obrigatorio(actionId, "action_id é obrigatório");
        this.tentativaId = obrigatorio(tentativaId, "tentativa é obrigatória");
        this.situacaoId = obrigatorio(situacaoId, "situação é obrigatória");
        if (tipoAcao == null || categoriaEsperada == null || resultado == null
                || desfecho == null || contexto == null) {
            throw new IllegalArgumentException(
                    "tipo, categoria esperada, resultado, desfecho e contexto são obrigatórios");
        }
        if (resultado == ResultadoAvaliacaoAcaoInstrumental.ERRADA
                && diagnostico == null) {
            throw new IllegalArgumentException("ação errada exige diagnóstico factual");
        }
        if (resultado != ResultadoAvaliacaoAcaoInstrumental.ERRADA
                && diagnostico != null) {
            throw new IllegalArgumentException("somente ação errada carrega diagnóstico");
        }
        this.tipoAcao = tipoAcao;
        this.categoriaEsperada = categoriaEsperada;
        this.categoriaEscolhida = categoriaEscolhida;
        this.concordouComCategoriaDivergente = concordouComCategoriaDivergente;
        this.resultado = resultado;
        this.diagnostico = diagnostico;
        this.desfecho = desfecho;
        this.rejeicoesConsecutivas = rejeicoesConsecutivas;
        this.rejectionSequenceId = limpar(rejectionSequenceId);
        this.contexto = contexto;
        this.regraSemantica = obrigatorio(regraSemantica,
                "regra semântica é obrigatória");

        Set<String> participantes = new LinkedHashSet<String>();
        participantes.add(TentativaClassificacaoCategoriaAditiva.CHAVE_PROPRIETARIO);
        participantes.add(getAlvoSemantico());
        participantes.add("tentativa.classificacao." + this.tentativaId);
        participantes.add("situacao." + this.situacaoId);
        participantes.add("categoria." + categoriaEsperada.name());
        if (categoriaEscolhida != null) {
            participantes.add("categoria." + categoriaEscolhida.name());
        }
        participantes.addAll(contexto.getParticipantesSemanticos());
        this.participantesSemanticos = Collections.unmodifiableList(
                new ArrayList<String>(participantes));
    }

    public String getActionId() { return actionId; }
    public OrigemAcao getOrigemAcao() { return OrigemAcao.ORIGEM_USUARIO; }
    public TarefaInteracao getTarefaInteracao() { return TarefaInteracao.SELECIONAR; }
    public TipoSituacaoAditiva getCategoria() { return categoriaEsperada; }
    public String getProprietarioSemantico() {
        return TentativaClassificacaoCategoriaAditiva.CHAVE_PROPRIETARIO;
    }
    public String getAlvoSemantico() {
        return tipoAcao == TipoAcao.ESCOLHA_CATEGORIA
                ? TentativaClassificacaoCategoriaAditiva.ALVO_ESCOLHA
                : TentativaClassificacaoCategoriaAditiva.ALVO_CONFIRMACAO;
    }
    public ResultadoAvaliacaoAcaoInstrumental getResultado() { return resultado; }
    public String getTipoDiagnosticoFactual() {
        return diagnostico == null ? "" : diagnostico.name();
    }
    public String getValorPropostoFactual() {
        if (tipoAcao == TipoAcao.ESCOLHA_CATEGORIA) {
            return categoriaEscolhida == null ? "" : categoriaEscolhida.name();
        }
        return Boolean.TRUE.equals(concordouComCategoriaDivergente) ? "SIM" : "NAO";
    }
    public String getValorEsperadoFactual() {
        return tipoAcao == TipoAcao.ESCOLHA_CATEGORIA
                ? categoriaEsperada.name() : "NAO";
    }
    public String getRegraSemantica() { return regraSemantica; }
    public ContextoAcaoInstrumental getContexto() { return contexto; }
    public List<String> getParticipantesSemanticos() { return participantesSemanticos; }
    public String getRejectionSequenceId() {
        return rejectionSequenceId.length() == 0 ? null : rejectionSequenceId;
    }

    public String getTentativaId() { return tentativaId; }
    public String getSituacaoId() { return situacaoId; }
    public TipoAcao getTipoAcao() { return tipoAcao; }
    public TipoSituacaoAditiva getCategoriaEsperada() { return categoriaEsperada; }
    public TipoSituacaoAditiva getCategoriaEscolhida() { return categoriaEscolhida; }
    public Boolean getConcordouComCategoriaDivergente() {
        return concordouComCategoriaDivergente;
    }
    public TipoDiagnostico getDiagnostico() { return diagnostico; }
    public Desfecho getDesfecho() { return desfecho; }
    public int getRejeicoesConsecutivas() { return rejeicoesConsecutivas; }
    public boolean foiCorreta() {
        return resultado == ResultadoAvaliacaoAcaoInstrumental.CORRETA;
    }
    public boolean foiErrada() {
        return resultado == ResultadoAvaliacaoAcaoInstrumental.ERRADA;
    }
    public boolean atingiuLimite() {
        return desfecho == Desfecho.REEXPLICAR_CATEGORIA_APOS_LIMITE;
    }

    private static String obrigatorio(String valor, String mensagem) {
        String normalizado = limpar(valor);
        if (normalizado.length() == 0) { throw new IllegalArgumentException(mensagem); }
        return normalizado;
    }

    private static String limpar(String valor) {
        return valor == null ? "" : valor.trim();
    }
}
