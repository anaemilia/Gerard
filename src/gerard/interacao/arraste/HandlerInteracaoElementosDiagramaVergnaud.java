package gerard.interacao.arraste;

import gerard.campoaditivo.diagrama.elementos.ConectorVergnaud;
import gerard.campoaditivo.diagrama.elementos.ElementoVergnaud;
import java.awt.Rectangle;

/**
 * Mantém o estado e a mecânica local do gesto de reposicionamento de um
 * elemento ou de um conector do diagrama de Vergnaud (fase 7.4 do roteiro de
 * extração de handlers — ver .claude/skills/gerard-handlers-de-interacao).
 *
 * Elemento e conector são mutuamente exclusivos dentro de um mesmo gesto: a
 * tela decide, no pickup, qual dos dois foi alvo (pelo resultado do próprio
 * hit-testing, que continua só na tela) e chama iniciarElemento ou
 * iniciarConector — nunca os dois no mesmo gesto, mesma regra que já valia
 * antes desta extração.
 *
 * Este handler NÃO conhece: hit-testing (a lista completa de elementos e
 * conectores continua só na tela), política de duplo-clique, fantasma/
 * arraste elástico, foco de outros protocolos (item textual, quadradinho do
 * Venn), log de ação instrumental ou repaint. Conhece só: qual elemento ou
 * conector está ativo, o deslocamento do pickup, o limiar de arraste
 * estrutural (para não confundir clique/duplo-clique com arraste real — ver
 * ControladorLimiarArrasteEstrutural) e a mecânica de mover cada um dentro
 * dos limites recebidos da tela.
 */
public final class HandlerInteracaoElementosDiagramaVergnaud {
    private final ControladorLimiarArrasteEstrutural limiar =
            new ControladorLimiarArrasteEstrutural();

    private ElementoVergnaud elementoAtivo;
    private int deslocamentoX;
    private int deslocamentoY;

    private ConectorVergnaud conectorAtivo;
    private int mouseAnteriorX;
    private int mouseAnteriorY;

    public boolean iniciarElemento(ElementoVergnaud elemento, int mouseX, int mouseY) {
        if (elemento == null) {
            cancelar();
            return false;
        }
        cancelar();
        elementoAtivo = elemento;
        deslocamentoX = mouseX - elemento.x;
        deslocamentoY = mouseY - elemento.y;
        limiar.iniciar(mouseX, mouseY);
        return true;
    }

    public boolean iniciarConector(ConectorVergnaud conector, int mouseX, int mouseY) {
        if (conector == null) {
            cancelar();
            return false;
        }
        cancelar();
        conectorAtivo = conector;
        mouseAnteriorX = mouseX;
        mouseAnteriorY = mouseY;
        limiar.iniciar(mouseX, mouseY);
        return true;
    }

    /**
     * Aplica o movimento ao elemento ou conector ativo, respeitando o
     * limiar de arraste estrutural (pequenas oscilações do mouse não movem
     * nada, exatamente como antes da extração). Devolve true só quando algo
     * de fato se moveu, para a tela decidir se repinta.
     */
    public boolean mover(int mouseX, int mouseY, Rectangle limites) {
        if (!limiar.deveMovimentar(mouseX, mouseY)) {
            return false;
        }
        if (elementoAtivo != null) {
            elementoAtivo.moverPara(mouseX - deslocamentoX, mouseY - deslocamentoY, limites);
            return true;
        }
        if (conectorAtivo != null) {
            int dx = mouseX - mouseAnteriorX;
            int dy = mouseY - mouseAnteriorY;
            conectorAtivo.mover(dx, dy, limites);
            mouseAnteriorX = mouseX;
            mouseAnteriorY = mouseY;
            return true;
        }
        return false;
    }

    /**
     * Encerra só o limiar de arraste estrutural, sem descartar ainda o
     * elemento/conector ativo — mesma ordem de finalização em dois passos já
     * usada na tela antes desta extração (limiar finalizado logo
     * no início de mouseReleased; elemento/conector só são liberados depois,
     * ao final do processamento da soltura, via cancelar()).
     */
    public void finalizarLimiar() {
        limiar.finalizar();
    }

    public void cancelar() {
        elementoAtivo = null;
        deslocamentoX = 0;
        deslocamentoY = 0;
        conectorAtivo = null;
        mouseAnteriorX = 0;
        mouseAnteriorY = 0;
        limiar.finalizar();
    }

    public boolean estaAtivo() {
        return elementoAtivo != null || conectorAtivo != null;
    }

    public ElementoVergnaud obterElementoAtivo() {
        return elementoAtivo;
    }

    public ConectorVergnaud obterConectorAtivo() {
        return conectorAtivo;
    }
}
