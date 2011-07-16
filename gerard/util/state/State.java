/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gerard.util.state;

import gerard.banco.BancoSingleton;
import gerard.model.Configuracao;
import gerard.util.ComponenteDoDiagrama;
import gerard.util.SituacaoProblema;
import gerard.util.desenhos.Desenho;

public abstract class State {

    protected BancoSingleton banco;
    protected int vetorCodProblema[]=null;
    protected int indexCodProblemaAtual = 1;
    protected SituacaoProblema sp;
    protected int codProblema;

    public State(){
        banco = BancoSingleton.getInstancia();
    }
    protected int codProblemaRandom() {
        if (vetorCodProblema.length == 1) {
            return 0;
        }
        int numero = (int) (Math.random() * vetorCodProblema.length)+0;
        //para nao mostrar o mesmo problema consecutivo
        while (numero == this.indexCodProblemaAtual) {
            numero = (int) (Math.random() * vetorCodProblema.length)+0;
        }
        return numero;
    }

    public SituacaoProblema inicializar() {
        //Obter codigo dos problemas existentes no banco
        vetorCodProblema = this.banco.getCodProblemas(getTipoProblema());
        if (vetorCodProblema == null) {
            return null;//nao existe situações problemas no banco para o usuário resolver
        } else {
            this.indexCodProblemaAtual = this.codProblemaRandom();
            this.codProblema = this.vetorCodProblema[this.indexCodProblemaAtual];
            sp = this.banco.getSituacaoProblema(codProblema, getNumeroComponentesDaSP());
            this.inserirTabelaTentativa();
            return sp;
        }
    }

    public abstract String getTipoProblema();
    public abstract int getNumeroComponentesDaSP();

    public abstract ComponenteDoDiagrama getDiagrama(int diagrama, Desenho desenho);

    public void acertouCategorizacao(){

    }
    public void inserirTabelaTentativa(){}


    public void comecar() {
    }

    public void setPasso(int passo) {
    }

    public void setErroCategorização(int diagrama, String mensagem) {
    }

    public void exibirMaisDica(String mensagem) {
    }

    public void posicionamento(String mensagem, String elemento, int pos, String valor) {
    }

    public void sinal(String mensagem, String botao, String valor) {
    }

    public void botaoSIMouNao(String mensagem, String botao, String valor) {
    }

    public void computador(String mensagem) {
        //computador
    }

    public void digita(String mensagem, String text, String valor) {
    }

    public boolean salvarEstado(Configuracao get) {
       return true;
    }

    public Configuracao abrirEstado() {
        return null;
    }

    public SituacaoProblema getSituacaoProblema() {
        return null;
    }

    public void botaoDesfazer(String jOptionPaneTexto) {
        
    }

    public void botaoRefazer(String jOptionPaneTexto) {
        
    }


}
