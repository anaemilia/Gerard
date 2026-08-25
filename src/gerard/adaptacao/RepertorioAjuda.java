package gerard.adaptacao;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/** Repertório imutável pertencente a exatamente um proprietário semântico. */
public final class RepertorioAjuda {

    private final String proprietarioSemantico;
    private final Map<String, ItemRepertorioAjuda> itensPorCodigo;

    public RepertorioAjuda(String proprietarioSemantico, List<ItemRepertorioAjuda> itens) {
        this.proprietarioSemantico = textoObrigatorio(proprietarioSemantico, "proprietário semântico");
        if (itens == null) {
            throw new IllegalArgumentException("itens não podem ser nulos");
        }
        Map<String, ItemRepertorioAjuda> copia = new LinkedHashMap<String, ItemRepertorioAjuda>();
        for (ItemRepertorioAjuda item : itens) {
            if (item == null) {
                throw new IllegalArgumentException("item de repertório não pode ser nulo");
            }
            if (copia.put(item.getCodigo(), item) != null) {
                throw new IllegalArgumentException("código de ajuda duplicado: " + item.getCodigo());
            }
        }
        this.itensPorCodigo = Collections.unmodifiableMap(copia);
    }

    public String getProprietarioSemantico() { return proprietarioSemantico; }
    public Map<String, ItemRepertorioAjuda> getItensPorCodigo() { return itensPorCodigo; }

    public Optional<ItemRepertorioAjuda> obter(String codigo) {
        return Optional.ofNullable(itensPorCodigo.get(codigo));
    }

    private static String textoObrigatorio(String valor, String nome) {
        String normalizado = valor == null ? "" : valor.trim();
        if (normalizado.isEmpty()) {
            throw new IllegalArgumentException(nome + " não pode ser vazio");
        }
        return normalizado;
    }
}
