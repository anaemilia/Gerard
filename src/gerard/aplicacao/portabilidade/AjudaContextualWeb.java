package gerard.aplicacao.portabilidade;

import gerard.i18n.ServicoLocalizacao;

/**
 * Único ponto do pacote portabilidade que resolve texto localizado dos tips
 * de i18n puro (dica de pré-classificação, material concreto) — sem
 * depender do scaffolding concreto do menu "E agora?", que hoje fica atrás
 * de {@link PortaAjudaContextual} (ver ServicoSorteioAtividadeWeb). Mesmo
 * padrão já usado por MensagemFeedbackIncognitaWeb neste pacote.
 */
final class AjudaContextualWeb {
    private AjudaContextualWeb() {
    }

    /**
     * Mesmo texto do "?" ao lado do enunciado no desktop
     * (botaoAtalhoProximoPasso, Main.java), visível enquanto a categoria
     * ainda não foi confirmada.
     */
    static String textoDicaProximoPasso() {
        return ServicoLocalizacao.getInstancia().texto("ui.hint.nextStep.tooltip");
    }

    /**
     * Texto de instrução do material concreto (AG_EMCME) — mesma chave já
     * usada como descrição da área COMPLEMENTAR do menu "E agora?"
     * (ui.collections.description, ver nomeArea/ui.collections.title acima),
     * já que hoje o único caso portado é o Venn de duas coleções formando uma
     * coleção total (Composição de Medidas). Antes vivia hardcoded em
     * MaterialConcretoQuadradinhos.tsx — movido para cá para não ter duas
     * fontes de texto de interface fora de mensagens_pt.properties.
     */
    static String textoInstrucaoMaterialConcreto() {
        return ServicoLocalizacao.getInstancia().texto("ui.collections.description");
    }

    /** Tooltip real do botão "+" de um agrupamento de quadradinhos (Main.java). */
    static String textoAdicionarQuadradinho() {
        return ServicoLocalizacao.getInstancia().texto("ui.tooltip.venn.addSquare");
    }

    /** Tooltip real do botão "−" de um agrupamento de quadradinhos (Main.java). */
    static String textoRemoverQuadradinho() {
        return ServicoLocalizacao.getInstancia().texto("ui.tooltip.venn.removeSquare");
    }
}
