package gerard.aplicacao.portabilidade;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import gerard.campoaditivo.modelo.TipoSituacaoAditiva;

/** Controles HTTP publicados pela aplicação; clientes apenas os materializam. */
public final class AcoesDisponiveisAtividadeWeb {
    private AcoesDisponiveisAtividadeWeb() {
    }

    public static List<Object> sorteios() {
        List<Object> acoes = new ArrayList<Object>();
        acoes.add(acao("SORTEAR_MEDIDAS", "POST", "/api/sorteios/medidas"));
        acoes.add(acao("SORTEAR_RELACOES", "POST", "/api/sorteios/relacoes"));
        return acoes;
    }

    public static List<Object> classificacaoCategoria() {
        List<Object> acoes = sorteios();
        for (TipoSituacaoAditiva categoria : TipoSituacaoAditiva.values()) {
            Map<String, Object> corpo = new LinkedHashMap<String, Object>();
            corpo.put("categoria", categoria.name());
            acoes.add(acao("ESCOLHER_CATEGORIA", "POST",
                    "/api/classificacao/categoria", corpo));
        }
        return acoes;
    }

    public static List<Object> confirmacaoCategoria() {
        List<Object> acoes = new ArrayList<Object>();
        Map<String, Object> sim = new LinkedHashMap<String, Object>();
        sim.put("concordou", Boolean.TRUE);
        acoes.add(acao("CONFIRMAR_CATEGORIA_DIVERGENTE", "POST",
                "/api/classificacao/confirmacao", sim));
        Map<String, Object> nao = new LinkedHashMap<String, Object>();
        nao.put("concordou", Boolean.FALSE);
        acoes.add(acao("CONFIRMAR_CATEGORIA_DIVERGENTE", "POST",
                "/api/classificacao/confirmacao", nao));
        return acoes;
    }

    public static List<Object> modelagemPapel(boolean concluida, String papelAlvo) {
        List<Object> acoes = sorteios();
        acoes.add(acao("REINICIAR_TENTATIVA", "POST", "/api/reiniciar"));
        if (!concluida) {
            Map<String, Object> corpo = new LinkedHashMap<String, Object>();
            corpo.put("papel_id", papelAlvo);
            acoes.add(acao("PROPOR_VALOR_PAPEL", "POST", "/api/acoes/posicionar", corpo));
        }
        return acoes;
    }

    private static Map<String, Object> acao(String id, String metodo, String href) {
        return acao(id, metodo, href, null);
    }

    private static Map<String, Object> acao(String id, String metodo, String href,
            Map<String, Object> corpo) {
        Map<String, Object> acao = new LinkedHashMap<String, Object>();
        acao.put("id", id);
        acao.put("metodo", metodo);
        acao.put("href", href);
        if (corpo != null) acao.put("corpo", corpo);
        return acao;
    }
}
