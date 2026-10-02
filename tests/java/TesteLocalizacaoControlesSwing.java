import gerard.i18n.ServicoLocalizacao;
import gerard.idioma.IdiomaInterface;
import gerard.ui.swing.LocalizacaoControlesSwing;
import javax.swing.UIManager;

/** Botões Sim/Não do Swing seguem o idioma da interface (mensagens_*.properties). */
public class TesteLocalizacaoControlesSwing {
    public static void main(String[] args) {
        ServicoLocalizacao loc = ServicoLocalizacao.getInstancia();
        loc.definirIdioma(IdiomaInterface.PORTUGUES);
        LocalizacaoControlesSwing.instalar();
        exigir("Sim".equals(UIManager.getString("OptionPane.yesButtonText")), "pt: Sim");
        exigir("Não".equals(UIManager.getString("OptionPane.noButtonText")), "pt: Não");
        loc.definirIdioma(IdiomaInterface.INGLES);
        exigir("Yes".equals(UIManager.getString("OptionPane.yesButtonText")), "en: Yes");
        exigir("No".equals(UIManager.getString("OptionPane.noButtonText")), "en: No");
        loc.definirIdioma(IdiomaInterface.PORTUGUES);
        exigir("Sim".equals(UIManager.getString("OptionPane.yesButtonText")), "volta para pt: Sim");
        System.out.println("Teste aprovado: botões Sim/Não seguem o idioma da interface.");
    }
    private static void exigir(boolean ok, String msg) { if (!ok) throw new AssertionError(msg); }
}
