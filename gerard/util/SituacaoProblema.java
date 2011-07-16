/*
 * Situacao Problema possui os dados referentes à situação-problema apresentada
 * para o usuário solucionar.
 * Toda situacao problema possui:
 * 1-Categoria = (tipo do problema como Composicao,Transformação..)
 * 2-Resposta = (Resultado do problema)
 * 3- Conjunto de componentes (toda situação-problema possui um diagrama relacionado
 * este diagrama é composto por partes (quadrado, circulo) que devem ser preenchidos
 * por um componente
 */
package gerard.util;

import java.awt.Point;
import java.util.ArrayList;

/**
 *
 * @author Kecia
 */
public class SituacaoProblema {

    public static final int COMPOSICAO = 1;
    public static final int TRANSFORMACAO = 2;
    public static final int COMPARACAO = 3;
    public static final int MULTIPLICACAO = 4;
    public static final int DIVISAO_PARTES = 5;
    public static final int DIVISAO_COTAS = 6;
    public static final String composicao = "COMPOSICAO";
    public static final String transformacao = "TRANSFORMACAO";
    public static final String comparacao = "COMPARACAO";
    public static final String multiplicacao = "MULTIPLICACAO";
    public static final String divisao_partes = "DIVISAO_PARTES";
    public static final String divisao_cotas = "DIVISAO_COTAS";
    public static final String aditiva = "ADITIVA";
    public static final String multiplicativa = "MULTIPLICATIVA";
    private int categoria;
    private String questao;
    private String resposta;
    private int height; //altura das letras do texto a ser mostrado na tela
    private ArrayList<ComponenteDaSP> componentesSP;//texto correspondente a cada parte do diagrama
    private int numeroComponentes = 0;
    private String sinal;
    private String rotuloEsquerda;//rotulo do diagrama
    private String rotuloDireita;//rotudo do diagrama

    public SituacaoProblema() {
        categoria = -1;
        questao = null;
        resposta = null;
        height = 0;
        componentesSP = null;//texto correspondente a cada parte do diagrama
        numeroComponentes = 0;
        sinal = null;
        rotuloEsquerda = null;//rotulo do diagrama
        rotuloDireita = null;//rotudo do diagrama
    }

    public SituacaoProblema(String questao, int categoria, String resposta, String rotuloEsquerda, String rotuloDireita, int numeroComponentes) {
        this.categoria = categoria;
        this.questao = questao.replace("?", " ?");//para desenhar na tela
        this.resposta = resposta;
        this.numeroComponentes = numeroComponentes;
        this.componentesSP = new ArrayList<ComponenteDaSP>();
        this.rotuloDireita = (rotuloDireita == null ? " " : rotuloDireita);
        this.rotuloEsquerda = (rotuloEsquerda == null ? " " : rotuloEsquerda);
    }

    public SituacaoProblema(String questao, String categoria, String resposta, String rotuloEsquerda, String rotuloDireita, int numeroComponentes) {
        this.categoria = this.getCodigoCategoria(categoria);
        this.questao = questao.replace("?", " ?");
        this.resposta = resposta;
        this.numeroComponentes = numeroComponentes;
        this.componentesSP = new ArrayList<ComponenteDaSP>();
        this.rotuloDireita = (rotuloDireita == null ? " " : rotuloDireita);
        this.rotuloEsquerda = (rotuloEsquerda == null ? " " : rotuloEsquerda);
    }
    //aditivas


    public void setComponentes(ArrayList<ComponenteDaSP> cdsp) {

        if (cdsp.size() == this.numeroComponentes) {
            this.componentesSP = cdsp;
            this.organizarComponentes();//a ordem dos componentes equivale a ordem dos mesmos nos diagramas
            this.setPosicoes();

        }
    }

    public String getQuestao() {
        return this.questao;
    }

    public String getResposta() {
        return this.resposta;
    }

    public ComponenteDaSP getComponenteSpAt(int i) {

        return this.componentesSP.get(i);
    }

    public ArrayList<ComponenteDaSP> getComponentes() {
        return this.componentesSP;
    }

    public int getNumeroComponentes() {
        return this.numeroComponentes;
    }

    public int getHeight() {
        return this.height;
    }

    public int getCategoria() {
        return this.categoria;
    }

    public int getComponenteWidhtAt(int cc) {
        return this.componentesSP.get(cc).getWidth();
    }

    public Point getComponentePointAt(int cc) {
        return this.componentesSP.get(cc).getPoint();
    }

    public void setHeight(int height) {
        for (int cc = 0; cc < this.numeroComponentes; cc++) {
            this.componentesSP.get(cc).setHeight(height);
        }
        this.height = height;
    }

    public String getComponenteTextoAt(int cc) {

        return this.componentesSP.get(cc).getTexto();
    }

    public void setComponenteWidthAt(int cc, int stringWidth) {
        this.componentesSP.get(cc).setWidht(stringWidth);
    }

    public int getComponentePosicaoAt(int cc) {
        return this.componentesSP.get(cc).getPosicao();
    }

    public void setComponentePointAt(int cc, int x, int y) {
        this.componentesSP.get(cc).setPoint(new Point(x, y));
    }

    public String getSinal() {
        return this.sinal;
    }

    public String getRotuloEsquerda() {
        return this.rotuloEsquerda;
    }

    public String getRotuloDireita() {
        return this.rotuloDireita;
    }

    private int getCodigoCategoria(String categoria) {
        if (categoria.equalsIgnoreCase(SituacaoProblema.comparacao)) {
            return SituacaoProblema.COMPARACAO;
        }
        if (categoria.equalsIgnoreCase(SituacaoProblema.composicao)) {
            return SituacaoProblema.COMPOSICAO;
        }
        if (categoria.equalsIgnoreCase(SituacaoProblema.transformacao)) {
            return SituacaoProblema.TRANSFORMACAO;
        }
        if (categoria.equalsIgnoreCase(SituacaoProblema.multiplicacao)) {
            return SituacaoProblema.MULTIPLICACAO;
        }
        if (categoria.equalsIgnoreCase(SituacaoProblema.divisao_partes)) {
            return SituacaoProblema.DIVISAO_PARTES;
        }
        if (categoria.equalsIgnoreCase(SituacaoProblema.divisao_cotas)) {
            return SituacaoProblema.DIVISAO_COTAS;
        }
        return 0;

    }
    public static String getEstrutura(String categoria) {
        if (categoria.equalsIgnoreCase(SituacaoProblema.comparacao) ||
                categoria.equalsIgnoreCase(SituacaoProblema.composicao) ||
                categoria.equalsIgnoreCase(SituacaoProblema.transformacao)) {
            return SituacaoProblema.aditiva;
        }
        else
        if (categoria.equalsIgnoreCase(SituacaoProblema.multiplicacao) ||
                categoria.equalsIgnoreCase(SituacaoProblema.divisao_partes) ||
                categoria.equalsIgnoreCase(SituacaoProblema.divisao_cotas)) {
            return SituacaoProblema.multiplicativa;
        }
        return null;

    }


    private void setPosicoes() {
        String vetor[];
        vetor = eliminarEspacosEmBranco(this.questao.split(" "));
        String valor;
        for (int i = 0; i < this.componentesSP.size(); i++) {
            valor = this.componentesSP.get(i).getTexto();

            for (int cont = 0; vetor[cont]!=null; cont++) {

                if (vetor[cont].indexOf(valor) == 0 && !jaContemPos(cont, i)) {

                    this.componentesSP.get(i).setPosicaoNoTexto(cont);
                }
            }
        }

    }

    private void organizarComponentes() {
        ArrayList<ComponenteDaSP> x = new ArrayList<ComponenteDaSP>();
        int tamanho = this.componentesSP.size();
        int v[] = new int[tamanho];
        for (int i = 0; i < tamanho; i++) {
            v[i] = this.componentesSP.get(i).getPosicao();
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

        for (cont = 0; cont < this.componentesSP.size(); cont++) {
            for (cont2 = 0; cont2 < this.componentesSP.size(); cont2++) {
                if (v[cont] == this.componentesSP.get(cont2).getPosicao()) {
                    x.add(this.componentesSP.get(cont2));
                }
            }
        }

        this.componentesSP = x;
    }

    private String[] eliminarEspacosEmBranco(String[] aux2) {
        String aux3[] = new String[aux2.length + 1];
        int cont = 0;
        for (int i = 0; i < aux2.length; i++) {
            if (!aux2[i].equalsIgnoreCase("")) {
                aux3[cont++] = aux2[i];
            }

        }
        aux3[cont] = null;
        return aux3;
    }

    private boolean jaContemPos(int posicao, int ate) {
        for (int cont = 0; cont < ate; cont++) {
            if (this.componentesSP.get(cont).getPosicao() == posicao) {
                return true;
            }
        }
        return false;
    }
}
