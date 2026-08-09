package gerard.semantica.quantidade;

import gerard.semantica.numero.ConversorTextoParaInteiroSemantico;

public final class TesteFronteiraContextoQuantidade {

    public static void main(String[] args) {
        ContextoQuantidade contexto = new ContextoQuantidade() {
            public String getCodigoIdioma() { return "pt-BR"; }
            public String getContexto() { return "Dinheiro"; }
            public String getEnunciado() { return "Maria tinha R$ 1.250,00."; }
            public String getRepresentacaoVisual() { return ""; }
            public String getObservacoes() { return ""; }
        };

        ServicoQuantidadeContextual servico =
                new ServicoQuantidadeContextual();
        exigir(servico.resolverPerfil(contexto).getTipo()
                        == TipoGrandezaQuantitativa.MONETARIA,
                "contexto independente deveria identificar grandeza monetária");
        exigir("1.250".equals(servico.formatarMedidaParaDiagrama(
                        1250, contexto)),
                "formatação deveria usar o idioma do contrato");

        ConversorTextoParaInteiroSemantico conversor =
                new ConversorTextoParaInteiroSemantico();
        exigir(Integer.valueOf(1250).equals(
                        conversor.converter("1.250,00", contexto)),
                "conversor deveria aceitar o contrato sem modelo concreto");

        System.out.println("Teste aprovado: quantidade independe da situação concreta.");
    }

    private static void exigir(boolean condicao, String mensagem) {
        if (!condicao) {
            throw new AssertionError(mensagem);
        }
    }
}
