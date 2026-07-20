package gerard.interpretacao.classificacao;

import gerard.campoaditivo.modelo.TipoSituacaoAditiva;
import gerard.idioma.IdiomaInterface;

/**
 * Exemplo rotulado usado para treinar o classificador leve de categorias de Vergnaud.
 */
public class ExemploSituacaoVergnaud {
    private final String texto;
    private final TipoSituacaoAditiva categoria;
    private final IdiomaInterface idioma;
    private final String contexto;

    public ExemploSituacaoVergnaud(String texto, TipoSituacaoAditiva categoria) {
        this(texto, categoria, IdiomaInterface.PORTUGUES, "");
    }

    public ExemploSituacaoVergnaud(String texto, TipoSituacaoAditiva categoria, IdiomaInterface idioma, String contexto) {
        this.texto = texto == null ? "" : texto;
        this.categoria = categoria;
        this.idioma = idioma == null ? IdiomaInterface.PORTUGUES : idioma;
        this.contexto = contexto == null ? "" : contexto;
    }

    public String getTexto() {
        return texto;
    }

    public TipoSituacaoAditiva getCategoria() {
        return categoria;
    }

    public IdiomaInterface getIdioma() {
        return idioma;
    }

    public String getContexto() {
        return contexto;
    }
}
