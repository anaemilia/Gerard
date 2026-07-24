package gerard.agente.zdp;

/**
 * Camada de estratégia pedagógica escolhida pelo Agente ZDP para uma ação
 * avaliada pelo Agente Monitor. Subconjunto implementável agora das camadas
 * N0-N7 descritas em gerard-ajuda-adaptativa/references/agente-zdp.md
 * (Tabela 49 do relatório de pesquisa 2026) — as camadas N3 em diante (ajuda
 * sobre o modelo, ajuda representacional, ponte entre representações,
 * antecipação) dependem de um catálogo de conteúdo pedagógico e de análise
 * de padrões entre problemas que ainda não foram decididos, e ficam de fora
 * de propósito, pelo mesmo critério já usado para adiar a ação 2 do Agente
 * Modelador.
 */
public enum CamadaEstrategiaZDP {
    /** N0 — sem erro recente nessa tarefa: só conduz o fluxo normal. */
    CONDUCAO_MINIMA,
    /** N1 — primeiro erro nessa tarefa: questionamento leve, sem elaborar a causa. */
    QUESTIONAMENTO_LEVE,
    /** N2 — erro repetido na mesma tarefa: a mensagem anterior não bastou, precisa ser mais específica. */
    AJUDA_ESPECIFICA,
    /** Acerto após erro(s) recentes na mesma tarefa: indício de correção — retirar o apoio gradualmente. */
    RETIRADA_PROGRESSIVA
}
