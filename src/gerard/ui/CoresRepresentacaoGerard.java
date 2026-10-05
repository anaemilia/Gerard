package gerard.ui;

import java.awt.Color;

/**
 * Ponto único das cores da REPRESENTAÇÃO (unidades/quadradinhos do diagrama
 * e controles de +/- ao redor dele), separado de propósito das cores de
 * feedback (UITemaGerard.COR_SUCESSO/COR_ERRO). Opção A do
 * prompt-claude-code-cores.md: a representação não usa o azul institucional,
 * que fica reservado a ação/foco e ao sucesso. Só apresentação — nenhuma
 * regra semântica depende destes valores.
 */
public final class CoresRepresentacaoGerard {

    private CoresRepresentacaoGerard() {
    }

    /** Contorno e traço da representação (INK da paleta). */
    public static final Color TRACO = UITemaGerard.COR_TEXTO;

    /** Sombra projetada das peças (INK). */
    public static final Color SOMBRA = UITemaGerard.COR_TEXTO;

    // Unidades por papel semântico (preenchimento, borda, texto).
    public static final Color COMPOSICAO_BASE = UITemaGerard.COR_ERRO;
    public static final Color COMPOSICAO_BORDA = TRACO;
    public static final Color COMPOSICAO_TEXTO = Color.WHITE;

    public static final Color COMPARACAO_CORRESPONDENTE_BASE = UITemaGerard.COR_ERRO_CLARO;
    public static final Color COMPARACAO_CORRESPONDENTE_BORDA = UITemaGerard.COR_ERRO_TEXTO;
    public static final Color COMPARACAO_CORRESPONDENTE_TEXTO = new Color(58, 36, 36);

    public static final Color COMPARACAO_EXCEDENTE_BASE = new Color(148, 163, 184);
    public static final Color COMPARACAO_EXCEDENTE_BORDA = new Color(71, 85, 105);
    public static final Color COMPARACAO_EXCEDENTE_TEXTO = new Color(31, 41, 55);

    public static final Color TRANSFORMACAO_NEGATIVA_BASE = new Color(111, 143, 151);
    public static final Color TRANSFORMACAO_NEGATIVA_BORDA = new Color(70, 99, 110);
    public static final Color TRANSFORMACAO_NEGATIVA_TEXTO = new Color(245, 248, 250);

    public static final Color TRANSFORMACAO_POSITIVA_BASE = UITemaGerard.COR_ERRO_CLARO;
    public static final Color TRANSFORMACAO_POSITIVA_BORDA = new Color(86, 116, 126);
    public static final Color TRANSFORMACAO_POSITIVA_TEXTO = new Color(35, 45, 49);

    // Controles neutros (adicionar/remover quadradinho, sinal): paleta cinza fria.
    public static final Color CONTROLE_FUNDO = UITemaGerard.COR_SUPERFICIE;
    public static final Color CONTROLE_FUNDO_FOCADO = UITemaGerard.COR_SUPERFICIE_SUAVE;
    public static final Color CONTROLE_BORDA = UITemaGerard.COR_TEXTO_SECUNDARIO;
    public static final Color CONTROLE_SINAL = new Color(0x3D, 0x47, 0x59);
    public static final Color CONTROLE_FUNDO_DESABILITADO = UITemaGerard.COR_FUNDO;
    public static final Color CONTROLE_BORDA_DESABILITADA = UITemaGerard.COR_BORDA_CONTROLE;
    public static final Color CONTROLE_SINAL_DESABILITADO = UITemaGerard.COR_BORDA_CONTROLE;
}
