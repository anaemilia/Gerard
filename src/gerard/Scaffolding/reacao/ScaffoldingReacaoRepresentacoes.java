package gerard.Scaffolding.reacao;

import gerard.campoaditivo.semantica.PoliticaValoresAditivos;

/**
 * Modulo de reacao entre representacoes do scaffolding.
 *
 * A classe concentra regras-base, puras, para manter consistencia entre:
 * - eixo x dos inteiros;
 * - circulo da relacao/transformacao;
 * - medidas retangulares do diagrama de Vergnaud.
 *
 * As operacoes sobre componentes Swing e elementos concretos do diagrama ficam
 * na tela principal; este modulo fornece a semantica reativa comum.
 */
public class ScaffoldingReacaoRepresentacoes {
    private final PoliticaValoresAditivos politicaValores =
            new PoliticaValoresAditivos();

    public static final class ResultadoQuantidadeDependente {
        private final int indiceDependente;
        private final Integer valor;

        private ResultadoQuantidadeDependente(int indiceDependente, Integer valor) {
            this.indiceDependente = indiceDependente;
            this.valor = valor;
        }

        public static ResultadoQuantidadeDependente ausente() {
            return new ResultadoQuantidadeDependente(-1, null);
        }

        public static ResultadoQuantidadeDependente calculado(
                int indiceDependente, Integer valor) {
            return new ResultadoQuantidadeDependente(indiceDependente, valor);
        }

        public boolean foiCalculado() {
            return indiceDependente >= 0 && valor != null;
        }

        public int getIndiceDependente() {
            return indiceDependente;
        }

        public Integer getValor() {
            return valor;
        }
    }


    public enum OrigemAlteracao {
        EIXO,
        NUMERO_RELATIVO,
        ESTADO_INICIAL,
        ESTADO_FINAL,
        TEXTO,
        ARRASTE,
        OUTRA
    }

    public Integer calcularEstadoFinal(Integer estadoInicial, Integer relacao) {
        if (estadoInicial == null || relacao == null) {
            return null;
        }
        return Integer.valueOf(estadoInicial.intValue() + relacao.intValue());
    }

    /**
     * Quantidades de medidas representam cardinais e, por isso, não podem ser
     * negativas. O sinal pertence somente à transformação ou ao valor relativo.
     */
    public boolean quantidadeEhNaoNegativa(Integer quantidade) {
        return politicaValores.quantidadeEhNaoNegativa(quantidade);
    }

    /**
     * Verifica antecipadamente se a aplicação de uma transformação/relação a
     * uma quantidade conhecida produziria outra quantidade válida.
     */
    public boolean relacaoPreservaQuantidadeNaoNegativa(
            Integer quantidadeBase, Integer relacao) {
        return politicaValores.relacaoPreservaQuantidadeNaoNegativa(
                quantidadeBase, relacao);
    }

    /**
     * Calcula qual quantidade vizinha da relação seria alterada. O resultado
     * carrega também o índice real do elemento dependente, permitindo que a
     * política semântica decida se aquele papel é uma quantidade ou um valor
     * assinado.
     */
    public ResultadoQuantidadeDependente calcularQuantidadeDependente(
            int indiceRelacao,
            Integer valorAnterior,
            Integer valorPosterior,
            boolean valorAnteriorDesconhecido,
            int valorRelativo
    ) {
        if (indiceRelacao < 1) {
            return ResultadoQuantidadeDependente.ausente();
        }
        if (valorAnteriorDesconhecido && valorPosterior != null) {
            return ResultadoQuantidadeDependente.calculado(
                    indiceRelacao - 1,
                    calcularEstadoInicial(valorPosterior,
                            Integer.valueOf(valorRelativo)));
        }
        if (valorAnterior != null) {
            return ResultadoQuantidadeDependente.calculado(
                    indiceRelacao + 1,
                    calcularEstadoFinal(valorAnterior,
                            Integer.valueOf(valorRelativo)));
        }
        if (valorPosterior != null) {
            return ResultadoQuantidadeDependente.calculado(
                    indiceRelacao - 1,
                    calcularEstadoInicial(valorPosterior,
                            Integer.valueOf(valorRelativo)));
        }
        return ResultadoQuantidadeDependente.ausente();
    }

    public Integer calcularRelacao(Integer estadoInicial, Integer estadoFinal) {
        if (estadoInicial == null || estadoFinal == null) {
            return null;
        }
        return Integer.valueOf(estadoFinal.intValue() - estadoInicial.intValue());
    }

    public Integer calcularEstadoInicial(Integer estadoFinal, Integer relacao) {
        if (estadoFinal == null || relacao == null) {
            return null;
        }
        return Integer.valueOf(estadoFinal.intValue() - relacao.intValue());
    }

    public String formatarNumeroRelativo(int valor) {
        if (valor == 0) {
            return "0";
        }
        return (valor < 0 ? "-" : "+") + Math.abs(valor);
    }

    public String sinalDe(int valor) {
        return valor < 0 ? "-" : "+";
    }

    public String valorAbsolutoComoTexto(int valor) {
        return Integer.toString(Math.abs(valor));
    }

    public int calcularValorRelativo(String base, String sinal) {
        int absoluto = 0;
        try {
            absoluto = Math.abs(Integer.parseInt(removerSinal(base)));
        } catch (Exception ex) {
            absoluto = 0;
        }
        return "-".equals(sinal) ? -absoluto : absoluto;
    }

    public String removerSinal(String valor) {
        if (valor == null) {
            return "";
        }
        String texto = valor.trim();
        while (texto.startsWith("+") || texto.startsWith("-")) {
            texto = texto.substring(1).trim();
        }
        return texto;
    }
}
