package gerard.aplicacao.portabilidade;

import gerard.campoaditivo.modelo.TipoSituacaoAditiva;
import gerard.dominio.campoaditivo.ajuda.HistorinhaAjudaVisual;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Projeta para o cliente web o repertório de historinhas (representação passiva de
 * ajuda visual) <b>da própria categoria</b>: quem possui o repertório é
 * {@link TipoSituacaoAditiva#selecionarRepertorioAjudaVisual()}, e uma categoria sem
 * repertório (ex.: Transformação de Medidas) projeta lista vazia — o cliente não
 * escolhe nem fixa o conteúdo de outra categoria.
 *
 * As historinhas só aparecem na escalada de Scaffolding no limite (3ª tentativa
 * rejeitada consecutiva), fato do domínio exposto pela modelagem em
 * {@code escalada_no_limite}. A referência de conteúdo é opaca (ver
 * {@link HistorinhaAjudaVisual}): aqui ela é entregue como está; resolvê-la em arquivo,
 * incluindo variante de idioma, é trabalho do adaptador de cada plataforma.
 */
public final class ProjetorAjudaVisualWeb {

    private ProjetorAjudaVisualWeb() {
    }

    public static List<Object> projetar(TipoSituacaoAditiva categoria, String situacaoId,
            Object modelagem) {
        List<Object> resultado = new ArrayList<Object>();
        if (categoria == null || !escaladaNoLimite(modelagem)) {
            return resultado;
        }
        for (HistorinhaAjudaVisual historinha
                : categoria.selecionarRepertorioAjudaVisual().getHistorinhas()) {
            Map<String, Object> item = new LinkedHashMap<String, Object>();
            item.put("identificador", historinha.getIdentificador());
            item.put("referencia", historinha.getReferenciaConteudo());
            item.put("da_situacao_atual", Boolean.valueOf(
                    historinha.getIdSituacaoCurada().equals(situacaoId)));
            resultado.add(item);
        }
        return resultado;
    }

    private static boolean escaladaNoLimite(Object modelagem) {
        return modelagem instanceof Map
                && Boolean.TRUE.equals(((Map<?, ?>) modelagem).get("escalada_no_limite"));
    }
}
