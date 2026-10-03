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


    /**
     * Fundo geral das telas auxiliares (janela). Paleta neutra alinhada ao
     * Design System do site institucional (2026-09-30, ver
     * prompt-claude-code-cores.md) — cinza frio, substituindo o bege/taupe
     * quente anterior. Atualizado no lugar (decisão da pesquisadora): manter
     * um único arquivo/conjunto de constantes em vez de criar classes novas
     * paralelas, para não deixar dois sistemas de cor ativos ao mesmo tempo
     * (os 31 arquivos que já usam UITemaGerard continuam funcionando sem
     * alteração).
     */
    public static final Color COR_FUNDO = new Color(0xF7, 0xF9, 0xFC);

    /** Superfície principal de cartões, painéis e áreas de diagrama. */
    public static final Color COR_SUPERFICIE = new Color(0xFF, 0xFF, 0xFF);

    /** Superfície de apoio para campos e resumos. */
    public static final Color COR_SUPERFICIE_SUAVE = new Color(0xEE, 0xF1, 0xF5);

    /**
     * Cor "primária" neutra (antes bege/taupe escuro, antes disso azul). O
     * azul institucional é usado à parte em ações/foco/seleção — ver
     * COR_ACAO — e no feedback de sucesso — ver COR_SUCESSO.
     */
    public static final Color COR_PRIMARIA = new Color(0x0E, 0x17, 0x26);

    /** Tom escuro neutro usado em títulos, bordas e textos de ação. */
    public static final Color COR_PRIMARIA_ESCURA = new Color(0x0E, 0x17, 0x26);

    /** Texto principal. */
    public static final Color COR_TEXTO = new Color(0x0E, 0x17, 0x26);

    /** Texto secundário e explicações (contraste ≥4.5:1 sobre branco). */
    public static final Color COR_TEXTO_SECUNDARIO = new Color(0x5B, 0x65, 0x77);

    /** Bordas discretas dos cartões e campos. */
    public static final Color COR_BORDA = new Color(0xE1, 0xE6, 0xEE);

    /** Bordas de controles interativos (botão secundário, campos) — mais contrastante que COR_BORDA. */
    public static final Color COR_BORDA_CONTROLE = new Color(0xC5, 0xCC, 0xD8);

    /** Fundo neutro suave para cabeçalhos e destaques. */
    public static final Color COR_DESTAQUE = new Color(0xEE, 0xF1, 0xF5);

    /** Fundo de campos temporariamente desabilitados. */
    public static final Color COR_CAMPO_DESABILITADO = new Color(0xEE, 0xF1, 0xF5);

    /** Fundo da área de conteúdo principal — mais claro que COR_FUNDO (geral). */
    public static final Color COR_FUNDO_CONTEUDO = new Color(0xFF, 0xFF, 0xFF);

    /** Tom de ícone/traço em estado desabilitado. */
    public static final Color COR_ICONE_DESABILITADO = new Color(0xC5, 0xCC, 0xD8);

    /** Tom usado em bordas/linhas tracejadas discretas. */
    public static final Color COR_TRACEJADO = new Color(0xC5, 0xCC, 0xD8);

    /**
     * Azul institucional reservado a ações/marca: foco, seleção e botões
     * primários (ver GerardButton) — nunca na área da tarefa/diagrama, que
     * permanece neutra (COR_PRIMARIA/COR_TEXTO). Mesma decisão "Opção A" do
     * prompt-claude-code-cores.md: o mesmo azul também é COR_SUCESSO: isso
     * amplia deliberadamente o escopo antes documentado em
     * gerard-identidade-visual ("uso restrito a sinais ao usuário") para
     * incluir ações primárias — atualizado junto nesta mudança.
     */
    public static final Color COR_ACAO = new Color(0x1F, 0x56, 0xD6);

    /** Estado de hover do azul de ação. */
    public static final Color COR_ACAO_HOVER = new Color(0x19, 0x46, 0xB3);

    /** Estado pressionado do azul de ação; também usado como texto sobre fundo azul claro. */
    public static final Color COR_ACAO_PRESSIONADA = new Color(0x17, 0x3A, 0x8C);

    /** Fundo azul claro (hover leve, fundos de destaque de ação). */
    public static final Color COR_ACAO_FUNDO = new Color(0xE3, 0xEC, 0xFD);

    /**
     * Tom âmbar reservado aos ícones do editor de narrativa (T de editar,
     * círculo com marca de confirmação de concluir) — símbolo e cor
     * fornecidos pela usuária, mesmo tom (#B88430) usado no protótipo web
     * (.botao-editar-narrativa svg). Não é cor de feedback (ver COR_SUCESSO/
     * COR_ERRO) nem faz parte da paleta neutra geral — uso restrito a esses
     * dois ícones utilitários do editor de enunciado.
     */
    public static final Color COR_ICONE_NARRATIVA = new Color(0xB8, 0x84, 0x30);

    /** Cor de texto dos itens de menu. */
    public static final Color COR_TEXTO_MENU = new Color(0x0E, 0x17, 0x26);

    /**
     * Cor reservada para feedback de SUCESSO (ex.: confirmação de acerto,
     * proximidade correta durante arraste, diagrama completo). Segue
     * {@link FeedbackMode#MODO_ATUAL} — o sucesso nunca depende só da cor:
     * sempre acompanhado de texto/ícone (ver SeloConclusaoModelagem e
     * afins), nunca introduzido isoladamente.
     */
    public static final Color COR_SUCESSO = FeedbackMode.MODO_ATUAL == FeedbackMode.AZUL_UNIFICADO_COM_ACAO
            ? new Color(0x1F, 0x56, 0xD6) : new Color(0x16, 0x60, 0x3C);

    /** Fundo suave para rótulos e áreas de feedback de sucesso. */
    public static final Color COR_SUCESSO_FUNDO = FeedbackMode.MODO_ATUAL == FeedbackMode.AZUL_UNIFICADO_COM_ACAO
            ? new Color(0xE3, 0xEC, 0xFD) : new Color(0xE7, 0xF5, 0xEE);

    /** Texto sobre fundo de sucesso. */
    public static final Color COR_SUCESSO_TEXTO = FeedbackMode.MODO_ATUAL == FeedbackMode.AZUL_UNIFICADO_COM_ACAO
            ? new Color(0x17, 0x3A, 0x8C) : new Color(0x16, 0x60, 0x3C);

    /**
     * Cor reservada para feedback de ERRO (ex.: valor inválido, tentativa
     * incorreta). Uso restrito a sinais ao usuário — segue o padrão
     * cultural de vermelho para erro.
     */
    public static final Color COR_ERRO = new Color(0xC9, 0x4F, 0x45);

    /** Fundo suave para rótulos e áreas de feedback de erro. */
    public static final Color COR_ERRO_FUNDO = new Color(0xFD, 0xEC, 0xEA);

    /** Texto sobre fundo de erro. */
    public static final Color COR_ERRO_TEXTO = new Color(0xC0, 0x45, 0x3B);

    /**
     * Cor reservada para feedback de ALERTA (aviso, não erro nem sucesso).
     * Sem consumidor conhecido nesta versão — token preparado junto com
     * SUCESSO/ERRO pelo mesmo pedido (prompt-claude-code-cores.md); só deve
     * ser usada quando uma tela precisar de fato distinguir aviso de erro.
     */
    public static final Color COR_ALERTA = new Color(0x8A, 0x4B, 0x06);

    /** Fundo suave para rótulos e áreas de feedback de alerta. */
    public static final Color COR_ALERTA_FUNDO = new Color(0xFD, 0xF3, 0xE1);

    /** Texto sobre fundo de alerta. */
    public static final Color COR_ALERTA_TEXTO = new Color(0x8A, 0x4B, 0x06);

    /** Alias mantido para compatibilidade com chamadas anteriores. */
    @Deprecated
    public static final Font FONTE_BOTAO_MENU = FONTE_BOTAO_MENU_PRINCIPAL;
}
