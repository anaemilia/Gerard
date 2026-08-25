package gerard.adaptacao;

import java.util.Objects;

/**
 * Identificador aberto do menor escopo que possui os fatos de uma decisão.
 * Não é enum: novos tipos de objeto semântico não precisam alterar este tipo.
 */
public final class EscopoProprietarioSemantico {

    public static final EscopoProprietarioSemantico PAPEL = de("PAPEL");
    public static final EscopoProprietarioSemantico RELACAO = de("RELACAO");
    public static final EscopoProprietarioSemantico TENTATIVA = de("TENTATIVA");
    public static final EscopoProprietarioSemantico SITUACAO = de("SITUACAO");

    private final String chave;

    private EscopoProprietarioSemantico(String chave) {
        this.chave = chave;
    }

    public static EscopoProprietarioSemantico de(String chave) {
        String normalizada = chave == null ? "" : chave.trim();
        if (normalizada.isEmpty()) {
            throw new IllegalArgumentException("chave de escopo não pode ser vazia");
        }
        return new EscopoProprietarioSemantico(normalizada);
    }

    public String getChave() { return chave; }

    @Override
    public boolean equals(Object outro) {
        return this == outro || (outro instanceof EscopoProprietarioSemantico
                && chave.equals(((EscopoProprietarioSemantico) outro).chave));
    }

    @Override
    public int hashCode() {
        return Objects.hash(chave);
    }

    @Override
    public String toString() {
        return chave;
    }
}
