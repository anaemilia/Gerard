package gerard.semantica.quantidade;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.NumberFormat;
import java.util.Locale;

/**
 * Contagem usa uma unidade visual por elemento. Dinheiro pode agrupar unidades
 * apenas quando existe divisor comum exato, preservando a equivalência.
 */
public final class PoliticaEscalaVisualQuantidade {
    private static final int LIMITE_PREFERENCIAL = 36;

    public EscalaVisualQuantidade calcular(PerfilQuantidadeSituacao perfil,
            int[] valores, boolean[] conhecidos) {
        if (perfil == null
                || perfil.getTipo() == TipoGrandezaQuantitativa.CONTAGEM) {
            return new EscalaVisualQuantidade(BigDecimal.ONE, "");
        }
        int maximo = 0;
        int divisor = 0;
        for (int i = 0; i < 3; i++) {
            if (conhecidos != null && i < conhecidos.length && !conhecidos[i]) {
                continue;
            }
            int absoluto = valores != null && i < valores.length
                    ? Math.abs(valores[i]) : 0;
            maximo = Math.max(maximo, absoluto);
            if (absoluto > 0) {
                divisor = divisor == 0 ? absoluto : mdc(divisor, absoluto);
            }
        }
        if (divisor <= 1 || maximo <= LIMITE_PREFERENCIAL
                || maximo / divisor > LIMITE_PREFERENCIAL) {
            return new EscalaVisualQuantidade(BigDecimal.ONE, "");
        }
        BigDecimal unidade = new BigDecimal(divisor);
        return new EscalaVisualQuantidade(unidade,
                "■ = " + formatarMoeda(unidade, perfil));
    }

    private int mdc(int a, int b) {
        return BigInteger.valueOf(a).gcd(BigInteger.valueOf(b)).intValue();
    }

    private String formatarMoeda(BigDecimal valor,
            PerfilQuantidadeSituacao perfil) {
        NumberFormat formato = NumberFormat.getNumberInstance(new Locale("pt", "BR"));
        formato.setMinimumFractionDigits(2);
        formato.setMaximumFractionDigits(2);
        return perfil.getUnidade().getSimbolo() + " " + formato.format(valor);
    }
}
