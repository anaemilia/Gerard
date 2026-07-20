package gerard.campoaditivo.conclusao;

import gerard.Scaffolding.questionamento.ScaffoldingQuestionamento;

/**
 * Regra única para impedir que a sincronização resolva visualmente a incógnita
 * antes de o usuário concluir o protocolo previsto: posicionar "?" e, depois,
 * substituí-lo por um número usando mouse/texto.
 */
public final class PoliticaPreenchimentoIncognita {
    private final ScaffoldingQuestionamento questionamento =
            new ScaffoldingQuestionamento();

    public boolean ehPapelDaIncognita(String papelAlvo, String papelIncognita) {
        String alvo = limpar(papelAlvo);
        String incognita = limpar(papelIncognita);
        if (alvo.length() == 0 || incognita.length() == 0
                || "papel.valor".equals(alvo)
                || "papel.valor".equals(incognita)) {
            return false;
        }
        return questionamento.papeisCompativeis(alvo, incognita)
                || questionamento.papeisCompativeis(incognita, alvo);
    }

    /**
     * Enquanto o valor não tiver sido informado pelo protocolo de mouse/texto,
     * qualquer cálculo ou sincronização automática deve preservar o marcador ?.
     */
    public boolean devePreservarMarcador(String papelAlvo, String papelIncognita,
            boolean preenchidoPeloProtocoloMouseTexto) {
        return ehPapelDaIncognita(papelAlvo, papelIncognita)
                && !preenchidoPeloProtocoloMouseTexto;
    }

    private String limpar(String valor) {
        return valor == null ? "" : valor.trim();
    }
}
