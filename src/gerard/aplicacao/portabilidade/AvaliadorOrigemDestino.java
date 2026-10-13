package gerard.aplicacao.portabilidade;

import gerard.aplicacao.interacao.PortaPersistenciaAcaoInstrumental;
import gerard.campoaditivo.modelo.TipoSituacaoAditiva;
import gerard.campoaditivo.semantica.CatalogoPapeisSemanticosAditivos;
import gerard.dominio.atividade.ContextoAcaoInstrumental;
import gerard.i18n.ServicoLocalizacao;

import java.util.function.Supplier;

/**
 * O juiz único do posicionamento de um número sobre uma figura, para os dois clientes (Swing e web): o critério é só
 * se o posicionamento está certo ou incorreto, via o scaffolding portátil (ScaffoldingQuestionamento, sem Swing) pela
 * porta neutra {@link PortaQuestionamentoPosicionamento}. Quem instancia o scaffolding concreto é o adaptador,
 * entregue pela raiz de composição; a aplicação não importa infraestrutura. Cada cliente decide o que fazer com o
 * veredito (a web publica o posicionamento incorreto na cena; o desktop abre a pergunta persistente) — aqui só se
 * julga e, quando aplicável, entrega à tentativa o registro de posicionamento constituído pelo descritor do papel de
 * origem.
 */
public final class AvaliadorOrigemDestino {
    private static final CatalogoPapeisSemanticosAditivos CATALOGO =
            new CatalogoPapeisSemanticosAditivos();

    private final PortaQuestionamentoPosicionamento scaffolding;

    public AvaliadorOrigemDestino(PortaQuestionamentoPosicionamento scaffolding) {
        this.scaffolding = scaffolding == null
                ? PortaQuestionamentoPosicionamento.NAO_APLICAVEL : scaffolding;
    }

    /**
     * @param contextoDoRegistro descreve a ação para o log do cliente; avaliado só se o questionamento for
     *        aplicável. {@code null}, ou persistência {@code null}, avalia sem registrar (reavaliações que não são
     *        uma ação nova do usuário).
     */
    public ResultadoQuestionamentoPosicionamento avaliar(
            String origemPapelId, String chavePapelAlvo, TipoSituacaoAditiva categoria,
            Supplier<ContextoAcaoInstrumental> contextoDoRegistro, PortaPersistenciaAcaoInstrumental persistencia) {
        ServicoLocalizacao localizacao = ServicoLocalizacao.getInstancia();
        ResultadoQuestionamentoPosicionamento resultado = scaffolding.avaliar(
                origemPapelId, chavePapelAlvo, localizacao.texto(chavePapelAlvo), localizacao.descricaoTipo(categoria));
        if (resultado != null && resultado.isAplicavel() && categoria != null
                && contextoDoRegistro != null && persistencia != null) {
            persistencia.persistir(CATALOGO.obterDescritor(origemPapelId).avaliarPosicionamento(
                    CATALOGO.obterDescritor(chavePapelAlvo), categoria, contextoDoRegistro.get()));
        }
        return resultado;
    }
}
