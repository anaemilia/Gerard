package gerard.ui.enunciado.editor;

/**
 * Mantém o estado e a mecânica local do gesto de arrastar uma
 * {@link PecaPalavraRascunho} — mesmo papel que
 * {@code gerard.interacao.arraste.HandlerInteracaoElementoTextoMovel} cumpre
 * para o texto do enunciado durante o jogo, só que aqui a peça não se move
 * livremente: o próprio {@link GeometriaEditorNarrativa} recalcula a grade de
 * posições a cada quadro, então este handler só acompanha o ponteiro do
 * mouse (para desenhar um "fantasma" da peça sendo arrastada) e guarda de
 * onde ela partiu, para o controlador decidir o que fazer ao soltar.
 *
 * Também resolve a distinção entre clique e arraste: um clique parado (sem
 * deslocamento relevante do mouse) equivale ao botão "×" do protótipo web
 * (remove a peça da frase) ou ao clique de um saco (insere no fim da frase);
 * só um deslocamento real dispara a lógica de posição de soltura.
 */
public final class HandlerInteracaoPecaPalavraRascunho {
    private static final int LIMIAR_ARRASTE_PIXELS = 5;

    private PecaPalavraRascunho pecaAtiva;
    private String sacoOrigem;
    private int xInicial;
    private int yInicial;
    private int xAtual;
    private int yAtual;

    public boolean iniciar(PecaPalavraRascunho peca, String sacoOrigem, int mouseX, int mouseY) {
        if (peca == null || !peca.isManipulavel()) {
            cancelar();
            return false;
        }
        this.pecaAtiva = peca;
        this.sacoOrigem = sacoOrigem;
        this.xInicial = mouseX;
        this.yInicial = mouseY;
        this.xAtual = mouseX;
        this.yAtual = mouseY;
        return true;
    }

    public void moverPara(int mouseX, int mouseY) {
        if (pecaAtiva == null) {
            return;
        }
        xAtual = mouseX;
        yAtual = mouseY;
    }

    public boolean foiApenasClique() {
        int dx = xAtual - xInicial;
        int dy = yAtual - yInicial;
        return dx * dx + dy * dy <= LIMIAR_ARRASTE_PIXELS * LIMIAR_ARRASTE_PIXELS;
    }

    public boolean estaAtivo() {
        return pecaAtiva != null;
    }

    public PecaPalavraRascunho obterPecaAtiva() {
        return pecaAtiva;
    }

    public String obterSacoOrigem() {
        return sacoOrigem;
    }

    public int obterXAtual() {
        return xAtual;
    }

    public int obterYAtual() {
        return yAtual;
    }

    public PecaPalavraRascunho concluir() {
        PecaPalavraRascunho peca = pecaAtiva;
        cancelar();
        return peca;
    }

    public void cancelar() {
        pecaAtiva = null;
        sacoOrigem = null;
    }
}
