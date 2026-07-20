package gerard.semantica.quantidade;

import gerard.campoaditivo.modelo.SituacaoProblemaAditiva;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Locale;

/** Fachada usada pelas camadas legadas sem espalhar regras de grandeza. */
public final class ServicoQuantidadeContextual {
    private final ResolvedorPerfilQuantidadeSituacao resolvedor =
            new ResolvedorPerfilQuantidadeSituacao();
    private final ConversorTextoParaQuantidadeSemantica conversor =
            new ConversorTextoParaQuantidadeSemantica();

    public PerfilQuantidadeSituacao resolverPerfil(
            SituacaoProblemaAditiva situacao) {
        return resolvedor.resolver(situacao);
    }

    /**
     * Ponte compatível com o estado inteiro consolidado. Contagem exige inteiro;
     * dinheiro aceita formas como 25,00, sem truncar centavos não nulos.
     */
    public Integer converterParaInteiroLegado(String texto,
            SituacaoProblemaAditiva situacao) {
        PerfilQuantidadeSituacao perfil = resolverPerfil(situacao);
        BigDecimal decimal = conversor.converterDecimal(texto);
        if (decimal == null || !perfil.getGrandeza().aceitaMagnitude(decimal)) {
            return null;
        }
        BigDecimal normalizado = decimal.stripTrailingZeros();
        if (normalizado.scale() > 0) {
            return null;
        }
        try {
            return Integer.valueOf(normalizado.intValueExact());
        } catch (ArithmeticException ex) {
            return null;
        }
    }

    public String formatarInteiroLegado(int valor,
            SituacaoProblemaAditiva situacao, boolean explicitarSinal) {
        PerfilQuantidadeSituacao perfil = resolverPerfil(situacao);
        if (perfil.getTipo() == TipoGrandezaQuantitativa.MONETARIA) {
            NumberFormat formato = NumberFormat.getNumberInstance(localeDaSituacao(situacao));
            // O parâmetro já chega como inteiro (sem centavos); forçar duas
            // casas decimais aqui exibiria ",00" mesmo quando o enunciado
            // nunca apresentou o valor com casas decimais.
            formato.setMinimumFractionDigits(0);
            formato.setMaximumFractionDigits(0);
            String magnitude = formato.format(Math.abs(valor));
            if (valor < 0) return "-" + magnitude;
            if (explicitarSinal && valor > 0) return "+" + magnitude;
            return magnitude;
        }
        if (valor < 0) return Integer.toString(valor);
        if (explicitarSinal && valor > 0) return "+" + valor;
        return Integer.toString(valor);
    }

    private Locale localeDaSituacao(SituacaoProblemaAditiva situacao) {
        String codigo = situacao == null ? "pt-BR" : situacao.getCodigoIdioma();
        if (codigo == null || codigo.trim().length() == 0) {
            return new Locale("pt", "BR");
        }
        return Locale.forLanguageTag(codigo.replace('_', '-'));
    }
}
