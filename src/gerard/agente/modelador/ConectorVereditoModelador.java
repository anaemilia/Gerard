package gerard.agente.modelador;

import gerard.agente.modelousuario.DiagnosticoTarefa;
import gerard.agente.modelousuario.NivelSuporte;
import gerard.agente.zdp.CamadaEstrategiaZDP;
import gerard.campoaditivo.modelo.TipoSituacaoAditiva;

/**
 * Ponte entre a Estratégia Pedagógica do Agente ZDP (percepção formal do
 * Agente Modelador, ver agente-modelador.md) e o Agente Modelador (armazena
 * "casos" no Modelo do Usuário).
 *
 * Preenche os campos "tarefa", "suporte" e "regraDeAcao" do
 * DiagnosticoTarefa. "suporte" vem da camada de estratégia decidida pelo
 * Agente ZDP (QUESTIONAMENTO_LEVE/AJUDA_ESPECIFICA viram PARCIAL;
 * CONDUCAO_MINIMA/RETIRADA_PROGRESSIVA viram NENHUM; nenhuma camada
 * implementada hoje produz TOTAL, que corresponde a ajuda
 * concreta/automatização — ver camadas N3+ em agente-zdp.md, ainda não
 * decididas). "regraDeAcao" usa o mesmo vocabulário de Tarefa de Interação
 * de Shneiderman já usado no log (SELECIONAR, POSICIONAR, ...; ver
 * gerard-log-acao-instrumental).
 *
 * Dois métodos, porque nem toda ação instrumental tem veredito certo/errado
 * (ver AgenteMonitor.perceberAcao): registrarVeredito é para ações
 * avaliáveis (POSICIONAR, hoje); registrarAcaoNeutra é para ações sem
 * papel-alvo ainda, como SELECIONAR — sempre suporte=NENHUM, sem passar
 * pela camada do ZDP (que pressupõe recorrência de erro/acerto,
 * inaplicável aqui).
 *
 * Os campos "internalizado" e "probabilidadeSaberConteudo" seguem no valor
 * padrão (false/0.0) de propósito: dependem do teorema de Bayes e de uma
 * leitura de estabilização ao longo de várias tentativas (ver "Entrada
 * empírica para a ação 2" em agente-modelador.md) — cálculos distintos da
 * inferência via J48.PART + APRIORI (essa parte da ação 2 já existe, ver
 * InferenciaRegrasModelador e a aba de Modelo do Usuário na Visão de
 * Pesquisador), e que ainda não têm quem os calcule. Decisão confirmada com
 * o usuário em 2026-07-21 e reafirmada em 2026-07-22.
 */
public class ConectorVereditoModelador {
    private final AgenteModelador agenteModelador;

    public ConectorVereditoModelador(AgenteModelador agenteModelador) {
        this.agenteModelador = agenteModelador;
    }

    public void registrarVeredito(String idUsuario, TipoSituacaoAditiva categoria, String chavePapelAlvo,
                                   CamadaEstrategiaZDP estrategia, String regraDeAcao) {
        if (idUsuario == null || categoria == null || chavePapelAlvo == null) {
            return;
        }
        String tarefa = categoria.name() + ":" + chavePapelAlvo;
        DiagnosticoTarefa diagnostico = new DiagnosticoTarefa(tarefa);
        diagnostico.setSuporte(mapearSuporte(estrategia));
        diagnostico.setRegraDeAcao(regraDeAcao);
        agenteModelador.armazenarCaso(idUsuario, diagnostico);
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

    private NivelSuporte mapearSuporte(CamadaEstrategiaZDP estrategia) {
        if (estrategia == CamadaEstrategiaZDP.QUESTIONAMENTO_LEVE
                || estrategia == CamadaEstrategiaZDP.AJUDA_ESPECIFICA) {
            return NivelSuporte.PARCIAL;
        }
        return NivelSuporte.NENHUM;
    }
}
