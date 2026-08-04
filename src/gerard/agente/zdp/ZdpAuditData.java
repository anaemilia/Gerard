package gerard.agente.zdp;

import gerard.agente.conhecimento.RuleActivationAudit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Registro de auditoria de UMA decisão do AgenteZDP — entrada, estado antes
 * (a contagem de erro que realmente decide a camada, ver
 * errosConsecutivosPorTarefa em AgenteZDP), decisão, regras consultadas
 * (R-PED-001/002 via consultarConhecimento), saída e mudança de estado.
 * Montado DENTRO de decidirEstrategia, mesmos parâmetros que o método já
 * recebe.
 */
public final class ZdpAuditData {
    private final String eventoRecebido;
    private final String avaliacao;
    private final int errosConsecutivosAntes;
    private final int errosConsecutivosDepois;
    private final int errosTotaisAntes;
    private final int errosTotaisDepois;
    private final boolean ajudaPreviaAntes;
    private final boolean ajudaPreviaDepois;

    private final String intervencao;
    private final int nivelAjuda;
    private final boolean forneceResposta;
    private final boolean bloqueiaAcao;
    private final boolean permiteNovaTentativa;
    private final String justificativa;

    private final List<RuleActivationAudit> regrasAtivadas;

    private final String camadaEstrategia;

    private final long processingTimeMs;
    private final String erro;

    public ZdpAuditData(String eventoRecebido, String avaliacao, int errosConsecutivosAntes,
            int errosConsecutivosDepois, int errosTotaisAntes, int errosTotaisDepois, boolean ajudaPreviaAntes,
            boolean ajudaPreviaDepois, String intervencao, int nivelAjuda, boolean forneceResposta,
            boolean bloqueiaAcao, boolean permiteNovaTentativa, String justificativa,
            List<RuleActivationAudit> regrasAtivadas, String camadaEstrategia, long processingTimeMs, String erro) {
        this.eventoRecebido = eventoRecebido;
        this.avaliacao = avaliacao;
        this.errosConsecutivosAntes = errosConsecutivosAntes;
        this.errosConsecutivosDepois = errosConsecutivosDepois;
        this.errosTotaisAntes = errosTotaisAntes;
        this.errosTotaisDepois = errosTotaisDepois;
        this.ajudaPreviaAntes = ajudaPreviaAntes;
        this.ajudaPreviaDepois = ajudaPreviaDepois;
        this.intervencao = intervencao;
        this.nivelAjuda = nivelAjuda;
        this.forneceResposta = forneceResposta;
        this.bloqueiaAcao = bloqueiaAcao;
        this.permiteNovaTentativa = permiteNovaTentativa;
        this.justificativa = justificativa;
        this.regrasAtivadas = regrasAtivadas == null
                ? Collections.<RuleActivationAudit>emptyList() : new ArrayList<RuleActivationAudit>(regrasAtivadas);
        this.camadaEstrategia = camadaEstrategia;
        this.processingTimeMs = processingTimeMs;
        this.erro = erro;
    }

    public String getEventoRecebido() { return eventoRecebido; }
    public String getAvaliacao() { return avaliacao; }
    public int getErrosConsecutivosAntes() { return errosConsecutivosAntes; }
    public int getErrosConsecutivosDepois() { return errosConsecutivosDepois; }
    public int getErrosTotaisAntes() { return errosTotaisAntes; }
    public int getErrosTotaisDepois() { return errosTotaisDepois; }
    public boolean isAjudaPreviaAntes() { return ajudaPreviaAntes; }
    public boolean isAjudaPreviaDepois() { return ajudaPreviaDepois; }
    public String getIntervencao() { return intervencao; }
    public int getNivelAjuda() { return nivelAjuda; }
    public boolean isForneceResposta() { return forneceResposta; }
    public boolean isBloqueiaAcao() { return bloqueiaAcao; }
    public boolean isPermiteNovaTentativa() { return permiteNovaTentativa; }
    public String getJustificativa() { return justificativa; }
    public List<RuleActivationAudit> getRegrasAtivadas() { return Collections.unmodifiableList(regrasAtivadas); }
    public String getCamadaEstrategia() { return camadaEstrategia; }
    public long getProcessingTimeMs() { return processingTimeMs; }
    public String getErro() { return erro; }
}
