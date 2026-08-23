package gerard.dominio.campoaditivo;

import gerard.dominio.campoaditivo.evento.PublicadorEventoDominio;
import gerard.semantica.numero.DominioNumerico;

/**
 * Fábrica dos três papéis do esquema Composição de Transformações
 * (Transformacao1, Transformacao2, TransformacaoFinal).
 *
 * Os três são INTEIROS (categoria "Relações" de Vergnaud — números
 * relativos, não medidas). Forma FIGURA_ELIPTICA nos três, mesma
 * convenção já usada para papéis de transformação em
 * FabricaPapeisTransformacaoMedidas — inferida por consistência com essa
 * convenção, não confirmada contra captura de tela de produção (diferente
 * da decisão de forma em FabricaPapeisComparacaoMedidas, que foi
 * verificada contra a tela real).
 */
public final class FabricaPapeisComposicaoDeTransformacoes {

    private FabricaPapeisComposicaoDeTransformacoes() { }

    public static PapelQuantitativo transformacao1(PublicadorEventoDominio publicador) {
        return new PapelQuantitativo("papel.transformacao1", "Transformação 1", DominioNumerico.INTEIROS,
                new DescritorRepresentacaoPapel(TipoRepresentacaoAbstrata.FIGURA_ELIPTICA, "", "rotulo.papel.transformacao1",
                        "explicacao.papel.transformacao"),
                publicador);
    }

    public static PapelQuantitativo transformacao2(PublicadorEventoDominio publicador) {
        return new PapelQuantitativo("papel.transformacao2", "Transformação 2", DominioNumerico.INTEIROS,
                new DescritorRepresentacaoPapel(TipoRepresentacaoAbstrata.FIGURA_ELIPTICA, "", "rotulo.papel.transformacao2",
                        "explicacao.papel.transformacao"),
                publicador);
    }

    public static PapelQuantitativo transformacaoFinal(PublicadorEventoDominio publicador) {
        return new PapelQuantitativo("papel.transformacaoFinal", "Transformação Resultante", DominioNumerico.INTEIROS,
                new DescritorRepresentacaoPapel(TipoRepresentacaoAbstrata.FIGURA_ELIPTICA, "", "rotulo.papel.transformacaoFinal",
                        "explicacao.papel.transformacaoFinal"),
                publicador);
    }
}
