package gerard.ui.categoria;

import java.awt.Rectangle;

/**
 * Geometria da faixa de atalhos de categoria: o grupo inteiro (6 ícones, 2 sorteios, separador e o "?" de próximo
 * passo) é centralizado na largura da tela. Só calcula retângulos; quem tem os botões os posiciona.
 */
public final class GeometriaBarraAtalhosCategoria {
    public static final int LARGURA_SEPARADOR = 32;
    public static final int LARGURA_BOTAO_SORTEIO = 34;
    public static final int GAP_BOTAO_SORTEIO = 16;

    public Rectangle composicao, transformacao, comparacao;
    public Rectangle sorteioMedidas, separador, sorteioRelacoes;
    public Rectangle composicaoTransformacoes, transformacaoRelacao, composicaoRelacoes;
    public Rectangle areaGrupoMedidas, areaGrupoRelacoes;
    public int xCentroGrupoMedidas, xCentroGrupoRelacoes;
    public int xPadraoProximoPasso, yPadraoProximoPasso;

    private GeometriaBarraAtalhosCategoria() {
    }

    public static GeometriaBarraAtalhosCategoria calcular(int larguraTela, int alturaPainelAtalhos) {
        GeometriaBarraAtalhosCategoria g = new GeometriaBarraAtalhosCategoria();
        int lar = IconeCategoria.LARGURA, alt = IconeCategoria.ALTURA;
        int gapEntreIcones = 24;
        int gapAntesBotao = 32;
        int larguraTotal = lar * 6 + gapEntreIcones * 4 + LARGURA_SEPARADOR
                + GAP_BOTAO_SORTEIO * 4 + LARGURA_BOTAO_SORTEIO * 2
                + gapAntesBotao + IconeProximoPasso.LARGURA;
        int x = Math.max(18, (larguraTela - larguraTotal) / 2);
        int centroFaixa = 45 + alturaPainelAtalhos / 2;
        int yIcone = centroFaixa - alt / 2;
        int ySorteio = centroFaixa - LARGURA_BOTAO_SORTEIO / 2;

        g.composicao = new Rectangle(x, yIcone, lar, alt);
        x += lar + gapEntreIcones;
        g.transformacao = new Rectangle(x, yIcone, lar, alt);
        x += lar + gapEntreIcones;
        g.comparacao = new Rectangle(x, yIcone, lar, alt);
        x += lar;

        // Os dois sorteios ficam colados um de cada lado do separador central: simétricos, nenhum solto na borda.
        x += GAP_BOTAO_SORTEIO;
        g.sorteioMedidas = new Rectangle(x, ySorteio, LARGURA_BOTAO_SORTEIO, LARGURA_BOTAO_SORTEIO);
        x += LARGURA_BOTAO_SORTEIO + GAP_BOTAO_SORTEIO;

        g.separador = new Rectangle(x, yIcone, LARGURA_SEPARADOR, alt);
        x += LARGURA_SEPARADOR;

        x += GAP_BOTAO_SORTEIO;
        g.sorteioRelacoes = new Rectangle(x, ySorteio, LARGURA_BOTAO_SORTEIO, LARGURA_BOTAO_SORTEIO);
        x += LARGURA_BOTAO_SORTEIO + GAP_BOTAO_SORTEIO;

        g.composicaoTransformacoes = new Rectangle(x, yIcone, lar, alt);
        x += lar + gapEntreIcones;
        g.transformacaoRelacao = new Rectangle(x, yIcone, lar, alt);
        x += lar + gapEntreIcones;
        g.composicaoRelacoes = new Rectangle(x, yIcone, lar, alt);
        x += lar + gapAntesBotao;

        g.xCentroGrupoMedidas = (g.composicao.x + g.comparacao.x + lar) / 2;
        g.xCentroGrupoRelacoes = (g.composicaoTransformacoes.x + g.composicaoRelacoes.x + lar) / 2;

        // Retângulo delimitador de cada grupo: padding menor em cima (não encosta no rótulo), maior nos demais lados.
        int padLados = 10, padTopo = 4, padBaixo = 10;
        g.areaGrupoMedidas = new Rectangle(g.composicao.x - padLados, g.composicao.y - padTopo,
                (g.comparacao.x + lar) - g.composicao.x + padLados * 2, alt + padTopo + padBaixo);
        g.areaGrupoRelacoes = new Rectangle(g.composicaoTransformacoes.x - padLados, g.composicaoTransformacoes.y - padTopo,
                (g.composicaoRelacoes.x + lar) - g.composicaoTransformacoes.x + padLados * 2, alt + padTopo + padBaixo);

        // Posição padrão do "?" de próximo passo: ao lado dos 6 ícones (aplicada em reposicionarBotaoAtalhoProximoPasso).
        g.xPadraoProximoPasso = x;
        g.yPadraoProximoPasso = centroFaixa - IconeProximoPasso.ALTURA / 2;
        return g;
    }
}
