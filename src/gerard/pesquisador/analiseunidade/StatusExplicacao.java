package gerard.pesquisador.analiseunidade;

import java.util.Locale;

/**
 * Os quatro estados válidos de C (perguntas explicativas) e D (respostas),
 * idênticos por definição do pacote (modelo_unidade_analise_abcd_gerard_v2.json,
 * "status_explicacao_permitidos").
 */
public enum StatusExplicacao {
    NOT_OPENED,
    OPENED_NOT_ANSWERED,
    PARTIALLY_ANSWERED,
    ANSWERED;

    public String paraTexto() {
        return name().toLowerCase(Locale.ROOT);
    }
}
