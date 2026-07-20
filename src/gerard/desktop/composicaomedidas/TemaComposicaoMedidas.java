package gerard.desktop.composicaomedidas;

import java.awt.Color;
import java.awt.Font;

/**
 * Identidade visual desta tela, conforme a seção 5 das instruções:
 * fundo cinza suave e quente, cor reservada exclusivamente a
 * feedback/sinal — azul para sucesso, vermelho/terracota para erro.
 */
public final class TemaComposicaoMedidas {

    private TemaComposicaoMedidas() {
    }

    /** Fundo geral da janela (referência web: #E7E4DC). */
    public static final Color FUNDO_GERAL = new Color(0xE7, 0xE4, 0xDC);

    /** Fundo da área de conteúdo (referência web: #F7F6F1). */
    public static final Color FUNDO_CONTEUDO = new Color(0xF7, 0xF6, 0xF1);

    /** Borda neutra discreta, sem carga de significado. */
    public static final Color BORDA_NEUTRA = new Color(0xD8, 0xD4, 0xC8);

    /** Preenchimento neutro das caixas do diagrama, em repouso. */
    public static final Color CAIXA_NEUTRA_FUNDO = new Color(0xFC, 0xFB, 0xF8);

    /** Texto principal. */
    public static final Color TEXTO = new Color(0x33, 0x2E, 0x28);

    /** Texto secundário (rótulos "Parte", "Todo", instruções). */
    public static final Color TEXTO_SECUNDARIO = new Color(0x74, 0x6E, 0x62);

    /** Azul = sucesso (referência: #2E5EAA). */
    public static final Color SUCESSO = new Color(0x2E, 0x5E, 0xAA);

    /** Fundo suave de sucesso. */
    public static final Color SUCESSO_FUNDO = new Color(0xE4, 0xEC, 0xF6);

    /** Vermelho/terracota = erro (referência: #A8452C). */
    public static final Color ERRO = new Color(0xA8, 0x45, 0x2C);

    /** Fundo suave de erro. */
    public static final Color ERRO_FUNDO = new Color(0xF6, 0xE7, 0xE2);

    /** Realce discreto de hover (não é feedback de correção — mantido neutro). */
    public static final Color HOVER_NEUTRO = new Color(0xEC, 0xE9, 0xE0);

    public static final Font FONTE_ENUNCIADO = new Font("Georgia", Font.ITALIC, 18);
    public static final Font FONTE_NUMERO = new Font("Arial", Font.BOLD, 18);
    public static final Font FONTE_ROTULO = new Font("Arial", Font.BOLD, 12);
    public static final Font FONTE_DICA = new Font("Arial", Font.PLAIN, 12);
    public static final Font FONTE_LINK = new Font("Arial", Font.BOLD, 13);
}
