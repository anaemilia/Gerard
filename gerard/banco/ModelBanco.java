/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gerard.banco;

import gerard.util.HistoricoObserver;
import gerard.util.ManipularProperties;
import gerard.util.MensagensObserver;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ModelBanco {

    private ArrayList telalBancoObserver;
    private ArrayList mensagensObserver;
    private ArrayList historicoObserver;
    private String mensagem;
    private BancoSingleton banco;
    private Usuario usuarioAtual;
    private String avisos = "gerard.propriedades.avisos2";
    private String historico = null;

    public ModelBanco() {
        telalBancoObserver = new ArrayList();
        this.historicoObserver = new ArrayList();
        this.mensagensObserver = new ArrayList();
        banco = BancoSingleton.getInstancia();
    }
    //TelaObserver

    public void registerObserver(TelaBancoObserver o) {
        this.telalBancoObserver.add(o);
    }

    public void removeObserver(TelaBancoObserver o) {
        int i = this.telalBancoObserver.indexOf(o);
        if (i >= 0) {
            this.telalBancoObserver.remove(i);
        }
    }

    public void notifyTelaObservers() {

        for (int i = 0; i < this.telalBancoObserver.size(); i++) {
            TelaBancoObserver jopo = (TelaBancoObserver) this.telalBancoObserver.get(i);
            jopo.updateTela(mensagem);

        }
    }

    public void registerObserver(MensagensObserver o) {
        this.mensagensObserver.add(o);
    }

    public void removeObserver(MensagensObserver o) {
        int i = this.mensagensObserver.indexOf(o);
        if (i >= 0) {
            this.mensagensObserver.remove(i);
        }
    }

    public void notifyMensagemObservers() {

        for (int i = 0; i < this.mensagensObserver.size(); i++) {
            MensagensObserver jopo = (MensagensObserver) this.mensagensObserver.get(i);
            jopo.mostrarMensagemBanco(mensagem);

        }
    }

    public String getMensagem() {
        return this.mensagem;
    }

    public Usuario getUsuarioAtual() {
        return this.usuarioAtual;
    }

    public void conectarUsuario(String login, char[] password) {
        String senha = String.valueOf(password);
        if (login.equalsIgnoreCase("") || senha.equalsIgnoreCase("")) {
            //campo vazio envia mensagem dizendo para usuario preencher todos os campos
            this.mensagem = ManipularProperties.getMensagemEm("preencherCampos", avisos);
            this.notifyMensagemObservers();
        } else {
            //campos nao estao vazios, entao verificar se o par (usuario,senha) existe no banco
            this.usuarioAtual = this.banco.getUsuario(login);
            if (this.usuarioAtual != null) {
                //verificar senha
                if (this.usuarioAtual.getSenha().compareTo(senha) != -1) {
                    //usuario está logado
                    //atulalizar tela
                    String sexo = (this.usuarioAtual.getSexo().equalsIgnoreCase("F") ? "a" : "o");
                    String novaString[] = {this.usuarioAtual.getNome(), sexo};
                    String mensagem = ManipularProperties.getMensagemEm("saudacao", avisos);
                    this.mensagem = ManipularProperties.replaceString(novaString, mensagem);
                    this.notifyTelaObservers();
                } else {
                    //senha invalida
                    this.mensagem = ManipularProperties.getMensagemEm("senhaErrada", avisos);
                    this.notifyMensagemObservers();
                }

            } else {
                //usuario nao existe, enviar mensagem dizendo para ele fazer o cadastro
                this.mensagem = ManipularProperties.getMensagemEm("fazerCadastro", avisos);
                this.notifyMensagemObservers();
            }
        }
    }

    public void cadastrarUsuario(Usuario usuario, String senhaConfirmacao) {
       if(validarDadosUsuario(usuario,senhaConfirmacao)){
            //tudo certo, cadastrar usuario
            if (this.banco.getUsuario(usuario.getCpfLogin()) != null) {//cadastro já existe
                this.mensagem = ManipularProperties.getMensagemEm("cadastroExiste", avisos);
                this.notifyMensagemObservers();
            } else {
                String novaSenha = verificarSenha(usuario.getSenha());
                if (novaSenha.length() > usuario.getSenha().length()) {
                    usuario.setSenha(novaSenha);
                }
                if (this.banco.cadastrarUsuario(usuario)) //cadastro realizado com sucesso
                {
                    this.mensagem = ManipularProperties.getMensagemEm("cadastroSucesso", avisos);
                } else {
                    this.mensagem = ManipularProperties.getMensagemEm("cadastroFalha", avisos);
                }
                this.notifyMensagemObservers();
            }
        }
    }
    private boolean validarDadosUsuario(Usuario usuario, String senhaConfirmacao){
          if (usuario.getCpfLogin().equalsIgnoreCase("")
                || usuario.getSenha().equalsIgnoreCase("")
                || usuario.getCidadeEscola().equalsIgnoreCase("")
                || usuario.getEmail().equalsIgnoreCase("")
                || usuario.getEstadoEscola().equalsIgnoreCase("")
                || usuario.getIdade().equalsIgnoreCase("")
                || usuario.getNome().equalsIgnoreCase("")
                || usuario.getNomeEscola().equalsIgnoreCase("")
                || usuario.getSexo().equalsIgnoreCase("")) {
            //campo vazio envia mensagem dizendo parthisa usuario preencher todos os campos
            this.mensagem = ManipularProperties.getMensagemEm("preencherCampos", avisos);
            this.notifyMensagemObservers();
            return false;
        }
        if (!validarNome(usuario.getNome())) {
            //verificar se os nomes so possuem letras
            this.mensagem = ManipularProperties.getMensagemEm("nomeUsuarioIvalido", avisos);
            this.notifyMensagemObservers();
            return false;
        }
         if (!validarNome(usuario.getNomeEscola())) {
            this.mensagem = ManipularProperties.getMensagemEm("nomeEscolaIvalido", avisos);
            this.notifyMensagemObservers();
            return false;
        }
        if (!validarNome(usuario.getCidadeEscola())) {
            this.mensagem = ManipularProperties.getMensagemEm("cidadeEscolaIvalido", avisos);
            this.notifyMensagemObservers();
            return false;
        }
        if (!this.validarCpf(usuario.getCpfLogin())) {
            //cpf invalido
            this.mensagem = ManipularProperties.getMensagemEm("cpfInvalido", avisos);
            this.notifyMensagemObservers();
            return false;
        }
          if (!validarIdade(usuario.getIdade())) {
            //verificar idade
            this.mensagem = ManipularProperties.getMensagemEm("idadeInvalida", avisos);
            this.notifyMensagemObservers();
            return false;
        }
          if (!validarEmail(usuario.getEmail())) {
            //email invalido
            this.mensagem = ManipularProperties.getMensagemEm("emailInvalido", avisos);
            this.notifyMensagemObservers();
            return false;
        }
          if (usuario.getSenha().length() < 6) {
            this.mensagem = ManipularProperties.getMensagemEm("senhaInvalida", avisos);
            this.notifyMensagemObservers();
            return false;
        }
          if (!usuario.getSenha().equals(senhaConfirmacao)) {
            this.mensagem = ManipularProperties.getMensagemEm("senhasDiferentes", avisos);
            this.notifyMensagemObservers();
            return false;
        }
        return true;
    }
    private boolean validarCpf(String cpf) {

        int cont,
                d1, //digito verifcador1
                d2,//digito verificador2
                k = 11,
                soma = 0,
                soma2 = 0,
                vetor[] = new int[11],
                rest;
        char c;
        if (cpf.length() != 11) {
            return false;
        }
        if (cpf.equalsIgnoreCase("00000000000")
                || cpf.equalsIgnoreCase("11111111111")
                || cpf.equalsIgnoreCase("22222222222")
                || cpf.equalsIgnoreCase("33333333333")
                || cpf.equalsIgnoreCase("44444444444")
                || cpf.equalsIgnoreCase("55555555555")
                || cpf.equalsIgnoreCase("66666666666")
                || cpf.equalsIgnoreCase("77777777777")
                || cpf.equalsIgnoreCase("88888888888")
                || cpf.equalsIgnoreCase("99999999999")) {
            return false;
        }

        for (cont = 0; cont < 11; cont++) {
            c = cpf.charAt(cont);
            if (Character.isDigit(c)) {
                vetor[cont] = Integer.parseInt(c + "");

            } else {
                return false;//cpf invalido
            }
        }

        for (cont = 0; cont < 9; cont++) {
            soma += vetor[cont] * --k;
            soma2 += vetor[cont] * (k + 1);
        }
        rest = soma % 11;
        d1 = (rest < 2 ? 0 : 11 - rest);
        if (d1 != vetor[9]) {
            return false;
        }

        soma2 += d1 * 2;
        rest = soma2 % 11;
        d2 = (rest < 2 ? 0 : 11 - rest);
        if (d2 != vetor[10]) {
            return false;
        }

        return true;
    }

    private boolean validarNome(String nome) {
        int tamanho = nome.length();
        char c;
        for (int i = 0; i < tamanho; i++) {
            c = nome.charAt(i);
            if (!Character.isLetter(c)) {
                if (c == ' '); else {
                    return false;
                }
            }
        }
        return true;
    }

    private boolean validarIdade(String idade) {
        int tamanho = idade.length();
        char c;
        for (int i = 0; i < tamanho; i++) {
            c = idade.charAt(i);
            if (!Character.isDigit(c)) {
                return false;
            }
        }
        int x = Integer.parseInt(idade);
        if (x <= 0 || x > 100) {
            return false;
        }
        return true;
    }

    private boolean validarEmail(String email) {
        Pattern p = Pattern.compile("^[\\w-]+(\\.[\\w-]+)*@([\\w-]+\\.)+[a-zA-Z]{2,7}$");
        Matcher m = p.matcher(email);
        if (!m.find()) {
            return false;
        }
        return true;
    }

    private String verificarSenha(String senha) {
        //se a senha tiver caracteres ' (aspa simples) deve ser add outra aspa ('') para inserir no banco
        int tamanho = senha.length();
        char c;
        char nova[] = new char[tamanho * 2];
        int aux = 0;
        for (int i = 0; i < tamanho; i++, aux++) {
            c = senha.charAt(i);
            if ((c) == '\'') {
                nova[aux++] = '\'';
            }
            nova[aux] = c;
        }
        String auxS = String.valueOf(nova);
        auxS = auxS.substring(0, aux);

        return auxS;


    }

    public String getHistorico() {
        return this.historico;
    }

    public void registerObserver(HistoricoObserver o) {
        this.historicoObserver.add(o);
    }

    public void removeObserver(HistoricoObserver o) {
        int i = this.historicoObserver.indexOf(o);
        if (i >= 0) {
            this.historicoObserver.remove(i);
        }
    }

    public void notifyHistoricoObservers() {

        for (int i = 0; i < this.telalBancoObserver.size(); i++) {
            HistoricoObserver jopo = (HistoricoObserver) this.historicoObserver.get(i);
            jopo.updateHistorico();

        }
    }

    public void exibirHistorico() {
        this.historico = this.banco.getHistorico(this.usuarioAtual.getCpfLogin());
        if (this.historico == null) {
            this.mensagem = ManipularProperties.getMensagemEm("semHistorico", avisos);
            this.notifyMensagemObservers();
        } else {
            this.notifyHistoricoObservers();
        }
    }

    public void exibirMaisHistorico() {
        this.historico = this.banco.getMaisHistorico(this.usuarioAtual.getCpfLogin());
        if (this.historico == null) {
            this.mensagem = ManipularProperties.getMensagemEm("semMaisHistorico", avisos);
            this.notifyMensagemObservers();
        } else {
            this.notifyHistoricoObservers();
        }
    }

    public void editarCadastrado(Usuario usuario, String senhaConfirmacao){
            if(this.validarDadosUsuario(usuario, senhaConfirmacao)){
                if(this.banco.editarCadastro(usuario)){
                    this.usuarioAtual=usuario;
                    this.mensagem = ManipularProperties.getMensagemEm("editarCadastroSucesso", avisos);
                }
                else
                     this.mensagem = ManipularProperties.getMensagemEm("editarCadastroFalha", avisos);
                this.notifyMensagemObservers();
            }

    }
}
