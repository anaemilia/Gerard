/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gerard.controller;

import gerard.banco.Usuario;
import gerard.util.SituacaoProblema;
import gerard.util.desenhos.Desenho;
import java.awt.Component;


/**
 *
 * @author Kecia
 */
public interface ControllerInterface {

    public void setDiagramaComposicaoMultiplicacao();

    public void setDiagramaTransformacaoDPartes();

    public void setDiagramaComparacaoDCotas();

    public void setProximoPasso();

    public void maisDica();

    public void veriricarResultado(String text);

    public void setSP(SituacaoProblema aux);

    public void setDesenho(Desenho desenho);

    public void verificarPosicionamento(int x, int y);

    public void verificarSinal(String sinal);

    public Component getJPanel();

    public void desconectarUsuario();

    public void cadastrarUsuario();

    public void conectarUsuario(String text, char[] password);

    public void setAplicativoAditivas();

    public void cadastrarUsuario(Usuario usuario, String senhaConfirmacao);

    public void cancelarCadastro();

    public void verificarSIMeNAO(String string);

    public void setTelaLogin();

    public void setAplicativoMultiplicativas();

    public void exibirHistorico();

    public void exibirMaisHistorico();

    public void sairHistorico();

    public void novoEstado();

    public void recomecarEstado();

    public void desfazer();

    public void refazer();

    public void abrir();

    public void salvar();

    public void editarCadastro();

    public void editarCadastro(Usuario usuario, String senhaConfirmacao);

    public void cancelarEditarCadastro();

    public void sobre();

    public void teoria();

    public void setAplicativo1();

  
}
