package gerard.desktop.composicaomedidas;

import java.awt.Rectangle;

/**
 * Uma das três caixas do diagrama (Parte 1, Parte 2 ou Todo). Mantém a
 * posição/tamanho na tela, o valor esperado, o valor atualmente
 * posicionado (se houver) e o estado de interação da caixa.
 */
public final class CaixaDiagrama {

    public enum Papel { PARTE1, PARTE2, TODO }

    public final Papel papel;
    public final String rotulo;
    public Rectangle limites = new Rectangle();

    /** Valor correto esperado nesta caixa (null se esta caixa é a incógnita). */
    public final Integer valorEsperado;

    /** Valor atualmente posicionado na caixa (null = vazia). */
    public Integer valorAtual;

    /** Verdadeiro quando esta caixa é a incógnita ("?") da situação. */
    public final boolean ehIncognita;

    /** Verdadeiro quando a incógnita já foi confirmada com o valor certo. */
    public boolean incognitaConfirmada;

    /** Texto do campo de entrada da incógnita, enquanto não confirmado. */
    public String textoDigitadoIncognita = "";

    /** Mensagem de dica fixa exibida após um erro nesta caixa (vazio = sem dica). */
    public String dicaErro = "";

    public CaixaDiagrama(Papel papel, String rotulo, Integer valorEsperado, boolean ehIncognita) {
        this.papel = papel;
        this.rotulo = rotulo;
        this.valorEsperado = valorEsperado;
        this.ehIncognita = ehIncognita;
    }

    public boolean estaVazia() {
        return valorAtual == null;
    }

    public boolean estaCorreta() {
        if (ehIncognita) {
            return incognitaConfirmada;
        }
        return valorAtual != null && valorAtual.equals(valorEsperado);
    }

    public void limpar() {
        valorAtual = null;
        incognitaConfirmada = false;
        textoDigitadoIncognita = "";
        dicaErro = "";
    }
}
