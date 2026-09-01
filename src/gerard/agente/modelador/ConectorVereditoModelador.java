package gerard.agente.modelador;

import gerard.agente.modelousuario.DiagnosticoTarefa;
import gerard.agente.modelousuario.NivelSuporte;
import gerard.campoaditivo.modelo.TipoSituacaoAditiva;
import gerard.dominio.atividade.RegistroFactualAcaoInstrumental;

/**
 * Porta de entrada de casos no Agente Modelador. O caminho arquitetural
 * vigente recebe diretamente o {@link RegistroFactualAcaoInstrumental} produzido
 * pelo proprietário semântico e apenas o projeta para o Modelo do Usuário,
 * sem recalcular C/E ou diagnóstico.
 *
 * Os campos "internalizado" e "probabilidadeSaberConteudo" seguem no valor
 * padrão (false/0.0) de propósito: dependem do teorema de Bayes e de uma
 * leitura de estabilização ao longo de várias tentativas (ver "Entrada
 * empírica para a ação 2" em agente-modelador.md) — cálculos distintos da
 * inferência via J48/PART + Apriori (essa parte da ação 2 já existe, ver
 * InferenciaRegrasModelador e a aba de Modelo do Usuário na Visão de
 * Pesquisador), e que ainda não têm quem os calcule. Decisão confirmada com
 * o usuário em 2026-07-21 e reafirmada em 2026-07-22.
 */
public class ConectorVereditoModelador {
    private final AgenteModelador agenteModelador;

    public ConectorVereditoModelador(AgenteModelador agenteModelador) {
        this.agenteModelador = agenteModelador;
    }

    /**
     * Recebe o mesmo registro produzido pelo proprietário semântico, sem
     * recalcular o veredito produzido pelo proprietário semântico.
     */
    public void registrarAcaoInstrumental(
            String idUsuario,
            RegistroFactualAcaoInstrumental registro,
            NivelSuporte suporteFactual,
            String idempotencyKey) {
        if (idUsuario == null || registro == null || registro.getCategoria() == null) {
            return;
        }
        DiagnosticoTarefa diagnostico = new DiagnosticoTarefa(
                registro.getCategoria().name() + ":" + registro.getAlvoSemantico());
        diagnostico.setRegraDeAcao(registro.getTarefaInteracao().name());
        diagnostico.setSuporte(suporteFactual);
        diagnostico.setActionId(registro.getActionId());
        diagnostico.setAvaliacao(registro.getResultado().name());
        diagnostico.setTipoErro(registro.getTipoDiagnosticoFactual().length() == 0
                ? null : registro.getTipoDiagnosticoFactual());
        diagnostico.setParticipantesSemanticos(registro.getParticipantesSemanticos());
        String chaveIdempotencia = idempotencyKey == null
                || idempotencyKey.trim().length() == 0
                        ? registro.getActionId() : idempotencyKey;
        agenteModelador.armazenarCaso(idUsuario, diagnostico, chaveIdempotencia);
    }

    public void registrarAcaoNeutra(String idUsuario, TipoSituacaoAditiva categoria, String regraDeAcao) {
        if (idUsuario == null || categoria == null) {
            return;
        }
        String tarefa = categoria.name() + ":" + (regraDeAcao == null ? "" : regraDeAcao.toLowerCase());
        DiagnosticoTarefa diagnostico = new DiagnosticoTarefa(tarefa);
        diagnostico.setSuporte(NivelSuporte.NENHUM);
        diagnostico.setRegraDeAcao(regraDeAcao);
        agenteModelador.armazenarCaso(idUsuario, diagnostico);
    }

}
