import gerard.campoaditivo.diagrama.elementos.CirculoVenn;
import gerard.campoaditivo.diagrama.elementos.ElementoTextoMovel;
import gerard.campoaditivo.diagrama.elementos.ElementoVergnaud;
import gerard.campoaditivo.diagrama.elementos.QuadradinhoVenn;
import gerard.campoaditivo.diagrama.modelo.TipoFiguraDiagrama;
import gerard.campoaditivo.modelo.SituacaoProblemaAditiva;
import gerard.campoaditivo.modelo.TipoSituacaoAditiva;
import gerard.campoaditivo.sincronizacao.EstadoSemanticoCompartilhado;
import gerard.campoaditivo.venn.interacao.RepresentacaoVennEditavel;
import gerard.idioma.IdiomaInterface;
import java.awt.Rectangle;
import java.lang.reflect.Method;
import javax.swing.SwingUtilities;

public final class TesteSincronizacaoControlesPorCategoria {
    public static void main(String[] args) throws Exception {
        final Throwable[] erro = new Throwable[1];
        SwingUtilities.invokeAndWait(new Runnable() {
            @Override
            public void run() {
                try {
                    testarComposicao();
                    testarTransformacaoNegativa();
                    testarMagnitudeTransformacaoNegativa();
                    testarComparacao();
                } catch (Throwable t) {
                    erro[0] = t;
                }
            }
        });
        if (erro[0] != null) {
            throw new RuntimeException(erro[0]);
        }
        System.out.println("Teste aprovado: composição, transformação e comparação sincronizam texto, Vergnaud e unidades sem alterar a curadoria.");
        System.exit(0);
    }

    private static void testarComposicao() throws Exception {
        SituacaoProblemaAditiva curada = novaSituacaoComposicao();
        Main.TelaGerard tela = prepararTela(
                TipoSituacaoAditiva.COMPOSICAO_MEDIDAS,
                curada,
                new String[] {"papel.parte1", "papel.parte2", "papel.todo"},
                new int[] {3, 8, 11},
                new TipoFiguraDiagrama[] {
                    TipoFiguraDiagrama.RETANGULO,
                    TipoFiguraDiagrama.RETANGULO,
                    TipoFiguraDiagrama.RETANGULO
                });

        removerUmaUnidade(tela, 0);
        exigirValores(tela, new int[] {2, 8, 10}, "composição");
        exigirContagensVisuais(tela, new int[] {2, 8, 10}, "composição");
        exigirTextos(tela, new String[] {"2", "8", "10"}, "composição");
        exigir("3".equals(curada.getQuantidade1())
                        && "8".equals(curada.getQuantidade2())
                        && "11".equals(curada.getResultado()),
                "A curadoria da composição foi modificada.");
    }

    private static void testarTransformacaoNegativa() throws Exception {
        SituacaoProblemaAditiva curada = novaSituacaoTransformacao();
        Main.TelaGerard tela = prepararTela(
                TipoSituacaoAditiva.TRANSFORMACAO_MEDIDAS,
                curada,
                new String[] {"papel.estadoInicial", "papel.transformacao", "papel.estadoFinal"},
                new int[] {10, -3, 7},
                new TipoFiguraDiagrama[] {
                    TipoFiguraDiagrama.RETANGULO,
                    TipoFiguraDiagrama.ELIPSE,
                    TipoFiguraDiagrama.RETANGULO
                });

        removerUmaUnidade(tela, 0);
        exigirValores(tela, new int[] {9, -3, 6}, "transformação");
        // O tabuleiro concreto representa também a magnitude da transformação.
        exigirContagensVisuais(tela, new int[] {9, 3, 6}, "transformação");
        exigirTextos(tela, new String[] {"9", "-3", "6"}, "transformação");
        exigir("10".equals(curada.getEstadoInicial())
                        && "3".equals(curada.getTransformacao())
                        && "negativo".equals(curada.getSinalTransformacao())
                        && "7".equals(curada.getEstadoFinal()),
                "A curadoria da transformação foi modificada.");
    }

    private static void testarMagnitudeTransformacaoNegativa() throws Exception {
        SituacaoProblemaAditiva curada = novaSituacaoTransformacao();
        Main.TelaGerard tela = prepararTela(
                TipoSituacaoAditiva.TRANSFORMACAO_MEDIDAS,
                curada,
                new String[] {"papel.estadoInicial", "papel.transformacao", "papel.estadoFinal"},
                new int[] {10, -2, 8},
                new TipoFiguraDiagrama[] {
                    TipoFiguraDiagrama.RETANGULO,
                    TipoFiguraDiagrama.ELIPSE,
                    TipoFiguraDiagrama.RETANGULO
                });

        adicionarUmaUnidade(tela, 1);
        exigirValores(tela, new int[] {10, -3, 7},
                "magnitude da transformação negativa");
        exigirContagensVisuais(tela, new int[] {10, 3, 7},
                "magnitude da transformação negativa");
        exigirTextos(tela, new String[] {"10", "-3", "7"},
                "magnitude da transformação negativa");
        exigir("3".equals(curada.getTransformacao())
                        && "negativo".equals(curada.getSinalTransformacao()),
                "A manipulação concreta modificou a curadoria da transformação.");
    }

    private static void testarComparacao() throws Exception {
        SituacaoProblemaAditiva curada = novaSituacaoComparacao();
        Main.TelaGerard tela = prepararTela(
                TipoSituacaoAditiva.COMPARACAO_MEDIDAS,
                curada,
                new String[] {"papel.referido", "papel.diferenca", "papel.referendo"},
                new int[] {3, 6, 9},
                new TipoFiguraDiagrama[] {
                    TipoFiguraDiagrama.RETANGULO,
                    TipoFiguraDiagrama.ELIPSE,
                    TipoFiguraDiagrama.RETANGULO
                });

        removerUmaUnidade(tela, 0);
        exigirValores(tela, new int[] {2, 6, 8}, "comparação");
        // Ordem visual das barras: referido e referendo; o valor relativo é cartão.
        exigirContagensVisuais(tela, new int[] {2, 8}, "comparação");
        exigirTextos(tela, new String[] {"2", "6", "8"}, "comparação");
        exigir("3".equals(curada.getReferido())
                        && "9".equals(curada.getReferendo())
                        && "6".equals(curada.getValorRelativo()),
                "A curadoria da comparação foi modificada.");
    }

    private static Main.TelaGerard prepararTela(
            TipoSituacaoAditiva tipo,
            SituacaoProblemaAditiva curada,
            String[] chaves,
            int[] valores,
            TipoFiguraDiagrama[] figuras) throws Exception {
        Main.TelaGerard tela = new Main.TelaGerard();
        tela.setSize(1240, 760);
        tela.tipoSituacaoSelecionada = tipo;
        tela.categoriaSelecionadaParaAtividade = true;
        tela.textoProblemaEhMensagemSistema = false;
        tela.situacaoProblemaAtual = curada;
        tela.definicaoDiagramaAtual = tela.catalogoDefinicoesAditivas.obter(tipo);
        tela.indicesElementosEstadoCompartilhado = new int[] {0, 1, 2};
        tela.elementosVergnaud.clear();
        tela.elementosTexto.clear();
        tela.itensArrastaveis.clear();
        tela.circulosVenn.clear();
        tela.quadradinhosVenn.clear();
        tela.estadoSemanticoCompartilhado.limpar(tipo);

        for (int i = 0; i < 3; i++) {
            ElementoVergnaud elemento = new ElementoVergnaud(
                    100 + i * 120, 300, 80, 50,
                    figuras[i], chaves[i],
                    new Rectangle(20, 220, 620, 500), false);
            elemento.textoEditavel = Integer.toString(valores[i]);
            tela.elementosVergnaud.add(elemento);

            ElementoTextoMovel texto = new ElementoTextoMovel(
                    Integer.toString(valores[i]), i * 2);
            String valorOriginal = Integer.toString(valores[i]);
            texto.vincularSemantica(
                    chaves[i], 0, valorOriginal.length(), valorOriginal);
            tela.elementosTexto.add(texto);
        }

        Method sincronizar = Main.TelaGerard.class.getDeclaredMethod(
                "sincronizarTodasAsRepresentacoesAPartirDoVergnaud",
                ElementoVergnaud.class,
                EstadoSemanticoCompartilhado.Origem.class);
        sincronizar.setAccessible(true);
        sincronizar.invoke(
                tela,
                tela.elementosVergnaud.get(0),
                EstadoSemanticoCompartilhado.Origem.EDICAO_TEXTO);
        return tela;
    }

    private static void adicionarUmaUnidade(Main.TelaGerard tela, int indiceVisual) {
        CirculoVenn agrupamento = tela.circulosVenn.get(indiceVisual);
        RepresentacaoVennEditavel representacao = new RepresentacaoVennEditavel(
                agrupamento, "", tela.operacoesUnidadesVenn);
        exigir(representacao.podeAdicionarUnidade(),
                "A adição deveria estar habilitada antes da operação.");
        exigir(representacao.adicionarUnidade().foiRealizada(),
                "A adição não foi realizada.");
    }

    private static void removerUmaUnidade(Main.TelaGerard tela, int indiceVisual) {
        CirculoVenn agrupamento = tela.circulosVenn.get(indiceVisual);
        RepresentacaoVennEditavel representacao = new RepresentacaoVennEditavel(
                agrupamento, "", tela.operacoesUnidadesVenn);
        exigir(representacao.podeRemoverUnidade(),
                "A remoção deveria estar habilitada antes da operação.");
        exigir(representacao.removerUnidade().foiRealizada(),
                "A remoção não foi realizada.");
    }

    private static void exigirValores(
            Main.TelaGerard tela, int[] esperados, String categoria) throws Exception {
        Method obter = Main.TelaGerard.class.getDeclaredMethod(
                "obterValorNumericoDoElemento", ElementoVergnaud.class);
        obter.setAccessible(true);
        for (int i = 0; i < esperados.length; i++) {
            Integer atual = (Integer) obter.invoke(tela, tela.elementosVergnaud.get(i));
            exigir(atual != null && atual.intValue() == esperados[i],
                    "Valor de Vergnaud inconsistente na " + categoria
                            + ": índice=" + i + "; esperado=" + esperados[i]
                            + "; atual=" + atual);
        }
    }

    private static void exigirContagensVisuais(
            Main.TelaGerard tela, int[] esperados, String categoria) {
        int indiceEsperado = 0;
        for (CirculoVenn circulo : tela.circulosVenn) {
            if (!circulo.exibirQuadradinhos) {
                continue;
            }
            int quantidade = 0;
            for (QuadradinhoVenn q : tela.quadradinhosVenn) {
                if (circulo.contem(q.centroX(), q.centroY())) {
                    quantidade++;
                }
            }
            exigir(indiceEsperado < esperados.length
                            && quantidade == esperados[indiceEsperado],
                    "Quantidade visual inconsistente na " + categoria
                            + ": posição=" + indiceEsperado
                            + "; esperado=" + (indiceEsperado < esperados.length
                                    ? esperados[indiceEsperado] : -1)
                            + "; atual=" + quantidade);
            indiceEsperado++;
        }
        exigir(indiceEsperado == esperados.length,
                "Quantidade de agrupamentos editáveis inesperada na " + categoria + ".");
    }

    private static void exigirTextos(
            Main.TelaGerard tela, String[] esperados, String categoria) {
        for (int i = 0; i < esperados.length; i++) {
            String atual = tela.elementosTexto.get(i).valor;
            exigir(esperados[i].equals(atual),
                    "Texto inconsistente na " + categoria
                            + ": índice=" + i + "; esperado=" + esperados[i]
                            + "; atual=" + atual);
        }
    }

    private static SituacaoProblemaAditiva novaSituacaoComposicao() {
        return new SituacaoProblemaAditiva(
                "comp", true, TipoSituacaoAditiva.COMPOSICAO_MEDIDAS,
                IdiomaInterface.PORTUGUES, "Composição", "", "", "",
                "", "", "", "", "3", "8", "11",
                "", "", "", "", "todo", "", "");
    }

    private static SituacaoProblemaAditiva novaSituacaoTransformacao() {
        return new SituacaoProblemaAditiva(
                "trans", true, TipoSituacaoAditiva.TRANSFORMACAO_MEDIDAS,
                IdiomaInterface.PORTUGUES, "Transformação", "", "", "",
                "10", "3", "negativo", "7", "", "", "",
                "", "", "", "", "estado_final", "", "");
    }

    private static SituacaoProblemaAditiva novaSituacaoComparacao() {
        return new SituacaoProblemaAditiva(
                "compa", true, TipoSituacaoAditiva.COMPARACAO_MEDIDAS,
                IdiomaInterface.PORTUGUES, "Comparação", "", "", "",
                "", "", "", "", "", "", "",
                "3", "9", "6", "positivo", "referendo", "", "");
    }

    private static void exigir(boolean condicao, String mensagem) {
        if (!condicao) {
            throw new AssertionError(mensagem);
        }
    }
}
