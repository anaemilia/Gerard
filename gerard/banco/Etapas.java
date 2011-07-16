/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gerard.banco;

public class Etapas {

  //  private String codEtapa; auto increment
    public static String VALOR_CERTO = "CERTO";
    public static String VALOR_ERRADO = "ERRADO";
    private String descricao;
    private String valor;
    private String codTentativa;
    private String ator;
    private String codErro;
    private String codObjeto;
    private String cpf;
    private String codProblema;

  /*  public String getcodEtapa() {
        return codEtapa;
    }*/

    public String getdescricao() {
        return descricao;
    }

    public String getvalor() {
        return valor;
    }

    public String getcodTentativa() {
        return codTentativa;
    }

    public String getator() {
        return ator;
    }

    public String getcodErro() {
        return codErro;
    }

    public String getcodObjeto() {
        return codObjeto;
    }

    public String getcpf() {
        return cpf;
    }

    public String getcodProblema() {
        return codProblema;
    }

   /* public void setcodEtapa(String codEtapa) {
        this.codEtapa = codEtapa;
    }*/

    public void setdescricao(String descricao) {
        this.descricao = descricao;
    }

    public void setvalor(String valor) {
        this.valor = valor;
    }

    public void setcodTentativa(String codTentativa) {
        this.codTentativa = codTentativa;
    }

    public void setator(String ator) {
        this.ator = ator;
    }

    public void setcodErro(String codErro) {
        this.codErro = codErro;
    }

    public void setcodObjeto(String codObjeto) {
        this.codObjeto = codObjeto;
    }

    public void setcpf(String cpf) {
        this.cpf = cpf;
    }

    public void setcodProblema(String codProblema) {
        this.codProblema = codProblema;
    }
}
