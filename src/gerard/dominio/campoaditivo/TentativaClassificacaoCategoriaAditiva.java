package gerard.dominio.campoaditivo;

import gerard.campoaditivo.modelo.SituacaoProblemaAditiva;
import gerard.campoaditivo.modelo.TipoSituacaoAditiva;
import gerard.dominio.atividade.ContextoAcaoInstrumental;
import gerard.dominio.atividade.ResultadoAvaliacaoAcaoInstrumental;
import java.util.UUID;

/**
 * Agregado da tentativa de classificar uma situação-problema aditiva.
 *
 * <p>A situação fornece sua categoria curada. Este agregado possui a
 * comparação com a escolha do participante, a confirmação sobre uma escolha
 * divergente e a sequência corrente de rejeições. Não conhece Swing,
 * persistência ou serviços externos de decisão.</p>
 */
public final class TentativaClassificacaoCategoriaAditiva {

    public static final String CHAVE_PROPRIETARIO = "tentativa.classificacao_categoria";
    public static final String ALVO_ESCOLHA = "situacao.categoria";
    public static final String ALVO_CONFIRMACAO = "situacao.categoria.confirmacao";
    public static final int LIMITE_REJEICOES_CONSECUTIVAS = 3;

    private final String tentativaId;
    private final String situacaoId;
    private final TipoSituacaoAditiva categoriaEsperada;
    private int rejeicoesConsecutivas;
    private String rejectionSequenceId;
    private TipoSituacaoAditiva categoriaDivergentePendente;
    private boolean encerrada;

    public TentativaClassificacaoCategoriaAditiva(SituacaoProblemaAditiva situacao) {
        if (situacao == null || situacao.getTipo() == null) {
            throw new IllegalArgumentException(
                    "situação curada e categoria são obrigatórias");
        }
        this.tentativaId = UUID.randomUUID().toString();
        this.situacaoId = obrigatorio(situacao.getId(),
                "identidade da situação é obrigatória");
        this.categoriaEsperada = situacao.getTipo();
    }

    public RegistroAcaoClassificacaoCategoria avaliarEscolha(
            TipoSituacaoAditiva categoriaEscolhida,
            ContextoAcaoInstrumental contexto) {
        exigirAtiva();
        if (contexto == null) {
            throw new IllegalArgumentException("contexto instrumental é obrigatório");
        }
        if (categoriaEscolhida == null) {
            throw new IllegalArgumentException("categoria escolhida é obrigatória");
        }
        boolean correta = categoriaEscolhida == categoriaEsperada;
        if (correta) {
            categoriaDivergentePendente = null;
            rejectionSequenceId = null;
            rejeicoesConsecutivas = 0;
            encerrada = true;
            return novoRegistro(
                    RegistroAcaoClassificacaoCategoria.TipoAcao.ESCOLHA_CATEGORIA,
                    categoriaEscolhida, null,
                    ResultadoAvaliacaoAcaoInstrumental.CORRETA, null,
                    RegistroAcaoClassificacaoCategoria.Desfecho.ACEITAR_CATEGORIA,
                    null, contexto,
                    "regra.classificacao.categoriaDaSituacao");
        }

        String sequencia = registrarRejeicao();
        boolean limite = rejeicoesConsecutivas >= LIMITE_REJEICOES_CONSECUTIVAS;
        categoriaDivergentePendente = limite ? null : categoriaEscolhida;
        if (limite) { encerrada = true; }
        return novoRegistro(
                RegistroAcaoClassificacaoCategoria.TipoAcao.ESCOLHA_CATEGORIA,
                categoriaEscolhida, null,
                ResultadoAvaliacaoAcaoInstrumental.ERRADA,
                RegistroAcaoClassificacaoCategoria.TipoDiagnostico.CATEGORIA_DIVERGENTE,
                limite
                        ? RegistroAcaoClassificacaoCategoria.Desfecho.REEXPLICAR_CATEGORIA_APOS_LIMITE
                        : RegistroAcaoClassificacaoCategoria.Desfecho.QUESTIONAR_CATEGORIA_ESCOLHIDA,
                sequencia, contexto,
                "regra.classificacao.categoriaDaSituacao");
    }

    public RegistroAcaoClassificacaoCategoria avaliarConfirmacaoCategoriaDivergente(
            boolean concordou,
            ContextoAcaoInstrumental contexto) {
        exigirAtiva();
        if (categoriaDivergentePendente == null) {
            throw new IllegalStateException(
                    "não há escolha divergente aguardando confirmação");
        }
        if (contexto == null) {
            throw new IllegalArgumentException("contexto instrumental é obrigatório");
        }
        TipoSituacaoAditiva categoriaConfirmada = categoriaDivergentePendente;
        categoriaDivergentePendente = null;
        if (!concordou) {
            return novoRegistro(
                    RegistroAcaoClassificacaoCategoria.TipoAcao.CONFIRMACAO_CATEGORIA_DIVERGENTE,
                    categoriaConfirmada, Boolean.FALSE,
                    ResultadoAvaliacaoAcaoInstrumental.CORRETA, null,
                    RegistroAcaoClassificacaoCategoria.Desfecho.CONTINUAR_CLASSIFICACAO,
                    null, contexto,
                    "regra.classificacao.reconhecerCategoriaDivergente");
        }

        String sequencia = registrarRejeicao();
        boolean limite = rejeicoesConsecutivas >= LIMITE_REJEICOES_CONSECUTIVAS;
        if (limite) { encerrada = true; }
        return novoRegistro(
                RegistroAcaoClassificacaoCategoria.TipoAcao.CONFIRMACAO_CATEGORIA_DIVERGENTE,
                categoriaConfirmada, Boolean.TRUE,
                ResultadoAvaliacaoAcaoInstrumental.ERRADA,
                RegistroAcaoClassificacaoCategoria.TipoDiagnostico
                        .CONCORDANCIA_COM_CATEGORIA_DIVERGENTE,
                limite
                        ? RegistroAcaoClassificacaoCategoria.Desfecho.REEXPLICAR_CATEGORIA_APOS_LIMITE
                        : RegistroAcaoClassificacaoCategoria.Desfecho.CONTINUAR_CLASSIFICACAO,
                sequencia, contexto,
                "regra.classificacao.reconhecerCategoriaDivergente");
    }

    public String getTentativaId() { return tentativaId; }
    public String getSituacaoId() { return situacaoId; }
    public TipoSituacaoAditiva getCategoriaEsperada() { return categoriaEsperada; }
    public int getRejeicoesConsecutivas() { return rejeicoesConsecutivas; }
    public String getRejectionSequenceIdAtual() { return rejectionSequenceId; }
    public boolean estaEncerrada() { return encerrada; }
    public boolean aguardaConfirmacao() { return categoriaDivergentePendente != null; }

    private String registrarRejeicao() {
        if (rejectionSequenceId == null) {
            rejectionSequenceId = UUID.randomUUID().toString();
            rejeicoesConsecutivas = 0;
        }
        rejeicoesConsecutivas++;
        return rejectionSequenceId;
    }

    private RegistroAcaoClassificacaoCategoria novoRegistro(
            RegistroAcaoClassificacaoCategoria.TipoAcao tipoAcao,
            TipoSituacaoAditiva categoriaEscolhida,
            Boolean concordou,
            ResultadoAvaliacaoAcaoInstrumental resultado,
            RegistroAcaoClassificacaoCategoria.TipoDiagnostico diagnostico,
            RegistroAcaoClassificacaoCategoria.Desfecho desfecho,
            String sequencia,
            ContextoAcaoInstrumental contexto,
            String regra) {
        return new RegistroAcaoClassificacaoCategoria(
                UUID.randomUUID().toString(), tentativaId, situacaoId, tipoAcao,
                categoriaEsperada, categoriaEscolhida, concordou, resultado,
                diagnostico, desfecho, rejeicoesConsecutivas, sequencia,
                contexto, regra);
    }

    private void exigirAtiva() {
        if (encerrada) {
            throw new IllegalStateException("a tentativa de classificação foi encerrada");
        }
    }

    private static String obrigatorio(String valor, String mensagem) {
        String normalizado = valor == null ? "" : valor.trim();
        if (normalizado.length() == 0) { throw new IllegalArgumentException(mensagem); }
        return normalizado;
    }
}
