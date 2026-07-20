package gerard.Scaffolding.grafico;

import gerard.interpretacao.simbolo.SimboloDesconhecido;

import gerard.i18n.ServicoLocalizacao;
import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Stroke;

/**
 * Scaffolding visual para apoiar a escolha de sinal de numeros relativos.
 *
 * O modulo desenha apenas o eixo x do universo dos inteiros,
 * com lado negativo e lado positivo, e destaca em azul o numero apos a escolha
 * do sinal no menu de radio buttons.
 *
 * Nesta versao, o eixo e apresentado como painel flutuante: ele nao altera a
 * representacao formal do diagrama e pode ser arrastado pela tela. O proprio
 * painel possui um botao "Esconder" para ocultar o apoio visual.
 *
 * O ponto azul tambem funciona como ponto de controle navegavel. Quando o
 * usuario clica no eixo ou arrasta esse ponto, o valor inteiro escolhido e
 * atualizado e pode ser sincronizado com o circulo de relacao do diagrama de
 * Vergnaud pela tela principal.
 */
public class ScaffoldingGraficoInteiros {

    /**
     * Distingue a manipulação do componente de tela da alteração do valor
     * semântico representado pelo eixo. Mover ou ocultar o painel não altera
     * a modelagem; navegar pelo ponto azul ou clicar no eixo altera.
     */
    public enum NaturezaInteracao {
        NENHUMA,
        COMPONENTE_TELA,
        VALOR_SEMANTICO
    }

    private static final Color COR_PAINEL = gerard.ui.UITemaGerard.COR_SUPERFICIE;
    private static final Color COR_CABECALHO = gerard.ui.UITemaGerard.COR_SUPERFICIE_SUAVE;
    private static final Color COR_BORDA = gerard.ui.UITemaGerard.COR_BORDA;
    private static final Color COR_TEXTO = gerard.ui.UITemaGerard.COR_TEXTO;
    private static final Color COR_TEXTO_SECUNDARIO = gerard.ui.UITemaGerard.COR_TEXTO_SECUNDARIO;
    private static final Color COR_AZUL = gerard.ui.UITemaGerard.COR_TEXTO;
    private static final Color COR_BOTAO = gerard.ui.UITemaGerard.COR_FUNDO_CONTEUDO;
    private static final int TOPO_PADRAO_DIAGRAMA = 215;
    private static final int MARGEM_ABAIXO_DIVISORIA = 12;

    private final LayoutPainelEixoInteiros layoutPainel = new LayoutPainelEixoInteiros();

    private boolean visivel;
    private Rectangle circuloReferencia;
    private Rectangle painelFlutuante;
    private String valorBase;
    private String sinalEscolhido;
    private int valorNavegavel;
    private boolean valorNavegavelDefinido;
    private boolean magnitudeDesconhecida;
    private int escalaFixa;
    private boolean escalaFixaDefinida;
    private boolean arrastandoPainel;
    private boolean arrastandoPontoControle;
    private int deslocamentoArrasteX;
    private int deslocamentoArrasteY;
    private boolean ocultadoPorInteracao;
    private boolean valorAlteradoPorInteracao;
    private boolean botaoEsconderEmFoco;

    public void mostrar(Rectangle circulo, String valor) {
        if (circulo == null) {
            ocultar();
            return;
        }
        this.circuloReferencia = new Rectangle(circulo);
        this.valorBase = normalizarValor(valor);
        this.sinalEscolhido = null;
        this.valorNavegavel = obterValorAbsoluto(valorBase);
        this.valorNavegavelDefinido = false;
        this.escalaFixa = Math.max(5, Math.abs(this.valorNavegavel));
        this.escalaFixaDefinida = true;
        this.visivel = true;
        this.ocultadoPorInteracao = false;
        this.valorAlteradoPorInteracao = false;

        if (painelFlutuante == null) {
            painelFlutuante = calcularPainelInicial(1240, 760, null);
        }
    }

    public void registrarEscolha(String valor, String sinal) {
        this.valorBase = normalizarValor(valor);
        this.sinalEscolhido = "-".equals(sinal) ? "-" : "+";
        this.magnitudeDesconhecida = magnitudeEhDesconhecida(this.valorBase);
        int absoluto = obterValorAbsoluto(valorBase);
        this.valorNavegavel = "-".equals(this.sinalEscolhido) ? -absoluto : absoluto;
        if (!this.escalaFixaDefinida) {
            this.escalaFixa = Math.max(5, absoluto);
            this.escalaFixaDefinida = true;
        } else if (absoluto > this.escalaFixa) {
            // A escala pode crescer para comportar um novo valor externo, mas
            // nunca diminui quando o ponto de controle é deslocado para perto
            // de zero. Assim, marcas, espaçamento e extremos permanecem estáveis.
            this.escalaFixa = absoluto;
        }
        this.valorNavegavelDefinido = true;
        this.visivel = true;
        this.ocultadoPorInteracao = false;
        this.valorAlteradoPorInteracao = false;
        if (painelFlutuante == null) {
            painelFlutuante = calcularPainelInicial(1240, 760, null);
        }
    }

    public void atualizarCirculo(Rectangle circulo) {
        if (circulo != null) {
            this.circuloReferencia = new Rectangle(circulo);
        }
    }

    public void ocultar() {
        this.visivel = false;
        this.circuloReferencia = null;
        this.valorBase = "";
        this.sinalEscolhido = null;
        this.valorNavegavel = 0;
        this.valorNavegavelDefinido = false;
        this.magnitudeDesconhecida = false;
        this.escalaFixa = 0;
        this.escalaFixaDefinida = false;
        this.arrastandoPainel = false;
        this.arrastandoPontoControle = false;
        this.ocultadoPorInteracao = false;
        this.valorAlteradoPorInteracao = false;
        this.painelFlutuante = null;
    }

    public boolean isVisivel() {
        return visivel;
    }

    public boolean estaArrastando() {
        return arrastandoPainel || arrastandoPontoControle;
    }

    public boolean estaArrastandoPainel() {
        return arrastandoPainel;
    }

    public boolean estaArrastandoPontoControle() {
        return arrastandoPontoControle;
    }

    public boolean foiOcultadoPorInteracao() {
        return ocultadoPorInteracao;
    }

    public boolean houveAlteracaoValorPorInteracao() {
        return valorAlteradoPorInteracao;
    }

    public boolean contemPontoControle(int mouseX, int mouseY) {
        if (!visivel || !valorNavegavelDefinido || painelFlutuante == null) {
            return false;
        }
        return obterRetanguloPontoControle().contains(mouseX, mouseY);
    }

    public String obterDicaPontoControle() {
        return ServicoLocalizacao.getInstancia().texto("ui.tooltip.integerAxisBluePoint");
    }

    public boolean contemBotaoEsconder(int mouseX, int mouseY) {
        return visivel && painelFlutuante != null
                && obterRetanguloBotaoEsconder().contains(mouseX, mouseY);
    }

    /**
     * Classifica previamente a interação para que a tela possa bloquear
     * somente alterações semânticas antes do primeiro posicionamento.
     * A movimentação e a visibilidade do painel permanecem sempre disponíveis.
     */
    public NaturezaInteracao identificarNaturezaInteracao(
            int mouseX, int mouseY, int larguraTela, int alturaTela,
            Rectangle areaDiagrama) {
        if (!visivel) {
            return NaturezaInteracao.NENHUMA;
        }
        garantirPainel(larguraTela, alturaTela, areaDiagrama);
        if (painelFlutuante == null || !painelFlutuante.contains(mouseX, mouseY)) {
            return NaturezaInteracao.NENHUMA;
        }
        if (obterRetanguloBotaoEsconder().contains(mouseX, mouseY)) {
            return NaturezaInteracao.COMPONENTE_TELA;
        }
        if ((valorNavegavelDefinido && obterRetanguloPontoControle().contains(mouseX, mouseY))
                || (valorNavegavelDefinido && cliqueSobreEixo(mouseX, mouseY))) {
            return NaturezaInteracao.VALOR_SEMANTICO;
        }
        return NaturezaInteracao.COMPONENTE_TELA;
    }

    public String obterDicaBotaoEsconder() {
        return ServicoLocalizacao.getInstancia().texto("ui.tooltip.integerAxis.hide");
    }

    public void atualizarFocoBotaoEsconder(int mouseX, int mouseY) {
        botaoEsconderEmFoco = contemBotaoEsconder(mouseX, mouseY);
    }

    public void limparFocoBotaoEsconder() {
        botaoEsconderEmFoco = false;
    }

    public void limparAlteracaoValorPorInteracao() {
        valorAlteradoPorInteracao = false;
    }

    public int getValorNavegavel() {
        return valorNavegavel;
    }

    public String getValorAbsolutoNavegavel() {
        return Integer.toString(Math.abs(valorNavegavel));
    }

    public String getSinalNavegavel() {
        return valorNavegavel < 0 ? "-" : "+";
    }

    /**
     * Processa o clique no painel flutuante. Retorna true quando o clique foi
     * consumido pelo grafico, evitando que o clique selecione elementos do
     * diagrama que estejam atras do painel.
     */
    public boolean processarPressionamento(int mouseX, int mouseY, int larguraTela, int alturaTela, Rectangle areaDiagrama) {
        ocultadoPorInteracao = false;
        valorAlteradoPorInteracao = false;
        if (!visivel) {
            return false;
        }
        garantirPainel(larguraTela, alturaTela, areaDiagrama);

        Rectangle botao = obterRetanguloBotaoEsconder();
        if (botao.contains(mouseX, mouseY)) {
            ocultar();
            ocultadoPorInteracao = true;
            return true;
        }

        if (valorNavegavelDefinido && obterRetanguloPontoControle().contains(mouseX, mouseY)) {
            arrastandoPontoControle = true;
            atualizarValorPeloMouse(mouseX);
            return true;
        }

        if (valorNavegavelDefinido && cliqueSobreEixo(mouseX, mouseY)) {
            arrastandoPontoControle = true;
            atualizarValorPeloMouse(mouseX);
            return true;
        }

        if (painelFlutuante != null && painelFlutuante.contains(mouseX, mouseY)) {
            arrastandoPainel = true;
            deslocamentoArrasteX = mouseX - painelFlutuante.x;
            deslocamentoArrasteY = mouseY - painelFlutuante.y;
            return true;
        }

        return false;
    }

    public void arrastarPara(int mouseX, int mouseY, int larguraTela, int alturaTela) {
        if (arrastandoPontoControle) {
            atualizarValorPeloMouse(mouseX);
            return;
        }

        if (!arrastandoPainel || painelFlutuante == null) {
            return;
        }

        int novoX = mouseX - deslocamentoArrasteX;
        int novoY = mouseY - deslocamentoArrasteY;
        painelFlutuante.x = limitar(novoX, 8, Math.max(8, larguraTela - painelFlutuante.width - 8));
        painelFlutuante.y = limitar(novoY, 50, Math.max(50, alturaTela - painelFlutuante.height - 8));
    }

    public void finalizarArraste() {
        arrastandoPainel = false;
        arrastandoPontoControle = false;
    }

    public void desenhar(Graphics2D g2, int larguraTela, int alturaTela, Rectangle areaDiagrama) {
        if (!visivel || g2 == null) {
            return;
        }

        garantirPainel(larguraTela, alturaTela, areaDiagrama);
        if (painelFlutuante == null) {
            return;
        }

        Graphics2D g = (Graphics2D) g2.create();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        manterPainelNaTela(larguraTela, alturaTela);

        int x = painelFlutuante.x;
        int y = painelFlutuante.y;
        int largura = painelFlutuante.width;
        int altura = painelFlutuante.height;

        desenharPainel(g, x, y, largura, altura);
        desenharCabecalhoPainel(g, x, y, largura);
        desenharEixos(g, x, y, largura, altura);
        desenharNumeroEscolhido(g, x, y, largura, altura);

        g.dispose();
    }

    private void garantirPainel(int larguraTela, int alturaTela, Rectangle areaDiagrama) {
        if (painelFlutuante == null) {
            painelFlutuante = calcularPainelInicial(larguraTela, alturaTela, areaDiagrama);
        } else {
            ajustarDimensoesPainel(larguraTela, alturaTela);
        }
    }

    private void ajustarDimensoesPainel(int larguraTela, int alturaTela) {
        if (painelFlutuante == null) {
            return;
        }
        Dimension dimensao = layoutPainel.calcularDimensao(larguraTela, obterEscalaAtual(), valorNavegavelDefinido);
        if (painelFlutuante.width == dimensao.width
                && painelFlutuante.height == dimensao.height) {
            return;
        }
        int centroX = painelFlutuante.x + painelFlutuante.width / 2;
        int centroY = painelFlutuante.y + painelFlutuante.height / 2;
        painelFlutuante.width = dimensao.width;
        painelFlutuante.height = dimensao.height;
        painelFlutuante.x = centroX - painelFlutuante.width / 2;
        painelFlutuante.y = centroY - painelFlutuante.height / 2;
        manterPainelNaTela(larguraTela, alturaTela);
    }

    /**
     * Posiciona o painel encostado no topo da área do diagrama de Vergnaud —
     * sempre acima do retângulo superior do diagrama, nunca acima do próprio
     * topo do diagrama (essa faixa é ocupada pelo título da categoria e pelo
     * texto do enunciado, então flutuar o painel ali trocaria uma
     * sobreposição por outra).
     */
    private Rectangle calcularPainelInicial(int larguraTela, int alturaTela, Rectangle areaDiagrama) {
        Dimension dimensao = layoutPainel.calcularDimensao(larguraTela, obterEscalaAtual(), valorNavegavelDefinido);
        int largura = dimensao.width;
        int altura = dimensao.height;
        int x = Math.max(18, (larguraTela - largura) / 2);

        int topoDiagrama = areaDiagrama != null ? areaDiagrama.y : TOPO_PADRAO_DIAGRAMA;
        int y = topoDiagrama + MARGEM_ABAIXO_DIVISORIA;

        return new Rectangle(x, y, largura, altura);
    }

    private void manterPainelNaTela(int larguraTela, int alturaTela) {
        if (painelFlutuante == null) {
            return;
        }
        painelFlutuante.x = limitar(painelFlutuante.x, 8, Math.max(8, larguraTela - painelFlutuante.width - 8));
        painelFlutuante.y = limitar(painelFlutuante.y, 50, Math.max(50, alturaTela - painelFlutuante.height - 8));
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

    private Rectangle obterRetanguloBotaoEsconder() {
        if (painelFlutuante == null) {
            return new Rectangle(0, 0, 0, 0);
        }
        int larguraBotao = layoutPainel.getTamanhoBotao();
        int alturaBotao = layoutPainel.getTamanhoBotao();
        int x = painelFlutuante.x + painelFlutuante.width - larguraBotao
                - layoutPainel.getMargemBotaoDireita();
        int y = painelFlutuante.y + layoutPainel.getMargemBotaoTopo();
        return new Rectangle(x, y, larguraBotao, alturaBotao);
    }

    private String textoLocalizado(String chave) {
        return ServicoLocalizacao.getInstancia().texto(chave);
    }

    private void desenharPainel(Graphics2D g, int x, int y, int largura, int altura) {
        Composite compositeOriginal = g.getComposite();
        Stroke strokeOriginal = g.getStroke();

        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.96f));
        g.setColor(COR_PAINEL);
        g.fillRoundRect(x, y, largura, altura, 16, 16);

        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
        g.setColor(COR_BORDA);
        g.setStroke(new BasicStroke(1.1f));
        g.drawRoundRect(x, y, largura, altura, 16, 16);

        g.setComposite(compositeOriginal);
        g.setStroke(strokeOriginal);
    }

    private void desenharCabecalhoPainel(Graphics2D g, int x, int y, int largura) {
        Stroke strokeOriginal = g.getStroke();

        int alturaCabecalho = layoutPainel.getAlturaCabecalho();
        g.setColor(COR_CABECALHO);
        g.fillRoundRect(x + 1, y + 1, largura - 2, alturaCabecalho, 16, 16);
        g.setColor(COR_BORDA);
        g.setStroke(new BasicStroke(1.0f));
        g.drawLine(x + 10, y + alturaCabecalho + 1,
                x + largura - 10, y + alturaCabecalho + 1);

        g.setFont(new Font("Arial", Font.BOLD, 12));
        g.setColor(COR_TEXTO);
        g.drawString(textoLocalizado("ui.integerAxis.title"), x + 12,
                y + layoutPainel.getOffsetTituloCabecalho());

        Rectangle botao = obterRetanguloBotaoEsconder();
        g.setColor(botaoEsconderEmFoco ? gerard.ui.UITemaGerard.COR_SUPERFICIE_SUAVE : COR_BOTAO);
        g.fillRoundRect(botao.x, botao.y, botao.width, botao.height, 8, 8);
        g.setColor(botaoEsconderEmFoco ? gerard.ui.UITemaGerard.COR_TEXTO_SECUNDARIO : COR_BORDA);
        g.drawRoundRect(botao.x, botao.y, botao.width, botao.height, 8, 8);
        desenharIconeOlho(g, botao);

        g.setStroke(strokeOriginal);
    }


    private void desenharIconeOlho(Graphics2D g, Rectangle botao) {
        Stroke strokeOriginal = g.getStroke();
        g.setStroke(new BasicStroke(1.6f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g.setColor(COR_TEXTO_SECUNDARIO);

        int cx = botao.x + botao.width / 2;
        int cy = botao.y + botao.height / 2;
        int meiaLargura = 8;
        int meiaAltura = 5;

        java.awt.geom.Path2D.Double olho = new java.awt.geom.Path2D.Double();
        olho.moveTo(cx - meiaLargura, cy);
        olho.curveTo(cx - 4, cy - meiaAltura, cx + 4, cy - meiaAltura, cx + meiaLargura, cy);
        olho.curveTo(cx + 4, cy + meiaAltura, cx - 4, cy + meiaAltura, cx - meiaLargura, cy);
        olho.closePath();
        g.draw(olho);
        g.fillOval(cx - 2, cy - 2, 4, 4);
        g.setStroke(strokeOriginal);
    }

    private void desenharEixos(Graphics2D g, int x, int y, int largura, int altura) {
        int origemX = obterOrigemX();
        int origemY = obterOrigemY();
        int xEsquerda = obterXEsquerda();
        int xDireita = obterXDireita();
        int escala = obterEscalaAtual();

        Stroke strokeOriginal = g.getStroke();
        g.setFont(new Font("Arial", Font.PLAIN, 11));
        FontMetrics fm = g.getFontMetrics();

        g.setColor(COR_TEXTO_SECUNDARIO);
        g.setStroke(new BasicStroke(1.4f));
        g.drawLine(xEsquerda, origemY, xDireita, origemY);

        desenharSeta(g, xDireita, origemY, -1, 0);
        desenharSeta(g, xEsquerda, origemY, 1, 0);

        int espacamento = obterEspacamentoAtual();
        for (int i = -escala; i <= escala; i++) {
            int px = origemX + i * espacamento;
            if (px < xEsquerda || px > xDireita) {
                continue;
            }
            g.drawLine(px, origemY - 4, px, origemY + 4);
            String rotulo = Integer.toString(i);
            g.drawString(rotulo, px - fm.stringWidth(rotulo) / 2, origemY + 20);
        }

        g.setFont(new Font("Arial", Font.BOLD, 12));
        g.setColor(COR_TEXTO_SECUNDARIO);
        g.drawString(textoLocalizado("ui.integerAxis.negativeIntegers"), xEsquerda, y + layoutPainel.getOffsetRotulosLados());
        String positivo = textoLocalizado("ui.integerAxis.positiveIntegers");
        g.drawString(positivo, xDireita - g.getFontMetrics().stringWidth(positivo), y + layoutPainel.getOffsetRotulosLados());
        g.drawString(textoLocalizado("ui.integerAxis.axisLabel"), xDireita - 8, origemY - 8);

        if (valorNavegavelDefinido) {
            g.setFont(new Font("Arial", Font.PLAIN, 10));
            g.setColor(gerard.ui.UITemaGerard.COR_TEXTO_SECUNDARIO);
            String instrucao = textoLocalizado("ui.integerAxis.instruction");
            g.drawString(instrucao, origemX - g.getFontMetrics().stringWidth(instrucao) / 2, y + layoutPainel.getOffsetInstrucao());
        }

        g.setStroke(strokeOriginal);
    }

    private void desenharSeta(Graphics2D g, int x, int y, int sentidoX, int sentidoY) {
        int tamanho = 7;
        if (sentidoX < 0) {
            g.drawLine(x, y, x - tamanho, y - tamanho / 2);
            g.drawLine(x, y, x - tamanho, y + tamanho / 2);
        } else if (sentidoX > 0) {
            g.drawLine(x, y, x + tamanho, y - tamanho / 2);
            g.drawLine(x, y, x + tamanho, y + tamanho / 2);
        } else if (sentidoY < 0) {
            g.drawLine(x, y, x - tamanho / 2, y - tamanho);
            g.drawLine(x, y, x + tamanho / 2, y - tamanho);
        } else if (sentidoY > 0) {
            g.drawLine(x, y, x - tamanho / 2, y + tamanho);
            g.drawLine(x, y, x + tamanho / 2, y + tamanho);
        }
    }

    private void desenharNumeroEscolhido(Graphics2D g, int x, int y, int largura, int altura) {
        if (!valorNavegavelDefinido) {
            return;
        }

        int origemY = obterOrigemY();
        int px = obterXDoValor(obterValorParaPosicionamento());
        String rotulo = formatarNumeroEscolhido();

        g.setFont(new Font("Arial", Font.BOLD, 12));
        FontMetrics fm = g.getFontMetrics();
        int larguraTexto = fm.stringWidth(rotulo);

        desenharMarcadorPontoControle(g, px, origemY);
        g.setColor(COR_AZUL);
        g.drawString(rotulo, px - larguraTexto / 2, origemY + 20);
    }


    private void desenharMarcadorPontoControle(Graphics2D g, int px, int py) {
        Color azul = COR_AZUL;
        g.setColor(azul);
        g.fillOval(px - 7, py - 7, 14, 14);
        g.setColor(COR_PAINEL);
        g.fillOval(px - 3, py - 3, 6, 6);
        g.setColor(azul);
        g.setStroke(new BasicStroke(1.6f));
        g.drawOval(px - 7, py - 7, 14, 14);
    }

    public Rectangle obterAreaVisualPontoControle() {
        if (!visivel || painelFlutuante == null || !valorNavegavelDefinido) {
            return new Rectangle(0, 0, 0, 0);
        }
        int px = obterXDoValor(obterValorParaPosicionamento());
        int py = obterOrigemY();
        return new Rectangle(px - 7, py - 7, 14, 14);
    }

    /** Retorna uma cópia da área do painel para o marcador de origem. */
    public Rectangle obterAreaVisualPainel() {
        return painelFlutuante == null
                ? new Rectangle(0, 0, 0, 0)
                : new Rectangle(painelFlutuante);
    }

    public void desenharPontoControleEmPrimeiroPlano(Graphics2D g2) {
        if (g2 == null || !visivel || painelFlutuante == null || !valorNavegavelDefinido) {
            return;
        }
        desenharMarcadorPontoControle(g2, obterXDoValor(obterValorParaPosicionamento()), obterOrigemY());
    }

    private String formatarNumeroEscolhido() {
        if (!valorNavegavelDefinido || magnitudeDesconhecida) {
            return "";
        }
        return Integer.toString(valorNavegavel);
    }

    /**
     * Enquanto a magnitude ainda não foi escolhida, o ponto de controle deve
     * permanecer do lado do sinal já registrado no diagrama de Vergnaud, e
     * não colapsar na origem — caso contrário o eixo perde a sincronização
     * visual com o sinal que o círculo da relação já exibe.
     */
    private int obterValorParaPosicionamento() {
        if (magnitudeDesconhecida && valorNavegavelDefinido) {
            return "-".equals(sinalEscolhido) ? -1 : 1;
        }
        return valorNavegavel;
    }

    private boolean magnitudeEhDesconhecida(String textoNormalizado) {
        return textoNormalizado == null
                || SimboloDesconhecido.eh(textoNormalizado)
                || textoNormalizado.length() == 0;
    }

    private int obterValorAbsoluto(String texto) {
        try {
            String limpo = normalizarValor(texto);
            if (magnitudeEhDesconhecida(limpo)) {
                return 0;
            }
            return Math.abs(Integer.parseInt(limpo));
        } catch (Exception ex) {
            return 0;
        }
    }

    private String normalizarValor(String valor) {
        if (valor == null) {
            return "";
        }
        String texto = valor.trim();
        while (texto.startsWith("+") || texto.startsWith("-")) {
            texto = texto.substring(1).trim();
        }
        return texto;
    }

    private int obterOrigemX() {
        if (painelFlutuante == null) {
            return 0;
        }
        return painelFlutuante.x + painelFlutuante.width / 2;
    }

    private int obterOrigemY() {
        if (painelFlutuante == null) {
            return 0;
        }
        return painelFlutuante.y
                + layoutPainel.getOffsetOrigemEixo(valorNavegavelDefinido);
    }

    private int obterXEsquerda() {
        if (painelFlutuante == null) {
            return 0;
        }
        return painelFlutuante.x + layoutPainel.getMargemHorizontalEixo();
    }

    private int obterXDireita() {
        if (painelFlutuante == null) {
            return 0;
        }
        return painelFlutuante.x + painelFlutuante.width - layoutPainel.getMargemHorizontalEixo();
    }

    private int obterEscalaAtual() {
        if (!escalaFixaDefinida) {
            escalaFixa = Math.max(5, Math.abs(valorNavegavel));
            escalaFixaDefinida = true;
        }
        return escalaFixa;
    }

    private int obterEspacamentoAtual() {
        int escala = obterEscalaAtual();
        int larguraMeiaReta = Math.max(1, Math.min(obterOrigemX() - obterXEsquerda(), obterXDireita() - obterOrigemX()));
        return Math.max(1, larguraMeiaReta / escala);
    }

    private int obterXDoValor(int valor) {
        int escala = obterEscalaAtual();
        int valorLimitado = limitar(valor, -escala, escala);
        return obterOrigemX() + valorLimitado * obterEspacamentoAtual();
    }

    private int obterValorPeloX(int mouseX) {
        int escala = obterEscalaAtual();
        int x = limitar(mouseX, obterXEsquerda(), obterXDireita());
        int relativo = x - obterOrigemX();
        int espacamento = Math.max(1, obterEspacamentoAtual());
        int valor = (int) Math.round(relativo / (double) espacamento);
        return limitar(valor, -escala, escala);
    }

    private void atualizarValorPeloMouse(int mouseX) {
        int novoValor = obterValorPeloX(mouseX);
        if (novoValor != valorNavegavel || !valorNavegavelDefinido || magnitudeDesconhecida) {
            valorNavegavel = novoValor;
            valorBase = Integer.toString(Math.abs(novoValor));
            sinalEscolhido = novoValor < 0 ? "-" : "+";
            valorNavegavelDefinido = true;
            magnitudeDesconhecida = false;
            valorAlteradoPorInteracao = true;
        }
    }

    private Rectangle obterRetanguloPontoControle() {
        if (painelFlutuante == null || !valorNavegavelDefinido) {
            return new Rectangle(0, 0, 0, 0);
        }
        int px = obterXDoValor(obterValorParaPosicionamento());
        int py = obterOrigemY();
        return new Rectangle(px - 14, py - 14, 28, 28);
    }

    private boolean cliqueSobreEixo(int mouseX, int mouseY) {
        if (painelFlutuante == null) {
            return false;
        }
        int origemY = obterOrigemY();
        return mouseX >= obterXEsquerda()
                && mouseX <= obterXDireita()
                && mouseY >= origemY - 24
                && mouseY <= origemY + 24;
    }
}
