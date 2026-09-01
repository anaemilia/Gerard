import gerard.campoaditivo.curadoria.DialogoCuradoriaNarrativaRica;
import gerard.campoaditivo.curadoria.RepositorioCuradoriaNarrativaRica;
import gerard.campoaditivo.modelo.SituacaoProblemaAditiva;
import gerard.campoaditivo.modelo.TipoSituacaoAditiva;
import gerard.ui.UITemaGerard;
import java.awt.Component;
import java.awt.Container;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;

/**
 * Contrato gráfico e acessível do editor da narrativa rica.
 *
 * Este teste exige um ambiente com display. Ele não salva nem promove a
 * narrativa: apenas confirma que a interface materializa a decisão humana
 * sem herdar a marca de validação do registro tabular histórico.
 */
public final class TesteDialogoCuradoriaNarrativaRica {
    private static final String[] ABAS = {
        "Participantes", "Famílias", "Objetos", "Estado inicial",
        "Eventos", "Estado final", "Correspondências"
    };

    private TesteDialogoCuradoriaNarrativaRica() {
    }

    public static void main(String[] args) throws Exception {
        Path diretorio = Files.createTempDirectory(
                "gerard-curadoria-narrativa-rica-ui-");
        AtomicReference<Throwable> falha = new AtomicReference<Throwable>();
        try {
            SwingUtilities.invokeAndWait(() -> {
                JDialog dialogo = null;
                try {
                    SituacaoProblemaAditiva tabularValidada = registroTabular();
                    dialogo = new DialogoCuradoriaNarrativaRica(
                            null,
                            tabularValidada,
                            new RepositorioCuradoriaNarrativaRica(
                                    diretorio.toFile()));
                    dialogo.pack();
                    dialogo.setSize(980, 660);
                    dialogo.validate();

                    verificarContrato(dialogo);
                    if (args.length > 0 && !args[0].trim().isEmpty()) {
                        renderizar(dialogo, Paths.get(args[0]));
                    }
                } catch (Throwable ex) {
                    falha.set(ex);
                } finally {
                    if (dialogo != null) {
                        dialogo.dispose();
                    }
                }
            });
        } finally {
            Files.deleteIfExists(diretorio);
        }
        if (falha.get() != null) {
            throw new RuntimeException(falha.get());
        }
        System.out.println("TESTE_DIALOGO_CURADORIA_NARRATIVA_RICA_UI_OK");
    }

    private static void verificarContrato(JDialog dialogo) {
        checar("título da janela",
                "Narrativa rica da situação-problema".equals(
                        dialogo.getTitle()));

        JTabbedPane abas = unico(dialogo, JTabbedPane.class,
                "conjunto de abas");
        checar("quantidade de abas", abas.getTabCount() == ABAS.length);
        for (int i = 0; i < ABAS.length; i++) {
            checar("aba " + i, ABAS[i].equals(abas.getTitleAt(i)));
        }

        JCheckBox promocao = null;
        for (JCheckBox caixa : componentes(dialogo, JCheckBox.class)) {
            if ("Validada pelo pesquisador".equals(caixa.getText())) {
                promocao = caixa;
                break;
            }
        }
        checar("controle de promoção localizado", promocao != null);
        checar("validação tabular não promove narrativa rica",
                !promocao.isSelected());
        checar("controle de promoção disponível", promocao.isEnabled());
        checar("controle usa texto neutro",
                UITemaGerard.COR_TEXTO.equals(promocao.getForeground()));
        checar("tooltip explica a autoridade humana",
                promocao.getToolTipText() != null
                && promocao.getToolTipText().contains("revisão humana"));
        checar("nome acessível presente",
                "Validar narrativa rica pelo pesquisador".equals(
                        promocao.getAccessibleContext().getAccessibleName()));
        checar("descrição acessível presente",
                promocao.getAccessibleContext().getAccessibleDescription()
                        != null
                && !promocao.getAccessibleContext()
                        .getAccessibleDescription().trim().isEmpty());

        Container conteudo = dialogo.getContentPane();
        checar("fundo segue a paleta neutra quente",
                conteudo instanceof JPanel
                && UITemaGerard.COR_FUNDO_CONTEUDO.equals(
                        conteudo.getBackground()));
        checar("ação de salvar identificável",
                botao(dialogo, "Salvar narrativa rica") != null);
        checar("ação de cancelar identificável",
                botao(dialogo, "Cancelar") != null);
    }

    private static void renderizar(JDialog dialogo, Path destino)
            throws Exception {
        Path pai = destino.toAbsolutePath().getParent();
        if (pai != null) {
            Files.createDirectories(pai);
        }
        Container conteudo = dialogo.getContentPane();
        int largura = Math.max(1, conteudo.getWidth());
        int altura = Math.max(1, conteudo.getHeight());
        BufferedImage imagem = new BufferedImage(
                largura, altura, BufferedImage.TYPE_INT_ARGB);
        Graphics2D grafico = imagem.createGraphics();
        try {
            conteudo.printAll(grafico);
        } finally {
            grafico.dispose();
        }
        ImageIO.write(imagem, "png", destino.toFile());
    }

    private static JButton botao(Container raiz, String texto) {
        for (JButton botao : componentes(raiz, JButton.class)) {
            if (texto.equals(botao.getText())) {
                return botao;
            }
        }
        return null;
    }

    private static <T extends Component> T unico(
            Container raiz, Class<T> tipo, String descricao) {
        List<T> encontrados = componentes(raiz, tipo);
        checar(descricao + " único", encontrados.size() == 1);
        return encontrados.get(0);
    }

    private static <T extends Component> List<T> componentes(
            Container raiz, Class<T> tipo) {
        List<T> encontrados = new ArrayList<T>();
        for (Component componente : raiz.getComponents()) {
            if (tipo.isInstance(componente)) {
                encontrados.add(tipo.cast(componente));
            }
            if (componente instanceof Container) {
                encontrados.addAll(componentes(
                        (Container) componente, tipo));
            }
        }
        return encontrados;
    }

    private static SituacaoProblemaAditiva registroTabular() {
        return new SituacaoProblemaAditiva(
                "situacao.ui.narrativa", "grupo.ui", "original", "", true,
                TipoSituacaoAditiva.TRANSFORMACAO_MEDIDAS, "pt-BR",
                "Nadia tinha dez morangos e consumiu três.",
                "Morangos", "curadoria", "",
                "10", "3", "negativo", "7",
                "", "", "",
                "", "", "", "",
                "estado_final", "TRANSFORMACAO_MEDIDAS", "",
                "Nadia", "", "",
                "", "", "", "", "", "",
                "", "", "");
    }

    private static void checar(String rotulo, boolean condicao) {
        if (!condicao) {
            throw new AssertionError(rotulo);
        }
        System.out.println("OK - " + rotulo);
    }
}
