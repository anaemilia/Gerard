package gerard.interacao.arraste;

import gerard.campoaditivo.diagrama.elementos.ItemTextoArrastavel;
import gerard.interacao.ResumoGestoArraste;
import java.util.UUID;

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
    private String gestoId;
    private int inicioPonteiroX;
    private int inicioPonteiroY;
    private int ultimoPonteiroX;
    private int ultimoPonteiroY;
    private int amostras;
    private int mudancasOrientacao;
    private String ultimaOrientacao;
    private double distanciaPercorrida;

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
        gestoId = UUID.randomUUID().toString();
        inicioPonteiroX = ultimoPonteiroX = mouseX;
        inicioPonteiroY = ultimoPonteiroY = mouseY;
        amostras = 0;
        mudancasOrientacao = 0;
        ultimaOrientacao = "";
        distanciaPercorrida = 0.0;
        return true;
    }

    public ItemTextoArrastavel moverPara(int mouseX, int mouseY) {
        if (itemAtivo == null) {
            return null;
        }
        observarMovimento(mouseX, mouseY);
        itemAtivo.x = mouseX - deslocamentoX;
        itemAtivo.y = mouseY - deslocamentoY;
        return itemAtivo;
    }

    public ResultadoSoltura concluir(int mouseX, int mouseY) {
        observarMovimento(mouseX, mouseY);
        ItemTextoArrastavel item = itemAtivo;
        boolean moveu = item != null
                && (item.x != origemX || item.y != origemY);
        ResumoGestoArraste gesto = houveDeslocamentoDoPonteiro()
                ? new ResumoGestoArraste(
                        gestoId, inicioPonteiroX, inicioPonteiroY,
                        ultimoPonteiroX, ultimoPonteiroY, amostras,
                        mudancasOrientacao, Math.round(distanciaPercorrida),
                        System.currentTimeMillis())
                : null;
        cancelar();
        return new ResultadoSoltura(item, moveu, gesto);
    }

    public ResultadoSoltura concluir() {
        return concluir(ultimoPonteiroX, ultimoPonteiroY);
    }

    public void cancelar() {
        itemAtivo = null;
        deslocamentoX = 0;
        deslocamentoY = 0;
        origemX = 0;
        origemY = 0;
        gestoId = "";
        inicioPonteiroX = 0;
        inicioPonteiroY = 0;
        ultimoPonteiroX = 0;
        ultimoPonteiroY = 0;
        amostras = 0;
        mudancasOrientacao = 0;
        ultimaOrientacao = "";
        distanciaPercorrida = 0.0;
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

    private void observarMovimento(int ponteiroX, int ponteiroY) {
        if (itemAtivo == null) {
            return;
        }
        int dx = ponteiroX - ultimoPonteiroX;
        int dy = ponteiroY - ultimoPonteiroY;
        if (dx == 0 && dy == 0) {
            return;
        }
        String orientacao = Math.abs(dx) >= Math.abs(dy)
                ? (dx >= 0 ? "DIREITA" : "ESQUERDA")
                : (dy >= 0 ? "BAIXO" : "CIMA");
        if (ultimaOrientacao.length() > 0
                && !ultimaOrientacao.equals(orientacao)) {
            mudancasOrientacao++;
        }
        ultimaOrientacao = orientacao;
        distanciaPercorrida += Math.sqrt((double) dx * dx + (double) dy * dy);
        amostras++;
        ultimoPonteiroX = ponteiroX;
        ultimoPonteiroY = ponteiroY;
    }

    private boolean houveDeslocamentoDoPonteiro() {
        return ultimoPonteiroX != inicioPonteiroX
                || ultimoPonteiroY != inicioPonteiroY;
    }

    public static final class ResultadoSoltura {
        private final ItemTextoArrastavel item;
        private final boolean moveu;
        private final ResumoGestoArraste gesto;

        private ResultadoSoltura(ItemTextoArrastavel item, boolean moveu,
                ResumoGestoArraste gesto) {
            this.item = item;
            this.moveu = moveu;
            this.gesto = gesto;
        }

        public ItemTextoArrastavel getItem() {
            return item;
        }

        public boolean houveMovimento() {
            return moveu;
        }

        public ResumoGestoArraste getGestoConcluido() {
            return gesto;
        }
    }
}
