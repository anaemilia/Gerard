package gerard.ui.venn;

import gerard.campoaditivo.diagrama.elementos.CirculoVenn;
import java.text.Normalizer;
import java.util.Locale;

/**
 * Funções puras usadas pelo gráfico de comparação de barras (Venn
 * "comparação de medidas"): limites de quantidade, normalização de chaves de
 * papel semântico, e geometria do eixo fixo de comparação.
 *
 * Extraído de Main.TelaGerard como parte da Fase 2 do plano de refatoração —
 * ver PLANO_REFATORACAO_ARQUITETURA_GERARD.md.
 *
 * Métodos originais em TelaGerard mantidos como wrappers de 1 linha; nenhum
 * call site foi alterado.
 */
public final class UtilitariosComparacaoBarras {

    private UtilitariosComparacaoBarras() {
    }

    /**
     * As coleções manipuláveis devem representar a quantidade semântica
     * completa. O ajuste visual é feito pelo tamanho e pelo espaçamento dos
     * quadradinhos, e não pela supressão de unidades.
     */
    public static int limitarParaQuadradinhos(int valor) {
        return Math.abs(valor);
    }

    public static int limitarParaBarrinhasComparacao(int valor) {
        int v = Math.abs(valor);
        return Math.min(v, 40);
    }

    /** Remove acentos, deixa minúsculo e mantém só [a-z0-9], para comparar chaves de papel. */
    public static String normalizarChaveComparacao(String texto) {
        if (texto == null) return "";
        return Normalizer.normalize(texto, Normalizer.Form.NFD)
                .replaceAll("\\p{M}+", "")
                .toLowerCase(Locale.ROOT)
                .replaceAll("[^a-z0-9]+", "");
    }

    /** Y da base comum das duas barras (a mais baixa das duas), para alinhar o eixo. */
    public static int obterBaseFixaEixoComparacao(CirculoVenn referido, CirculoVenn referendo) {
        int baseReferido = referido.y + referido.altura - 8;
        int baseReferendo = referendo.y + referendo.altura - 8;
        return Math.min(baseReferido, baseReferendo);
    }

    /** Altura fixa do eixo de comparação, equilibrando escala ideal e espaço disponível. */
    public static int obterAlturaFixaEixoComparacao(CirculoVenn referido,
                                                      CirculoVenn referendo,
                                                      int valorMaximo) {
        int alturaIdeal = Math.max(120, Math.max(0, valorMaximo) * 18);
        int alturaDisponivel = Math.max(60, Math.min(referido.altura, referendo.altura) - 20);
        return Math.min(alturaIdeal, alturaDisponivel);
    }

    /**
     * X da escala vertical de comparação. Fica ao lado direito da caixa
     * "Valor relativo" quando ela já existe, para não ficar espremida entre
     * as barras Referido/Referendo e essa caixa; cai de volta para o lado da
     * barra do referendo quando a caixa ainda não foi criada.
     */
    public static int obterXEixoComparacao(CirculoVenn referendo, CirculoVenn valorRelativo) {
        CirculoVenn referencia = valorRelativo != null ? valorRelativo : referendo;
        return referencia.x + referencia.largura + 26;
    }
}
