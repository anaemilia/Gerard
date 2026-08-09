import gerard.Scaffolding.feedbackerro.ScaffoldingFeedbackMultissensorialErro;
import gerard.campoaditivo.diagrama.elementos.ItemTextoArrastavel;
import gerard.campoaditivo.diagrama.elementos.MarcadorTexto;
import gerard.interacao.arraste.SessaoArrasteTextoParaDiagrama;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/** Contrato de regressão: erro semântico não remove o número do diagrama. */
public final class TesteFeedbackProxyPosicionamento {

    public static void main(String[] args) throws Exception {
        SessaoArrasteTextoParaDiagrama sessao = new SessaoArrasteTextoParaDiagrama();
        MarcadorTexto marcador = new MarcadorTexto(100, 80, 28, 24, "6", false, "papel.estadoFinal");
        ItemTextoArrastavel proxy = sessao.iniciarPorMarcador(marcador);
        exigir(proxy != null, "o proxy deve ser criado");
        exigir(sessao.deveManterNoDiagramaAposErro(proxy, true, false),
                "erro semântico sobre o diagrama deve manter o item manipulável");
        exigir(!sessao.deveDescartarAoSoltar(proxy, true, false),
                "erro semântico sobre o diagrama não pode descartar o item");
        exigir(sessao.deveDescartarAoSoltar(proxy, false, false),
                "somente a soltura fora do diagrama pode descartar o proxy");

        List<ItemTextoArrastavel> itensDoDiagrama = new ArrayList<ItemTextoArrastavel>();
        itensDoDiagrama.add(proxy);
        sessao.confirmarPersistencia(proxy);
        exigir(!sessao.ehProxyAtivo(proxy),
                "após a promoção, o item deixa de ser proxy transitório");
        exigir(itensDoDiagrama.contains(proxy),
                "a promoção deve preservar o item no diagrama");

        final int xOriginal = proxy.x;
        final AtomicInteger repaints = new AtomicInteger();
        final AtomicInteger conclusoes = new AtomicInteger();
        ScaffoldingFeedbackMultissensorialErro feedback =
                new ScaffoldingFeedbackMultissensorialErro();
        feedback.sinalizarErro(proxy, new Runnable() {
            public void run() {
                repaints.incrementAndGet();
            }
        }, new Runnable() {
            public void run() {
                conclusoes.incrementAndGet();
            }
        });

        long limite = System.currentTimeMillis() + 2500L;
        while (conclusoes.get() == 0 && System.currentTimeMillis() < limite) {
            Thread.sleep(25L);
        }

        exigir(conclusoes.get() == 1,
                "o feedback deve concluir após o tremor");
        exigir(proxy.x == xOriginal,
                "a posição horizontal deve ser restaurada após o tremor");
        exigir(repaints.get() >= 3,
                "o tremor deve solicitar múltiplos repaints");
        exigir(itensDoDiagrama.contains(proxy),
                "som, tremor e tip não podem remover o item do diagrama");

        int xReposicionado = proxy.x + 40;
        proxy.x = xReposicionado;
        exigir(proxy.x == xReposicionado,
                "o item incorreto deve continuar manipulável após o feedback");

        System.out.println("TesteFeedbackProxyPosicionamento: OK");
    }

    private static void exigir(boolean condicao, String mensagem) {
        if (!condicao) {
            throw new AssertionError(mensagem);
        }
    }
}
