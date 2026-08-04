package gerard.pesquisador.analiseunidade;

import gerard.pesquisador.auditoria.OrigemAvaliacao;

/**
 * Os seis tipos de protocolo da unidade de análise A-B-C-D (pacote
 * 2026-07-31, PROMPT_CLAUDE_UNIDADE_ANALISE_ABCD_EXPLICACOES_OPCIONAIS.md).
 *
 * Achado real ao mapear contra o código existente (não inventado): o Gérard
 * já usa exatamente estes seis nomes como rótulo de {@code registrarAcaoGranular}
 * em Main.java (SELECIONAR/POSICIONAR/ORIENTACAO/CAMINHO/TEXTO/QUANTIFICAR),
 * mas isso é telemetria de interação (mouse), não avaliação de agente.
 * {@link #deOrigem(OrigemAvaliacao)} mapeia o outro vocabulário existente —
 * {@link OrigemAvaliacao}, que É avaliado pelos três agentes — usando a
 * MESMA string que Main.java já passa pra
 * {@code ConectorVereditoModelador.registrarVeredito} no respectivo ponto de
 * chamada (conferido lendo o código, não suposto):
 * <ul>
 * <li>SOLTURA_USUARIO → POSICIONAR (mouseReleased chama registrarVeredito
 * com "POSICIONAR")</li>
 * <li>SELECAO_CATEGORIA → SELECIONAR (clicarAtalhoCategoria chama com
 * "SELECIONAR")</li>
 * <li>SELECAO_SINAL → SELECIONAR (escolha de sinal do número relativo chama
 * com "SELECIONAR")</li>
 * <li>QUANTIFICACAO → TEXTO (confirmarValorIncognitaAceito chama com
 * "TEXTO", não "QUANTIFICAR" — o Gérard real trata a confirmação do valor
 * digitado como ação de texto, mesmo sendo um valor numérico; ver limitação
 * no relatório)</li>
 * <li>SOLICITACAO_AJUDA → sem mapeamento (null) — pedir ajuda não é uma
 * classificação de SELECIONAR/POSICIONAR/ORIENTAR/QUANTIFICAR/CAMINHO/TEXTO,
 * é uma solicitação de apoio pedagógico. Achado adicional: este valor do
 * enum nunca é instanciado em nenhum ponto de chamada real de Main.java
 * (confirmado por busca no código) — é um caso teórico, não observável
 * ainda no Gérard atual.</li>
 * </ul>
 * ORIENTAR, CAMINHO e QUANTIFICAR (como tipo distinto de TEXTO) nunca
 * aparecem como protocol_type de uma B_user_action nesta versão do Gérard —
 * só existem como telemetria granular (registrarAcaoGranular), nunca ligados
 * a uma avaliação de agente. Ver matriz dos seis protocolos no relatório.
 */
public enum TipoProtocolo {
    SELECIONAR,
    POSICIONAR,
    ORIENTAR,
    QUANTIFICAR,
    CAMINHO,
    TEXTO;

    public static TipoProtocolo deOrigem(OrigemAvaliacao origem) {
        if (origem == null) {
            return null;
        }
        switch (origem) {
            case SOLTURA_USUARIO:
                return POSICIONAR;
            case SELECAO_CATEGORIA:
                return SELECIONAR;
            case SELECAO_SINAL:
                return SELECIONAR;
            case QUANTIFICACAO:
                return TEXTO;
            default:
                return null;
        }
    }

    /** Motivo honesto quando {@link #deOrigem} devolve null pra uma origem canônica. */
    public static String motivoSemProtocolo(OrigemAvaliacao origem) {
        if (origem == OrigemAvaliacao.SOLICITACAO_AJUDA) {
            return "solicitacao_de_ajuda_nao_e_uma_acao_classificavel_nos_seis_protocolos";
        }
        return "origem_nao_canonica_nao_produz_instancia_de_protocolo";
    }
}
