package gerard.aplicacao.portabilidade;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Os recursos de uma tentativa e onde moram. É o único lugar que conhece a forma das URIs: a aplicação publica as ações
 * já resolvidas para elas (ver {@link #resolver}), e a infraestrutura as reconhece pelos mesmos modelos.
 *
 * <pre>
 * /api/v1/tentativas                                   a coleção (criar uma tentativa = sortear uma situação)
 * /api/v1/tentativas/{id}/classificacao                a escolha da categoria
 * /api/v1/tentativas/{id}/classificacao/confirmacao    a resposta à pergunta de confirmação da categoria
 * /api/v1/tentativas/{id}/papeis/{papel}/posicionamento   o número solto sobre a figura do papel
 * /api/v1/tentativas/{id}/papeis/{papel}/valor            o valor proposto para o papel
 * /api/v1/tentativas/{id}/papeis/{papel}/valor/confirmacao  a resposta à confirmação do valor
 * /api/v1/tentativas/{id}/papeis/{papel}/quadradinhos     o material concreto do papel
 * /api/v1/tentativas/{id}/papeis/{papel}/sinal            o sinal escolhido para o papel
 * /api/v1/tentativas/{id}/papeis/{papel}/eixo/visibilidade  o eixo dos inteiros do papel, revelado ou oculto
 * /api/v1/tentativas/{id}/papeis/{papel}/eixo/projecao    o valor projetado no eixo
 * /api/v1/tentativas/{id}/operacoes                    a operação escolhida
 * /api/v1/tentativas/{id}/conclusao                    a escolha depois da conclusão
 * /api/v1/tentativas/{id}/ajudas                       os pedidos de ajuda contextual
 * /api/v1/tentativas/{id}/reinicio                     o recomeço da modelagem
 * /api/v1/tentativas/{id}/idioma                       o idioma da situação
 * </pre>
 */
public final class RotasTentativa {
    public static final String BASE = "/api/v1/tentativas";

    private RotasTentativa() {
    }

    public static String tentativa(String id) {
        return BASE + "/" + id;
    }

    public static String classificacao(String id) {
        return tentativa(id) + "/classificacao";
    }

    public static String confirmacaoClassificacao(String id) {
        return classificacao(id) + "/confirmacao";
    }

    public static String papel(String id, String papel) {
        return tentativa(id) + "/papeis/" + papel;
    }

    public static String posicionamento(String id, String papel) {
        return papel(id, papel) + "/posicionamento";
    }

    public static String valor(String id, String papel) {
        return papel(id, papel) + "/valor";
    }

    public static String confirmacaoValor(String id, String papel) {
        return valor(id, papel) + "/confirmacao";
    }

    public static String quadradinhos(String id, String papel) {
        return papel(id, papel) + "/quadradinhos";
    }

    public static String sinal(String id, String papel) {
        return papel(id, papel) + "/sinal";
    }

    public static String visibilidadeEixo(String id, String papel) {
        return papel(id, papel) + "/eixo/visibilidade";
    }

    public static String projecaoEixo(String id, String papel) {
        return papel(id, papel) + "/eixo/projecao";
    }

    public static String operacoes(String id) {
        return tentativa(id) + "/operacoes";
    }

    public static String conclusao(String id) {
        return tentativa(id) + "/conclusao";
    }

    public static String ajudas(String id) {
        return tentativa(id) + "/ajudas";
    }

    public static String reinicio(String id) {
        return tentativa(id) + "/reinicio";
    }

    public static String idioma(String id) {
        return tentativa(id) + "/idioma";
    }

    /**
     * Aponta cada ação publicada ao recurso que a realiza. As ações são criadas pelas atividades sem saber qual é a
     * tentativa; quem projeta o estado conhece o id e resolve tudo num só lugar. Uma ação sem recurso é erro de
     * programação, não algo a deixar passar para o cliente.
     */
    @SuppressWarnings("unchecked")
    static void resolver(List<Object> acoes, String id) {
        if (acoes == null) {
            return;
        }
        for (Object item : acoes) {
            Map<String, Object> acao = (Map<String, Object>) item;
            String acaoId = String.valueOf(acao.get("id"));
            Object corpo = acao.get("corpo");
            String papel = corpo instanceof Map ? (String) ((Map<String, Object>) corpo).get("papel_id") : null;
            switch (acaoId) {
                case "SORTEAR_MEDIDAS":
                    acao.put("href", BASE);
                    acao.put("corpo", grupo("MEDIDAS"));
                    break;
                case "SORTEAR_RELACOES":
                    acao.put("href", BASE);
                    acao.put("corpo", grupo("RELACOES"));
                    break;
                case "REINICIAR_TENTATIVA":
                    acao.put("href", reinicio(id));
                    break;
                case "ESCOLHER_CATEGORIA":
                    acao.put("href", classificacao(id));
                    break;
                case "CONFIRMAR_CATEGORIA_DIVERGENTE":
                    acao.put("href", confirmacaoClassificacao(id));
                    break;
                case "ESCOLHER_OPERACAO_RELACAO":
                    acao.put("href", operacoes(id));
                    break;
                case "PROPOR_VALOR_PAPEL":
                    acao.put("href", valor(id, exigir(papel, acaoId)));
                    break;
                case "POSICIONAR_CONHECIDO":
                case "ENGATAR_INCOGNITA":
                    acao.put("href", posicionamento(id, exigir(papel, acaoId)));
                    break;
                case "AJUSTAR_QUADRADINHO":
                    acao.put("href", quadradinhos(id, exigir(papel, acaoId)));
                    break;
                case "ESCOLHER_SINAL_NUMERO_RELATIVO":
                    acao.put("href", sinal(id, exigir(papel, acaoId)));
                    break;
                default:
                    throw new IllegalStateException("ação publicada sem recurso: " + acaoId);
            }
        }
    }

    private static Map<String, Object> grupo(String grupo) {
        Map<String, Object> corpo = new LinkedHashMap<String, Object>();
        corpo.put("grupo_sorteio", grupo);
        return corpo;
    }

    private static String exigir(String papel, String acaoId) {
        if (papel == null) {
            throw new IllegalStateException("ação " + acaoId + " sem papel_id no corpo");
        }
        return papel;
    }
}
