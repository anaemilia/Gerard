package gerard.aplicacao.portabilidade;

import gerard.Scaffolding.ajudacontextual.ScaffoldingAjudaContextual;
import gerard.i18n.ServicoLocalizacao;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Único ponto do pacote portabilidade que resolve texto localizado dos tips
 * (dica de pré-classificação e menu "E agora?"). ServicoSorteioAtividadeWeb
 * continua decidindo QUAIS áreas mostrar e QUANDO (estado da atividade, não
 * i18n); esta classe decide só COMO resolver cada chave já apontada por
 * ScaffoldingAjudaContextual (gerard.Scaffolding.ajudacontextual — a classe
 * pura de domínio, sem import nenhum) para texto exibível, via
 * ServicoLocalizacao (mensagens_pt/en/fr/es.properties). Mesmo padrão já
 * usado por MensagemFeedbackIncognitaWeb neste pacote.
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
     * Estrutura do menu "E agora?" para uma área — cabeçalho e rótulos das 3
     * opções, sem a mensagem (só resolvida em mensagem(), no clique — ver
     * mostrarMenuAjudaContextual/criarOpcaoAjudaContextual, Main.java).
     */
    static Map<String, Object> projetarArea(
            ScaffoldingAjudaContextual scaffolding, ScaffoldingAjudaContextual.Area area) {
        ServicoLocalizacao localizacao = ServicoLocalizacao.getInstancia();
        Map<String, Object> item = new LinkedHashMap<String, Object>();
        item.put("area", area.name());
        item.put("cabecalho", localizacao.formatar("ui.help.header", nomeArea(scaffolding, area)));
        List<Object> opcoes = new ArrayList<Object>();
        for (ScaffoldingAjudaContextual.Intencao intencao
                : ScaffoldingAjudaContextual.Intencao.values()) {
            Map<String, Object> opcao = new LinkedHashMap<String, Object>();
            opcao.put("intencao", intencao.name());
            opcao.put("rotulo", localizacao.texto(scaffolding.obterChaveOpcao(intencao)));
            opcoes.add(opcao);
        }
        item.put("opcoes", opcoes);
        return item;
    }

    /**
     * Espelha obterNomeAreaAjudaContextual (Main.java). COMPLEMENTAR lá
     * varia por tipo de representação (Venn de medidas, barras de
     * comparação, ou o Vann genérico); no web hoje só existe o caso Venn de
     * Composição de Medidas, então fixa em ui.collections.title — nenhuma
     * das outras representações complementares foi portada ainda.
     */
    static String nomeArea(ScaffoldingAjudaContextual scaffolding, ScaffoldingAjudaContextual.Area area) {
        if (area == ScaffoldingAjudaContextual.Area.COMPLEMENTAR) {
            return ServicoLocalizacao.getInstancia().texto("ui.collections.title");
        }
        return ServicoLocalizacao.getInstancia().texto(scaffolding.obterChaveArea(area));
    }

    /** Mensagem resolvida ao clicar numa opção do menu "E agora?". */
    static String mensagem(ScaffoldingAjudaContextual scaffolding,
            ScaffoldingAjudaContextual.Area area, ScaffoldingAjudaContextual.Intencao intencao) {
        return ServicoLocalizacao.getInstancia().texto(scaffolding.obterChaveMensagem(area, intencao));
    }

    /** Rótulo de uma opção isolada (usado no log granular de ajudaContextual). */
    static String rotuloOpcao(ScaffoldingAjudaContextual scaffolding,
            ScaffoldingAjudaContextual.Intencao intencao) {
        return ServicoLocalizacao.getInstancia().texto(scaffolding.obterChaveOpcao(intencao));
    }
}
