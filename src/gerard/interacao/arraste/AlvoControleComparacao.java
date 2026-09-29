package gerard.interacao.arraste;

/**
 * Porta do hit-test do ponto de controle e da escala da barra de Comparação
 * de Medidas (Fase 7.12). A geometria concreta (Rectangle, eixo, alturas)
 * permanece no adaptador da plataforma.
 */
public interface AlvoControleComparacao {

    /** @return true se a posição está sobre o ponto de controle ou a escala. */
    boolean contemControleOuEscala(int posicaoX, int posicaoY);
}
