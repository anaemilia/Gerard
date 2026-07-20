import gerard.Scaffolding.reacao.ScaffoldingReacaoRepresentacoes;
import gerard.campoaditivo.diagrama.elementos.ElementoVergnaud;
import gerard.campoaditivo.diagrama.modelo.TipoFiguraDiagrama;
import gerard.campoaditivo.modelo.SituacaoProblemaAditiva;
import gerard.campoaditivo.modelo.TipoSituacaoAditiva;
import gerard.campoaditivo.sincronizacao.EstadoSemanticoCompartilhado;
import gerard.idioma.IdiomaInterface;
import java.awt.Rectangle;
import java.lang.reflect.Method;
import javax.swing.SwingUtilities;

public final class TesteSinalRelativoSemQuantidadeNegativa {
    public static void main(String[] args) throws Exception {
        testarRegraPura();
        testarEstadoCompartilhado();

        final Throwable[] erro = new Throwable[1];
        SwingUtilities.invokeAndWait(new Runnable() {
            @Override
            public void run() {
                try {
                    testarTelaComparacao();
                    testarTelaTransformacaoNegativaValida();
                } catch (Throwable t) {
                    erro[0] = t;
                }
            }
        });
        if (erro[0] != null) {
            throw new RuntimeException(erro[0]);
        }

        System.out.println("Teste aprovado: somente o valor relativo pode ser negativo; quantidades permanecem não negativas e sincronizadas.");
        System.exit(0);
    }

    private static void testarRegraPura() {
        ScaffoldingReacaoRepresentacoes regra = new ScaffoldingReacaoRepresentacoes();
        exigir(!regra.relacaoPreservaQuantidadeNaoNegativa(6, -8),
                "6 + (-8) deveria ser rejeitado por produzir quantidade -2.");
        exigir(regra.relacaoPreservaQuantidadeNaoNegativa(9, -6),
                "9 + (-6) deveria ser aceito por produzir quantidade 3.");
        exigir(regra.quantidadeEhNaoNegativa(0),
                "Zero deve ser quantidade válida.");
        exigir(!regra.quantidadeEhNaoNegativa(-1),
                "Quantidade negativa não deve ser válida.");
    }

    private static void testarEstadoCompartilhado() {
        EstadoSemanticoCompartilhado estado = new EstadoSemanticoCompartilhado();
        EstadoSemanticoCompartilhado.Snapshot invalido = estado.atualizar(
                TipoSituacaoAditiva.COMPARACAO_MEDIDAS,
                new Integer[] {6, -8, -2},
                new boolean[] {true, true, true},
                1,
                EstadoSemanticoCompartilhado.Origem.PROTOCOLO);
        exigir(invalido.isConhecido(0) && invalido.valorOuZero(0) == 6,
                "O referido válido deveria permanecer conhecido.");
        exigir(invalido.isConhecido(1) && invalido.valorOuZero(1) == -8,
                "O valor relativo negativo deveria permanecer permitido.");
        exigir(!invalido.isConhecido(2),
                "O referendo negativo deveria ser removido do estado semântico.");

        EstadoSemanticoCompartilhado.Snapshot valido = estado.atualizar(
                TipoSituacaoAditiva.COMPARACAO_MEDIDAS,
                new Integer[] {9, -6, 3},
                new boolean[] {true, true, true},
                1,
                EstadoSemanticoCompartilhado.Origem.PROTOCOLO);
        exigir(valido.isConhecido(2) && valido.valorOuZero(2) == 3,
                "Uma comparação negativa válida deveria manter a quantidade 3.");

        EstadoSemanticoCompartilhado.Snapshot relacoes = estado.atualizar(
                TipoSituacaoAditiva.COMPOSICAO_RELACOES,
                new Integer[] {-2, -3, -5},
                new boolean[] {true, true, true},
                -1,
                EstadoSemanticoCompartilhado.Origem.PROTOCOLO);
        exigir(relacoes.isConhecido(0) && relacoes.valorOuZero(0) == -2
                        && relacoes.isConhecido(2) && relacoes.valorOuZero(2) == -5,
                "Relações assinadas não devem ser confundidas com quantidades de medidas.");
    }

    private static void testarTelaComparacao() throws Exception {
        Main.TelaGerard tela = prepararTela(
                TipoSituacaoAditiva.COMPARACAO_MEDIDAS,
                novaSituacaoComparacao(),
                new int[] {6, 8, 14});
        ElementoVergnaud relacao = tela.elementosVergnaud.get(1);

        Method validar = Main.TelaGerard.class.getDeclaredMethod(
                "valorRelativoPreservaQuantidadesNaoNegativas",
                ElementoVergnaud.class, int.class);
        validar.setAccessible(true);
        boolean negativoInvalido = ((Boolean) validar.invoke(tela, relacao, -8)).booleanValue();
        boolean positivoValido = ((Boolean) validar.invoke(tela, relacao, 8)).booleanValue();
        exigir(!negativoInvalido,
                "A tela deveria bloquear -8 quando o referido é 6.");
        exigir(positivoValido,
                "A tela deveria aceitar +8 quando o referido é 6.");

        Method aplicar = Main.TelaGerard.class.getDeclaredMethod(
                "aplicarEdicaoValorRelativoComparacao", int.class);
        aplicar.setAccessible(true);
        aplicar.invoke(tela, -8);

        exigir(valor(tela, 0) == 6,
                "O referido foi alterado após uma escolha inválida.");
        exigir(valor(tela, 1) == 8,
                "O valor relativo válido anterior deveria ser preservado.");
        exigir(valor(tela, 2) == 14,
                "O referendo não poderia virar -2 nem ser modificado pela escolha inválida.");
    }

    private static void testarTelaTransformacaoNegativaValida() throws Exception {
        Main.TelaGerard tela = prepararTela(
                TipoSituacaoAditiva.TRANSFORMACAO_MEDIDAS,
                novaSituacaoTransformacao(),
                new int[] {9, -6, 3});
        ElementoVergnaud relacao = tela.elementosVergnaud.get(1);
        Method validar = Main.TelaGerard.class.getDeclaredMethod(
                "valorRelativoPreservaQuantidadesNaoNegativas",
                ElementoVergnaud.class, int.class);
        validar.setAccessible(true);
        exigir(((Boolean) validar.invoke(tela, relacao, -6)).booleanValue(),
                "Uma transformação negativa que termina em 3 deveria ser aceita.");
    }

    private static Main.TelaGerard prepararTela(
            TipoSituacaoAditiva tipo,
            SituacaoProblemaAditiva situacao,
            int[] valores) throws Exception {
        Main.TelaGerard tela = new Main.TelaGerard();
        tela.setSize(1200, 760);
        tela.tipoSituacaoSelecionada = tipo;
        tela.categoriaSelecionadaParaAtividade = true;
        tela.textoProblemaEhMensagemSistema = false;
        tela.situacaoProblemaAtual = situacao;
        tela.definicaoDiagramaAtual = tela.catalogoDefinicoesAditivas.obter(tipo);
        tela.indicesElementosEstadoCompartilhado = new int[] {0, 1, 2};
        tela.elementosVergnaud.clear();
        tela.elementosTexto.clear();
        tela.itensArrastaveis.clear();
        tela.circulosVenn.clear();
        tela.quadradinhosVenn.clear();
        tela.estadoSemanticoCompartilhado.limpar(tipo);

        TipoFiguraDiagrama[] figuras = new TipoFiguraDiagrama[] {
            TipoFiguraDiagrama.RETANGULO,
            TipoFiguraDiagrama.ELIPSE,
            TipoFiguraDiagrama.RETANGULO
        };
        String[] papeis = tipo == TipoSituacaoAditiva.COMPARACAO_MEDIDAS
                ? new String[] {"papel.referido", "papel.diferenca", "papel.referendo"}
                : new String[] {"papel.estadoInicial", "papel.transformacao", "papel.estadoFinal"};
        for (int i = 0; i < 3; i++) {
            ElementoVergnaud elemento = new ElementoVergnaud(
                    100 + i * 120, 300, 80, 50,
                    figuras[i], papeis[i],
                    new Rectangle(20, 220, 620, 500), false);
            elemento.textoEditavel = i == 1 && valores[i] >= 0
                    ? "+" + valores[i]
                    : Integer.toString(valores[i]);
            tela.elementosVergnaud.add(elemento);
        }

        Method sincronizar = Main.TelaGerard.class.getDeclaredMethod(
                "sincronizarTodasAsRepresentacoesAPartirDoVergnaud",
                ElementoVergnaud.class,
                EstadoSemanticoCompartilhado.Origem.class);
        sincronizar.setAccessible(true);
        sincronizar.invoke(tela, tela.elementosVergnaud.get(1),
                EstadoSemanticoCompartilhado.Origem.PROTOCOLO);
        return tela;
    }

    private static int valor(Main.TelaGerard tela, int indice) throws Exception {
        Method obter = Main.TelaGerard.class.getDeclaredMethod(
                "obterValorNumericoDoElemento", ElementoVergnaud.class);
        obter.setAccessible(true);
        Integer resultado = (Integer) obter.invoke(tela, tela.elementosVergnaud.get(indice));
        return resultado == null ? Integer.MIN_VALUE : resultado.intValue();
    }

    private static SituacaoProblemaAditiva novaSituacaoComparacao() {
        return new SituacaoProblemaAditiva(
                "cmp-neg", true, TipoSituacaoAditiva.COMPARACAO_MEDIDAS,
                IdiomaInterface.PORTUGUES, "Comparação", "", "", "",
                "", "", "", "", "", "", "",
                "6", "14", "8", "positivo", "referendo", "", "");
    }

    private static SituacaoProblemaAditiva novaSituacaoTransformacao() {
        return new SituacaoProblemaAditiva(
                "tr-neg", true, TipoSituacaoAditiva.TRANSFORMACAO_MEDIDAS,
                IdiomaInterface.PORTUGUES, "Transformação", "", "", "",
                "9", "6", "negativo", "3", "", "", "",
                "", "", "", "", "estado_final", "", "");
    }

    private static void exigir(boolean condicao, String mensagem) {
        if (!condicao) {
            throw new AssertionError(mensagem);
        }
    }
}
