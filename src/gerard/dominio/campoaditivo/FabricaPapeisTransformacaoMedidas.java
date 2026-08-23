package gerard.dominio.campoaditivo;

import gerard.dominio.campoaditivo.evento.PublicadorEventoDominio;
import gerard.semantica.numero.DominioNumerico;

/**
 * Fábrica dos três papéis do esquema Transformação de Medidas
 * (EstadoInicial, Transformacao, EstadoFinal).
 *
 * Não altera PapelQuantitativo além do que já foi corrigido em conjunto
 * (baseline v2) — esta fábrica só usa o construtor público, que já era
 * genérico o suficiente para qualquer esquema do campo aditivo, não só
 * Composição de Medidas.
 */
public final class FabricaPapeisTransformacaoMedidas {

    private FabricaPapeisTransformacaoMedidas() { }

    public static PapelQuantitativo estadoInicial(PublicadorEventoDominio publicador) {
        return new PapelQuantitativo("papel.estadoInicial", "Estado Inicial", DominioNumerico.NATURAIS,
                new DescritorRepresentacaoPapel(TipoRepresentacaoAbstrata.FIGURA_RETANGULAR, "", "rotulo.papel.estadoInicial",
                        "explicacao.papel.estadoInicial"),
                publicador);
    }

    public static PapelQuantitativo transformacao(PublicadorEventoDominio publicador) {
        return new PapelQuantitativo("papel.transformacao", "Transformação", DominioNumerico.INTEIROS,
                new DescritorRepresentacaoPapel(TipoRepresentacaoAbstrata.FIGURA_ELIPTICA, "", "rotulo.papel.transformacao",
                        "explicacao.papel.transformacao"),
                publicador);
    }

    public static PapelQuantitativo estadoFinal(PublicadorEventoDominio publicador) {
        return new PapelQuantitativo("papel.estadoFinal", "Estado Final", DominioNumerico.NATURAIS,
                new DescritorRepresentacaoPapel(TipoRepresentacaoAbstrata.FIGURA_RETANGULAR, "", "rotulo.papel.estadoFinal",
                        "explicacao.papel.estadoFinal"),
                publicador);
    }
}
