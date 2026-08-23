import gerard.campoaditivo.curadoria.TelaCuradoriaSituacoes;
import gerard.campoaditivo.servico.RepositorioSituacoesAditivas;
import gerard.idioma.IdiomaInterface;
import gerard.idioma.IdiomaSituacao;
import java.awt.Component;
import java.awt.Container;
import java.awt.Window;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

public class TesteBloqueioDinamicoIdiomaCuradoria {
    private static <T extends Component> List<T> componentes(Container raiz, Class<T> tipo) {
        List<T> encontrados = new ArrayList<T>();
        for (Component c : raiz.getComponents()) {
            if (tipo.isInstance(c)) encontrados.add(tipo.cast(c));
            if (c instanceof Container) encontrados.addAll(componentes((Container) c, tipo));
        }
        return encontrados;
    }

    private static JDialog dialogoCuradoria() {
        for (Window w : Window.getWindows()) {
            if (w instanceof JDialog && w.isShowing()
                    && "Curadoria da situação-problema".equals(((JDialog) w).getTitle())) {
                return (JDialog) w;
            }
        }
        return null;
    }

    public static void main(String[] args) throws Exception {
        final AtomicReference<Throwable> falha = new AtomicReference<Throwable>();
        SwingUtilities.invokeAndWait(() -> {
            JFrame frame = new JFrame("teste");
            TelaCuradoriaSituacoes tela = new TelaCuradoriaSituacoes(new RepositorioSituacoesAditivas(), null);
            frame.setContentPane(tela);
            frame.pack();
            frame.setVisible(true);

            Timer timer = new Timer(900, e -> {
                JDialog dialogo = dialogoCuradoria();
                try {
                    if (dialogo == null) throw new AssertionError("Diálogo de curadoria não localizado");
                    JComboBox<?> comboTraducao = null;
                    JComboBox<?> comboIdiomaVersao = null;
                    JComboBox<?> comboTipoVersao = null;
                    for (JComboBox<?> combo : componentes(dialogo, JComboBox.class)) {
                        if ("Escolha o idioma da nova tradução".equals(combo.getToolTipText())) {
                            comboTraducao = combo;
                        } else if ("Idioma desta versão linguística".equals(combo.getToolTipText())) {
                            comboIdiomaVersao = combo;
                        } else if (combo.getSelectedItem() instanceof String) {
                            String valor = String.valueOf(combo.getSelectedItem());
                            if ("original".equalsIgnoreCase(valor) || "traducao".equalsIgnoreCase(valor)) {
                                comboTipoVersao = combo;
                            }
                        }
                    }
                    if (comboTraducao == null) throw new AssertionError("Seletor de idioma da tradução não localizado");
                    if (comboIdiomaVersao == null) throw new AssertionError("Idioma da versão aberta não localizado");
                    if (comboTipoVersao == null) throw new AssertionError("Tipo da versão aberta não localizado");
                    boolean versaoOriginal = "original".equalsIgnoreCase(
                            String.valueOf(comboTipoVersao.getSelectedItem()));
                    IdiomaSituacao idiomaDaVersao = (IdiomaSituacao) comboIdiomaVersao.getSelectedItem();
                    if (idiomaDaVersao == null) throw new AssertionError("Idioma da versão aberta não selecionado");
                    boolean semanticaEditavelNaVersao = versaoOriginal
                            && IdiomaSituacao.paraIdiomaInterface(idiomaDaVersao.getCodigo())
                                    == IdiomaInterface.PORTUGUES;
                    String codigoOriginal = idiomaDaVersao.getCodigo();
                    int original = -1;
                    int outro = -1;
                    for (int i = 0; i < comboTraducao.getItemCount(); i++) {
                        String codigo = ((IdiomaSituacao) comboTraducao.getItemAt(i)).getCodigo();
                        if (codigo.equalsIgnoreCase(codigoOriginal)) original = i;
                        else if (outro < 0) outro = i;
                    }
                    if (original < 0) throw new AssertionError("Idioma da versão aberta não disponível no editor de tradução");
                    if (outro < 0) throw new AssertionError("Outro idioma não disponível");

                    comboTraducao.setSelectedIndex(outro);

                    List<JTextField> camposVisiveis = new ArrayList<JTextField>();
                    for (JTextField campo : componentes(dialogo, JTextField.class)) {
                        if (campo.isShowing()) camposVisiveis.add(campo);
                    }
                    if (camposVisiveis.isEmpty()) throw new AssertionError("Campos semânticos não localizados");
                    for (JTextField campo : camposVisiveis) {
                        if (campo.isEditable()) throw new AssertionError("Campo semântico permaneceu editável: " + campo.getText());
                    }

                    JTextArea traducao = null;
                    JTextArea originalArea = null;
                    for (JTextArea area : componentes(dialogo, JTextArea.class)) {
                        if (!area.isShowing()) continue;
                        if ("Digite ou revise o enunciado traduzido".equals(area.getToolTipText())) traducao = area;
                        else originalArea = area;
                    }
                    if (traducao == null || !traducao.isEditable()) throw new AssertionError("Área da tradução não permaneceu editável");
                    if (originalArea == null || originalArea.isEditable()) throw new AssertionError("Enunciado original não foi bloqueado");

                    JCheckBox validacaoTraducao = null;
                    JCheckBox validacaoOriginal = null;
                    for (JCheckBox caixa : componentes(dialogo, JCheckBox.class)) {
                        if ("Tradução validada".equals(caixa.getText())) validacaoTraducao = caixa;
                        else if (caixa.getText() == null || caixa.getText().isEmpty()) validacaoOriginal = caixa;
                    }
                    if (validacaoTraducao == null || !validacaoTraducao.isEnabled()) throw new AssertionError("Validação da tradução foi bloqueada");
                    if (validacaoOriginal == null || validacaoOriginal.isEnabled()) throw new AssertionError("Validação da versão original permaneceu ativa");

                    comboTraducao.setSelectedIndex(original);
                    boolean algumCampoReaberto = false;
                    for (JTextField campo : componentes(dialogo, JTextField.class)) {
                        if (campo.isEditable()) { algumCampoReaberto = true; break; }
                    }
                    if (semanticaEditavelNaVersao && !algumCampoReaberto) {
                        throw new AssertionError("Campos da versão semântica original não foram reabertos");
                    }
                    if (!semanticaEditavelNaVersao && algumCampoReaberto) {
                        throw new AssertionError("Campos semânticos herdados ficaram editáveis");
                    }
                    if (!originalArea.isEditable()) throw new AssertionError("Enunciado original não foi reaberto");
                    if (!validacaoOriginal.isEnabled()) throw new AssertionError("Validação original não foi reaberta");
                } catch (Throwable ex) {
                    falha.set(ex);
                } finally {
                    if (dialogo != null) dialogo.dispose();
                    frame.dispose();
                }
            });
            timer.setRepeats(false);
            timer.start();
            try {
                Method abrir = TelaCuradoriaSituacoes.class.getDeclaredMethod("abrirTelaCuradoriaSituacao", int.class);
                abrir.setAccessible(true);
                abrir.invoke(tela, 0);
            } catch (Throwable ex) {
                falha.set(ex);
            }
        });
        if (falha.get() != null) throw new RuntimeException(falha.get());
        System.out.println("TESTE_BLOQUEIO_DINAMICO_IDIOMA_CURADORIA_UI_OK");
    }
}
