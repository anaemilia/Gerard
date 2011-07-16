package gerard.controller;

import gerard.util.Aplicativo1.ControllerAplicativo1;
import gerard.util.ManipularProperties;
import gerard.banco.Usuario;
import gerard.model.GerardModelInterface;
import gerard.banco.ModelBanco;
import gerard.util.SituacaoProblema;
import gerard.util.desenhos.Desenho;
import gerard.util.state.State;
import gerard.util.state.EstruturasAditivas;
import gerard.util.state.EstruturasAditivasApplet;
import gerard.util.state.EstruturasAditivasHistorico;
import gerard.util.state.EstruturasMultiplicativas;
import gerard.util.state.EstruturasMultiplicativasApplet;
import gerard.util.state.EstruturasMultiplicativasHistorico;
import gerard.view.JDialogMensagens;
import gerard.view.JDialogSobre;
import gerard.view.JPanelAplicativo1;
import gerard.view.JPanelCadastro;
import gerard.view.JPanelHistorico;
import gerard.view.JPanelLogin;
import gerard.view.JPanelPrincipalView;
import gerard.view.JPanelTeoria;
import java.awt.CardLayout;
import java.awt.Component;
import javax.swing.JApplet;
import javax.swing.JPanel;

/**
 *
 * @author Kecia
 */
public final class GerardController implements ControllerInterface {

    private GerardModelInterface model;
    private JPanelPrincipalView viewPrincipal;
    private JPanelLogin viewLogin;
    private JPanelCadastro viewCadastro;
    private JPanelHistorico viewHistorico;
    private JPanelAplicativo1 viewAplicativo1;
    
    private JPanelTeoria viewTeoria;
    private int estadoAutal = 0; //aditivas = 0 e multiplicativas = 1;
    private JPanel cardPanel = new JPanel(new CardLayout());
    private Component component_pai;
    private Usuario usuarioAtual;
    private ModelBanco modelBanco;
    private boolean flagEhApplet = true;
    private ControllerAplicativo1 controllerAplicativo1;

    public GerardController(Component component_pai, GerardModelInterface model) {

        this.model = model;
        this.component_pai = component_pai;
        this.usuarioAtual = null;
        if (!this.ehApplet()) {
            this.modelBanco = new ModelBanco();
            this.flagEhApplet = false;

        }
        this.controllerAplicativo1 = new ControllerAplicativo1(this);
        criarPaneis();

    }

    public void criarPaneis() {


        if (this.flagEhApplet) {
            this.viewLogin = new JPanelLogin(this.component_pai, this);
            cardPanel.add(this.viewLogin, "telaLogin");
            this.viewLogin.modoApplet();

            JDialogMensagens jDialogMensagens = new JDialogMensagens(this.component_pai, this, this.model,this.controllerAplicativo1);

        } else {
            this.viewLogin = new JPanelLogin(this.component_pai, this, this.modelBanco);
            cardPanel.add(this.viewLogin, "telaLogin");

            this.viewCadastro = new JPanelCadastro(this.component_pai, this);
            cardPanel.add(this.viewCadastro, "telaCadastro");

            this.viewHistorico = new JPanelHistorico(this.component_pai, this, this.modelBanco);
            cardPanel.add(this.viewHistorico, "telaHistorico");

            this.viewAplicativo1 = new JPanelAplicativo1(this.component_pai,controllerAplicativo1);
            cardPanel.add(this.viewAplicativo1, "telaAplicativo1");

            JDialogMensagens jDialogMensagens = new JDialogMensagens(this.component_pai, this, this.model, this.modelBanco,this.controllerAplicativo1);
        }

        this.viewPrincipal = new JPanelPrincipalView(this.component_pai, this, this.model);
        cardPanel.add(this.viewPrincipal, "telaPrincipal");

        this.viewTeoria = new JPanelTeoria(this.component_pai, this);
        cardPanel.add(this.viewTeoria, "telaTeoria");

    }

    private boolean ehApplet() {
        if (this.component_pai instanceof JApplet) {
            return true;
        }
        return false;
    }

    public void setDiagramaComposicaoMultiplicacao() {
        if (model.getProximoPasso() == 1) {
            if (this.estadoAutal == 0) {
                model.validarDiagrama(SituacaoProblema.COMPOSICAO);
            } else {
                model.validarDiagrama(SituacaoProblema.MULTIPLICACAO);
            }
        } else {
            model.exibirProximoPasso();
        }
    }

    public void setDiagramaTransformacaoDPartes() {
        if (model.getProximoPasso() == 1) {

            if (this.estadoAutal == 0) {
                model.validarDiagrama(SituacaoProblema.TRANSFORMACAO);
            } else {
                model.validarDiagrama(SituacaoProblema.DIVISAO_PARTES);
            }
        } else {
            model.exibirProximoPasso();
        }
    }

    public void setDiagramaComparacaoDCotas() {
        if (model.getProximoPasso() == 1) {

            if (this.estadoAutal == 0) {
                model.validarDiagrama(SituacaoProblema.COMPARACAO);
            } else {
                model.validarDiagrama(SituacaoProblema.DIVISAO_COTAS);
            }

        } else {
            model.exibirProximoPasso();
        }
    }

    public void setProximoPasso() {
        model.exibirProximoPasso();
    }

    public void maisDica() {
        model.exibirMaisDica();
    }

    public void veriricarResultado(String text) {
        model.validarResultado(text);
    }

    public void setSP(SituacaoProblema aux) {
        model.setSP(aux);
    }

    public void setDesenho(Desenho desenho) {
        model.setDesenho(desenho);
    }

    public void verificarPosicionamento(int x, int y) {
        model.validarPosicionamento(x, y);
    }

    public void verificarSinal(String sinal) {
        model.validarSinal(sinal);
    }

    public Component getJPanel() {
        return this.cardPanel;
    }

    public void desconectarUsuario() {
        this.usuarioAtual = null;
        //mandar alguma coisa para o model
        this.viewLogin.setBotaoCadastrarVisible();
        String mensagem = ManipularProperties.getMensagemEm("saudacao", "gerard.propriedades.avisos2");
        String novaString[] = {" ", "o(a)"};
        mensagem = ManipularProperties.replaceString(novaString, mensagem);
        this.viewLogin.setLabelMensagem1("Olá! Seja bem vindo!");
    }

    public void cadastrarUsuario() {
        this.viewCadastro.modoCadastrar();
        trocarPanel("telaCadastro");
    }

    public void conectarUsuario(String text, char[] password) {
        this.modelBanco.conectarUsuario(text, password);
        this.usuarioAtual = modelBanco.getUsuarioAtual();
    }

    public void trocarPanel(String panel) {
        CardLayout card = (CardLayout) this.cardPanel.getLayout();
        card.show(this.cardPanel, panel);
    }

    public void cadastrarUsuario(Usuario usuario, String senhaConfirmacao) {
        this.modelBanco.cadastrarUsuario(usuario, senhaConfirmacao);
    }

    public void cancelarCadastro() {
        this.usuarioAtual = null;
        this.trocarPanel("telaLogin");
    }

    public void verificarSIMeNAO(String string) {
        this.model.verificarSIMeNAO(string);
    }

    public void setTelaLogin() {
        if (this.usuarioAtual != null) {
            String sexo = (this.usuarioAtual.getSexo().equalsIgnoreCase("F") ? "a" : "o");
            String novaString[] = {this.usuarioAtual.getNome(), sexo};
            String mensagem = ManipularProperties.getMensagemEm("saudacao", "gerard.propriedades.avisos2");
            mensagem = ManipularProperties.replaceString(novaString, mensagem);
            this.viewLogin.updateTela(mensagem);
        }
        this.viewPrincipal.removeJMenuBar();
        this.trocarPanel("telaLogin");
    }

    public void setAplicativoAditivas() {
        this.estadoAutal = 0;
        State state = null;
        if (this.flagEhApplet) {//aplicativo applet
            this.viewPrincipal.modoVisitante();
            state = new EstruturasAditivasApplet();

        } else {//desktop
            state = new EstruturasAditivas();//default
            if (this.usuarioAtual == null) {
                this.viewPrincipal.modoVisitante();
                state = new EstruturasAditivas();
            } else {
                this.viewPrincipal.modoUsuarioCadastrado();
                String cpf = this.usuarioAtual.getCpfLogin();
                state = new EstruturasAditivasHistorico(cpf);
            }
        }
        this.viewPrincipal.modoAditivas();
        this.viewPrincipal.setJMenuBar();
        trocarPanel("telaPrincipal");
        model.setEstado(state);


    }

    public void setAplicativoMultiplicativas() {
        this.estadoAutal = 1;//multiplicativo
        State estado = null;
        if (this.flagEhApplet) {//aplicativo applet
            this.viewPrincipal.modoVisitante();
            estado = new EstruturasMultiplicativasApplet();
        } else {
            if (this.usuarioAtual == null) {
                this.viewPrincipal.modoVisitante();
                estado = new EstruturasMultiplicativas();
            } else {
                this.viewPrincipal.modoUsuarioCadastrado();
                String cpf = this.usuarioAtual.getCpfLogin();
                estado= new EstruturasMultiplicativasHistorico(cpf);
            }
        }
        this.viewPrincipal.modoMultiplicativas();
        this.viewPrincipal.setJMenuBar();
        trocarPanel("telaPrincipal");
        model.setEstado(estado);

    }

    public void exibirHistorico() {
        this.modelBanco.exibirHistorico();
        this.viewPrincipal.removeJMenuBar();
        this.trocarPanel("telaHistorico");

    }

    public void exibirMaisHistorico() {
        this.modelBanco.exibirMaisHistorico();
    }

    public void sairHistorico() {
        //this.viewPrincipal.modoUsuarioCadastrado();
        this.viewPrincipal.setJMenuBar();
        this.trocarPanel("telaPrincipal");
    }

    public void novoEstado() {
        model.inicializar();
    }

    public void recomecarEstado() {
        model.recomecar();
    }

    public void desfazer() {
        this.model.desfazer();
    }

    public void refazer() {
        this.model.refazer();
    }

    public void abrir() {
        model.abrir();
    }

    public void salvar() {
        model.salvar();
    }

    public void editarCadastro() {
        //exibe tela editar
        this.viewPrincipal.removeJMenuBar();
        this.viewCadastro.modoEditar(this.usuarioAtual);
        trocarPanel("telaCadastro");
    }

    public void editarCadastro(Usuario usuario, String senhaConfirmacao) {
        this.modelBanco.editarCadastrado(usuario, senhaConfirmacao);
        this.usuarioAtual = this.modelBanco.getUsuarioAtual();
    }

    public void cancelarEditarCadastro() {

        this.viewPrincipal.setJMenuBar();
        this.trocarPanel("telaPrincipal");
    }

    public void sobre() {
        new JDialogSobre(this.component_pai);
    }

    public void teoria() {
        this.viewPrincipal.removeJMenuBar();
        this.viewTeoria.html();
        this.trocarPanel("telaTeoria");
    }

    public void setAplicativo1() {
        this.controllerAplicativo1.setViewAplicativo1(viewAplicativo1);
        this.trocarPanel("telaAplicativo1");
    }
}
