package gerard.aplicacao.portabilidade;

import gerard.aplicacao.DecisorAjudaVisual;
import gerard.campoaditivo.diagrama.modelo.CenaDiagramaAditivo;
import gerard.campoaditivo.modelo.SituacaoProblemaAditiva;
import gerard.dominio.campoaditivo.ajuda.ApoioHistorinhaIlustrada;
import gerard.dominio.campoaditivo.ajuda.ApoioHistorinhaTextual;
import gerard.dominio.campoaditivo.ajuda.ApoioVisual;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Projeta para o cliente web o repertório de historinhas (representação passiva de
 * ajuda visual) <b>da própria categoria</b>: quem possui o repertório é
 * a política de apoio visual, e uma categoria sem
 * repertório (ex.: Transformação de Medidas) projeta lista vazia — o cliente não
 * escolhe nem fixa o conteúdo de outra categoria.
 *
 * As historinhas só aparecem na escalada de Scaffolding no limite (3ª tentativa
 * rejeitada consecutiva), fato informado pela tentativa de modelagem, para
 * situações com papéis relativos. A referência de conteúdo é opaca (ver
 * {@link HistorinhaAjudaVisual}): aqui ela é entregue como está; resolvê-la em arquivo,
 * incluindo variante de idioma, é trabalho do adaptador de cada plataforma.
 */
public final class ProjetorAjudaVisualWeb {

    private ProjetorAjudaVisualWeb() {
    }

    public static boolean deveAcionar(SituacaoProblemaAditiva situacao, boolean escaladaNoLimite) {
        return DecisorAjudaVisual.acionada(situacao, escaladaNoLimite);
    }

    /** Apoios visuais decididos pelo backend para a situação neste momento (mesma fonte da cena). */
    public static List<Object> projetar(SituacaoProblemaAditiva situacao, boolean escaladaNoLimite) {
        return projetar(DecisorAjudaVisual.decidir(situacao, escaladaNoLimite));
    }

    /**
     * Apoios visuais que a cena carrega. É o que Swing e React consomem: cada cliente
     * renderiza a primeira entrada pelo seu {@code tipo}; nenhum escolhe entre elas.
     */
    public static List<Object> projetar(CenaDiagramaAditivo cena) {
        return cena == null ? new ArrayList<Object>()
                : projetar(cena.getApoiosVisuais(), cena.getArgumentosHistorinha());
    }

    private static List<Object> projetar(List<ApoioVisual> apoios) {
        return projetar(apoios, null);
    }

    private static List<Object> projetar(List<ApoioVisual> apoios,
            gerard.campoaditivo.diagrama.modelo.ArgumentosHistorinhaCena argumentos) {
        List<Object> resultado = new ArrayList<Object>();
        for (ApoioVisual apoio : apoios) {
            Map<String, Object> item = new LinkedHashMap<String, Object>();
            item.put("tipo", apoio.getTipo());
            item.put("identificador", apoio.getIdentificador());
            if (apoio instanceof ApoioHistorinhaIlustrada) {
                item.put("referencia", ((ApoioHistorinhaIlustrada) apoio).getReferenciaConteudo());
                if (argumentos != null && argumentos.possuiTrechos()) {
                    item.put("idioma", argumentos.getIdioma());
                    item.put("trechos", new ArrayList<String>(argumentos.getTrechos()));
                    if (!argumentos.getLegendas().isEmpty()) {
                        // Cronograma das legendas DENTRO da animação (um laço de duracao_s segundos).
                        item.put("duracao_s", Double.valueOf(argumentos.getDuracaoSegundos()));
                        List<Object> legendas = new ArrayList<Object>();
                        for (gerard.campoaditivo.diagrama.modelo.ArgumentosHistorinhaCena.Legenda legenda
                                : argumentos.getLegendas()) {
                            Map<String, Object> l = new LinkedHashMap<String, Object>();
                            l.put("texto", legenda.getTexto());
                            l.put("inicio_s", Double.valueOf(legenda.getInicioSegundos()));
                            l.put("fim_s", Double.valueOf(legenda.getFimSegundos()));
                            legendas.add(l);
                        }
                        item.put("legendas", legendas);
                    }
                }
            } else if (apoio instanceof ApoioHistorinhaTextual) {
                item.put("texto", ((ApoioHistorinhaTextual) apoio).getTexto());
            }
            resultado.add(item);
        }
        return resultado;
    }
}
