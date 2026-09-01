package gerard.interacao.arraste;

/**
 * Porta portatil entre o protocolo de interacao e uma representacao concreta
 * de eixo dos inteiros. A implementacao visual possui a geometria e o
 * hit-testing; o handler possui somente o ciclo pressionar-mover-concluir.
 */
public interface AlvoInteracaoEixoInteiros {

    enum NaturezaInteracao {
        NENHUMA,
        COMPONENTE_VISUAL,
        VALOR_SEMANTICO
    }

    enum ModoManipulacao {
        NENHUM,
        PAINEL,
        PONTO_CONTROLE
    }

    NaturezaInteracao identificarNatureza(int posicaoX, int posicaoY);

    boolean processarPressionamento(int posicaoX, int posicaoY);

    ModoManipulacao obterModoManipulacao();

    boolean foiOcultadoPorInteracao();

    boolean houveAlteracaoValorPorInteracao();

    void moverPara(int posicaoX, int posicaoY);

    void finalizarManipulacao();
}
