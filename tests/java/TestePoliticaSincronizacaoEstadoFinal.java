package gerard.campoaditivo.sincronizacao;

public final class TestePoliticaSincronizacaoEstadoFinal {
    public static void main(String[] args) {
        PoliticaSincronizacaoEstadoFinal politica =
                new PoliticaSincronizacaoEstadoFinal();
        exigir(politica.aceita(true, "papel.valor"),
                "marcacao de incognita principal deve habilitar");
        exigir(politica.aceita(false, "papel.estadoFinal"),
                "papel estado final deve habilitar");
        exigir(!politica.aceita(false, "papel.estadoInicial"),
                "outro papel nao deve habilitar");
        exigir(!politica.aceita(false, null),
                "papel ausente nao deve habilitar");
    }

    private static void exigir(boolean condicao, String mensagem) {
        if (!condicao) {
            throw new AssertionError(mensagem);
        }
    }
}
