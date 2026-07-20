package gerard.semantica.papel;

import gerard.semantica.numero.DominioNumerico;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Fonte conceitual dos papéis quantitativos e de seus universos numéricos.
 * A transformação e o número/valor relativo pertencem a Z; medidas e estados
 * pertencem a N0.
 */
public final class CatalogoPapeisSemanticos {
    private final Map<String, PapelQuantitativo> exatos =
            new LinkedHashMap<String, PapelQuantitativo>();

    public CatalogoPapeisSemanticos() {
        registrarNatural("papel.parte", "Parte");
        registrarNatural("papel.parte1", "Parte 1");
        registrarNatural("papel.parte2", "Parte 2");
        registrarNatural("papel.todo", "Todo");
        registrarNatural("papel.estadoInicial", "Estado inicial");
        registrarNatural("papel.estadoIntermediario", "Estado intermediário");
        registrarNatural("papel.estadoFinal", "Estado final");
        registrarNatural("papel.referido", "Referido");
        registrarNatural("papel.referendo", "Referendo");
        registrarNatural("papel.referente", "Referendo");

        registrarInteiro("papel.transformacao", "Transformação");
        registrarInteiro("papel.transformacao1", "Transformação 1");
        registrarInteiro("papel.transformacao2", "Transformação 2");
        registrarInteiro("papel.transformacaoFinal", "Transformação resultante");
        registrarInteiro("papel.diferenca", "Valor relativo");
        registrarInteiro("papel.valorRelativo", "Valor relativo");
        registrarInteiro("papel.numeroRelativo", "Número relativo");

        // Relações aditivas explícitas são grandezas relativas e, portanto,
        // utilizam o mesmo universo inteiro dos valores relativos.
        registrarInteiro("papel.relacao", "Relação");
        registrarInteiro("papel.relacaoInicial", "Relação inicial");
        registrarInteiro("papel.relacaoFinal", "Relação final");
        registrarInteiro("papel.relacao1", "Relação 1");
        registrarInteiro("papel.relacao2", "Relação 2");
    }

    private void registrarNatural(String chave, String nome) {
        exatos.put(chave, new PapelQuantitativo(chave, nome, DominioNumerico.NATURAIS));
    }

    private void registrarInteiro(String chave, String nome) {
        exatos.put(chave, new PapelQuantitativo(chave, nome, DominioNumerico.INTEIROS));
    }

    public PapelQuantitativo obter(String chavePapel) {
        String chave = chavePapel == null ? "" : chavePapel.trim();
        PapelQuantitativo papel = exatos.get(chave);
        if (papel != null) {
            return papel;
        }
        if (chave.startsWith("papel.transformacao")) {
            return new PapelQuantitativo(chave, "Transformação", DominioNumerico.INTEIROS);
        }
        if (chave.startsWith("papel.relacao")) {
            return new PapelQuantitativo(chave, "Relação", DominioNumerico.INTEIROS);
        }
        if (chave.startsWith("papel.parte")) {
            return new PapelQuantitativo(chave, "Parte", DominioNumerico.NATURAIS);
        }
        return new PapelQuantitativo(chave, "Valor", DominioNumerico.NATURAIS);
    }

    public DominioNumerico dominioDoPapel(String chavePapel) {
        return obter(chavePapel).getDominio();
    }

    public boolean papelPermiteSinal(String chavePapel) {
        return dominioDoPapel(chavePapel) == DominioNumerico.INTEIROS;
    }

    public Map<String, PapelQuantitativo> papeisRegistrados() {
        return Collections.unmodifiableMap(exatos);
    }
}
