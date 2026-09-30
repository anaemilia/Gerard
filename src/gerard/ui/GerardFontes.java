package gerard.ui;

import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.GraphicsEnvironment;
import java.io.File;
import java.io.IOException;
import java.util.Vector;
import javax.swing.UIManager;
import javax.swing.plaf.FontUIResource;

/**
 * Registro e escala tipográfica das fontes IBM Plex Sans/Mono (SIL Open Font
 * License — ver resources/fonts/LICENSE.txt), alinhadas ao Design System do
 * site institucional (ver prompt-claude-code-cores.md, seção 5, 2026-09-30).
 *
 * Se os arquivos .ttf não estiverem presentes ou não puderem ser carregados,
 * cada método de escala cai para uma fonte do sistema equivalente (Arial ou
 * Monospaced) em vez de falhar — a aparência degrada, mas o Gérard continua
 * funcionando.
 */
public final class GerardFontes {

    private GerardFontes() {
    }

    private static final String DIRETORIO = "resources/fonts";

    private static Font plexSansRegular;
    private static Font plexSansMedium;
    private static Font plexSansSemiBold;
    private static Font plexMonoRegular;
    private static Font plexMonoMedium;

    private static boolean registradas;

    /** Carrega e registra as cinco fontes no ambiente gráfico. Idempotente. */
    public static synchronized void registrar() {
        if (registradas) {
            return;
        }
        plexSansRegular = carregar("IBMPlexSans-Regular.ttf");
        plexSansMedium = carregar("IBMPlexSans-Medium.ttf");
        plexSansSemiBold = carregar("IBMPlexSans-SemiBold.ttf");
        plexMonoRegular = carregar("IBMPlexMono-Regular.ttf");
        plexMonoMedium = carregar("IBMPlexMono-Medium.ttf");
        registradas = true;
    }

    private static Font carregar(String nomeArquivo) {
        File arquivo = new File(DIRETORIO, nomeArquivo);
        if (!arquivo.isFile()) {
            System.err.println("Fonte não encontrada: " + arquivo.getAbsolutePath());
            return null;
        }
        try {
            Font fonte = Font.createFont(Font.TRUETYPE_FONT, arquivo);
            GraphicsEnvironment.getLocalGraphicsEnvironment().registerFont(fonte);
            return fonte;
        } catch (FontFormatException | IOException ex) {
            System.err.println("Não foi possível carregar a fonte " + nomeArquivo + ": " + ex.getMessage());
            return null;
        }
    }

    private static Font derivar(Font base, String nomeAlternativo, int estiloAlternativo, float tamanho) {
        if (base != null) {
            return base.deriveFont(tamanho);
        }
        return new Font(nomeAlternativo, estiloAlternativo, Math.round(tamanho));
    }

    /** Enunciado da situação-problema: Plex Sans SemiBold 22px. */
    public static Font enunciado() {
        registrar();
        return derivar(plexSansSemiBold, "Arial", Font.BOLD, 22f);
    }

    /** Títulos de painel: Plex Sans SemiBold 17px. */
    public static Font tituloPainel() {
        registrar();
        return derivar(plexSansSemiBold, "Arial", Font.BOLD, 17f);
    }

    /** Texto corrido: Plex Sans Regular 16px. */
    public static Font texto() {
        registrar();
        return derivar(plexSansRegular, "Arial", Font.PLAIN, 16f);
    }

    /** Rótulos pequenos: Plex Sans Medium 13px. */
    public static Font rotuloPequeno() {
        registrar();
        return derivar(plexSansMedium, "Arial", Font.PLAIN, 13f);
    }

    /** Texto de botão (peso não especificado na escala do prompt; Medium por consistência com rótulos). */
    public static Font botao() {
        registrar();
        return derivar(plexSansMedium, "Arial", Font.PLAIN, 15f);
    }

    /** Números do diagrama e do material concreto: Plex Mono Medium 24px, algarismos de largura igual. */
    public static Font numeroDiagrama() {
        registrar();
        return derivar(plexMonoMedium, "Monospaced", Font.PLAIN, 24f);
    }

    /** Texto monoespaçado corrido (uso geral fora do diagrama), Plex Mono Regular. */
    public static Font monoTexto(float tamanho) {
        registrar();
        return derivar(plexMonoRegular, "Monospaced", Font.PLAIN, tamanho);
    }

    /**
     * Plex Sans num tamanho fora da escala nomeada acima (telas com
     * densidade própria, como diálogos de curadoria) — negrito aqui sempre
     * vira SemiBold (peso 600, nunca o negrito pesado padrão, ver seção 5 do
     * prompt-claude-code-cores.md), nunca um peso mais pesado.
     */
    public static Font sans(boolean negrito, float tamanho) {
        registrar();
        Font base = negrito ? plexSansSemiBold : plexSansRegular;
        return derivar(base, "Arial", negrito ? Font.BOLD : Font.PLAIN, tamanho);
    }

    /**
     * Aplica {@link #texto()} como fonte padrão de todos os componentes
     * Swing (substitui cada chave "*.font" do UIDefaults — a L&F padrão
     * (Metal) não interpreta um curinga literal, por isso a substituição é
     * feita chave a chave). Telas específicas continuam livres para pedir
     * uma fonte da escala acima (enunciado, título de painel etc.) quando
     * migradas.
     */
    public static void aplicarFontesGlobais() {
        Font padrao = texto();
        FontUIResource recurso = new FontUIResource(padrao);
        for (Object chave : new Vector<Object>(UIManager.getLookAndFeelDefaults().keySet())) {
            if (String.valueOf(chave).endsWith(".font")) {
                UIManager.put(chave, recurso);
            }
        }
    }
}
