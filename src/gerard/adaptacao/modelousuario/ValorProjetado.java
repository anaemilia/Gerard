package gerard.adaptacao.modelousuario;

import java.util.Optional;

/**
 * Valor imutável que não converte ausência em zero, falso ou texto vazio.
 * Também distingue ausência no modelo de omissão intencional na projeção.
 */
public final class ValorProjetado<T> {

    private static final String MOTIVO_NAO_SOLICITADO =
            "dimensão fora da projeção deste proprietário semântico";

    private final EstadoValorProjetado estado;
    private final T valor;
    private final String motivoAusencia;

    private ValorProjetado(EstadoValorProjetado estado, T valor, String motivoAusencia) {
        this.estado = estado;
        this.valor = valor;
        this.motivoAusencia = motivoAusencia;
    }

    public static <T> ValorProjetado<T> presente(T valor) {
        if (valor == null) {
            throw new IllegalArgumentException("valor presente não pode ser nulo");
        }
        return new ValorProjetado<T>(EstadoValorProjetado.PRESENTE, valor, null);
    }

    public static <T> ValorProjetado<T> ausente(String motivo) {
        return new ValorProjetado<T>(EstadoValorProjetado.AUSENTE_NO_MODELO, null,
                textoObrigatorio(motivo, "motivo da ausência"));
    }

    public static <T> ValorProjetado<T> naoSolicitado() {
        return new ValorProjetado<T>(EstadoValorProjetado.NAO_SOLICITADO, null,
                MOTIVO_NAO_SOLICITADO);
    }

    public EstadoValorProjetado getEstado() { return estado; }
    public Optional<T> getValor() { return Optional.ofNullable(valor); }
    public String getMotivoAusencia() { return motivoAusencia; }
    public boolean estaPresente() { return estado == EstadoValorProjetado.PRESENTE; }
    public boolean foiSolicitado() { return estado != EstadoValorProjetado.NAO_SOLICITADO; }

    private static String textoObrigatorio(String valor, String nome) {
        String normalizado = valor == null ? "" : valor.trim();
        if (normalizado.isEmpty()) {
            throw new IllegalArgumentException(nome + " não pode ser vazio");
        }
        return normalizado;
    }
}
