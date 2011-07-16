package gerard.model;

import gerard.banco.Etapas;
import gerard.util.DesenharObserver;
import gerard.util.DesfazerRefazerObserver;
import gerard.util.MensagensObserver;
import gerard.util.MensagensAjuda;
import gerard.util.ComponenteDoDiagrama;
import gerard.util.ComponenteDaSP;
import gerard.util.ManipularProperties;
import gerard.util.MensagensAjudasInterface;
import gerard.util.SituacaoProblema;
import gerard.util.desenhos.Desenho;
import gerard.util.desenhos.FaixaDesenho;
import gerard.util.desenhos.QuadroSelecaoDesenho;
import gerard.util.desenhos.SituacaoProblemaDesenho;
import gerard.util.desenhos.ValorDesenho;
import gerard.util.state.State;
import gerard.util.state.EstruturasAditivas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Shape;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

public class GerardModel implements GerardModelInterface {

    private ArrayList joptionPaneObserver;
    private ArrayList desenharObserver;
    private ArrayList desfazerRefazerObserver;
    private Desenho desenho;//conjunto de desenhos (Decorator patterns) atual
    private Desenho backup;//salva o desenho anterior
    // private ArrayList<Desenho> desenhoArray;
    //  private ArrayList<Desenho> backupArray;
    private ComponenteDoDiagrama diagrama;//diagramas (Ex: diagrama de composicao,transformacao..)
    //Mensagens a serem exibidas
    private MensagensAjudasInterface mensagens;
    //Selecao dos valores para o usuario arrastar
    private Rectangle2D.Double quadradoSelecao;
    private ValorDesenho valorSelecao;
    //Ordem para desenhar as partes selecionadas
    private int[] posicoesSelecao = new int[3];
    private int contadorPosicoesSelecao = 0;
    private String jOptionPaneTexto;//mensagens a serem exibidas
    //Situacao-problema atual com os respectivos atributos utilizados
    private SituacaoProblema correntSp;
    //Passo atual do usuário (Ex: categorização, composicao do diagrama..)
    private int passo = 0;
    //Numero de componentes da siutacao problema e consequente numero de componentes do diagrama correspondente
    private int numeroComponentes = 0;
    //Numero de vezes que o usuario tem que arrastar elementos ate o proximo passo ser incrementado
    private int contadorDragDrop = 0;
    // Ponto onde deve ser digitado o resultado;
    private Point pontoResultado;
    //private State estadoAtual = null;
    private State estado;
    //manipular arquivo de propriedades
    private ManipularProperties mp;
    //contador de erros consecutivos
    private int contadorDeErrosConsecutivos = 0;
    //configuração para abrir e salvar no banco
    private ArrayList<Configuracao> configuracao;
    //contado das configuracoes
    private int contadorCongfiguracao;
    //para desenhar siutacao problema quando o usuario abrir um estado salvo
    private SituacaoProblemaDesenho situacaoProblemaDesenho;
    private Graphics2D g2d;//usado para abrir um estado salvo
    private Dimension dimension;//usado para abrir um estado salvo
    //para desenhar faixa quando o usuario abrir um estado salvo
    private FaixaDesenho faixaDesenho;
//private   quadradoSelecaoDesenho=null;
    private int testando = 0;

    public GerardModel() {
        mp = new ManipularProperties("gerard.propriedades.historico");
        mensagens = new MensagensAjuda();
        joptionPaneObserver = new ArrayList();
        desenharObserver = new ArrayList();
        this.desfazerRefazerObserver = new ArrayList();
        passo = 1;
        this.correntSp = null;
        this.estado = new EstruturasAditivas();
        configuracao = new ArrayList<Configuracao>();
        //       backup=new ArrayList<Desenho>();
        //inicializar();
    }

    public void incrementarPasso() {

        this.passo++;
        this.estado.setPasso(this.passo);
    }

    public void incrementarContadorSelecao() {
        this.contadorPosicoesSelecao++;
    }

    public void inicializar() {
        this.contadorDeErrosConsecutivos = 0;
        this.passo = 1;
        jOptionPaneTexto = mensagens.proximoPasso(1);//primeira mensagem a ser exibida
        this.desenho = null;
        this.backup = null;
        this.diagrama = null;
        this.quadradoSelecao = null;
        this.valorSelecao = null;
        this.correntSp = this.estado.inicializar();
        configuracao.clear();
        this.notfyDesfazerObserver(false);
        this.notfyRefazerObserver(false);
        //   this.backup.clear();

        if (this.correntSp != null) {
            this.numeroComponentes = this.correntSp.getNumeroComponentes();
            posicoesSelecao = new int[this.numeroComponentes];
            contadorPosicoesSelecao = 0;

            this.contadorDragDrop = this.numeroComponentes;
            this.passo = 1;
            DesenharObserver jopo = (DesenharObserver) this.desenharObserver.get(0);
            jopo.updateInicio();
        //    this.notifyJOptionObservers(1);
           // this.estado.comecar();

            backup = this.desenho;
            ///para FazerDesfazer
            this.contadorCongfiguracao = -1;
            this.novaConfiguracao();


        } else {
            this.jOptionPaneTexto = ManipularProperties.getMensagemEm("semDadosBanco", "gerard.propriedades.avisos2");
            this.notifyJOptionObservers(1);
        }


    }

    public SituacaoProblema getSituacaoProblema() {
        return this.correntSp;
    }

    public void validarDiagrama(int diagrama) {
        if (diagrama == this.correntSp.getCategoria()) {//o usuario acertou a categorização
            //this.notifyDesenharDiagrama();
            incrementarPasso();
            this.jOptionPaneTexto = this.mensagens.proximoPasso(passo);
            this.diagrama = this.estado.getDiagrama(diagrama, this.desenho);
            this.desenho = (Desenho) this.diagrama;
            this.estado.acertouCategorizacao();
            this.notifyDesenharObserver();//avisa os observer para desenhar o diagrama
            this.notifyJOptionObservers(1);//mensagemOk
            backup = this.desenho;//guarda os desenhos sem a seleceção das partes
            //calcula qual das partes do texto será exibida primeiro
            this.posicoesSelecao = calcularOrdemSelecao(this.correntSp);
            //mostrar selecao
            desenharSelecao();
            this.novaConfiguracao();
            this.notifyDesenharObserver();
            //salvar no banco caso o usuario esteja logado
            this.errosConsecutivos(0);
            this.notfyDesfazerObserver(true);
            this.notfySalvarObserver(true);
            
        } else {//o usurario cometeu erro de categorizacao

            this.jOptionPaneTexto = mensagens.erroCategorizacao(diagrama);
            this.estado.setErroCategorização(diagrama, this.jOptionPaneTexto);

            this.notifyJOptionObservers(3);//mensagemSIMouNAO
            exibirProximoPasso();

            this.errosConsecutivos(-1);
        }
    }

    public void exibirProximoPasso() {
        this.jOptionPaneTexto = this.mensagens.proximoPasso(passo);
        if (this.passo == 4) {
            this.notifyJOptionObservers(3);
        } else {
            this.notifyJOptionObservers(1);
        }
    }

    public void exibirMaisDica() {
        this.jOptionPaneTexto = this.mensagens.maisDica(passo, this.correntSp.getCategoria());
        this.estado.exibirMaisDica(jOptionPaneTexto);
        if (passo == 1) {
            this.notifyJOptionObservers(3);
        } else {
            this.notifyJOptionObservers(1);
        }
        // exibirProximoPasso();

    }

    public int getProximoPasso() {
        return this.passo;
    }

    public void validarResultado(String text) {
        int posicaoAtual = this.posicoesSelecao[this.contadorPosicoesSelecao];
        ComponenteDaSP parteTexto = this.correntSp.getComponenteSpAt(posicaoAtual);
        Shape parteDiagrama = this.diagrama.getShapeAt(posicaoAtual);
        int posX = parteDiagrama.getBounds().x;
        int posY = parteDiagrama.getBounds().y;
        int width = parteDiagrama.getBounds().width;
        int height = parteDiagrama.getBounds().height;
        posX = posX + (width - parteTexto.getWidth()) / 2;
        posY = posY - 4 + (height + this.correntSp.getHeight()) / 2;
        if (text.equalsIgnoreCase(this.correntSp.getResposta())) {
            //acertou se for um valor natural. se for relativo falta o sinal
            if (parteTexto.getRepresentacao() == ComponenteDaSP.CIRCULO) {
                //if (posicaoAtual == 1) {
                //for numero relativo tem que digitar o sinal
                this.jOptionPaneTexto = this.mensagens.proximoPasso(5);

                this.estado.digita(this.jOptionPaneTexto, text, Etapas.VALOR_ERRADO);
                this.notifyJOptionObservers(2);



                //chegar aqui significa acertar o sinal
                this.desenho = new ValorDesenho(this.backup, parteTexto.getSinal()+ text, posX, posY, parteTexto.getWidth(), this.correntSp.getHeight(), Color.black);
            } else {
                this.desenho = new ValorDesenho(this.backup, text, posX, posY, parteTexto.getWidth(), this.correntSp.getHeight(), Color.black);
            }

            this.notifyDesabilitarJtextField();
            this.notifyDesenharObserver();

            this.incrementarPasso();
            this.estado.computador(this.mensagens.proximoPasso(passo));
            this.exibirProximoPasso();
            this.backup = this.desenho;
            this.novaConfiguracao();

        } else if (text.equalsIgnoreCase(parteTexto.getSinal() + this.correntSp.getResposta())) {
            //acertou e nao falta o sinal
            this.desenho = new ValorDesenho(this.backup, text, posX, posY, parteTexto.getWidth(), this.correntSp.getHeight(), Color.black);

            this.notifyDesabilitarJtextField();
            this.notifyDesenharObserver();


            this.incrementarPasso();
            this.exibirProximoPasso();

            this.estado.digita(this.jOptionPaneTexto, text, Etapas.VALOR_ERRADO);

            this.backup = this.desenho;
            this.novaConfiguracao();


        } else { //errou
            this.jOptionPaneTexto = this.mensagens.erroCalculo();
            this.notifyJOptionObservers(1);

            this.estado.digita(this.jOptionPaneTexto, text, Etapas.VALOR_ERRADO);
            this.errosConsecutivos(-1);

        }

    }

    /* public void inicio() {
    String questao = "";
    this.contadorQuestao++;


    switch (contadorQuestao) {
    case 0:
    questao = "Ingrid tem 12 reais a mais que Ligiane. Se Ligiane possui 27 reais. Quantos reais Ingrid tem ?";
    this.correntSp = new SituacaoProblema(SituacaoProblema.COMPARACAO, questao, 3, "27", "12", "?", "39", 2, 11, 17, "+");
    break;

    case 4:
    questao = "Glaudenice tem 14 reais a menos que Nadia. Nadia tem 27 reais. Quantos reais Glaudenice tem ?";
    this.correntSp = new SituacaoProblema(SituacaoProblema.COMPARACAO, questao, 3, "?", "14", "27", "13", 16, 2, 10, "-");
    break;


    case 5:
    questao = "Rafael tinha 10 refrigerantes e João tomou 6 deles. Quantos refrigerantes restaram ?";
    this.correntSp = new SituacaoProblema(SituacaoProblema.TRANSFORMACAO, questao, 3, "10", "6", "?", "4", 2, 7, 12, "-");
    break;


    case 6:


    questao = "Numa sala existem 7 garotos e 12 garotas. Quantas pessoas existem ao todo ?";
    this.correntSp = new SituacaoProblema(SituacaoProblema.COMPOSICAO, questao, 3, "7", "12", "?", "19", 3, 6, 13, " ");
    break;

    case 8:
    questao = "Antonio tem 3 carros a menos que Silvio, sendo que silvio tem 5 carros. Quantos carros Antonio tem ?";
    this.correntSp = new SituacaoProblema(SituacaoProblema.COMPARACAO, questao, 3, "5", "3", "?", "2", 12, 2, 18, "-");
    // this.diagrama =  new DiagramaTransformacao(this.correntSp,2);


    break;

    case 9:
    questao = "Numa mesa existem ao todo 7 pessoas. 3 delas são mulheres. Quantos homens existem na mesa ?";
    SituacaoProblema aux = new SituacaoProblema(SituacaoProblema.COMPOSICAO, questao, 3, "3", "?", "7", "4", 7, 16, 5, " ");
    this.contadorQuestao = 0;
    break;
    default:
    break;

    }
    }*/
    //JOptionObservers
    public void registerObserver(MensagensObserver o) {
        this.joptionPaneObserver.add(o);

    }

    public void removeObserver(MensagensObserver o) {
        int i = this.joptionPaneObserver.indexOf(o);
        if (i >= 0) {
            this.joptionPaneObserver.remove(i);
        }
    }

    public void notifyJOptionObservers(int tipo) {

        for (int i = 0; i < joptionPaneObserver.size(); i++) {

            MensagensObserver jopo = (MensagensObserver) joptionPaneObserver.get(i);
            switch (tipo) {
                case 1:
                    jopo.mostrarMensagemOk();
                    break;
                case 2:
                    jopo.mensagemDigitarSinal();
                    break;
                case 3:
                    jopo.mostrarMensagemSimNao();
                    break;
            }
        }

    }

    public String getJOptionPaneTexto() {
        return this.jOptionPaneTexto;
    }

    //desenharObserver
    public void registerObserver(DesenharObserver o) {
        this.desenharObserver.add(o);
    }

    public void removeObserver(DesenharObserver o) {
        int i = this.desenharObserver.indexOf(o);
        if (i >= 0) {
            this.desenharObserver.remove(i);
        }
    }

    public void notifyDesenharObserver() {
        for (int i = 0; i < this.desenharObserver.size(); i++) {

            DesenharObserver jopo = (DesenharObserver) this.desenharObserver.get(i);
            jopo.updateDesenhar();
        }
    }

    public void notifyDesabilitarJtextField() {
        for (int i = 0; i < this.desenharObserver.size(); i++) {

            DesenharObserver jopo = (DesenharObserver) this.desenharObserver.get(i);
            jopo.updateDesabilitarJtextfile();
        }
    }

    public void notifyDigitarResultado() {
        for (int i = 0; i < this.desenharObserver.size(); i++) {

            DesenharObserver jopo = (DesenharObserver) this.desenharObserver.get(i);
            jopo.updateDigitarResultado();
        }
    }

    public void notfyAtualizarVariaveis() {
        for (int i = 0; i < this.desenharObserver.size(); i++) {

            DesenharObserver jopo = (DesenharObserver) this.desenharObserver.get(i);
            jopo.atualizarVariaveis();
        }
    }


    public void notifyMouseListener(boolean valor) {
        for (int i = 0; i < this.desenharObserver.size(); i++) {

            DesenharObserver jopo = (DesenharObserver) this.desenharObserver.get(i);
            jopo.setMouseListener(valor);
        }
    }

    public Desenho getDesenho() {
        return this.desenho;
    }

    public void setSP(SituacaoProblema aux) {
        //atualiza o valor da variavel correntSP (SituacaoProblema) com as
        //localizações (x,y) das partes do texto(situacao-problema) depois do
        //texto ser desenhado na tela;
        this.correntSp = aux;
    }

    public void setDesenho(Desenho desenho) {
        this.desenho = desenho;
    }

    public Rectangle2D.Double getQuadradoSelecao() {
        return this.quadradoSelecao;
    }

    public ValorDesenho getValorSelecionado() {
        return this.valorSelecao;
    }

    private void desenharSelecao() {
        //atualiza vairavel desenho para mostrar a seleção do icone a ser arrastado pelo usuario

        //Obtem qual parte do texto sera selecionado
        ComponenteDaSP parte = this.correntSp.getComponenteSpAt(this.posicoesSelecao[this.contadorPosicoesSelecao]);
        //cria desenho

        this.valorSelecao = new ValorDesenho(this.desenho, parte.getTexto(), parte.getPoint().x, parte.getPoint().y, parte.getWidth(), this.correntSp.getHeight(), Color.blue);
        //atualiza variavel desenho

        this.desenho = this.valorSelecao;

        //cria desenho do quadrado para selecao
        QuadroSelecaoDesenho quadradoSelecaoDesenho = new QuadroSelecaoDesenho(this.desenho, parte.getPoint().x, parte.getPoint().y + 5 - this.correntSp.getHeight(), parte.getWidth(), this.correntSp.getHeight());

        this.quadradoSelecao = quadradoSelecaoDesenho.getQuadroSelecao();
        //atualiza variavel desenho

        this.desenho = quadradoSelecaoDesenho;
        //notifica a view para desenhar na tela


        //  this.notifyDesenharObserver();


    }

    public void validarPosicionamento(int x, int y) {
        //verifica se o clique do usuario (x,y) corresponde a parte do diagrama correta
        //onde o vetor ComponenteDaSP(0) corresponde ao diagrama.getShapeAt(0)..etc

        int posicaoAtual = this.posicoesSelecao[this.contadorPosicoesSelecao];
        ComponenteDaSP parteTexto = this.correntSp.getComponenteSpAt(posicaoAtual);
        Shape parteDiagrama = this.diagrama.getShapeAt(posicaoAtual);
        if (parteDiagrama.contains(x, y)) {
            //acertou posicionamento
            int posX = parteDiagrama.getBounds().x;
            int posY = parteDiagrama.getBounds().y;
            int width = parteDiagrama.getBounds().width;
            int height = parteDiagrama.getBounds().height;

            if (--this.contadorDragDrop == 0) {//2,1,0..
                //se nao tem mais elemento para arrastar
//                this.estado.setDigitarResultado();
                this.incrementarPasso();

                this.estado.posicionamento(this.mensagens.proximoPasso(passo), parteTexto.getTexto(), posicaoAtual + 1, Etapas.VALOR_CERTO);

                this.desenho = this.backup;
                this.notifyDesenharObserver();
                this.pontoResultado = new Point(posX + 14, posY + height / 2);
                this.notifyDigitarResultado();
            } else { //ainda tem elemento para ser arrastado

                posX = posX + (width - parteTexto.getWidth()) / 2;
                posY = posY - 4 + (height + this.correntSp.getHeight()) / 2;
                this.desenho = new ValorDesenho(this.backup, parteTexto.getTexto(), posX, posY, parteTexto.getWidth(), this.correntSp.getHeight(), Color.black);
                this.notifyDesenharObserver();
                if (parteTexto.getRepresentacao() == ComponenteDaSP.CIRCULO) {

                    //se for numero relativo precisa digitar o sinal
                    this.jOptionPaneTexto = this.mensagens.proximoPasso(5);

                    this.estado.posicionamento(this.jOptionPaneTexto, parteTexto.getTexto(), posicaoAtual + 1, Etapas.VALOR_CERTO);

                    this.notifyJOptionObservers(2);

                    this.desenho = new ValorDesenho(this.backup, parteTexto.getSinal() + parteTexto.getTexto(), posX, posY, parteTexto.getWidth(), this.correntSp.getHeight(), Color.black);

                } else {
                    this.estado.posicionamento(this.mensagens.proximoPasso(passo), parteTexto.getTexto(), posicaoAtual + 1, Etapas.VALOR_CERTO);

                }
                this.incrementarContadorSelecao();
                this.backup = desenho;
                this.desenharSelecao();


                this.notifyDesenharObserver();

            }
            this.novaConfiguracao();
            this.exibirProximoPasso();

            this.errosConsecutivos(0);


        } else {
            //errou posicionamento
            this.jOptionPaneTexto = this.mensagens.erroPosicionamento();
            this.notifyJOptionObservers(1);
            this.desenho = this.backup;
            this.desenharSelecao();
            this.notifyDesenharObserver();

            int i;
            for (i = 0; i < this.correntSp.getNumeroComponentes(); i++) {
                parteDiagrama = this.diagrama.getShapeAt(i);
                if (parteDiagrama.contains(x, y)) {
                    break;
                }
            }
            this.estado.posicionamento(this.jOptionPaneTexto, parteTexto.getTexto(), i + 1, Etapas.VALOR_ERRADO);
            this.errosConsecutivos(-1);
        }
    }//fim do metodo validar posicionamento

    public void validarSinal(String sinal) {

        int posicaoAtual = this.posicoesSelecao[this.contadorPosicoesSelecao];
        ComponenteDaSP parteTexto = this.correntSp.getComponenteSpAt(posicaoAtual);
        String botao = sinal.equalsIgnoreCase("+") ? "mais" : "menos";

        if (!parteTexto.getSinal().equalsIgnoreCase(sinal)) {

            //errou
            this.jOptionPaneTexto = this.mensagens.erroSinal();

            this.estado.sinal(this.jOptionPaneTexto, botao, Etapas.VALOR_ERRADO);

            this.notifyJOptionObservers(1);

            this.jOptionPaneTexto = this.mensagens.proximoPasso(5);
            this.estado.computador(this.jOptionPaneTexto);

            this.notifyJOptionObservers(2);

            this.errosConsecutivos(-1);
        } else //acertou
        {
            this.estado.sinal(this.mensagens.proximoPasso(passo), botao, Etapas.VALOR_CERTO);
            this.errosConsecutivos(0);
        }


    }

    public Point getPontoResultado() {
        return this.pontoResultado;
    }

    private int[] calcularOrdemSelecao(SituacaoProblema sp) {
        int tamanho = sp.getNumeroComponentes();
        int v[] = new int[tamanho];
        for (int i = 0; i < tamanho; i++) {
            v[i] = sp.getComponenteSpAt(i).getPosicao();
        }
        int cont, aux, cont2 = 0, troca = 1;
        for (cont2 = 0; cont2 < tamanho - 1 && troca == 1; cont2++) {
            for (cont = 0, troca = 0; cont < tamanho - cont2 - 1; cont++) {
                if (v[cont] > v[cont + 1]) {
                    troca = 1;
                    aux = v[cont + 1];
                    v[cont + 1] = v[cont];
                    v[cont] = aux;
                }
            }
        }
        int posicoes[] = new int[tamanho];
        for (cont = 0; cont < tamanho; cont++) {
            for (cont2 = 0; cont2 < tamanho; cont2++) {
                if (v[cont] == sp.getComponenteSpAt(cont2).getPosicao()) {
                    posicoes[cont] = cont2;
                }
            }
        }
        return posicoes;

    }

    public void setEstado(State estado) {

        this.estado = estado;
        this.inicializar();
    }

    public void verificarSIMeNAO(String botao) {
        if (this.passo != 4) {
            String mensagem = this.mensagens.erroCategorizacao(this.correntSp.getCategoria());
            if (!this.jOptionPaneTexto.equalsIgnoreCase(mensagem)) {
                if (botao.equalsIgnoreCase("sim")) {
                    //errou
                    this.estado.botaoSIMouNao(this.jOptionPaneTexto, botao, Etapas.VALOR_ERRADO);

                    this.notifyJOptionObservers(3);
                } else {
                    //acertou
                    this.estado.botaoSIMouNao(this.mensagens.proximoPasso(passo), botao, Etapas.VALOR_CERTO);
                }

            } else if (this.jOptionPaneTexto.equalsIgnoreCase(mensagem)) {
                if (botao.equalsIgnoreCase("nao")) {
                    //errou
                    this.estado.botaoSIMouNao(this.jOptionPaneTexto, botao, Etapas.VALOR_ERRADO);

                    this.notifyJOptionObservers(3);
                } else {
                    //acertou
                    this.estado.botaoSIMouNao(this.mensagens.proximoPasso(passo), botao, Etapas.VALOR_CERTO);
                }
            }
        }//fim do  if (this.passo != 4) {
        else {
            if (botao.equalsIgnoreCase("sim")) {
                this.inicializar();
            } else {
                this.notifyDesabilitarJtextField();
                //this.notfyDesfazerObserver(false);
                this.notfyRefazerObserver(false);
                this.notfySalvarObserver(false);
            }
        }
    }

    public void recomecar() {
        contadorPosicoesSelecao = 0;
        this.contadorDragDrop = this.numeroComponentes;
        this.contadorDeErrosConsecutivos = 0;
        this.passo = 1;
        jOptionPaneTexto = mensagens.proximoPasso(1);//primeira mensagem a ser exibida
        this.desenho = null;
        this.backup = null;
        this.diagrama = null;
        this.quadradoSelecao = null;
        this.valorSelecao = null;
        DesenharObserver jopo = (DesenharObserver) this.desenharObserver.get(0);
        jopo.updateInicio();
        this.notifyJOptionObservers(1);
        this.estado.comecar();

        this.configuracao.clear();

        this.contadorCongfiguracao = -1;
        this.notfyDesfazerObserver(false);
        this.notfyRefazerObserver(false);
        this.novaConfiguracao();
    }

    public void errosConsecutivos(int x) {
        switch (x) {
            case -1:
                this.contadorDeErrosConsecutivos++;
                break;
            case 1:
                this.contadorDeErrosConsecutivos--;
                break;
            case 0:
                this.contadorDeErrosConsecutivos = 0;
                break;
        }
        if (this.contadorDeErrosConsecutivos == 3) {
            this.jOptionPaneTexto = ManipularProperties.getMensagemEm("recomecar", "gerard.propriedades.ajudas");
            this.notifyJOptionObservers(1);
            this.recomecar();
        }
    }

    public void registerObserver(DesfazerRefazerObserver o) {
        this.desfazerRefazerObserver.add(o);
    }

    public void removeObserver(DesfazerRefazerObserver o) {
        int i = this.desfazerRefazerObserver.indexOf(o);
        if (i >= 0) {
            this.desfazerRefazerObserver.remove(i);
        }
    }

    public void notfyDesfazerObserver(boolean d) {

        for (int i = 0; i < desfazerRefazerObserver.size(); i++) {

            DesfazerRefazerObserver jopo = (DesfazerRefazerObserver) desfazerRefazerObserver.get(i);
            jopo = (DesfazerRefazerObserver) this.desfazerRefazerObserver.get(i);
            jopo.atualizarDesfazer(d);
        }
    }

    public void notfyRefazerObserver(boolean r) {

        for (int i = 0; i < desfazerRefazerObserver.size(); i++) {

            DesfazerRefazerObserver jopo = (DesfazerRefazerObserver) desfazerRefazerObserver.get(i);
            jopo = (DesfazerRefazerObserver) this.desfazerRefazerObserver.get(i);
            jopo.atualizarRefazer(r);
        }
    }

    public void notfySalvarObserver(boolean s) {

        for (int i = 0; i < desfazerRefazerObserver.size(); i++) {
            DesfazerRefazerObserver jopo = (DesfazerRefazerObserver) desfazerRefazerObserver.get(i);
            jopo = (DesfazerRefazerObserver) this.desfazerRefazerObserver.get(i);
            jopo.atualizarBotaoSalvar(s);
        }
    }

    public void desfazer() {
        this.notfyRefazerObserver(true);
        this.notfySalvarObserver(true);
        this.contadorCongfiguracao--;
            if(this.contadorCongfiguracao>=0){
            this.backup = this.configuracao.get(this.contadorCongfiguracao).getBackup();
            this.contadorPosicoesSelecao = this.configuracao.get(this.contadorCongfiguracao).getContadorPosicoesSelecao();
            this.passo = this.configuracao.get(this.contadorCongfiguracao).getPasso();
            this.contadorDragDrop = this.configuracao.get(this.contadorCongfiguracao).getContadorDragDrop();
            this.desenho = this.backup;
            if (this.contadorCongfiguracao == 0) {
                this.notfyDesfazerObserver(false);
                this.notfySalvarObserver(false);
            } else if (this.contadorDragDrop != 0) {
                this.desenharSelecao();
            } else {
                this.notifyDigitarResultado();
            }
            if (this.contadorDragDrop == 1) {
                this.notifyDesabilitarJtextField();
                this.notifyMouseListener(true);
            }
            this.notifyDesenharObserver();
            this.exibirProximoPasso();
            this.estado.botaoDesfazer(this.jOptionPaneTexto);
        }
    }

    public void refazer() {
        this.notfyDesfazerObserver(true);
        this.contadorCongfiguracao++;
        this.backup = this.configuracao.get(this.contadorCongfiguracao).getBackup();
        this.contadorPosicoesSelecao = this.configuracao.get(this.contadorCongfiguracao).getContadorPosicoesSelecao();
        this.passo = this.configuracao.get(this.contadorCongfiguracao).getPasso();
        this.contadorDragDrop = this.configuracao.get(this.contadorCongfiguracao).getContadorDragDrop();
        this.desenho = this.backup;
        if (this.contadorCongfiguracao == (this.configuracao.size() - 1)) {
            this.notfyRefazerObserver(false);
            this.notfySalvarObserver(true);
        }
        if (this.contadorDragDrop != 0) {
            this.desenharSelecao();
        } else {
            if (this.passo == 4) {
                this.notifyDesabilitarJtextField();
            }
            this.notifyDigitarResultado();
        }
        this.notifyDesenharObserver();
        this.exibirProximoPasso();
        this.estado.botaoRefazer(this.jOptionPaneTexto);
    }

    public void novaConfiguracao() {

        Configuracao aux = new Configuracao(
                this.backup,
                this.contadorPosicoesSelecao,
                this.passo,
                this.contadorDragDrop);
        if (!arrayConfiguracaoContem(aux)) {
            this.contadorCongfiguracao++;
            configuracao.add(aux);
        }
    }

    public void novaConfiguracao(Desenho backup,int contadorPosicoesSelecao,int passo,int contadorDragDrop) {

        Configuracao aux = new Configuracao(
                backup,
                contadorPosicoesSelecao,
                passo,
                contadorDragDrop);
        if (!arrayConfiguracaoContem(aux)) {
            this.contadorCongfiguracao++;
            configuracao.add(aux);
        }
    }

    public boolean arrayConfiguracaoContem(Configuracao conf) {
        Configuracao c;
        for (int i = 0; i < this.configuracao.size(); i++) {
            c = this.configuracao.get(i);
            if (c.getContadorPosicoesSelecao() == conf.getContadorPosicoesSelecao()
                    && c.getContadorDragDrop() == conf.getContadorDragDrop()
                    && c.getPasso() == conf.getPasso()) {
                return true;
            }
        }
        return false;
    }

    public void abrir() {
        Configuracao conf = this.estado.abrirEstado();
        if (conf != null) {
            SituacaoProblema sp = this.estado.getSituacaoProblema();
            if (sp != null) {
                this.correntSp = sp;
                this.passo = conf.getPasso();
                this.contadorDragDrop = conf.getContadorDragDrop();
                this.contadorPosicoesSelecao = conf.getContadorPosicoesSelecao();
                atualizarVariaveis();
                 
            }
        } else {
            this.jOptionPaneTexto = ManipularProperties.getMensagemEm("semEstadoSalvo", "gerard.propriedades.avisos2");
        }
          this.notifyJOptionObservers(1);

    }

    public void salvar() {
        //salva configuracao que o usuario está vendo na tela
        Configuracao aux = new Configuracao(
                this.backup,
                this.contadorPosicoesSelecao,
                this.passo,
                this.contadorDragDrop);
        if (this.estado.salvarEstado(aux)) {
            this.jOptionPaneTexto = ManipularProperties.getMensagemEm("estadoSalvo", "gerard.propriedades.avisos2");
        } else {
            this.jOptionPaneTexto = ManipularProperties.getMensagemEm("problemasBase", "gerard.propriedades.avisos2");
        }
        this.notifyJOptionObservers(1);

    }

    private void atualizarVariaveis() {
        this.configuracao.clear();
        this.contadorCongfiguracao=-1;
        this.numeroComponentes = this.correntSp.getNumeroComponentes();
        for (int i = 1; i <= this.passo; i++) {
            switch (i) {
                case 1://categorizar
                    this.situacaoProblemaDesenho = new SituacaoProblemaDesenho(this.dimension, 10, 10, this.correntSp);
                    this.desenho = situacaoProblemaDesenho;
                    this.desenho.draw(this.g2d);
                    this.correntSp = situacaoProblemaDesenho.getSP();
                    this.faixaDesenho = new FaixaDesenho(this.desenho, 0, this.dimension.width, situacaoProblemaDesenho.alturaTexto());
                    this.desenho = this.faixaDesenho;
                    this.backup=this.desenho;
                    this.novaConfiguracao(this.backup,0,i,this.numeroComponentes);
                    this.diagrama = this.estado.getDiagrama(this.correntSp.getCategoria(), this.desenho);
                    this.desenho = (Desenho) this.diagrama;
                    this.desenho.setDistacia_Y(situacaoProblemaDesenho.alturaTexto() + 80);
                    this.desenho.centralizar(this.dimension);
                    this.backup = this.desenho;
                    this.posicoesSelecao = this.calcularOrdemSelecao(this.correntSp);
                    this.novaConfiguracao(this.backup,0,i,this.numeroComponentes);
                    this.notfyAtualizarVariaveis();
                    break;
                case 2://arrastar os elementos
                    for (int j = 0; j <= this.contadorPosicoesSelecao; j++) {
                        if(j>0){
                        int posicaoAtual = this.posicoesSelecao[j-1];
                        ComponenteDaSP parteTexto = this.correntSp.getComponenteSpAt(posicaoAtual);
                        Shape parteDiagrama = this.diagrama.getShapeAt(posicaoAtual);
                        int posX = parteDiagrama.getBounds().x;
                        int posY = parteDiagrama.getBounds().y;
                        int width = parteDiagrama.getBounds().width;
                        int height = parteDiagrama.getBounds().height;
                        posX = posX + (width - parteTexto.getWidth()) / 2;
                        posY = posY - 4 + (height + this.correntSp.getHeight()) / 2;
                        String sinal = (parteTexto.getSinal() != null ? parteTexto.getSinal() : "");
                        this.desenho = new ValorDesenho(this.backup, sinal + parteTexto.getTexto(), posX, posY, parteTexto.getWidth(), this.correntSp.getHeight(), Color.black);
                        this.backup = this.desenho;
                        }
                         this.novaConfiguracao(this.backup,j,i,this.numeroComponentes-j);
                    }
                    break;
                case 3://digitar resultado
                    this.novaConfiguracao(this.backup,this.numeroComponentes,i,0);
                    int posicaoAtual = this.posicoesSelecao[this.contadorPosicoesSelecao];
                    Shape parteDiagrama = this.diagrama.getShapeAt(posicaoAtual);
                    int posX = parteDiagrama.getBounds().x;
                    int posY = parteDiagrama.getBounds().y;
                    int height = parteDiagrama.getBounds().height;
                    this.pontoResultado = new Point(posX + 14, posY + height / 2);
                    this.notifyDigitarResultado();
                    break;

            }
        }
         
        if(this.passo==2)
                this.desenharSelecao();
        this.notfySalvarObserver(true);
        this.notfyDesfazerObserver(true);
        this.notfyRefazerObserver(false);
        this.notifyDesenharObserver();
        this.jOptionPaneTexto = this.mensagens.proximoPasso(passo);
       


    }

    public void setGraphics2D(Graphics2D g2d, Dimension d) {
        this.g2d = g2d;
        this.dimension = d;
    }
    public SituacaoProblemaDesenho getSituacaoProblemaDesenho(){
        return this.situacaoProblemaDesenho;
    }
    public FaixaDesenho getFaixaDesenho(){
        return this.faixaDesenho;
    }
}
