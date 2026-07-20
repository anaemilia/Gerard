package gerard.ui;

import java.awt.Color;
import java.awt.Font;

/**
 * Tema visual compartilhado pelos menus e componentes auxiliares do Gerard.
 *
 * Mantém separados o padrão dos botões da barra principal e o padrão dos
 * itens internos dos menus. Essa distinção preserva a hierarquia visual já
 * consolidada: 12 pt na barra principal e 14 pt nos itens dos menus.
 */
public final class UITemaGerard {

    private UITemaGerard() {
    }

    /** Fonte dos botões da barra principal. */
    public static final Font FONTE_BOTAO_MENU_PRINCIPAL = new Font("Arial", Font.BOLD, 12);

    /** Fonte dos itens de menus internos e menus popup. */
    public static final Font FONTE_ITEM_MENU = new Font("Arial", Font.BOLD, 14);


    /** Fonte dos títulos exibidos dentro dos submenus informativos. */
    public static final Font FONTE_TITULO_SUBMENU = new Font("Arial", Font.BOLD, 14);

    /** Fonte mínima dos textos e links exibidos dentro dos submenus informativos. */
    public static final Font FONTE_TEXTO_SUBMENU = new Font("Arial", Font.PLAIN, 14);

    /** Fonte das mensagens principais dos diálogos padronizados. */
    public static final Font FONTE_DIALOGO = new Font("Arial", Font.BOLD, 14);


    /** Fundo geral das telas auxiliares. */
    public static final Color COR_FUNDO = new Color(0xE7, 0xE4, 0xDC);

    /** Superfície principal de cartões e painéis. */
    public static final Color COR_SUPERFICIE = new Color(0xFC, 0xFB, 0xF8);

    /** Superfície de apoio para campos e resumos. */
    public static final Color COR_SUPERFICIE_SUAVE = new Color(0xEC, 0xE9, 0xE0);

    /**
     * Cor "primária" neutra (antes azul). O azul fica reservado
     * exclusivamente ao feedback de sucesso — ver COR_SUCESSO.
     */
    public static final Color COR_PRIMARIA = new Color(0x33, 0x2E, 0x28);

    /** Tom escuro neutro usado em títulos, bordas e textos de ação. */
    public static final Color COR_PRIMARIA_ESCURA = new Color(0x33, 0x2E, 0x28);

    /** Texto principal. */
    public static final Color COR_TEXTO = new Color(0x33, 0x2E, 0x28);

    /** Texto secundário e explicações. */
    public static final Color COR_TEXTO_SECUNDARIO = new Color(0x74, 0x6E, 0x62);

    /** Bordas discretas dos cartões e campos. */
    public static final Color COR_BORDA = new Color(0xD8, 0xD4, 0xC8);

    /** Fundo neutro suave para cabeçalhos e destaques. */
    public static final Color COR_DESTAQUE = new Color(0xEC, 0xE9, 0xE0);

    /** Fundo de campos temporariamente desabilitados. */
    public static final Color COR_CAMPO_DESABILITADO = new Color(0xF7, 0xF6, 0xF1);

    /** Fundo da área de conteúdo principal — mais claro que COR_FUNDO (geral). */
    public static final Color COR_FUNDO_CONTEUDO = new Color(0xF7, 0xF6, 0xF1);

    /** Tom de ícone/traço em estado desabilitado. */
    public static final Color COR_ICONE_DESABILITADO = new Color(0xBF, 0xBA, 0xAC);

    /** Tom usado em bordas/linhas tracejadas discretas. */
    public static final Color COR_TRACEJADO = new Color(0xAE, 0xA6, 0x96);

    /** Cor de texto dos itens de menu. */
    public static final Color COR_TEXTO_MENU = new Color(0x33, 0x2E, 0x28);

    /**
     * Cor reservada para feedback de SUCESSO (ex.: confirmação de acerto,
     * proximidade correta durante arraste). Uso restrito a sinais ao
     * usuário — não deve ser aplicada a elementos decorativos ou de marca.
     */
    public static final Color COR_SUCESSO = new Color(74, 130, 201);

    /** Fundo suave para rótulos e áreas de feedback de sucesso. */
    public static final Color COR_SUCESSO_FUNDO = new Color(228, 238, 253);

    /** Texto sobre fundo de sucesso. */
    public static final Color COR_SUCESSO_TEXTO = new Color(23, 63, 150);

    /**
     * Cor reservada para feedback de ERRO (ex.: valor inválido, tentativa
     * incorreta). Uso restrito a sinais ao usuário — segue o padrão
     * cultural de vermelho para erro.
     */
    public static final Color COR_ERRO = new Color(200, 40, 40);

    /** Fundo suave para rótulos e áreas de feedback de erro. */
    public static final Color COR_ERRO_FUNDO = new Color(253, 232, 232);

    /** Texto sobre fundo de erro. */
    public static final Color COR_ERRO_TEXTO = new Color(140, 27, 27);

    /** Alias mantido para compatibilidade com chamadas anteriores. */
    @Deprecated
    public static final Font FONTE_BOTAO_MENU = FONTE_BOTAO_MENU_PRINCIPAL;
}
