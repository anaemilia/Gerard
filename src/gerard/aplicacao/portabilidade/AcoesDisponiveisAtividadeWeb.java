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

    public static Map<String, Object> acaoReiniciar() {
        return acao("REINICIAR_TENTATIVA", "POST", "/api/reiniciar");
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

    public static List<Object> acaoPosicionarConhecido(String papelAlvo) {
        List<Object> acoes = new ArrayList<Object>();
        Map<String, Object> corpo = new LinkedHashMap<String, Object>();
        corpo.put("papel_id", papelAlvo);
        acoes.add(acao("POSICIONAR_CONHECIDO", "POST", "/api/acoes/posicionar-conhecido", corpo));
        return acoes;
    }

    public static List<Object> acaoEngatarIncognita(String papelAlvo) {
        List<Object> acoes = new ArrayList<Object>();
        Map<String, Object> corpo = new LinkedHashMap<String, Object>();
        corpo.put("papel_id", papelAlvo);
        acoes.add(acao("ENGATAR_INCOGNITA", "POST", "/api/acoes/engatar-incognita", corpo));
        return acoes;
    }

    public static List<Object> acaoAjustarQuadradinho(String papelAlvo) {
        List<Object> acoes = new ArrayList<Object>();
        Map<String, Object> corpo = new LinkedHashMap<String, Object>();
        corpo.put("papel_id", papelAlvo);
        acoes.add(acao("AJUSTAR_QUADRADINHO", "POST", "/api/acoes/quadradinho", corpo));
        return acoes;
    }

    /**
     * Duas opções pré-preenchidas (mesmo padrão de confirmacaoCategoria) —
     * escolher explicitamente positivo ou negativo para o papel revelado
     * que precisa de representação de sinal (ver
     * ServicoAtividadeWebComSinal.escolherSinalNumeroRelativo).
     */
    public static List<Object> acaoEscolherSinal(String papelAlvo) {
        List<Object> acoes = new ArrayList<Object>();
        Map<String, Object> positivo = new LinkedHashMap<String, Object>();
        positivo.put("papel_id", papelAlvo);
        positivo.put("sinal", "+");
        acoes.add(acao("ESCOLHER_SINAL_NUMERO_RELATIVO", "POST",
                "/api/acoes/escolher-sinal", positivo));
        Map<String, Object> negativo = new LinkedHashMap<String, Object>();
        negativo.put("papel_id", papelAlvo);
        negativo.put("sinal", "-");
        acoes.add(acao("ESCOLHER_SINAL_NUMERO_RELATIVO", "POST",
                "/api/acoes/escolher-sinal", negativo));
        return acoes;
    }

    public static List<Object> escolhaOperacaoRelacao(
            boolean segundaEtapaHabilitada, boolean concluida) {
        List<Object> acoes = sorteios();
        acoes.add(acao("REINICIAR_TENTATIVA", "POST", "/api/reiniciar"));
        if (!concluida) {
            acoes.add(acaoEscolherOperacaoRelacaoEntreTransformacoes(segundaEtapaHabilitada));
        }
        return acoes;
    }

    /**
     * Só o item ESCOLHER_OPERACAO_RELACAO (sem sorteios/reiniciar) — para
     * categorias que já montam sua própria lista base por outro caminho
     * (ver ServicoAtividadeWebTransformacaoRelacao, cujo segundo gate de
     * conclusão é independente da modelagem por incógnita, e
     * ServicoAtividadeWebComposicaoTransformacoes, cujas duas etapas podem
     * estar ativas/inativas de forma independente).
     */
    public static List<Object> acaoEscolherOperacaoRelacao() {
        List<Object> acoes = new ArrayList<Object>();
        acoes.add(acaoEscolherOperacaoRelacaoEntreTransformacoes(false));
        return acoes;
    }

    public static Map<String, Object> acaoEscolherOperacaoRelacaoComSeletor(
            boolean segundaEtapaHabilitada) {
        return acaoEscolherOperacaoRelacaoEntreTransformacoes(segundaEtapaHabilitada);
    }

    private static Map<String, Object> acaoEscolherOperacaoRelacaoEntreTransformacoes(
            boolean segundaEtapaHabilitada) {
        Map<String, Object> corpo = new LinkedHashMap<String, Object>();
        corpo.put("seletor", segundaEtapaHabilitada
                ? "ENTRE_ESTADO_E_TRANSFORMACAO" : "ENTRE_TRANSFORMACOES");
        return acao("ESCOLHER_OPERACAO_RELACAO", "POST", "/api/acoes/escolher-operacao", corpo);
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
