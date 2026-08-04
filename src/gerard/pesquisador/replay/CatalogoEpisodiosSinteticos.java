package gerard.pesquisador.replay;

import gerard.campoaditivo.modelo.TipoSituacaoAditiva;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Episódios SINTÉTICOS — ao contrário de dados/protocolos_reais_replay.tsv, não
 * são replay de gente real. São amplificações deliberadas dos MESMOS tipos
 * de erro reais já confirmados nas 16 sessões reais (todo→parte na
 * composição, estadoFinal/estadoInicial trocados com o número relativo na
 * transformação, referente↔referido na comparação), repetidas muitas vezes
 * para dar ao Agente Modelador (ação 2, PART+Apriori) volume suficiente por
 * (categoria, papel-alvo) para induzir uma regra condicional de verdade —
 * nenhuma das 16 sessões reais tem volume pra isso sozinha (rodando
 * TesteReplayProtocolosReais, as 16 produzem só a regra trivial "sempre
 * NENHUM", ver Number of Rules: 1 em cada uma).
 *
 * Cada engano se repete 3 vezes antes de ser corrigido (não só uma) —
 * padrão também visto de verdade (Valderlangela repetiu o mesmo erro 5
 * vezes seguidas antes de acertar, ver dados/protocolos_reais_replay.tsv). Isso
 * importa pro PART: como "tarefa" é indexado pelo ALVO tocado (não pelo
 * numeral arrastado), o alvo do engano acaba recebendo também o acerto
 * legítimo mais tarde no mesmo episódio (quando o papel certo é arrastado
 * pra ele) — com só 1 repetição isso empata 50/50 (sem sinal nenhum pra
 * árvore aprender); com 3 repetições do erro, esse alvo fica
 * majoritariamente errado (~75%), dando ao PART uma diferença real para
 * separar dos alvos sempre certos.
 *
 * Pedido explícito da usuária, 2026-07-30: "é apenas um teste para mim
 * mesma, não é para generalizar" — por isso os ids usam prefixo "sint_"
 * (nunca "hist_"), em todo lugar, pra nunca serem confundidos com dado real
 * numa leitura futura deste catálogo. Não deve ser citado como evidência de
 * nada sobre usuários reais do Gérard — existe só para provar que o
 * mecanismo de inferência de regras funciona quando há repetição
 * suficiente, coisa que o corpus histórico real disponível hoje não tem.
 */
public final class CatalogoEpisodiosSinteticos {
    private static final String PARTE1 = "papel.parte1";
    private static final String PARTE2 = "papel.parte2";
    private static final String TODO = "papel.todo";
    private static final String ESTADO_INICIAL = "papel.estadoInicial";
    private static final String TRANSFORMACAO = "papel.transformacao";
    private static final String ESTADO_FINAL = "papel.estadoFinal";
    private static final String REFERENTE = "papel.referente";
    private static final String REFERIDO = "papel.referido";
    private static final String DIFERENCA = "papel.diferenca";

    private CatalogoEpisodiosSinteticos() {
    }

    public static List<EpisodioReplayHumano> obterTodos() {
        List<EpisodioReplayHumano> todos = new ArrayList<EpisodioReplayHumano>();
        todos.addAll(episodiosComposicaoConfusa());
        todos.addAll(episodiosTransformacaoConfusa());
        todos.addAll(episodiosComparacaoConfusa());
        return todos;
    }

    /**
     * Amplifica o erro real "todo→parte" (visto em Valderlangela, Aldenira,
     * Felipe Wanderley, Experimento II, entre outros): confunde
     * sistematicamente o todo com a primeira parte, 3 vezes antes de
     * corrigir, em problemas de composição diferentes — dando ao PART
     * tarefa=COMPOSICAO_MEDIDAS:papel.parte1 majoritariamente errada
     * (~75%), contra papel.todo e papel.parte2 sempre certas.
     */
    private static List<EpisodioReplayHumano> episodiosComposicaoConfusa() {
        String u = "sint_composicao_confusa";
        String[] rotulos = {
                "vestidos de Ana", "figurinhas de Lucas", "cadeiras da sala", "bombons e balas",
                "CDs de Marta", "livros da estante", "canetas da caixa", "flores do jardim",
                "moedas da carteira", "lápis do estojo"
        };
        List<EpisodioReplayHumano> lista = new ArrayList<EpisodioReplayHumano>();
        for (int i = 0; i < rotulos.length; i++) {
            lista.add(new EpisodioReplayHumano(u, TipoSituacaoAditiva.COMPOSICAO_MEDIDAS,
                    "Sintético - Composição confusa (" + rotulos[i] + ")", Arrays.asList(
                    passo(TODO, PARTE1, false, "arrasta o todo para uma das partes (1a tentativa)"),
                    passo(TODO, PARTE1, false, "repete o mesmo engano (2a tentativa)"),
                    passo(TODO, PARTE1, false, "repete o mesmo engano de novo (3a tentativa)"),
                    passo(TODO, TODO, true, "finalmente corrige e arrasta o todo para o todo"),
                    passo(PARTE1, PARTE1, true, "arrasta a primeira parte corretamente"),
                    passo(PARTE2, PARTE2, true, "arrasta a segunda parte corretamente")
            )));
        }
        return lista;
    }

    /**
     * Amplifica o erro real de trocar estado final com o estado inicial na
     * transformação (visto em Glaudionor, Michele, Jamile S9, Experimento
     * II, FelipeWanderley 19-07-10): dando ao PART tarefa=
     * TRANSFORMACAO_MEDIDAS:papel.estadoInicial majoritariamente errada,
     * contra papel.estadoFinal e papel.transformacao sempre certas.
     */
    private static List<EpisodioReplayHumano> episodiosTransformacaoConfusa() {
        String u = "sint_transformacao_confusa";
        String[] rotulos = {
                "Pedro/selos", "Maria/figurinhas", "Rafael/refrigerantes", "João/bolinhas de gude",
                "Carla/adesivos", "Beto/figurinhas", "Sônia/botões", "Nando/cartas",
                "Vera/chaveiros", "Igor/moedas"
        };
        List<EpisodioReplayHumano> lista = new ArrayList<EpisodioReplayHumano>();
        for (int i = 0; i < rotulos.length; i++) {
            lista.add(new EpisodioReplayHumano(u, TipoSituacaoAditiva.TRANSFORMACAO_MEDIDAS,
                    "Sintético - Transformação confusa (" + rotulos[i] + ")", Arrays.asList(
                    passo(ESTADO_FINAL, ESTADO_INICIAL, false, "arrasta o estado final para o estado inicial (1a tentativa)"),
                    passo(ESTADO_FINAL, ESTADO_INICIAL, false, "repete o mesmo engano (2a tentativa)"),
                    passo(ESTADO_FINAL, ESTADO_INICIAL, false, "repete o mesmo engano de novo (3a tentativa)"),
                    passo(ESTADO_FINAL, ESTADO_FINAL, true, "finalmente corrige e arrasta o estado final para o lugar certo"),
                    passo(ESTADO_INICIAL, ESTADO_INICIAL, true, "arrasta o estado inicial corretamente"),
                    passo(TRANSFORMACAO, TRANSFORMACAO, true, "arrasta o número relativo corretamente")
            )));
        }
        return lista;
    }

    /**
     * Amplifica o erro real de confundir referente/referido na comparação
     * (visto em Valderlangela, Aldenira, Augusto, Glaudionor, Felipe
     * Wanderley, Jamile, entre outros): dando ao PART tarefa=
     * COMPARACAO_MEDIDAS:papel.referido majoritariamente errada, contra
     * papel.referente e papel.diferenca sempre certas.
     */
    private static List<EpisodioReplayHumano> episodiosComparacaoConfusa() {
        String u = "sint_comparacao_confusa";
        String[] rotulos = {
                "Carlos/Luiz", "Ingrid/Ligiane", "Glaudenice/Nádia", "Jammes/Gisele",
                "Ana/Bia", "Caio/Davi", "Elisa/Flávia", "Gustavo/Hugo",
                "Ivo/Júlia", "Karina/Lia"
        };
        List<EpisodioReplayHumano> lista = new ArrayList<EpisodioReplayHumano>();
        for (int i = 0; i < rotulos.length; i++) {
            lista.add(new EpisodioReplayHumano(u, TipoSituacaoAditiva.COMPARACAO_MEDIDAS,
                    "Sintético - Comparação confusa (" + rotulos[i] + ")", Arrays.asList(
                    passo(DIFERENCA, REFERIDO, false, "arrasta a diferença para o referido (1a tentativa)"),
                    passo(DIFERENCA, REFERIDO, false, "repete o mesmo engano (2a tentativa)"),
                    passo(DIFERENCA, REFERIDO, false, "repete o mesmo engano de novo (3a tentativa)"),
                    passo(DIFERENCA, DIFERENCA, true, "finalmente corrige e arrasta a diferença para o número relativo"),
                    passo(REFERENTE, REFERENTE, true, "arrasta o referente corretamente"),
                    passo(REFERIDO, REFERIDO, true, "arrasta o referido corretamente")
            )));
        }
        return lista;
    }

    private static PassoReplayHumano passo(String chavePapelNumeral, String chavePapelAlvo,
            boolean corretoNoProtocoloOriginal, String descricao) {
        return new PassoReplayHumano(chavePapelNumeral, chavePapelAlvo, corretoNoProtocoloOriginal, descricao);
    }
}
