package gerard.dominio.campoaditivo;

/**
 * Referência estável à tentativa de modelagem em curso. Quem precisa saber em que fase está a
 * atividade (logger, publicador de gestos, estado compartilhado, sincronizador do enunciado)
 * recebe uma referência de método deste objeto; a decisão pertence à tentativa, não à tela. A
 * tela apenas informa qual é a tentativa corrente quando uma situação é carregada.
 */
public final class TentativaCorrente {
    private volatile TentativaModelagemAditiva atual;

    public void definir(TentativaModelagemAditiva tentativa) {
        this.atual = tentativa;
    }

    /** Só a primeira modelagem gera registro factual; sem tentativa, nada o impede. */
    public boolean admiteRegistroFactual() {
        TentativaModelagemAditiva t = atual;
        return t == null || t.admiteRegistroFactual();
    }

    /** Exploração (recálculo de valores e dos números do enunciado) só após a conclusão correta. */
    public boolean admiteExploracao() {
        TentativaModelagemAditiva t = atual;
        return t != null && t.admiteExploracao();
    }
}
