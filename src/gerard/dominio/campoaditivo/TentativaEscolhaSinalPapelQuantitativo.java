package gerard.dominio.campoaditivo;

import gerard.campoaditivo.modelo.TipoSituacaoAditiva;
import gerard.dominio.atividade.ContextoAcaoInstrumental;
import gerard.dominio.atividade.ResultadoAvaliacaoAcaoInstrumental;
import gerard.semantica.numero.NumeroInteiro;
import gerard.semantica.numero.OpcaoSinalNumeroInteiro;
import java.util.UUID;

/**
 * Proprietário da avaliação do sinal escolhido para um papel quantitativo.
 *
 * <p>O papel identifica o conhecimento em jogo e o {@link NumeroInteiro}
 * esperado decide a correspondência do sinal. O objeto mantém apenas a
 * sequência factual das rejeições desse papel na situação corrente. Não
 * conhece Swing, persistência, Monitor ou ZDP.</p>
 */
public final class TentativaEscolhaSinalPapelQuantitativo {

    public static final String REGRA_SEMANTICA =
            "regra.numeroInteiro.sinalRepresentado";

    private final String tentativaId;
    private final String situacaoGrupoId;
    private final TipoSituacaoAditiva categoria;
    private final String chavePapel;
    private final NumeroInteiro numeroEsperado;
    private int rejeicoesConsecutivas;
    private String rejectionSequenceId;

    public TentativaEscolhaSinalPapelQuantitativo(
            String situacaoGrupoId,
            TipoSituacaoAditiva categoria,
            String chavePapel,
            NumeroInteiro numeroEsperado) {
        this.tentativaId = UUID.randomUUID().toString();
        this.situacaoGrupoId = obrigatorio(
                situacaoGrupoId,
                "identidade conceitual da situação é obrigatória");
        if (categoria == null || numeroEsperado == null) {
            throw new IllegalArgumentException(
                    "categoria e número esperado são obrigatórios");
        }
        this.categoria = categoria;
        this.chavePapel = obrigatorio(
                chavePapel, "papel semântico é obrigatório");
        this.numeroEsperado = numeroEsperado;
    }

    /**
     * Constitui e avalia uma única ação SELECIONAR. Cada chamada recebe novo
     * action_id; somente rejeições consecutivas compartilham a sequência.
     */
    public RegistroAcaoEscolhaSinalPapelQuantitativo avaliarEscolha(
            OpcaoSinalNumeroInteiro sinalEscolhido,
            ContextoAcaoInstrumental contexto) {
        if (sinalEscolhido == null || contexto == null) {
            throw new IllegalArgumentException(
                    "sinal escolhido e contexto instrumental são obrigatórios");
        }

        boolean correta = numeroEsperado.correspondeAoSinalRepresentado(
                sinalEscolhido);
        RegistroAcaoEscolhaSinalPapelQuantitativo.TipoDiagnostico diagnostico =
                null;
        ResultadoAvaliacaoAcaoInstrumental resultado;
        String sequencia = null;
        if (correta) {
            resultado = ResultadoAvaliacaoAcaoInstrumental.CORRETA;
            rejeicoesConsecutivas = 0;
            rejectionSequenceId = null;
        } else {
            resultado = ResultadoAvaliacaoAcaoInstrumental.ERRADA;
            diagnostico = RegistroAcaoEscolhaSinalPapelQuantitativo
                    .TipoDiagnostico.SINAL_DIVERGENTE_DO_PAPEL;
            if (rejectionSequenceId == null) {
                rejectionSequenceId = UUID.randomUUID().toString();
                rejeicoesConsecutivas = 0;
            }
            rejeicoesConsecutivas++;
            sequencia = rejectionSequenceId;
        }

        return new RegistroAcaoEscolhaSinalPapelQuantitativo(
                UUID.randomUUID().toString(), tentativaId, situacaoGrupoId,
                categoria, chavePapel, numeroEsperado, sinalEscolhido,
                resultado, diagnostico, rejeicoesConsecutivas, sequencia,
                contexto, REGRA_SEMANTICA);
    }

    public String getTentativaId() { return tentativaId; }
    public String getSituacaoGrupoId() { return situacaoGrupoId; }
    public TipoSituacaoAditiva getCategoria() { return categoria; }
    public String getChavePapel() { return chavePapel; }
    public NumeroInteiro getNumeroEsperado() { return numeroEsperado; }
    public int getRejeicoesConsecutivas() { return rejeicoesConsecutivas; }
    public String getRejectionSequenceIdAtual() { return rejectionSequenceId; }

    private static String obrigatorio(String valor, String mensagem) {
        String normalizado = valor == null ? "" : valor.trim();
        if (normalizado.length() == 0) {
            throw new IllegalArgumentException(mensagem);
        }
        return normalizado;
    }
}
