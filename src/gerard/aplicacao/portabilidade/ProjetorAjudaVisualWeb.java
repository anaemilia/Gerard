package gerard.aplicacao.portabilidade;

import gerard.aplicacao.DecisorAjudaVisual;
import gerard.campoaditivo.diagrama.modelo.CenaDiagramaAditivo;
import gerard.campoaditivo.modelo.SituacaoProblemaAditiva;
import gerard.dominio.campoaditivo.ajuda.HistorinhaAjudaVisual;
import gerard.dominio.campoaditivo.ajuda.RepertorioAjudaVisual;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Projeta para o cliente web o repertório de historinhas (representação passiva de
 * ajuda visual) <b>da própria categoria</b>: quem possui o repertório é
 * {@code TipoSituacaoAditiva.selecionarRepertorioAjudaVisual()}, e uma categoria sem
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

    /** Ajuda visual decidida pelo backend para a situação neste momento (mesma fonte da cena). */
    public static List<Object> projetar(SituacaoProblemaAditiva situacao, boolean escaladaNoLimite) {
        return projetar(DecisorAjudaVisual.decidir(situacao, escaladaNoLimite));
    }

    /**
     * Ajuda visual que a cena carrega. É o que Swing e React consomem: cada cliente só
     * apresenta a primeira entrada (já a narrativa certa); nenhum escolhe entre elas.
     */
    public static List<Object> projetar(CenaDiagramaAditivo cena) {
        return cena == null ? new ArrayList<Object>() : projetar(cena.getAjudaVisual());
    }

    private static List<Object> projetar(RepertorioAjudaVisual repertorio) {
        List<Object> resultado = new ArrayList<Object>();
        for (HistorinhaAjudaVisual historinha : repertorio.getHistorinhas()) {
            Map<String, Object> item = new LinkedHashMap<String, Object>();
            item.put("identificador", historinha.getIdentificador());
            item.put("referencia", historinha.getReferenciaConteudo());
            resultado.add(item);
        }
        return resultado;
    }
}
