package gerard.dominio.campoaditivo;

import gerard.dominio.campoaditivo.evento.PublicadorEventoDominio;
import gerard.semantica.numero.DominioNumerico;

/**
 * Fábrica dos seis papéis do esquema Composição de Transformações:
 * três estados naturais e três transformações inteiras. As duas
 * transformações componentes são não nulas; a transformação resultante pode
 * ser zero quando os efeitos se anulam.
 *
 * As transformações componentes pertencem aos inteiros não nulos; a
 * resultante pertence aos inteiros e pode ser zero (categoria "Relações"
 * de Vergnaud — números relativos, não medidas). Forma FIGURA_ELIPTICA nos
 * três, mesma
 * convenção já usada para papéis de transformação em
 * FabricaPapeisTransformacaoMedidas — inferida por consistência com essa
 * convenção, não confirmada contra captura de tela de produção (diferente
 * da decisão de forma em FabricaPapeisComparacaoMedidas, que foi
 * verificada contra a tela real).
 */
public final class FabricaPapeisComposicaoDeTransformacoes {

    private FabricaPapeisComposicaoDeTransformacoes() { }

    public static PapelQuantitativo estadoInicial(PublicadorEventoDominio publicador) {
        return new PapelQuantitativo("papel.estadoInicial", "Estado Inicial", DominioNumerico.NATURAIS,
                new DescritorRepresentacaoPapel(TipoRepresentacaoAbstrata.FIGURA_RETANGULAR, "",
                        "rotulo.papel.estadoInicial", "explicacao.papel.estadoInicial"),
                publicador);
    }

    public static PapelQuantitativo transformacao1(PublicadorEventoDominio publicador) {
        return new PapelQuantitativo("papel.transformacao1", "Transformação 1", DominioNumerico.INTEIROS_NAO_NULOS,
                new DescritorRepresentacaoPapel(TipoRepresentacaoAbstrata.FIGURA_ELIPTICA, "", "rotulo.papel.transformacao1",
                        "explicacao.papel.transformacao"),
                publicador);
    }

    public static PapelQuantitativo estadoIntermediario(PublicadorEventoDominio publicador) {
        return new PapelQuantitativo("papel.estadoIntermediario", "Estado Intermediário", DominioNumerico.NATURAIS,
                new DescritorRepresentacaoPapel(TipoRepresentacaoAbstrata.FIGURA_RETANGULAR, "",
                        "rotulo.papel.estadoIntermediario", "explicacao.papel.estadoIntermediario"),
                publicador);
    }

    public static PapelQuantitativo transformacao2(PublicadorEventoDominio publicador) {
        return new PapelQuantitativo("papel.transformacao2", "Transformação 2", DominioNumerico.INTEIROS_NAO_NULOS,
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

    public static PapelQuantitativo estadoFinal(PublicadorEventoDominio publicador) {
        return new PapelQuantitativo("papel.estadoFinal", "Estado Final", DominioNumerico.NATURAIS,
                new DescritorRepresentacaoPapel(TipoRepresentacaoAbstrata.FIGURA_RETANGULAR, "",
                        "rotulo.papel.estadoFinal", "explicacao.papel.estadoFinal"),
                publicador);
    }
}
