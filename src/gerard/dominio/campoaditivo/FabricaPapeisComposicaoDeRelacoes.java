package gerard.dominio.campoaditivo;

import gerard.dominio.campoaditivo.evento.PublicadorEventoDominio;
import gerard.semantica.numero.DominioNumerico;

/**
 * Fábrica dos três papéis do esquema Composição de Relações
 * (Relacao1, Relacao2, RelacaoFinal).
 *
 * Os três são INTEIROS (categoria "Relações" de Vergnaud — números
 * relativos, não medidas). Forma FIGURA_ELIPTICA nos três — inferida por
 * consistência com a convenção já usada para papéis de transformação/valor
 * relativo nas outras fábricas do pacote, não confirmada contra captura de
 * tela de produção.
 */
public final class FabricaPapeisComposicaoDeRelacoes {

    private FabricaPapeisComposicaoDeRelacoes() { }

    public static PapelQuantitativo relacao1(PublicadorEventoDominio publicador) {
        return new PapelQuantitativo("papel.relacao1", "Relação 1", DominioNumerico.INTEIROS,
                new DescritorRepresentacaoPapel(TipoRepresentacaoAbstrata.FIGURA_ELIPTICA, "", "rotulo.papel.relacao1",
                        "explicacao.papel.relacao"),
                publicador);
    }

    public static PapelQuantitativo relacao2(PublicadorEventoDominio publicador) {
        return new PapelQuantitativo("papel.relacao2", "Relação 2", DominioNumerico.INTEIROS,
                new DescritorRepresentacaoPapel(TipoRepresentacaoAbstrata.FIGURA_ELIPTICA, "", "rotulo.papel.relacao2",
                        "explicacao.papel.relacao"),
                publicador);
    }

    public static PapelQuantitativo relacaoFinal(PublicadorEventoDominio publicador) {
        return new PapelQuantitativo("papel.relacaoFinal", "Relação Final", DominioNumerico.INTEIROS,
                new DescritorRepresentacaoPapel(TipoRepresentacaoAbstrata.FIGURA_ELIPTICA, "", "rotulo.papel.relacaoFinal",
                        "explicacao.papel.relacaoFinal"),
                publicador);
    }
}
