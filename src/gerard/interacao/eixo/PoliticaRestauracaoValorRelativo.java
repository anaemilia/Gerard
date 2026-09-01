package gerard.interacao.eixo;

/**
 * Decide o valor local exibido após uma edição do eixo ser rejeitada antes
 * de sua publicação no estado semântico compartilhado.
 */
public final class PoliticaRestauracaoValorRelativo {

    public int escolherValorSeguro(Integer valorAnterior, int candidato) {
        return valorAnterior != null
                ? valorAnterior.intValue()
                : moduloRepresentavelOuZero(candidato);
    }

    public int restaurarComoPositivo(int valor) {
        return moduloRepresentavelOuZero(valor);
    }

    private static int moduloRepresentavelOuZero(int valor) {
        return valor == Integer.MIN_VALUE ? 0 : Math.abs(valor);
    }
}
