package gerard.interacao.unidades;

/** Fato técnico devolvido pelo proprietário da quantidade após aplicar. */
public final class ResultadoAplicacaoControleUnidade {

    private final boolean limiteAtingidoAposAplicar;
    private final boolean podeRepetir;

    public ResultadoAplicacaoControleUnidade(
            boolean limiteAtingidoAposAplicar, boolean podeRepetir) {
        this.limiteAtingidoAposAplicar = limiteAtingidoAposAplicar;
        this.podeRepetir = podeRepetir;
    }

    public boolean isLimiteAtingidoAposAplicar() { return limiteAtingidoAposAplicar; }
    public boolean podeRepetir() { return podeRepetir; }
}
