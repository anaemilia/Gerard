package gerard.dominio.atividade;

public final class TesteCatalogoObjetosLogAcaoInstrumental {
    public static void main(String[] args) {
        CatalogoObjetosLogAcaoInstrumental catalogo = new CatalogoObjetosLogAcaoInstrumental();
        exigir(catalogo, "papel.estadoInicial", "OBJ1");
        exigir(catalogo, "papel.estadoFinal", "OBJ2");
        exigir(catalogo, "papel.referendo", "OBJ3");
        exigir(catalogo, "papel.referente", "OBJ3");
        exigir(catalogo, "papel.diferenca", "OBJ4");
        exigir(catalogo, "papel.transformacao2", "OBJ4");
        exigir(catalogo, "papel.relacaoFinal", "OBJ4");
        exigir(catalogo, "papel.referido", "OBJ5");
        exigir(catalogo, "papel.todo", "OBJ6");
        exigir(catalogo, "papel.parte1", "OBJ7");
        exigir(catalogo, "papel.valor", "OBJ8");
        exigir(catalogo, null, "OBJ8");
    }

    private static void exigir(CatalogoObjetosLogAcaoInstrumental catalogo,
            String papel, String esperado) {
        String atual = catalogo.obterCodigo(papel);
        if (!esperado.equals(atual)) {
            throw new AssertionError(papel + ": esperado=" + esperado + ", atual=" + atual);
        }
    }
}
