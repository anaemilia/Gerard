package gerard.dominio.atividade;

import gerard.campoaditivo.semantica.CatalogoPapeisSemanticosAditivos;
import gerard.campoaditivo.semantica.NaturezaPapelAditivo;

/** Associa identidades semanticas aos codigos OBJ do contrato de pesquisa. */
public final class CatalogoObjetosLogAcaoInstrumental {
    private final CatalogoPapeisSemanticosAditivos catalogoPapeis =
            new CatalogoPapeisSemanticosAditivos();

    public String obterCodigo(String chavePapel) {
        String chave = chavePapel == null ? "" : chavePapel.trim();
        if (catalogoPapeis.obterNatureza(chave)
                == NaturezaPapelAditivo.TRANSFORMACAO_OU_RELACAO) {
            return "OBJ4";
        }
        if ("papel.estadoInicial".equals(chave)) return "OBJ1";
        if ("papel.estadoFinal".equals(chave)) return "OBJ2";
        if ("papel.referendo".equals(chave)
                || "papel.referente".equals(chave)) return "OBJ3";
        if ("papel.referido".equals(chave)) return "OBJ5";
        if ("papel.todo".equals(chave)) return "OBJ6";
        if (chave.startsWith("papel.parte")) return "OBJ7";
        return "OBJ8";
    }
}
