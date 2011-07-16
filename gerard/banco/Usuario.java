/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gerard.banco;

/**
 *
 * @author Kecia
 */
public class Usuario {
    private String cpf_login;
    private String senha;
    private String nome;
    private String idade;
    private String sexo;
    private String grauFormacao;
    private String email;
    private String nomeEscola;
    private String cidadeEscola;
    private String estadoEscola;
   

    public Usuario() {
        this.cpf_login=null;
        senha = null;
        nome = null;
        idade = null;
        sexo = null;
        grauFormacao = null;
        email = null;
        nomeEscola = null;
        cidadeEscola = null;
        estadoEscola = null;
        
    }

    public Usuario(String cpf_login,String senha, String nome, String idade, String sexo, String grauFormacao,
            String email, String nomeEscola, String cidadeEscola, String estadoEscola) {

        this.cpf_login=cpf_login;
        this.senha = senha;
        this.nome = nome;
        this.idade = idade;
        this.sexo = sexo;
        this.grauFormacao = grauFormacao;
        this.email = email;
        this.nomeEscola = nomeEscola;
        this.cidadeEscola = cidadeEscola;
        this.estadoEscola = estadoEscola;
        
    }

   

    public void setCpfLogin(String cpf_login){
        this.cpf_login=cpf_login;
    }
     public void setSenha(String senha) {
        this.senha = senha;
    }
    public void setNome(String nome) {
        this.nome = nome;
    }

    public void setIdade(String idade) {
        this.idade = idade;
    }

    public void setSexo(String sexo) {
        this.sexo = sexo;
    }

    public void setGrauFormacao(String grauFormacao) {
        this.grauFormacao = grauFormacao;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setNomeEscola(String nomeEscola) {
        this.nomeEscola = nomeEscola;
    }

    public void setCidadeEscola(String cidadeEscola) {
        this.cidadeEscola = cidadeEscola;
    }

    public void setEstadoEscola(String estadoEscola) {
        this.estadoEscola = estadoEscola;
    }

   
   public String getCpfLogin(){
        return this.cpf_login;
    }
    public String getSenha() {
        return this.senha;
    }
     public String getNome() {
        return this.nome;
    }

    public String getIdade() {
        return this.idade;
    }

    public String getSexo() {
        return this.sexo;
    }

    public String getGrauFormacao() {
        return this.grauFormacao;
    }

    public String getEmail() {
        return this.email;
    }

    public String getNomeEscola() {
        return this.nomeEscola;
    }

    public String getCidadeEscola() {
        return this.cidadeEscola;
    }

    public String getEstadoEscola() {
        return this.estadoEscola;
    }

    

}
