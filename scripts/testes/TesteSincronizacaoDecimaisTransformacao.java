package gerard.campoaditivo.transformacao.processo;

import gerard.campoaditivo.modelo.TipoSituacaoAditiva;
import gerard.campoaditivo.sincronizacao.EstadoSemanticoCompartilhado;
import gerard.semantica.numero.ConversorTextoParaInteiroSemantico;

public final class TesteSincronizacaoDecimaisTransformacao {
    private static int verificacoes;

    public static void main(String[] args) {
        ConversorTextoParaInteiroSemantico conversor =
                new ConversorTextoParaInteiroSemantico();
        verificarConversao(conversor, "25,00", 25);
        verificarConversao(conversor, "18.00", 18);
        verificarConversao(conversor, "−4", -4);
        verificarConversao(conversor, "+8", 8);
        verificarConversao(conversor, "1.250,00", 1250);
        verificarConversao(conversor, "1,250.00", 1250);
        verificarConversao(conversor, "R$ 25,00", 25);
        verificarConversao(conversor, "25,50", null);
        verificarConversao(conversor, "?", null);

        EstadoSemanticoCompartilhado estado =
                new EstadoSemanticoCompartilhado();
        EstadoSemanticoCompartilhado.Snapshot snapshot = estado.atualizar(
                TipoSituacaoAditiva.TRANSFORMACAO_MEDIDAS,
                new Integer[] {
                    conversor.converter("25,00"),
                    conversor.converter("-18,00"),
                    null
                },
                new boolean[] {true, true, false},
                1,
                EstadoSemanticoCompartilhado.Origem.VERGNAUD);

        PlanoUnidadesProcessoTransformacao plano =
                new SincronizadorUnidadesProcessoTransformacao()
                        .criarPlano(snapshot);
        confirmar(plano.isConhecido(0) && plano.getQuantidade(0) == 25,
                "25,00 deve materializar 25 quadradinhos no estado inicial");
        confirmar(plano.isConhecido(1) && plano.getQuantidade(1) == 18,
                "-18,00 deve materializar 18 quadradinhos no funil de retirada");
        confirmar(plano.isConhecido(2) && plano.getQuantidade(2) == 7,
                "o estado final deve ser sincronizado como 7");
        confirmar("transformacao_negativa".equals(plano.getOrigem(1)),
                "o sinal negativo deve ser preservado na origem visual");

        System.out.println("Teste de decimais integrais e quadradinhos aprovado: "
                + verificacoes + " verificações.");
    }

    private static void verificarConversao(
            ConversorTextoParaInteiroSemantico conversor,
            String texto, Integer esperado) {
        Integer obtido = conversor.converter(texto);
        confirmar(esperado == null ? obtido == null : esperado.equals(obtido),
                "conversão de " + texto + ": esperado=" + esperado
                        + ", obtido=" + obtido);
    }

    private static void confirmar(boolean condicao, String mensagem) {
        verificacoes++;
        if (!condicao) {
            throw new AssertionError(mensagem);
        }
    }
}
