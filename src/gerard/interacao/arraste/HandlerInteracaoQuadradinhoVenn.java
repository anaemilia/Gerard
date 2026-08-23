package gerard.interacao.arraste;

import gerard.campoaditivo.diagrama.elementos.CirculoVenn;
import gerard.campoaditivo.diagrama.elementos.QuadradinhoVenn;

import java.util.List;

/**
 * Fase 7.5 do roteiro de {@code gerard-handlers-de-interacao}: extrai da
 * tela principal o estado mecânico do gesto de reposicionar um
 * {@link QuadradinhoVenn} já colocado no diagrama complementar (Venn) —
 * pickup, deslocamento livre (sem limiar estrutural nem zona de clamp, mesmo
 * comportamento de antes da extração) e detecção de qual {@link CirculoVenn}
 * continha o quadradinho no início e no fim do gesto.
 *
 * Fronteira de conhecimento (mesma divisão das Fases 7.2/7.3/7.4): este
 * handler só sabe mecânica do gesto. Não decide se um movimento de círculo
 * para outro é válido nem o que fazer com isso — {@link ResultadoSoltura}
 * devolve os fatos (houve movimento? de qual índice de círculo para qual?)
 * e quem chama decide a sincronização semântica correspondente
 * ({@code sincronizarTodasAsRepresentacoesAPartirDoDiagramaComplementar}),
 * exatamente como {@code HandlerInteracaoItemTextoArrastavel.ResultadoSoltura}
 * já faz para o protocolo do item textual.
 *
 * Deliberadamente FORA do escopo deste handler (permanecem na tela
 * principal): hit-testing do quadradinho (localizar qual quadradinho está
 * sob o ponteiro), o hover/foco fora de um arraste
 * (quadradinhoVennFocado, atualizado em mouseMoved e usado por
 * duplo-clique/tecla Delete — não é gesto de arraste), os controles de
 * clique de adicionar/remover quadradinho (affordance semântica
 * compartilhada com outras representações complementares, não exclusiva de
 * quadradinhos do Venn), duplo-clique para editar texto, e a própria
 * sincronização semântica.
 */
public final class HandlerInteracaoQuadradinhoVenn {

    private QuadradinhoVenn quadradinhoAtivo;
    private int deslocamentoX;
    private int deslocamentoY;
    private int indiceCirculoOrigem = -1;

    /**
     * Inicia o gesto, se {@code quadradinho} não for nulo. Calcula o
     * deslocamento entre o ponteiro e o canto do quadradinho (mesma
     * convenção de antes da extração) e localiza, dentre {@code circulos},
     * qual já continha o centro do quadradinho — guardado para servir de
     * fallback em {@link #concluir}, caso o quadradinho termine fora de
     * qualquer círculo.
     *
     * @return true se o gesto foi iniciado
     */
    public boolean iniciar(QuadradinhoVenn quadradinho, int x, int y, List<CirculoVenn> circulos) {
        if (quadradinho == null) {
            return false;
        }
        this.quadradinhoAtivo = quadradinho;
        this.deslocamentoX = x - quadradinho.x;
        this.deslocamentoY = y - quadradinho.y;
        this.indiceCirculoOrigem = encontrarIndiceCirculo(quadradinho, circulos);
        return true;
    }

    /**
     * Move o quadradinho ativo livremente para a posição do ponteiro — sem
     * limiar de arraste estrutural nem clamp por zona, mesmo comportamento
     * já existente antes desta extração (o quadradinho fica dentro do
     * painel complementar por convenção de uso, não por uma restrição
     * geométrica aplicada aqui).
     *
     * @return true se havia um quadradinho ativo (e ele foi movido)
     */
    public boolean mover(int x, int y) {
        if (quadradinhoAtivo == null) {
            return false;
        }
        quadradinhoAtivo.x = x - deslocamentoX;
        quadradinhoAtivo.y = y - deslocamentoY;
        return true;
    }

    /**
     * Encerra o gesto normalmente (soltura do mouse): localiza o círculo de
     * destino (mesma busca geométrica de {@link #iniciar}) e devolve os
     * fatos do gesto. Sempre cancela o estado interno antes de retornar,
     * mesmo quando não havia gesto ativo (nesse caso devolve um resultado
     * "sem movimento").
     */
    public ResultadoSoltura concluir(List<CirculoVenn> circulos) {
        boolean houveMovimento = quadradinhoAtivo != null;
        int indiceDestino = houveMovimento ? encontrarIndiceCirculo(quadradinhoAtivo, circulos) : -1;
        int indiceOrigem = indiceCirculoOrigem;
        cancelar();
        return new ResultadoSoltura(houveMovimento, indiceOrigem, indiceDestino);
    }

    /** Cancela o gesto sem produzir resultado — reset defensivo (início de mousePressed, troca de situação, etc.). */
    public void cancelar() {
        quadradinhoAtivo = null;
        deslocamentoX = 0;
        deslocamentoY = 0;
        indiceCirculoOrigem = -1;
    }

    public boolean estaAtivo() {
        return quadradinhoAtivo != null;
    }

    public QuadradinhoVenn obterQuadradinhoAtivo() {
        return quadradinhoAtivo;
    }

    private static int encontrarIndiceCirculo(QuadradinhoVenn quadradinho, List<CirculoVenn> circulos) {
        if (quadradinho == null || circulos == null) {
            return -1;
        }
        for (int i = 0; i < circulos.size(); i++) {
            CirculoVenn circulo = circulos.get(i);
            if (circulo != null && circulo.contem(quadradinho.centroX(), quadradinho.centroY())) {
                return i;
            }
        }
        return -1;
    }

    /** Fatos do gesto encerrado — quem chama decide o que fazer com eles. */
    public static final class ResultadoSoltura {
        private final boolean houveMovimento;
        private final int indiceOrigem;
        private final int indiceDestino;

        ResultadoSoltura(boolean houveMovimento, int indiceOrigem, int indiceDestino) {
            this.houveMovimento = houveMovimento;
            this.indiceOrigem = indiceOrigem;
            this.indiceDestino = indiceDestino;
        }

        public boolean houveMovimento() {
            return houveMovimento;
        }

        public int getIndiceOrigem() {
            return indiceOrigem;
        }

        public int getIndiceDestino() {
            return indiceDestino;
        }

        /**
         * Índice de círculo a usar para sincronizar as representações:
         * o de destino, se o quadradinho terminou dentro de um círculo;
         * senão o de origem — mesma regra de fallback já usada em
         * o processamento da soltura do mouse, antes desta extração.
         */
        public int obterIndiceParaSincronizacao() {
            return indiceDestino >= 0 ? indiceDestino : indiceOrigem;
        }
    }
}
