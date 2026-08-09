package gerard.interacao.arraste;

import gerard.campoaditivo.diagrama.elementos.ItemTextoArrastavel;

/**
 * Mantém o estado local do gesto de arraste de um item textual.
 *
 * Não conhece Swing, alvo semântico, sincronização, scaffolding ou logs. A
 * tela continua responsável por rotear os efeitos do gesto para esses
 * subsistemas; este handler é a fonte única apenas para pickup, deslocamento,
 * movimento e conclusão do gesto do item.
 */
public final class HandlerInteracaoItemTextoArrastavel {
    private ItemTextoArrastavel itemAtivo;
    private int deslocamentoX;
    private int deslocamentoY;
    private int origemX;
    private int origemY;

    public boolean iniciar(ItemTextoArrastavel item, int mouseX, int mouseY) {
        if (item == null) {
            cancelar();
            return false;
        }
        itemAtivo = item;
        deslocamentoX = mouseX - item.x;
        deslocamentoY = mouseY - item.y;
        origemX = item.x;
        origemY = item.y;
        return true;
    }

    public ItemTextoArrastavel moverPara(int mouseX, int mouseY) {
        if (itemAtivo == null) {
            return null;
        }
        itemAtivo.x = mouseX - deslocamentoX;
        itemAtivo.y = mouseY - deslocamentoY;
        return itemAtivo;
    }

    public ResultadoSoltura concluir() {
        ItemTextoArrastavel item = itemAtivo;
        boolean moveu = item != null
                && (item.x != origemX || item.y != origemY);
        cancelar();
        return new ResultadoSoltura(item, moveu);
    }

    public void cancelar() {
        itemAtivo = null;
        deslocamentoX = 0;
        deslocamentoY = 0;
        origemX = 0;
        origemY = 0;
    }

    public boolean estaAtivo() {
        return itemAtivo != null;
    }

    public ItemTextoArrastavel obterItemAtivo() {
        return itemAtivo;
    }

    public ItemTextoArrastavel identificarFoco(
            ItemTextoArrastavel candidato) {
        return candidato != null && candidato.estaNoDiagrama()
                ? candidato : null;
    }

    public static final class ResultadoSoltura {
        private final ItemTextoArrastavel item;
        private final boolean moveu;

        private ResultadoSoltura(ItemTextoArrastavel item, boolean moveu) {
            this.item = item;
            this.moveu = moveu;
        }

        public ItemTextoArrastavel getItem() {
            return item;
        }

        public boolean houveMovimento() {
            return moveu;
        }
    }
}
