package gerard.agente.modelador;

import gerard.agente.modelousuario.DiagnosticoTarefa;
import gerard.campoaditivo.modelo.TipoSituacaoAditiva;

/**
 * Ponte entre o veredito bruto do Agente Monitor (certo/errado de uma única
 * ação) e o Agente Modelador (armazena "casos" no Modelo do Usuário).
 *
 * Preenche só o campo "tarefa" do DiagnosticoTarefa. Os outros três campos
 * (suporte, internalizado, probabilidadeSaberConteudo) ficam no valor
 * padrão (null/false/0.0) de propósito: pela especificação
 * (gerard-modelo-usuario/SKILL.md e agente-modelador.md), nenhum dos três é
 * derivável de um único acerto/erro — dependem do teorema de Bayes e de
 * histórico de suporte, que fazem parte da ação 2 do Modelador (inferência
 * via J48.PART + APRIORI) e da Estratégia Pedagógica do Agente ZDP, nenhum
 * dos dois implementado ainda. Decisão confirmada com o usuário em
 * 2026-07-21.
 */
public class ConectorVereditoModelador {
    private final AgenteModelador agenteModelador;

    public ConectorVereditoModelador(AgenteModelador agenteModelador) {
        this.agenteModelador = agenteModelador;
    }

    public void registrarVeredito(String idUsuario, TipoSituacaoAditiva categoria, String chavePapelAlvo) {
        if (idUsuario == null || categoria == null || chavePapelAlvo == null) {
            return;
        }
        String tarefa = categoria.name() + ":" + chavePapelAlvo;
        agenteModelador.armazenarCaso(idUsuario, new DiagnosticoTarefa(tarefa));
    }
}
