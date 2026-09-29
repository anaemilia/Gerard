package gerard.aplicacao.portabilidade;

import gerard.dominio.atividade.ContextoAcaoInstrumental;

import java.util.List;

/**
 * Descritores de atividade (log de pesquisa, não texto de interface) das
 * ações instrumentais da modelagem web. Espelham os contextos do desktop,
 * com origem de evento própria da web para distinguir a plataforma.
 */
final class ContextosAcaoInstrumentalWeb {

    private ContextosAcaoInstrumentalWeb() {
    }

    static ContextoAcaoInstrumental posicionamento(String origemPapelId,
            String chavePapelAlvo, List<String> participantes) {
        return new ContextoAcaoInstrumental(
                "Posicionar elemento semântico do enunciado",
                "Arrastar e soltar",
                "Texto e diagrama de Vergnaud (web)",
                "Associar o elemento textual ao papel quantitativo correspondente",
                chavePapelAlvo,
                "WEB_POSICIONAR_PAPEL",
                "origem=" + origemPapelId + "; destino=" + chavePapelAlvo,
                "Elemento posicionado sobre um papel do diagrama",
                participantes);
    }

    static ContextoAcaoInstrumental valorIncognita(String chavePapelAlvo,
            int valor, List<String> participantes) {
        return new ContextoAcaoInstrumental(
                "Substituir incógnita por número",
                "Editor numérico",
                "Caixa da incógnita no diagrama (web)",
                "Informar valor numérico para o papel designado como incógnita",
                chavePapelAlvo,
                "WEB_PROPOR_VALOR_PAPEL",
                "valor=" + valor,
                "Valor numérico informado para a incógnita",
                participantes);
    }

    static ContextoAcaoInstrumental respostaConfirmacao(String chavePapelAlvo,
            boolean confirmou, int valor, List<String> participantes) {
        return new ContextoAcaoInstrumental(
                "Responder à confirmação do valor da incógnita",
                "Diálogo de confirmação",
                "Botões Sim/Não (web)",
                "Confirmar ou retirar o valor já rejeitado",
                chavePapelAlvo,
                "WEB_CONFIRMACAO_VALOR_INCOGNITA",
                "resposta=" + (confirmou ? "SIM" : "NAO") + "; valor=" + valor,
                "Resposta à pergunta de confirmação do valor",
                participantes);
    }

    static ContextoAcaoInstrumental sinal(String chavePapel, int base,
            String sinal, List<String> participantes) {
        return new ContextoAcaoInstrumental(
                "Escolher sinal do número relativo",
                "Selecionar uma opção de sinal",
                "Menu de sinal (web)",
                "Representar perda ou ganho com sinal",
                chavePapel + ".sinal",
                "WEB_MENU_SINAL",
                "valor=" + base + "; sinal=" + sinal,
                "Sinal selecionado para o papel quantitativo",
                participantes);
    }
}
