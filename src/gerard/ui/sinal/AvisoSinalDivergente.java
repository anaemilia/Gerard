package gerard.ui.sinal;

import gerard.campoaditivo.diagrama.elementos.ElementoVergnaud;
import gerard.campoaditivo.diagrama.elementos.ItemTextoArrastavel;
import java.awt.Point;
import java.util.Collection;

/**
 * Aviso persistente de sinal divergente do curado no número relativo: fica na tela até o menu de sinal ser
 * reaberto para o mesmo item/elemento. Ancorado no item (menu aberto a partir de um item solto no diagrama)
 * OU no elemento (aberto a partir do próprio círculo/retângulo) — nunca nos dois. Guarda o estado e responde
 * se ainda é válido e onde ancorar; quem pinta a anotação é a tela.
 */
public final class AvisoSinalDivergente {
    private ItemTextoArrastavel item;
    private ElementoVergnaud elemento;
    private String texto = "";
    private boolean mostrar;

    /** Passa a mostrar o aviso; o elemento só vale quando não há item. */
    public void exibir(ItemTextoArrastavel itemAncora, ElementoVergnaud elementoAncora, String mensagem) {
        this.item = itemAncora;
        this.elemento = itemAncora == null ? elementoAncora : null;
        this.texto = mensagem;
        this.mostrar = true;
    }

    /** Apaga o aviso; devolve se havia algo a apagar (quem chama também interrompe o tremor do feedback). */
    public boolean limpar() {
        boolean havia = mostrar || item != null || elemento != null;
        mostrar = false;
        texto = "";
        item = null;
        elemento = null;
        return havia;
    }

    /** Ainda deve aparecer: ligado, com mensagem e com a âncora ainda presente no diagrama. */
    public boolean estaVisivel(Collection<ItemTextoArrastavel> itensNoDiagrama,
            Collection<ElementoVergnaud> elementosNoDiagrama) {
        return mostrar
                && (item != null ? itensNoDiagrama.contains(item)
                        : (elemento != null && elementosNoDiagrama.contains(elemento)))
                && texto != null
                && texto.trim().length() > 0;
    }

    public String getTexto() {
        return texto;
    }

    /** Ponto de ancoragem da anotação: à direita da âncora, na metade da altura (nunca acima de y=50). */
    public Point ancora() {
        if (item != null) {
            return new Point(item.x + item.largura, Math.max(50, item.y + item.altura / 2));
        }
        return new Point(elemento.x + elemento.largura, Math.max(50, elemento.y + elemento.altura / 2));
    }
}
