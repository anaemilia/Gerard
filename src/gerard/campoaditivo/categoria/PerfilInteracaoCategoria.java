package gerard.campoaditivo.categoria;

import java.util.Collections;
import java.util.EnumSet;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Perfil imutável de uma categoria aditiva. Centraliza capacidades e papéis
 * que podem receber sinal, evitando condicionais espalhadas por tipo.
 */
public final class PerfilInteracaoCategoria {
    private final GrupoCategoriaAditiva grupo;
    private final Set<String> papeisAssinados;
    private final Set<Integer> indicesCompartilhadosAssinados;
    private final EnumSet<CapacidadeInteracaoCategoria> capacidades;

    public PerfilInteracaoCategoria(
            GrupoCategoriaAditiva grupo,
            Set<String> papeisAssinados,
            Set<Integer> indicesCompartilhadosAssinados,
            EnumSet<CapacidadeInteracaoCategoria> capacidades) {
        this.grupo = grupo == null
                ? GrupoCategoriaAditiva.SEM_RELACAO_ASSINADA : grupo;
        this.papeisAssinados = Collections.unmodifiableSet(
                new LinkedHashSet<String>(papeisAssinados == null
                        ? Collections.<String>emptySet() : papeisAssinados));
        this.indicesCompartilhadosAssinados = Collections.unmodifiableSet(
                new LinkedHashSet<Integer>(indicesCompartilhadosAssinados == null
                        ? Collections.<Integer>emptySet()
                        : indicesCompartilhadosAssinados));
        this.capacidades = capacidades == null
                ? EnumSet.noneOf(CapacidadeInteracaoCategoria.class)
                : EnumSet.copyOf(capacidades);
    }

    public GrupoCategoriaAditiva getGrupo() {
        return grupo;
    }

    public boolean possuiRelacaoAssinada() {
        return grupo == GrupoCategoriaAditiva.COM_RELACAO_ASSINADA
                && capacidades.contains(CapacidadeInteracaoCategoria.RELACAO_ASSINADA);
    }

    public boolean possuiCapacidade(CapacidadeInteracaoCategoria capacidade) {
        return capacidade != null && capacidades.contains(capacidade);
    }

    public boolean papelPermiteSinal(String chavePapel) {
        String chave = chavePapel == null ? "" : chavePapel.trim();
        if (chave.length() == 0) {
            return false;
        }
        if (papeisAssinados.contains(chave)) {
            return true;
        }
        if (chave.startsWith("papel.transformacao")
                && papeisAssinados.contains("papel.transformacao*")) {
            return true;
        }
        if (chave.startsWith("papel.relacao")
                && papeisAssinados.contains("papel.relacao*")) {
            return true;
        }
        return false;
    }

    public boolean indiceCompartilhadoPermiteSinal(int indice) {
        return indicesCompartilhadosAssinados.contains(Integer.valueOf(indice));
    }

    public Set<String> getPapeisAssinados() {
        return papeisAssinados;
    }
}
