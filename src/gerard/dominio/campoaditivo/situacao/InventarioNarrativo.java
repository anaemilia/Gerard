package gerard.dominio.campoaditivo.situacao;

import gerard.semantica.numero.NumeroNatural;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/** Inventário imutável de objetos contados de um participante. */
public final class InventarioNarrativo {
    private final Map<ObjetoContado, NumeroNatural> quantidades;

    public InventarioNarrativo(Map<ObjetoContado, NumeroNatural> quantidades) {
        LinkedHashMap<ObjetoContado, NumeroNatural> copia = new LinkedHashMap<>();
        if (quantidades != null) {
            for (Map.Entry<ObjetoContado, NumeroNatural> entrada : quantidades.entrySet()) {
                if (entrada.getKey() == null || entrada.getValue() == null) {
                    throw new IllegalArgumentException("objeto e quantidade do inventário são obrigatórios");
                }
                if (entrada.getValue().intValue() > 0) {
                    copia.put(entrada.getKey(), entrada.getValue());
                }
            }
        }
        this.quantidades = Collections.unmodifiableMap(copia);
    }

    public static InventarioNarrativo vazio() {
        return new InventarioNarrativo(Collections.<ObjetoContado, NumeroNatural>emptyMap());
    }

    public Map<ObjetoContado, NumeroNatural> getQuantidades() { return quantidades; }

    public NumeroNatural quantidadeDe(ObjetoContado objeto) {
        NumeroNatural quantidade = quantidades.get(objeto);
        return quantidade == null ? new NumeroNatural(0) : quantidade;
    }

    public NumeroNatural totalDaFamilia(FamiliaObjeto familia) {
        int total = 0;
        for (Map.Entry<ObjetoContado, NumeroNatural> entrada : quantidades.entrySet()) {
            if (entrada.getKey().pertenceA(familia)) {
                total = Math.addExact(total, entrada.getValue().intValue());
            }
        }
        return new NumeroNatural(total);
    }

    public InventarioNarrativo acrescentar(ObjetoContado objeto, NumeroNatural quantidade) {
        exigirAlteracao(objeto, quantidade);
        int novoValor = Math.addExact(quantidadeDe(objeto).intValue(), quantidade.intValue());
        return substituir(objeto, novoValor);
    }

    public InventarioNarrativo retirar(ObjetoContado objeto, NumeroNatural quantidade) {
        exigirAlteracao(objeto, quantidade);
        int atual = quantidadeDe(objeto).intValue();
        if (quantidade.intValue() > atual) {
            throw new IllegalStateException("narrativa.inventario.quantidade_insuficiente");
        }
        return substituir(objeto, atual - quantidade.intValue());
    }

    public boolean mesmasQuantidades(InventarioNarrativo outro) {
        if (outro == null || quantidades.size() != outro.quantidades.size()) {
            return false;
        }
        for (Map.Entry<ObjetoContado, NumeroNatural> entrada : quantidades.entrySet()) {
            if (entrada.getValue().intValue()
                    != outro.quantidadeDe(entrada.getKey()).intValue()) {
                return false;
            }
        }
        return true;
    }

    private InventarioNarrativo substituir(ObjetoContado objeto, int valor) {
        LinkedHashMap<ObjetoContado, NumeroNatural> copia = new LinkedHashMap<>(quantidades);
        if (valor == 0) {
            copia.remove(objeto);
        } else {
            copia.put(objeto, new NumeroNatural(valor));
        }
        return new InventarioNarrativo(copia);
    }

    private static void exigirAlteracao(ObjetoContado objeto, NumeroNatural quantidade) {
        if (objeto == null || quantidade == null) {
            throw new IllegalArgumentException("objeto e quantidade da alteração são obrigatórios");
        }
    }
}
