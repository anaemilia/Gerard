package gerard.dominio.campoaditivo;

import gerard.dominio.campoaditivo.evento.PublicadorEventoDominio;
import gerard.semantica.numero.DominioNumerico;

/**
 * Fábrica dos três papéis do esquema Transformação de Relação
 * (RelacaoInicial, Transformacao, RelacaoFinal).
 *
 * As relações são inteiras e a transformação é um inteiro não nulo: ela
 * precisa alterar a relação (categoria "Relações" de Vergnaud — números
 * relativos, não medidas). Forma FIGURA_ELIPTICA nos três — inferida por
 * consistência com a convenção já usada para papéis de transformação/valor
 * relativo nas outras fábricas do pacote, não confirmada contra captura de
 * tela de produção.
 */
public final class FabricaPapeisTransformacaoDeRelacao {

    private FabricaPapeisTransformacaoDeRelacao() { }

    public static PapelQuantitativo relacaoInicial(PublicadorEventoDominio publicador) {
        return new PapelQuantitativo("papel.relacaoInicial", "Relação Inicial", DominioNumerico.INTEIROS,
                new DescritorRepresentacaoPapel(TipoRepresentacaoAbstrata.FIGURA_ELIPTICA, "", "rotulo.papel.relacaoInicial",
                        "explicacao.papel.relacaoInicial"),
                publicador);
    }

    public static PapelQuantitativo transformacao(PublicadorEventoDominio publicador) {
        return new PapelQuantitativo("papel.transformacao", "Transformação", DominioNumerico.INTEIROS_NAO_NULOS,
                new DescritorRepresentacaoPapel(TipoRepresentacaoAbstrata.FIGURA_ELIPTICA, "", "rotulo.papel.transformacao",
                        "explicacao.papel.transformacao"),
                publicador);
    }

    public static PapelQuantitativo relacaoFinal(PublicadorEventoDominio publicador) {
        return new PapelQuantitativo("papel.relacaoFinal", "Relação Final", DominioNumerico.INTEIROS,
                new DescritorRepresentacaoPapel(TipoRepresentacaoAbstrata.FIGURA_ELIPTICA, "", "rotulo.papel.relacaoFinal",
                        "explicacao.papel.relacaoFinal"),
                publicador);
    }
}
