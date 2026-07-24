package gerard.agente.zdp;

import gerard.campoaditivo.modelo.TipoSituacaoAditiva;
import java.util.HashMap;
import java.util.Map;

/**
 * Agente Baseado em Modelo descrito em gerard-ajuda-adaptativa/references/
 * agente-zdp.md: decide a estratégia pedagógica a partir da Ação
 * Instrumental Avaliada (percebida do Agente Monitor).
 *
 * Implementa só a parte diretamente derivável da recorrência de erro/acerto
 * numa mesma tarefa — as regras da Tabela 50 do relatório de pesquisa 2026
 * "primeiro erro", "erro repetido" e "acerto após erro" (camadas N0-N2 e o
 * sinal de retirada progressiva). As camadas N3+ (ajuda representacional,
 * ponte entre representações, antecipação) exigem um catálogo de conteúdo
 * pedagógico e análise de padrões entre problemas que ainda não foram
 * decididos, e ficam de fora de propósito — mesmo critério já usado para
 * adiar a ação 2 do Agente Modelador.
 *
 * Síncrono, chamado diretamente após o Agente Monitor avaliar — não é uma
 * Thread produtor-consumidor (a especificação original descreve os agentes
 * assim, mas o Agente Monitor e a ação 1 do Agente Modelador já foram
 * implementados de forma síncrona; este agente segue o mesmo padrão já em
 * produção, por decisão explícita do usuário em 2026-07-22).
 *
 * Estado interno: só a contagem de erros consecutivos por (usuário, tarefa)
 * — é o "estado atual do mundo" mínimo que o ZDP precisa guardar para
 * distinguir "primeiro erro" de "erro repetido" (ver agente-zdp.md). Não lê
 * nem escreve o Modelo do Usuário; isso fica para quando as camadas N3+
 * forem decididas.
 */
public final class AgenteZDP {
    private final Map<String, Integer> errosConsecutivosPorTarefa = new HashMap<String, Integer>();

    /**
     * Decide a estratégia pedagógica para a ação já avaliada pelo Agente
     * Monitor (certo/errado) numa dada tarefa (categoria + papel-alvo).
     */
    public CamadaEstrategiaZDP decidirEstrategia(String idUsuario, TipoSituacaoAditiva categoria,
                                                  String chavePapelAlvo, boolean correto) {
        String chave = chaveTarefa(idUsuario, categoria, chavePapelAlvo);
        Integer errosAnteriores = errosConsecutivosPorTarefa.get(chave);
        int errosAtuais = errosAnteriores == null ? 0 : errosAnteriores.intValue();

        if (!correto) {
            errosConsecutivosPorTarefa.put(chave, Integer.valueOf(errosAtuais + 1));
            return errosAtuais == 0 ? CamadaEstrategiaZDP.QUESTIONAMENTO_LEVE
                    : CamadaEstrategiaZDP.AJUDA_ESPECIFICA;
        }

        errosConsecutivosPorTarefa.remove(chave);
        return errosAtuais > 0 ? CamadaEstrategiaZDP.RETIRADA_PROGRESSIVA
                : CamadaEstrategiaZDP.CONDUCAO_MINIMA;
    }

    private String chaveTarefa(String idUsuario, TipoSituacaoAditiva categoria, String chavePapelAlvo) {
        return (idUsuario == null ? "" : idUsuario) + "|"
                + (categoria == null ? "" : categoria.name()) + ":"
                + (chavePapelAlvo == null ? "" : chavePapelAlvo);
    }
}
