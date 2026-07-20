package gerard.pesquisador.visualizacao;

import gerard.i18n.ServicoLocalizacao;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Desktop;
import java.awt.Font;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

/**
 * Painel Swing que embute visualizações D3.js por meio de JavaFX WebView.
 *
 * A classe usa reflexão para manter o projeto compilável mesmo em ambientes sem
 * JavaFX no classpath. Quando JavaFX está disponível, a visualização é exibida
 * dentro do Gerard. Se JavaFX não estiver disponível, a mesma visualização D3 é
 * oferecida como HTML externo, preservando os dados e o formato gerado.
 */
public class PainelD3WebView extends JPanel {

    private static final Color COR_FUNDO = new Color(246, 247, 248);
    private static final Color COR_SUPERFICIE = Color.WHITE;
    private static final Color COR_TEXTO = new Color(31, 41, 51);
    private static final Color COR_TEXTO_SECUNDARIO = new Color(82, 97, 107);
    private static final Color COR_BORDA = new Color(213, 218, 224);

    private final String html;
    private final ServicoLocalizacao i18n;

    public PainelD3WebView(String html, ServicoLocalizacao i18n) {
        super(new BorderLayout());
        this.html = html == null ? "" : html;
        this.i18n = i18n == null ? ServicoLocalizacao.getInstancia() : i18n;
        setBackground(COR_FUNDO);
        setBorder(BorderFactory.createEmptyBorder());
        if (!inicializarWebView()) {
            mostrarFallback();
        }
    }

    private boolean inicializarWebView() {
        try {
            final Class<?> jfxPanelClass = Class.forName("javafx.embed.swing.JFXPanel");
            final Object jfxPanel = jfxPanelClass.getDeclaredConstructor().newInstance();
            add((Component) jfxPanel, BorderLayout.CENTER);

            final Class<?> platformClass = Class.forName("javafx.application.Platform");
            try {
                platformClass.getMethod("setImplicitExit", boolean.class).invoke(null, Boolean.FALSE);
            } catch (Throwable ignorado) {
                // JavaFX 2+ oferece este método. Ambientes antigos podem ignorá-lo.
            }

            Runnable carregar = new Runnable() {
                public void run() {
                    try {
                        Class<?> webViewClass = Class.forName("javafx.scene.web.WebView");
                        Object webView = webViewClass.getDeclaredConstructor().newInstance();
                        Object engine = webViewClass.getMethod("getEngine").invoke(webView);
                        Method loadContent = engine.getClass().getMethod("loadContent", String.class);
                        loadContent.invoke(engine, html);

                        Class<?> parentClass = Class.forName("javafx.scene.Parent");
                        Class<?> sceneClass = Class.forName("javafx.scene.Scene");
                        Constructor<?> sceneConstructor = sceneClass.getConstructor(parentClass);
                        Object scene = sceneConstructor.newInstance(webView);
                        jfxPanelClass.getMethod("setScene", sceneClass).invoke(jfxPanel, scene);
                    } catch (final Throwable erro) {
                        SwingUtilities.invokeLater(new Runnable() {
                            public void run() {
                                removeAll();
                                mostrarFallback(erro);
                                revalidate();
                                repaint();
                            }
                        });
                    }
                }
            };
            platformClass.getMethod("runLater", Runnable.class).invoke(null, carregar);
            return true;
        } catch (Throwable erro) {
            return false;
        }
    }

    private void mostrarFallback() {
        mostrarFallback(null);
    }

    private void mostrarFallback(Throwable erro) {
        JPanel painel = new JPanel(new BorderLayout(10, 10));
        painel.setBackground(COR_SUPERFICIE);
        painel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(COR_BORDA),
                BorderFactory.createEmptyBorder(16, 16, 16, 16)
        ));

        JLabel titulo = new JLabel(texto("pesq.d3.fallback.title"));
        titulo.setFont(new Font("Arial", Font.BOLD, 15));
        titulo.setForeground(COR_TEXTO);
        painel.add(titulo, BorderLayout.NORTH);

        JTextArea msg = new JTextArea(texto("pesq.d3.fallback.message"));
        if (erro != null && erro.getMessage() != null) {
            msg.append("\n\n" + erro.getMessage());
        }
        msg.setEditable(false);
        msg.setLineWrap(true);
        msg.setWrapStyleWord(true);
        msg.setFont(new Font("Arial", Font.PLAIN, 13));
        msg.setForeground(COR_TEXTO_SECUNDARIO);
        msg.setBackground(COR_SUPERFICIE);
        msg.setBorder(BorderFactory.createEmptyBorder());
        painel.add(new JScrollPane(msg), BorderLayout.CENTER);

        JButton abrir = new JButton(texto("pesq.d3.fallback.openBrowser"));
        abrir.addActionListener(e -> abrirNoNavegador());
        painel.add(abrir, BorderLayout.SOUTH);
        add(painel, BorderLayout.CENTER);
    }

    private String texto(String chave) {
        return i18n.texto(chave);
    }

    private String formatar(String chave, Object... argumentos) {
        return i18n.formatar(chave, argumentos);
    }

    private void abrirNoNavegador() {
        File arquivo = null;
        try {
            arquivo = salvarHtmlTemporario();
            if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
                Desktop.getDesktop().browse(arquivo.toURI());
            } else {
                JOptionPane.showMessageDialog(this,
                        formatar("pesq.d3.fallback.browserUnsupported", arquivo.getAbsolutePath()),
                        texto("pesq.d3.fallback.title"),
                        JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (Exception ex) {
            String detalhe = arquivo == null ? ex.getMessage() : arquivo.getAbsolutePath() + "\n" + ex.getMessage();
            JOptionPane.showMessageDialog(this,
                    formatar("pesq.d3.fallback.openError", detalhe),
                    texto("pesq.d3.fallback.title"),
                    JOptionPane.WARNING_MESSAGE);
        }
    }

    private File salvarHtmlTemporario() throws Exception {
        File pasta = new File(System.getProperty("user.home"), "Gerard/logs");
        if (!pasta.exists()) {
            pasta.mkdirs();
        }
        File arquivo = new File(pasta, "gerard_visualizacoes_d3_atual.html");
        escreverHtml(arquivo);

        // Mantém também o nome antigo, sobrescrevendo-o, para evitar que uma versão
        // anterior do HTML continue sendo aberta manualmente pelo usuário.
        escreverHtml(new File(pasta, "gerard_visualizacoes_d3.html"));
        return arquivo;
    }
    private void escreverHtml(File arquivo) throws Exception {
        OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(arquivo), "UTF-8");
        try {
            writer.write(html);
        } finally {
            writer.close();
        }
    }

}
