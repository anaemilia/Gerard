package gerard.adaptacao.modelousuario;

import gerard.campoaditivo.modelo.TipoSituacaoAditiva;
import java.util.Optional;

/** Fotografia das categorias de maior e menor domínio, sem fabricar extremos. */
public final class DominioCategoriasProjetado {

    private final TipoSituacaoAditiva maiorDominio;
    private final TipoSituacaoAditiva menorDominio;

    public DominioCategoriasProjetado(
            TipoSituacaoAditiva maiorDominio,
            TipoSituacaoAditiva menorDominio) {
        if (maiorDominio == null && menorDominio == null) {
            throw new IllegalArgumentException("ao menos uma categoria de domínio deve estar presente");
        }
        this.maiorDominio = maiorDominio;
        this.menorDominio = menorDominio;
    }

    public Optional<TipoSituacaoAditiva> getMaiorDominio() {
        return Optional.ofNullable(maiorDominio);
    }

    public Optional<TipoSituacaoAditiva> getMenorDominio() {
        return Optional.ofNullable(menorDominio);
    }
}
