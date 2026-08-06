package gerard.dominio.campoaditivo;

import gerard.dominio.campoaditivo.evento.PublicadorEventoDominio;
import gerard.semantica.numero.DominioNumerico;

/**
 * Fábrica dos três papéis do esquema Comparação de Medidas
 * (Referido, ValorRelativo, Referendo).
 *
 * Não altera PapelQuantitativo além do que já foi corrigido em conjunto
 * (baseline v2) — esta fábrica só usa o construtor público, que já era
 * genérico o suficiente para qualquer esquema do campo aditivo.
 */
public final class FabricaPapeisComparacaoMedidas {

    private FabricaPapeisComparacaoMedidas() { }

    public static PapelQuantitativo referido(PublicadorEventoDominio publicador) {
        return new PapelQuantitativo("papel.referido", "Referido", DominioNumerico.NATURAIS,
                new DescritorRepresentacaoPapel(TipoRepresentacaoAbstrata.FIGURA_RETANGULAR_ARREDONDADA, "rotulo.papel.referido"),
                publicador);
    }

    public static PapelQuantitativo valorRelativo(PublicadorEventoDominio publicador) {
        return new PapelQuantitativo("papel.valorRelativo", "Valor Relativo", DominioNumerico.INTEIROS,
                new DescritorRepresentacaoPapel(TipoRepresentacaoAbstrata.FIGURA_ELIPTICA, "rotulo.papel.valorRelativo"),
                publicador);
    }

    public static PapelQuantitativo referendo(PublicadorEventoDominio publicador) {
        return new PapelQuantitativo("papel.referendo", "Referendo", DominioNumerico.NATURAIS,
                new DescritorRepresentacaoPapel(TipoRepresentacaoAbstrata.FIGURA_RETANGULAR_ARREDONDADA, "rotulo.papel.referendo"),
                publicador);
    }
}
