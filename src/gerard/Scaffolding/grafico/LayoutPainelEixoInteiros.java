package gerard.Scaffolding.grafico;

import java.awt.Dimension;

/**
 * Centraliza as dimensões e os espaçamentos do painel flutuante do eixo.
 *
 * O painel possui dois estados verticais:
 * - sem valor definido: apresenta somente cabeçalho, rótulos, eixo e marcas;
 * - com valor definido: reserva o espaço estritamente necessário para o valor
 *   selecionado e para a instrução de navegação.
 *
 * Dessa forma, não se mantém uma faixa vazia permanente acima ou abaixo do
 * eixo quando o ponto azul ainda não está ativo.
 */
public final class LayoutPainelEixoInteiros {
    private static final int LARGURA_MINIMA = 340;
    private static final int LARGURA_MAXIMA = 480;
    private static final int ALTURA_SEM_VALOR = 108;
    private static final int ALTURA_COM_VALOR = 132;
    private static final int MARGEM_TELA = 24;
    private static final int MARGEM_HORIZONTAL_EIXO = 28;
    private static final int ALTURA_CABECALHO = 32;
    private static final int OFFSET_ROTULOS_LADOS = 50;
    private static final int OFFSET_ORIGEM_EIXO_SEM_VALOR = 78;
    private static final int OFFSET_ORIGEM_EIXO_COM_VALOR = 92;
    private static final int OFFSET_INSTRUCAO = 125;
    private static final int TAMANHO_BOTAO = 24;
    private static final int MARGEM_BOTAO_DIREITA = 8;
    private static final int MARGEM_BOTAO_TOPO = 4;
    private static final int OFFSET_TITULO_CABECALHO = 22;

    /**
     * Mantido para compatibilidade com consumidores anteriores. Assume o
     * estado completo, com valor selecionado e instrução visível.
     */
    public Dimension calcularDimensao(int larguraTela, int escala) {
        return calcularDimensao(larguraTela, escala, true);
    }

    public Dimension calcularDimensao(int larguraTela, int escala,
                                      boolean valorNavegavelDefinido) {
        int escalaSegura = Math.max(5, Math.abs(escala));
        int espacamentoDesejado;
        if (escalaSegura <= 8) {
            espacamentoDesejado = 22;
        } else if (escalaSegura <= 12) {
            espacamentoDesejado = 18;
        } else {
            espacamentoDesejado = 14;
        }

        int larguraDesejada = 2 * escalaSegura * espacamentoDesejado
                + 2 * MARGEM_HORIZONTAL_EIXO;
        int larguraDisponivel = Math.max(LARGURA_MINIMA,
                Math.min(LARGURA_MAXIMA, Math.max(1, larguraTela - MARGEM_TELA)));
        int largura = limitar(larguraDesejada, LARGURA_MINIMA, larguraDisponivel);
        int altura = valorNavegavelDefinido
                ? ALTURA_COM_VALOR : ALTURA_SEM_VALOR;
        return new Dimension(largura, altura);
    }

    public int getMargemHorizontalEixo() {
        return MARGEM_HORIZONTAL_EIXO;
    }

    public int getAlturaCabecalho() {
        return ALTURA_CABECALHO;
    }

    public int getOffsetTituloCabecalho() {
        return OFFSET_TITULO_CABECALHO;
    }

    public int getOffsetRotulosLados() {
        return OFFSET_ROTULOS_LADOS;
    }

    public int getOffsetOrigemEixo(boolean valorNavegavelDefinido) {
        return valorNavegavelDefinido
                ? OFFSET_ORIGEM_EIXO_COM_VALOR
                : OFFSET_ORIGEM_EIXO_SEM_VALOR;
    }

    /** Mantido para compatibilidade: retorna o estado com valor definido. */
    public int getOffsetOrigemEixo() {
        return getOffsetOrigemEixo(true);
    }

    public int getOffsetInstrucao() {
        return OFFSET_INSTRUCAO;
    }

    public int getTamanhoBotao() {
        return TAMANHO_BOTAO;
    }

    public int getMargemBotaoDireita() {
        return MARGEM_BOTAO_DIREITA;
    }

    public int getMargemBotaoTopo() {
        return MARGEM_BOTAO_TOPO;
    }

    private int limitar(int valor, int minimo, int maximo) {
        if (valor < minimo) {
            return minimo;
        }
        if (valor > maximo) {
            return maximo;
        }
        return valor;
    }
}
