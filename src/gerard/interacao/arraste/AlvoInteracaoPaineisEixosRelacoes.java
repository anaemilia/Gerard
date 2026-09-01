package gerard.interacao.arraste;

/**
 * Porta portatil entre o protocolo de interacao e a representacao desktop
 * dos paineis de eixo associados aos papeis de Relacoes.
 *
 * O handler conhece somente a sequencia pressionar-mover-concluir. A
 * implementacao da porta permanece responsavel pela geometria, hit-testing e
 * estado visual da plataforma.
 */
public interface AlvoInteracaoPaineisEixosRelacoes {

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

    void moverPara(int posicaoX, int posicaoY);

    void finalizarManipulacao();
}
