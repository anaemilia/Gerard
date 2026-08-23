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
                new DescritorRepresentacaoPapel(TipoRepresentacaoAbstrata.FIGURA_RETANGULAR_ARREDONDADA, "", "rotulo.papel.referido",
                        "explicacao.papel.referido"),
                publicador);
    }

    /**
     * Chave "papel.valorRelativo" — o papel VIVO em Main.java para este
     * mesmo conceito usa a chave "papel.diferenca" (ver
     * Main.obterValorCuradoPorIndiceEChave). CatalogoExplicacoesConceituaisPapel
     * registra a chave de explicação desta fábrica sob "papel.diferenca",
     * não sob "papel.valorRelativo" — só a chave do papel diverge entre a
     * fábrica (piloto) e o uso real, o conceito é o mesmo.
     */
    public static PapelQuantitativo valorRelativo(PublicadorEventoDominio publicador) {
        return new PapelQuantitativo("papel.valorRelativo", "Valor Relativo", DominioNumerico.INTEIROS,
                new DescritorRepresentacaoPapel(TipoRepresentacaoAbstrata.FIGURA_ELIPTICA, "", "rotulo.papel.valorRelativo",
                        "explicacao.papel.diferenca"),
                publicador);
    }

    public static PapelQuantitativo referendo(PublicadorEventoDominio publicador) {
        return new PapelQuantitativo("papel.referendo", "Referendo", DominioNumerico.NATURAIS,
                new DescritorRepresentacaoPapel(TipoRepresentacaoAbstrata.FIGURA_RETANGULAR_ARREDONDADA, "", "rotulo.papel.referendo",
                        "explicacao.papel.referendo"),
                publicador);
    }
}
