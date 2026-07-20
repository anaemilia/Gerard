package gerard.campoaditivo.curadoria.sinal;

import gerard.campoaditivo.modelo.TipoSituacaoAditiva;
import java.math.BigDecimal;

/**
 * Regra única para sinais de transformações e valores relativos na curadoria.
 * A classe não depende de Swing e pode ser testada isoladamente.
 */
public final class PoliticaSinalCuradoria {

    public boolean exigeEscolha(TipoSituacaoAditiva tipo,
            PapelSinalCuradoria papel) {
        if (tipo == null || papel == null) {
            return false;
        }
        switch (tipo) {
            case TRANSFORMACAO_MEDIDAS:
            case COMPOSICAO_TRANSFORMACAO_MEDIDAS:
            case TRANSFORMACAO_RELACAO:
                return papel == PapelSinalCuradoria.TRANSFORMACAO;
            case COMPARACAO_MEDIDAS:
                return papel == PapelSinalCuradoria.VALOR_RELATIVO;
            default:
                return false;
        }
    }

    public ResultadoValidacaoSinalCuradoria validar(TipoSituacaoAditiva tipo,
            PapelSinalCuradoria papel, OpcaoSinalCuradoria opcao,
            String magnitude) {
        if (!exigeEscolha(tipo, papel)) {
            return ResultadoValidacaoSinalCuradoria.valido();
        }
        if (opcao == null || !opcao.isEscolhaValida()) {
            return ResultadoValidacaoSinalCuradoria.invalido(
                    "curadoria.sinal.obrigatorio");
        }
        String valor = magnitudeSemSinal(magnitude);
        if (valor.isEmpty() || "?".equals(valor)) {
            return ResultadoValidacaoSinalCuradoria.valido();
        }
        boolean zero = ehZero(valor);
        if (zero && opcao != OpcaoSinalCuradoria.NEUTRO) {
            return ResultadoValidacaoSinalCuradoria.invalido(
                    "curadoria.sinal.zeroExigeNeutro");
        }
        if (!zero && opcao == OpcaoSinalCuradoria.NEUTRO) {
            return ResultadoValidacaoSinalCuradoria.invalido(
                    "curadoria.sinal.neutroExigeZero");
        }
        return ResultadoValidacaoSinalCuradoria.valido();
    }

    public static String magnitudeSemSinal(String valor) {
        String v = valor == null ? "" : valor.trim();
        while (v.startsWith("+") || v.startsWith("-")) {
            v = v.substring(1).trim();
        }
        return v;
    }

    public static String aplicarSinal(String magnitude,
            OpcaoSinalCuradoria opcao) {
        String valor = magnitudeSemSinal(magnitude);
        if (valor.isEmpty() || "?".equals(valor)) {
            return valor;
        }
        if (opcao == OpcaoSinalCuradoria.NEGATIVO) {
            return "-" + valor;
        }
        if (opcao == OpcaoSinalCuradoria.POSITIVO) {
            return "+" + valor;
        }
        return valor;
    }

    public static boolean ehZero(String valor) {
        String v = magnitudeSemSinal(valor).replace(',', '.');
        if (v.isEmpty() || "?".equals(v)) {
            return false;
        }
        try {
            return new BigDecimal(v).compareTo(BigDecimal.ZERO) == 0;
        } catch (NumberFormatException ex) {
            return false;
        }
    }
}
