package gerard.aplicacao.portabilidade;

import gerard.campoaditivo.modelo.TipoSituacaoAditiva;
import gerard.campoaditivo.semantica.CatalogoPapeisSemanticosAditivos;
import gerard.i18n.ServicoLocalizacao;

import java.util.List;

/**
 * Compatibilidade semântica entre o elemento arrastado do enunciado (origem)
 * e o papel da figura-alvo (destino) — mesma avaliação usada pelo desktop em
 * avaliarQuestionamentoPosicionamento/Main.java, via o scaffolding portátil
 * já existente (ScaffoldingQuestionamento, sem Swing), aqui só pela porta
 * neutra {@link PortaQuestionamentoPosicionamento} — quem instancia o
 * scaffolding concreto é o adaptador, entregue pela raiz de composição
 * (ServidorPrototipoWeb); a aplicação não importa infraestrutura. Sem isso,
 * a versão web decidia essa compatibilidade só no cliente (papelId !==
 * figura.chave_papel_semantico em App.tsx) e o servidor aceitava cegamente
 * qualquer papel_id de destino, sem saber o que foi arrastado.
 */
final class AvaliadorOrigemDestinoWeb {
    private static final CatalogoPapeisSemanticosAditivos CATALOGO =
            new CatalogoPapeisSemanticosAditivos();

    private final PortaQuestionamentoPosicionamento scaffolding;

    AvaliadorOrigemDestinoWeb(PortaQuestionamentoPosicionamento scaffolding) {
        this.scaffolding = scaffolding == null
                ? PortaQuestionamentoPosicionamento.NAO_APLICAVEL : scaffolding;
    }

    /**
     * Avalia a soltura e, quando o questionamento é aplicável, entrega à
     * tentativa o registro de posicionamento constituído pelo descritor do
     * papel de origem (mesmo proprietário usado pelo desktop em
     * avaliarQuestionamentoPosicionamento).
     */
    ResultadoQuestionamentoPosicionamento avaliar(
            String origemPapelId, String chavePapelAlvo, TipoSituacaoAditiva categoria,
            List<String> participantes, EscopoTentativaWeb escopo) {
        ServicoLocalizacao localizacao = ServicoLocalizacao.getInstancia();
        String papelDoElementoNoDiagrama = localizacao.texto(chavePapelAlvo);
        String categoriaEscolhida = localizacao.descricaoTipo(categoria);
        ResultadoQuestionamentoPosicionamento resultado = scaffolding.avaliar(
                origemPapelId, chavePapelAlvo, papelDoElementoNoDiagrama, categoriaEscolhida);
        if (resultado != null && resultado.isAplicavel() && categoria != null && escopo != null) {
            escopo.persistir(CATALOGO.obterDescritor(origemPapelId).avaliarPosicionamento(
                    CATALOGO.obterDescritor(chavePapelAlvo), categoria,
                    ContextosAcaoInstrumentalWeb.posicionamento(
                            origemPapelId, chavePapelAlvo, participantes)));
        }
        return resultado;
    }
}
