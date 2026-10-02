package gerard.aplicacao.portabilidade;

import gerard.campoaditivo.modelo.TipoSituacaoAditiva;
import gerard.campoaditivo.modelo.SituacaoProblemaAditiva;
import gerard.campoaditivo.curadoria.SemanticaCuradaSituacao;
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
 * rejeitada consecutiva), fato informado pela tentativa de modelagem, para
 * situações com papéis relativos. A referência de conteúdo é opaca (ver
 * {@link HistorinhaAjudaVisual}): aqui ela é entregue como está; resolvê-la em arquivo,
 * incluindo variante de idioma, é trabalho do adaptador de cada plataforma.
 */
public final class ProjetorAjudaVisualWeb {

    private ProjetorAjudaVisualWeb() {
    }

    public static List<Object> projetar(SituacaoProblemaAditiva situacao,
            boolean escaladaNoLimite) {
        return situacao == null ? new ArrayList<Object>()
                : projetar(situacao.getTipo(), situacao.getId(),
                        SemanticaCuradaSituacao.possuiNumeroRelativo(situacao), escaladaNoLimite);
    }

    private static List<Object> projetar(TipoSituacaoAditiva categoria, String situacaoId,
            boolean possuiNumeroRelativo, boolean escaladaNoLimite) {
        List<Object> resultado = new ArrayList<Object>();
        if (categoria == null) {
            return resultado;
        }
        for (HistorinhaAjudaVisual historinha
                : categoria.selecionarRepertorioAjudaVisual(
                        possuiNumeroRelativo, escaladaNoLimite).getHistorinhas()) {
            Map<String, Object> item = new LinkedHashMap<String, Object>();
            item.put("identificador", historinha.getIdentificador());
            item.put("referencia", historinha.getReferenciaConteudo());
            item.put("da_situacao_atual", Boolean.valueOf(
                    historinha.getIdSituacaoCurada().equals(situacaoId)));
            resultado.add(item);
        }
        return resultado;
    }

}
