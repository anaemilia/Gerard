import gerard.campoaditivo.montagem.ConjuntoBlocosMontagem;
import gerard.campoaditivo.montagem.TelaMontagemSituacao;
import java.lang.reflect.Field;
import javax.swing.JButton;
import javax.swing.JTabbedPane;

public final class TesteAbaMontagem {
    public static void main(String[] args) throws Exception {
        System.setProperty("user.home", System.getProperty("java.io.tmpdir") + "/gerard_teste_aba_" + System.nanoTime());
        Main janela = new Main();
        try {
            if (!(janela.getContentPane().getComponent(0) instanceof JTabbedPane)) {
                throw new AssertionError("A janela principal não contém o painel de abas esperado.");
            }
            JTabbedPane abas = (JTabbedPane) janela.getContentPane().getComponent(0);
            if (abas.getTabCount() != 3) {
                throw new AssertionError("Esperadas 3 abas, encontradas " + abas.getTabCount());
            }
            if (!(abas.getComponentAt(1) instanceof TelaMontagemSituacao)) {
                throw new AssertionError("A segunda aba não é a construção de situação-problema.");
            }
            if (!"Construir situação-problema".equals(abas.getTitleAt(1))) {
                throw new AssertionError("O nome visível da segunda aba deve ser Construir situação-problema.");
            }
            TelaMontagemSituacao montagem = (TelaMontagemSituacao) abas.getComponentAt(1);
            abas.setSelectedIndex(1);

            Field campoAtividade = TelaMontagemSituacao.class.getDeclaredField("atividadeAtual");
            campoAtividade.setAccessible(true);
            Field campoBotao = TelaMontagemSituacao.class.getDeclaredField("botaoSortear");
            campoBotao.setAccessible(true);
            ConjuntoBlocosMontagem primeira = (ConjuntoBlocosMontagem) campoAtividade.get(montagem);
            JButton novoDiagrama = (JButton) campoBotao.get(montagem);
            novoDiagrama.doClick();
            ConjuntoBlocosMontagem segunda = (ConjuntoBlocosMontagem) campoAtividade.get(montagem);
            if (primeira == null || segunda == null) {
                throw new AssertionError("A aba não carregou atividades de construção.");
            }
            if (primeira.getSituacao().getId().equals(segunda.getSituacao().getId())) {
                throw new AssertionError("Novo diagrama repetiu imediatamente a mesma situação.");
            }
            if (primeira.getSituacao().getTipo() == segunda.getSituacao().getTipo()) {
                throw new AssertionError("Novo diagrama deveria alternar a categoria quando há opções disponíveis.");
            }

            abas.setSelectedIndex(0);
            System.out.println("Teste de interface aprovado: aba Construir integrada, botão Novo diagrama alterna situação e categoria.");
        } finally {
            janela.dispose();
        }
        System.exit(0);
    }
}
