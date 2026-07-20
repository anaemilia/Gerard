import gerard.campoaditivo.diagrama.elementos.ElementoTextoMovel;
import gerard.campoaditivo.diagrama.elementos.ItemTextoArrastavel;
import gerard.campoaditivo.modelo.TipoSituacaoAditiva;
import gerard.campoaditivo.sincronizacao.EstadoSemanticoCompartilhado;
import java.lang.reflect.Method;
import javax.swing.SwingUtilities;

public final class TesteSincronizacaoTextoTelaGerard {
    public static void main(String[] args) throws Exception {
        final Throwable[] erro = new Throwable[1];
        SwingUtilities.invokeAndWait(new Runnable() {
            @Override
            public void run() {
                try {
                    executar();
                } catch (Throwable t) {
                    erro[0] = t;
                }
            }
        });
        if (erro[0] != null) {
            throw new RuntimeException(erro[0]);
        }
        System.out.println("Teste aprovado: TelaGerard sincroniza valores conhecidos e preserva a interrogação.");
        System.exit(0);
    }

    private static void executar() throws Exception {
        Main.TelaGerard tela = new Main.TelaGerard();
        tela.tipoSituacaoSelecionada = TipoSituacaoAditiva.COMPARACAO_MEDIDAS;
        tela.categoriaSelecionadaParaAtividade = true;
        tela.textoProblemaEhMensagemSistema = false;
        tela.indicesElementosEstadoCompartilhado = new int[] {0, 1, 2};
        tela.elementosTexto.clear();
        tela.itensArrastaveis.clear();

        ElementoTextoMovel referido = new ElementoTextoMovel("6", 0);
        referido.vincularSemantica("papel.referido", 0, 1, "6");
        ElementoTextoMovel relativo = new ElementoTextoMovel("8", 2);
        relativo.vincularSemantica("papel.diferenca", 0, 1, "8");
        ElementoTextoMovel referendo = new ElementoTextoMovel("?", 4);
        referendo.vincularSemantica("papel.referendo", 0, 1, "?");
        tela.elementosTexto.add(referido);
        tela.elementosTexto.add(relativo);
        tela.elementosTexto.add(referendo);
        ItemTextoArrastavel movido = new ItemTextoArrastavel(
                120, 120, 16, 20, "6", false, "6", "papel.referido");
        ItemTextoArrastavel incognitaMovidaNoTexto = new ItemTextoArrastavel(
                160, 120, 16, 20, "?", false, "?", "papel.referendo");
        tela.itensArrastaveis.add(movido);
        tela.itensArrastaveis.add(incognitaMovidaNoTexto);

        EstadoSemanticoCompartilhado estado = new EstadoSemanticoCompartilhado();
        estado.limpar(TipoSituacaoAditiva.COMPARACAO_MEDIDAS);
        EstadoSemanticoCompartilhado.Snapshot snapshot = estado.atualizar(
                TipoSituacaoAditiva.COMPARACAO_MEDIDAS,
                new Integer[] {4, 8, 12},
                new boolean[] {true, true, true},
                0,
                EstadoSemanticoCompartilhado.Origem.DIAGRAMA_COMPLEMENTAR);

        Method aplicar = Main.TelaGerard.class.getDeclaredMethod(
                "aplicarEstadoCompartilhadoEmTodasAsRepresentacoes",
                EstadoSemanticoCompartilhado.Snapshot.class,
                boolean.class);
        aplicar.setAccessible(true);
        aplicar.invoke(tela, snapshot, false);

        exigir("4".equals(referido.valor), "Referido textual não atualizado.");
        exigir("8".equals(relativo.valor), "Valor relativo textual não atualizado.");
        exigir("?".equals(referendo.valor),
                "A interrogação textual foi substituída indevidamente.");
        exigir("4".equals(movido.valor), "Marcador deslocado do enunciado não atualizado.");
        exigir("?".equals(incognitaMovidaNoTexto.valor),
                "A interrogação deslocada no enunciado foi substituída indevidamente.");
        exigir(tela.itensArrastaveis.size() == 2,
                "A sincronização não deve criar ou remover itens.");
    }

    private static void exigir(boolean condicao, String mensagem) {
        if (!condicao) {
            throw new AssertionError(mensagem);
        }
    }
}
