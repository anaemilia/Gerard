package gerard.dominio.campoaditivo.situacao;

import java.util.Objects;

/**
 * Família conceitual de objetos contáveis, por exemplo rosas, bilas ou
 * figurinhas. As variações concretas permanecem em {@link ObjetoContado}.
 */
public final class FamiliaObjeto {
    private final String id;
    private final String nomeConceitual;

    public FamiliaObjeto(String id, String nomeConceitual) {
        this.id = obrigatorio(id, "id da família não pode ser vazio");
        this.nomeConceitual = obrigatorio(
                nomeConceitual, "nome conceitual da família não pode ser vazio");
    }

    public String getId() { return id; }
    public String getNomeConceitual() { return nomeConceitual; }

    @Override
    public boolean equals(Object outro) {
        return this == outro || (outro instanceof FamiliaObjeto
                && id.equals(((FamiliaObjeto) outro).id));
    }

    @Override
    public int hashCode() { return id.hashCode(); }

    @Override
    public String toString() { return nomeConceitual; }

    static String obrigatorio(String valor, String mensagem) {
        String normalizado = valor == null ? "" : valor.trim();
        if (normalizado.isEmpty()) {
            throw new IllegalArgumentException(mensagem);
        }
        return normalizado;
    }
}
