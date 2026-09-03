package gerard.campoaditivo.semantica;

public final class TesteCatalogoPapeisSemanticosAditivos {
    public static void main(String[] args) {
        CatalogoPapeisSemanticosAditivos catalogo =
                new CatalogoPapeisSemanticosAditivos();
        exigir(catalogo.chavePapelEspecifica("papel.parte1"),
                "papel canonico deve ser especifico");
        exigir(catalogo.chavePapelEspecifica("papel.referente"),
                "sinonimo historico deve continuar aceito");
        exigir(catalogo.chavePapelEspecifica("papel.transformacao7"),
                "prefixo legado de transformacao deve continuar aceito");
        exigir(!catalogo.chavePapelEspecifica("papel.valor"),
                "fallback generico nao deve ser papel especifico");
        exigir(!catalogo.chavePapelEspecifica("papel.desconhecido"),
                "chave desconhecida nao deve ser promovida");
        exigir(!catalogo.chavePapelEspecifica(null),
                "ausencia de chave nao deve ser promovida");
    }

    private static void exigir(boolean condicao, String mensagem) {
        if (!condicao) throw new AssertionError(mensagem);
    }
}
