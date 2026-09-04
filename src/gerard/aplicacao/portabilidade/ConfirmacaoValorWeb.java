package gerard.aplicacao.portabilidade;

import gerard.i18n.ServicoLocalizacao;
import java.util.List;
import java.util.Map;

/**
 * Resolve a pergunta de confirmação do valor da incógnita — mesmo texto real
 * do desktop (ui.question.valueMismatch, "Tem certeza que esse é o valor do
 * {0}?"), usado por confirmarValorIncognitaAceito/confirmarValorIncognitaTexto
 * (Main.java) ao final do protocolo mouse-texto. Único ponto do pacote
 * portabilidade que resolve esse texto — mesmo padrão de centralização já
 * aplicado a AjudaContextualWeb: ServicoSorteioAtividadeWeb decide apenas
 * onde/quando esse campo aparece no estado, nunca chama ServicoLocalizacao
 * diretamente para isso.
 *
 * Não há um campo comum "papel_desconhecido"/rótulo entre as seis modelagens
 * (EstadoAtividade, EstadoModelagemTernaria, EstadoEscolhaOperacao*), então a
 * busca usa a própria cena já projetada: a figura com a interação
 * EDITAR_VALOR é sempre a incógnita (protocolo mouse-texto), e seu rótulo já
 * é o nome conceitual localizado do papel (mesmo texto que
 * localizacao.texto(papelAlvo) resolveria no desktop).
 */
final class ConfirmacaoValorWeb {
    private ConfirmacaoValorWeb() {
    }

    static String perguntaParaCena(Object cena) {
        if (!(cena instanceof Map)) {
            return null;
        }
        Object figurasObj = ((Map<?, ?>) cena).get("figuras");
        if (!(figurasObj instanceof List)) {
            return null;
        }
        for (Object figuraObj : (List<?>) figurasObj) {
            if (!(figuraObj instanceof Map)) {
                continue;
            }
            Map<?, ?> figura = (Map<?, ?>) figuraObj;
            Object interacoesObj = figura.get("interacoes_permitidas");
            if (!(interacoesObj instanceof List)) {
                continue;
            }
            for (Object interacaoObj : (List<?>) interacoesObj) {
                if (interacaoObj instanceof Map
                        && "EDITAR_VALOR".equals(((Map<?, ?>) interacaoObj).get("tipo"))) {
                    Object rotulo = figura.get("rotulo");
                    return ServicoLocalizacao.getInstancia()
                            .formatar("ui.question.valueMismatch", rotulo);
                }
            }
        }
        return null;
    }
}
