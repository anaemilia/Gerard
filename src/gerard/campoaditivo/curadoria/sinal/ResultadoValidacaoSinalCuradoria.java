package gerard.campoaditivo.curadoria.sinal;

/** Resultado imutável da validação de uma escolha de sinal. */
public final class ResultadoValidacaoSinalCuradoria {
    private static final ResultadoValidacaoSinalCuradoria VALIDO =
            new ResultadoValidacaoSinalCuradoria(true, "");

    private final boolean valido;
    private final String chaveMensagem;

    private ResultadoValidacaoSinalCuradoria(boolean valido, String chaveMensagem) {
        this.valido = valido;
        this.chaveMensagem = chaveMensagem == null ? "" : chaveMensagem;
    }

    public static ResultadoValidacaoSinalCuradoria valido() {
        return VALIDO;
    }

    public static ResultadoValidacaoSinalCuradoria invalido(String chaveMensagem) {
        return new ResultadoValidacaoSinalCuradoria(false, chaveMensagem);
    }

    public boolean isValido() {
        return valido;
    }

    public String getChaveMensagem() {
        return chaveMensagem;
    }
}
