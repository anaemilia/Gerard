/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gerard.util.state;

import gerard.banco.Etapas;
import gerard.model.Configuracao;
import gerard.util.ComponenteDoDiagrama;
import gerard.util.ManipularProperties;
import gerard.util.SituacaoProblema;
import gerard.util.desenhos.Desenho;
import gerard.util.desenhos.DiagramaComparacaoDesenho;
import gerard.util.desenhos.DiagramaComposicaoDesenho;
import gerard.util.desenhos.DiagramaTransformacaoDesenho;

/**
 *
 * @author Kecia
 */
public class EstruturasAditivasHistorico extends State {

    private ManipularProperties mpHistorico = new ManipularProperties("gerard.propriedades.historico");
    private ManipularProperties mpAjudas = new ManipularProperties("gerard.propriedades.ajudas");
    private Etapas etapa = new Etapas();
    private String cpfUsuario;
    private int passo;
    private int codTentativas;

    public EstruturasAditivasHistorico(String cpfUsuario) {
        super();
        this.cpfUsuario = cpfUsuario;

    }

    @Override
    public String getTipoProblema() {
        return SituacaoProblema.aditiva;
    }

    @Override
    public int getNumeroComponentesDaSP() {
        return 3;
    }

    @Override
    public ComponenteDoDiagrama getDiagrama(int diagrama, Desenho desenho) {
        switch (diagrama) {
            case SituacaoProblema.COMPOSICAO:
                return new DiagramaComposicaoDesenho(desenho);

            case SituacaoProblema.TRANSFORMACAO:
                return new DiagramaTransformacaoDesenho(desenho);
            default:
                //SituacaoProblema.COMPARACAO:
                return new DiagramaComparacaoDesenho(desenho);
        }
    }

    public void acertouCategorizacao() {
        //usuario
        this.inserirTabelaEtapa(this.mpHistorico.replaceString(this.mpHistorico.getMensagem("botao." + sp.getCategoria()), this.mpHistorico.getMensagem("descricao.clicou")),
                etapa.VALOR_CERTO, this.mpHistorico.getMensagem("ator.usuario"), "0", "0", this.mpHistorico.getMensagem("botao." + sp.getCategoria()));
        //computador
        this.inserirTabelaEtapa(this.mpAjudas.getMensagem("passo." + this.passo), etapa.VALOR_CERTO,
                this.mpHistorico.getMensagem("ator.computador"), "0", "0", this.mpHistorico.getMensagem("janela.dialog"));
    }

    private String replaceHtml(String string) {
        String aux = string.replace("<html>", "");
        return aux.replace("</html>", "");
    }

    private void inserirTabelaEtapa(String descricao, String valor, String ator, String codErro, String codObjeto, String artefato) {

        etapa.setdescricao(this.replaceHtml(descricao).replace("\"", ""));
        etapa.setvalor(valor);
        etapa.setator(ator);
        etapa.setcodErro(codErro);
        etapa.setcodObjeto(codObjeto);

        etapa.setcodProblema(String.valueOf(this.codProblema));
        etapa.setcpf(this.cpfUsuario);
        etapa.setcodTentativa(String.valueOf(this.codTentativas));

        /*   String valores[] = {etapa.getdescricao(),
        etapa.getvalor(),
        etapa.getcodTentativa(),
        etapa.getator(),
        etapa.getcodErro(),
        etapa.getcodObjeto(),
        etapa.getcpf(),
        etapa.getcodProblema()
        };

         */


        this.banco.inserirEtapa(etapa, artefato);
    }

    @Override
    public void inserirTabelaTentativa() {
        codTentativas = this.banco.insertTentativa(cpfUsuario, codProblema);


    }

    public void comecar() {
        String mensagem = this.mpAjudas.getMensagem("passo.1");
        mensagem = this.replaceHtml(mensagem);
        //computador
        setTabelaEtapaAcaoComputador(mensagem);

    }

    public void setPasso(int passo) {
        this.passo = passo;
    }

    public void setErroCategorização(int diagrama, String mensagem) {
        //usuario
        this.inserirTabelaEtapa(this.mpHistorico.replaceString(this.mpHistorico.getMensagem("botao." + diagrama), this.mpHistorico.getMensagem("descricao.clicou")),
                etapa.VALOR_ERRADO, this.mpHistorico.getMensagem("ator.usuario"), "1", "0", this.mpHistorico.getMensagem("botao." + diagrama));
        //computador
        setTabelaEtapaAcaoComputador(mensagem);
    }

    public void exibirMaisDica(String mensagem) {
        //usuario
        this.inserirTabelaEtapa(this.mpHistorico.replaceString(this.mpHistorico.getMensagem("botao.maisdica"), this.mpHistorico.getMensagem("descricao.clicou")),
                etapa.VALOR_CERTO, this.mpHistorico.getMensagem("ator.usuario"), "0", "0", this.mpHistorico.getMensagem("botao.maisdica"));
        //computador
        setTabelaEtapaAcaoComputador(mensagem);
    }

    public void posicionamento(String mensagem, String elemento, int pos, String valor) {
        String posicao;
        int categoria = this.sp.getCategoria();
        posicao = this.mpHistorico.getMensagem("posicao." + pos + "." + categoria);
        String novaString[] = {elemento, posicao};
        String codErro = (valor.equalsIgnoreCase(etapa.VALOR_CERTO) ? "0" : "2");
        //usuario
        this.inserirTabelaEtapa(this.mpHistorico.replaceString(novaString, this.mpHistorico.getMensagem("descricao.arrastaElemento")),
                valor, this.mpHistorico.getMensagem("ator.usuario"), codErro, "0", posicao);
        //computador
        setTabelaEtapaAcaoComputador(mensagem);

    }

    public void sinal(String mensagem, String botao, String valor) {

        String codErro = (valor.equalsIgnoreCase(etapa.VALOR_CERTO) ? "0" : "3");
        //usuario
        this.inserirTabelaEtapa(this.mpHistorico.replaceString(this.mpHistorico.getMensagem("botao." + botao), this.mpHistorico.getMensagem("descricao.clicou")),
                valor, this.mpHistorico.getMensagem("ator.usuario"), codErro, "0", this.mpHistorico.getMensagem("botao." + botao));
        //computador
        setTabelaEtapaAcaoComputador(mensagem);
    }

    public void botaoSIMouNao(String mensagem, String botao, String valor) {
        String codErro = "0";//(valor.equalsIgnoreCase(etapa.VALOR_CERTO)?"0":"1");
        //usuario
        this.inserirTabelaEtapa(this.mpHistorico.replaceString(this.mpHistorico.getMensagem("botao." + botao), this.mpHistorico.getMensagem("descricao.clicou")),
                valor, this.mpHistorico.getMensagem("ator.usuario"), codErro, "0", this.mpHistorico.getMensagem("botao." + botao));
        //computador
        setTabelaEtapaAcaoComputador(mensagem);
    }

    public void computador(String mensagem) {
        //computador
        setTabelaEtapaAcaoComputador(mensagem);
    }

    @Override
    public void digita(String mensagem, String text, String valor) {

        String codErro = (valor.equalsIgnoreCase(etapa.VALOR_CERTO) ? "0" : "4");
        //usuario
        this.inserirTabelaEtapa(this.mpHistorico.replaceString(text, this.mpHistorico.getMensagem("descricao.digita")),
                valor, this.mpHistorico.getMensagem("ator.usuario"), codErro, "0", "");
        //computador
        setTabelaEtapaAcaoComputador(mensagem);
    }

    public void botaoDesfazer(String mensagem) {
        String botao = this.mpHistorico.getMensagem("botao.desfazer");
        String acao = this.mpHistorico.getMensagem("descricao.clicou");

        //usuario
        this.inserirTabelaEtapa(this.mpHistorico.replaceString(botao, acao),
                "0", this.mpHistorico.getMensagem("ator.usuario"), "0", "0", "");
        //computador
        setTabelaEtapaAcaoComputador(mensagem);
    }

    public void botaoRefazer(String mensagem) {
        String botao = this.mpHistorico.getMensagem("botao.refazer");
        String acao = this.mpHistorico.getMensagem("descricao.clicou");

        //usuario
        this.inserirTabelaEtapa(this.mpHistorico.replaceString(botao, acao),
                "0", this.mpHistorico.getMensagem("ator.usuario"), "0", "0", "");
        //computador
        setTabelaEtapaAcaoComputador(mensagem);
    }

    @Override
    public boolean salvarEstado(Configuracao conf) {
        return this.banco.salvarEstado(this.cpfUsuario, this.codProblema, this.codTentativas, conf);
    }

    public Configuracao abrirEstado() {
        Configuracao aux = this.banco.abrirEstado(cpfUsuario, getTipoProblema());
        return aux;
    }

    public SituacaoProblema getSituacaoProblema() {
        int codProblemaTemp = this.banco.getCodProblemaTemporario();
        // this.sp=
        SituacaoProblema aux = this.banco.getSituacaoProblema(codProblemaTemp, 3);
        if (aux != null) {
            this.sp = aux;
            this.codTentativas = this.banco.getCodTentativaTemporario();
            return this.sp;
        }


        return null;
    }

    public void setTabelaEtapaAcaoComputador(String mensagem) {
        this.inserirTabelaEtapa(mensagem, etapa.VALOR_CERTO,
                this.mpHistorico.getMensagem("ator.computador"), "0", "0", this.mpHistorico.getMensagem("janela.dialog"));
    }
}
