package gerard.campoaditivo.diagrama.elementos;

import gerard.campoaditivo.sincronizacao.texto.ElementoSemanticoTexto;
import java.awt.*;
import java.awt.geom.QuadCurve2D;

public class ElementoTextoMovel implements ElementoSemanticoTexto {
    public int x;
    public int y;
    public int yOriginal;
    public int largura;
    public int altura;
    public String valor;
    public final String valorOriginal;
    public int posicaoInicialTexto;
    public int xOriginal;
    public String chavePapelSemantico;
    public int inicioSemanticoLocal = -1;
    public int fimSemanticoLocal = -1;
    public String valorSemanticoOriginal = "";

    public ElementoTextoMovel(String valor) {
        this(valor, -1);
    }

    public ElementoTextoMovel(String valor, int posicaoInicialTexto) {
        this.valor = valor;
        this.valorOriginal = valor;
        this.posicaoInicialTexto = posicaoInicialTexto;
    }


    public void vincularSemantica(String chavePapel, int inicioLocal,
            int fimLocal, String valorOriginalDoPapel) {
        if (chavePapel == null || chavePapel.trim().length() == 0
                || inicioLocal < 0 || fimLocal <= inicioLocal
                || fimLocal > valorOriginal.length()) {
            return;
        }
        chavePapelSemantico = chavePapel;
        inicioSemanticoLocal = inicioLocal;
        fimSemanticoLocal = fimLocal;
        valorSemanticoOriginal = valorOriginalDoPapel == null
                ? valorOriginal.substring(inicioLocal, fimLocal)
                : valorOriginalDoPapel;
    }

    @Override
    public String getChavePapelSemantico() {
        return chavePapelSemantico;
    }

    @Override
    public String getValorSemanticoOriginal() {
        return valorSemanticoOriginal;
    }

    @Override
    public boolean possuiVinculoSemantico() {
        return chavePapelSemantico != null
                && inicioSemanticoLocal >= 0
                && fimSemanticoLocal > inicioSemanticoLocal;
    }

    @Override
    public boolean representaIncognitaOriginal() {
        return "?".equals(valorSemanticoOriginal == null
                ? "" : valorSemanticoOriginal.trim());
    }

    public int getInicioSemanticoAtual() {
        return possuiVinculoSemantico() ? inicioSemanticoLocal : -1;
    }

    public int getFimSemanticoAtual() {
        if (!possuiVinculoSemantico()) {
            return -1;
        }
        String atual = getValorSemanticoAtual();
        return inicioSemanticoLocal + atual.length();
    }

    public String getValorSemanticoAtual() {
        if (!possuiVinculoSemantico()) {
            return "";
        }
        int inicio = Math.min(inicioSemanticoLocal, valor.length());
        int fim = Math.min(valor.length(), Math.max(inicio, getFimSemanticoCalculado()));
        return valor.substring(inicio, fim);
    }

    private int getFimSemanticoCalculado() {
        int tamanhoPrefixo = inicioSemanticoLocal;
        int tamanhoSufixo = Math.max(0, valorOriginal.length() - fimSemanticoLocal);
        return Math.max(tamanhoPrefixo, valor.length() - tamanhoSufixo);
    }

    @Override
    public void atualizarValorSemantico(String novoValor) {
        if (!possuiVinculoSemantico() || novoValor == null) {
            return;
        }
        String prefixo = valorOriginal.substring(0, inicioSemanticoLocal);
        String sufixo = valorOriginal.substring(fimSemanticoLocal);
        valor = prefixo + novoValor + sufixo;
    }

    public void atualizarTamanho(FontMetrics fm) {
        largura = fm.stringWidth(valor);
        altura = fm.getHeight() - 5;
    }

    public boolean contem(int mx, int my) {
        return mx >= x - 4 && mx <= x + largura + 4 &&
               my >= y - altura && my <= y + 6;
    }
}
