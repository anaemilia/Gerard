package gerard.dominio.campoaditivo;

import gerard.campoaditivo.diagrama.modelo.TipoFiguraDiagrama;
import gerard.dominio.campoaditivo.evento.PublicadorEventoDominio;
import gerard.semantica.numero.DominioNumerico;

/**
 * Fábrica dos três papéis do esquema Transformação de Medidas
 * (EstadoInicial, Transformacao, EstadoFinal).
 *
 * Não altera PapelQuantitativo — a classe já congelada no baseline
 * "baseline-piloto-papel-quantitativo" permanece intocada; esta fábrica só
 * usa seu construtor público, que já era genérico o suficiente para
 * qualquer esquema do campo aditivo, não só Composição de Medidas.
 */
public final class FabricaPapeisTransformacaoMedidas {

    private FabricaPapeisTransformacaoMedidas() { }

    public static PapelQuantitativo estadoInicial(PublicadorEventoDominio publicador) {
        return new PapelQuantitativo("papel.estadoInicial", "Estado Inicial", DominioNumerico.NATURAIS,
                new RepresentacaoGraficaPapel(TipoFiguraDiagrama.RETANGULO, "rotulo.papel.estadoInicial"),
                publicador);
    }

    public static PapelQuantitativo transformacao(PublicadorEventoDominio publicador) {
        return new PapelQuantitativo("papel.transformacao", "Transformação", DominioNumerico.INTEIROS,
                new RepresentacaoGraficaPapel(TipoFiguraDiagrama.ELIPSE, "rotulo.papel.transformacao"),
                publicador);
    }

    public static PapelQuantitativo estadoFinal(PublicadorEventoDominio publicador) {
        return new PapelQuantitativo("papel.estadoFinal", "Estado Final", DominioNumerico.NATURAIS,
                new RepresentacaoGraficaPapel(TipoFiguraDiagrama.RETANGULO, "rotulo.papel.estadoFinal"),
                publicador);
    }
}
