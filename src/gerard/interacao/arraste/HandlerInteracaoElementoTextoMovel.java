package gerard.interacao.arraste;

import gerard.campoaditivo.diagrama.elementos.ElementoTextoMovel;
import gerard.interacao.geometria.LimitesMovimento;

/**
 * Mantém o estado e a mecânica local do gesto de um elemento textual.
 *
 * Não conhece papel semântico, conversão para item do diagrama, Swing,
 * scaffolding, sincronização ou logs. A tela decide qual modalidade de
 * movimento usar e fornece a geometria real permitida.
 */
public final class HandlerInteracaoElementoTextoMovel {
    private ElementoTextoMovel elementoAtivo;
    private int deslocamentoX;
    private int deslocamentoY;

    public boolean iniciar(ElementoTextoMovel elemento,
            int mouseX, int mouseY) {
        if (elemento == null) {
            cancelar();
            return false;
        }
        elementoAtivo = elemento;
        deslocamentoX = mouseX - elemento.x;
        deslocamentoY = mouseY - elemento.y;
        return true;
    }

    public ElementoTextoMovel moverLivrePara(int mouseX, int mouseY) {
        if (elementoAtivo == null) {
            return null;
        }
        elementoAtivo.x = mouseX - deslocamentoX;
        elementoAtivo.y = mouseY - deslocamentoY;
        return elementoAtivo;
    }

    public ElementoTextoMovel moverDentroDosLimites(int mouseX, int mouseY,
            LimitesMovimento limites) {
        if (elementoAtivo == null) {
            return null;
        }
        if (limites == null) {
            throw new IllegalArgumentException("limites obrigatorios");
        }

        int novoX = limites.limitarX(mouseX - deslocamentoX);
        int novoY = limites.limitarY(mouseY - deslocamentoY);
        elementoAtivo.x = novoX;
        elementoAtivo.y = novoY;
        return elementoAtivo;
    }

    public ElementoTextoMovel concluir() {
        ElementoTextoMovel elemento = elementoAtivo;
        cancelar();
        return elemento;
    }

    public void cancelar() {
        elementoAtivo = null;
        deslocamentoX = 0;
        deslocamentoY = 0;
    }

    public boolean estaAtivo() {
        return elementoAtivo != null;
    }

    public ElementoTextoMovel obterElementoAtivo() {
        return elementoAtivo;
    }

    public ElementoTextoMovel identificarFoco(
            ElementoTextoMovel candidato, boolean estaNaAreaDoTexto) {
        return candidato != null && estaNaAreaDoTexto ? candidato : null;
    }

}
