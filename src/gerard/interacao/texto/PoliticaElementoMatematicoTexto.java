package gerard.interacao.texto;

import gerard.interpretacao.simbolo.SimboloDesconhecido;

/**
 * Regra única para reconhecer os elementos matemáticos manipuláveis do texto.
 * Não depende de uma categoria específica e, portanto, aplica o mesmo fluxo a
 * números, números sinalizados, decimais e incógnitas.
 */
public final class PoliticaElementoMatematicoTexto {

    public boolean ehElementoMatematico(String valor) {
        String v = valor == null ? "" : valor.trim();
        if (SimboloDesconhecido.eh(v)) {
            return true;
        }
        return v.matches("[+-]?\\d+(?:[.,]\\d+)?");
    }

    public boolean possuiPapelSemantico(String chavePapel) {
        String chave = chavePapel == null ? "" : chavePapel.trim();
        return chave.startsWith("papel.") && !"papel.valor".equals(chave);
    }

    public boolean deveValidar(String valor, String chavePapel) {
        return ehElementoMatematico(valor) && possuiPapelSemantico(chavePapel);
    }
}
