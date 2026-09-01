package gerard.dominio.campoaditivo.situacao;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Objeto do domínio que pode ser contado. A identidade, a família, as
 * características e a chave visual abstrata não dependem de uma mídia.
 */
public final class ObjetoContado {
    private final String id;
    private final FamiliaObjeto familia;
    private final Map<String, String> caracteristicas;
    private final String chaveVisualAbstrata;

    public ObjetoContado(
            String id,
            FamiliaObjeto familia,
            Map<String, String> caracteristicas,
            String chaveVisualAbstrata) {
        this.id = FamiliaObjeto.obrigatorio(id, "id do objeto contado não pode ser vazio");
        if (familia == null) {
            throw new IllegalArgumentException("família do objeto contado é obrigatória");
        }
        this.familia = familia;
        this.caracteristicas = copiarCaracteristicas(caracteristicas);
        this.chaveVisualAbstrata = FamiliaObjeto.obrigatorio(
                chaveVisualAbstrata, "chave visual abstrata não pode ser vazia");
    }

    public String getId() { return id; }
    public FamiliaObjeto getFamilia() { return familia; }
    public Map<String, String> getCaracteristicas() { return caracteristicas; }
    public String getChaveVisualAbstrata() { return chaveVisualAbstrata; }

    public boolean pertenceA(FamiliaObjeto familiaEsperada) {
        return familia.equals(familiaEsperada);
    }

    public String valorDaCaracteristica(String chave) {
        return caracteristicas.get(chave);
    }

    @Override
    public boolean equals(Object outro) {
        return this == outro || (outro instanceof ObjetoContado
                && id.equals(((ObjetoContado) outro).id));
    }

    @Override
    public int hashCode() { return id.hashCode(); }

    private static Map<String, String> copiarCaracteristicas(Map<String, String> origem) {
        LinkedHashMap<String, String> copia = new LinkedHashMap<>();
        if (origem != null) {
            for (Map.Entry<String, String> entrada : origem.entrySet()) {
                String chave = FamiliaObjeto.obrigatorio(
                        entrada.getKey(), "chave de característica não pode ser vazia");
                String valor = FamiliaObjeto.obrigatorio(
                        entrada.getValue(), "valor de característica não pode ser vazio");
                copia.put(chave, valor);
            }
        }
        return Collections.unmodifiableMap(copia);
    }
}
