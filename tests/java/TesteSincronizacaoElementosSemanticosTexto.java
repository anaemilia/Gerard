import gerard.campoaditivo.diagrama.elementos.ElementoTextoMovel;
import gerard.campoaditivo.diagrama.elementos.ItemTextoArrastavel;
import gerard.campoaditivo.modelo.TipoSituacaoAditiva;
import gerard.campoaditivo.sincronizacao.EstadoSemanticoCompartilhado;
import gerard.campoaditivo.sincronizacao.texto.ElementoSemanticoTexto;
import gerard.campoaditivo.sincronizacao.texto.MapeadorPapelSemanticoTexto;
import gerard.campoaditivo.sincronizacao.texto.SincronizadorElementosSemanticosTexto;
import gerard.campoaditivo.sincronizacao.texto.SincronizadorElementosSemanticosTextoAditivo;
import java.util.ArrayList;
import java.util.List;

public final class TesteSincronizacaoElementosSemanticosTexto {
    public static void main(String[] args) {
        EstadoSemanticoCompartilhado estado = new EstadoSemanticoCompartilhado();
        estado.limpar(TipoSituacaoAditiva.COMPARACAO_MEDIDAS);
        EstadoSemanticoCompartilhado.Snapshot snapshot = estado.atualizar(
                TipoSituacaoAditiva.COMPARACAO_MEDIDAS,
                new Integer[] { 4, 8, 12 },
                new boolean[] { true, true, true },
                0,
                EstadoSemanticoCompartilhado.Origem.DIAGRAMA_COMPLEMENTAR);

        ElementoTextoMovel referido = new ElementoTextoMovel("6,", 10);
        referido.vincularSemantica("papel.referido", 0, 1, "6");
        ElementoTextoMovel relativo = new ElementoTextoMovel("8", 20);
        relativo.vincularSemantica("papel.diferenca", 0, 1, "8");
        ElementoTextoMovel referendo = new ElementoTextoMovel("?", 30);
        referendo.vincularSemantica("papel.referendo", 0, 1, "?");
        ItemTextoArrastavel marcadorMovido = new ItemTextoArrastavel(
                120, 120, 18, 20, "6", false, "6", "papel.referido");
        ItemTextoArrastavel incognitaMovidaNoTexto = new ItemTextoArrastavel(
                160, 120, 18, 20, "?", false, "?", "papel.referendo");

        List<ElementoSemanticoTexto> elementos = new ArrayList<ElementoSemanticoTexto>();
        elementos.add(referido);
        elementos.add(relativo);
        elementos.add(referendo);
        elementos.add(marcadorMovido);
        elementos.add(incognitaMovidaNoTexto);

        SincronizadorElementosSemanticosTexto sincronizador =
                new SincronizadorElementosSemanticosTextoAditivo();
        sincronizador.sincronizar(elementos, snapshot,
                new MapeadorPapelSemanticoTexto() {
                    @Override
                    public int paraIndiceSemantico(String chave) {
                        if ("papel.referido".equals(chave)) return 0;
                        if ("papel.diferenca".equals(chave)) return 1;
                        if ("papel.referendo".equals(chave)) return 2;
                        return -1;
                    }
                });

        exigir("4,".equals(referido.valor),
                "O referido textual deveria acompanhar o diagrama e preservar a pontuação.");
        exigir("8".equals(relativo.valor),
                "O valor relativo textual deveria permanecer sincronizado.");
        exigir("?".equals(referendo.valor),
                "A interrogação textual deve permanecer inalterada.");
        exigir("4".equals(marcadorMovido.valor),
                "O marcador deslocado dentro da área do enunciado deveria ser atualizado.");
        exigir("?".equals(incognitaMovidaNoTexto.valor),
                "A interrogação deslocada na área do enunciado deve permanecer inalterada.");
        exigir("?".equals(referendo.getValorSemanticoOriginal()),
                "A origem da incógnita deve permanecer estável para logs e validações.");

        // Correção 2026-08-18: "Retire os sinais dos números no texto. Não
        // faz sentido" — bug relatado pela usuária ao testar Transformação
        // de Relação ("Julia tem -3 bonecas a mais que Maria..."). Raiz:
        // vincularPapeisSemanticosAosElementosTexto (Main.java) passa
        // numero.getValorCanonico() como valorOriginalDoPapel — o valor
        // canônico do papel curado (com sinal, ex. "-3"), não o texto
        // literalmente digitado no enunciado (que é só "3"). O antigo
        // formatarValor lia esse "original" para decidir se preservava o
        // sinal, então o sinal curado vazava de volta pra frase. Simulado
        // aqui construindo o ElementoTextoMovel exatamente como o bug real
        // (vincularSemantica com o 4º parâmetro assinalado, embora o texto
        // do enunciado nunca tivesse sinal) — o texto sincronizado deve
        // mostrar sempre a magnitude, nunca o sinal.
        EstadoSemanticoCompartilhado estadoRelacao = new EstadoSemanticoCompartilhado();
        estadoRelacao.limpar(TipoSituacaoAditiva.TRANSFORMACAO_RELACAO);
        EstadoSemanticoCompartilhado.Snapshot snapshotRelacao = estadoRelacao.atualizar(
                TipoSituacaoAditiva.TRANSFORMACAO_RELACAO,
                new Integer[] { -3, 5, 2 },
                new boolean[] { true, true, true },
                0,
                EstadoSemanticoCompartilhado.Origem.DIAGRAMA_COMPLEMENTAR);

        ElementoTextoMovel relacaoInicialComSinalCurado = new ElementoTextoMovel("3", 50);
        relacaoInicialComSinalCurado.vincularSemantica("papel.relacaoInicial", 0, 1, "-3");
        ElementoTextoMovel transformacaoComSinalCurado = new ElementoTextoMovel("5", 60);
        transformacaoComSinalCurado.vincularSemantica("papel.transformacao", 0, 1, "+5");

        List<ElementoSemanticoTexto> elementosRelacao = new ArrayList<ElementoSemanticoTexto>();
        elementosRelacao.add(relacaoInicialComSinalCurado);
        elementosRelacao.add(transformacaoComSinalCurado);

        sincronizador.sincronizar(elementosRelacao, snapshotRelacao,
                new MapeadorPapelSemanticoTexto() {
                    @Override
                    public int paraIndiceSemantico(String chave) {
                        if ("papel.relacaoInicial".equals(chave)) return 0;
                        if ("papel.transformacao".equals(chave)) return 1;
                        return -1;
                    }
                });

        exigir("3".equals(relacaoInicialComSinalCurado.valor),
                "O sinal curado (\"-3\") não deve vazar para o número no enunciado — só a magnitude \"3\".");
        exigir("5".equals(transformacaoComSinalCurado.valor),
                "O sinal curado (\"+5\") não deve vazar para o número no enunciado — só a magnitude \"5\".");

        System.out.println("Teste aprovado: valores conhecidos acompanham os diagramas, a interrogação permanece estável, "
                + "e o sinal curado do papel nunca vaza para o número exibido no enunciado.");
    }

    private static void exigir(boolean condicao, String mensagem) {
        if (!condicao) {
            throw new AssertionError(mensagem);
        }
    }
}
