package gerard.ui.venn;

import gerard.campoaditivo.diagrama.elementos.CirculoVenn;
import gerard.campoaditivo.diagrama.elementos.QuadradinhoVenn;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Consultas puras sobre as listas de círculos e quadradinhos do diagrama
 * Venn/complementar: contagem, seleção e hit-test (qual elemento está sob um
 * ponto).
 *
 * Extraído de Main.TelaGerard como parte da Fase 2 do plano de refatoração —
 * ver PLANO_REFATORACAO_ARQUITETURA_GERARD.md.
 *
 * Estratégia desta extração: como esses métodos têm muitos pontos de chamada
 * dentro de TelaGerard (contarQuadradinhosNoCirculo sozinho tem 14), os
 * métodos originais foram mantidos em TelaGerard como wrappers de 1 linha
 * delegando para cá — nenhum call site precisou ser alterado. As listas
 * (quadradinhosVenn, circulosVenn) continuam sendo campos de TelaGerard;
 * aqui elas só são recebidas como parâmetro.
 */
public final class ConsultasDiagramaVenn {

    private ConsultasDiagramaVenn() {
    }

    /** Conta quantos quadradinhos têm o centro dentro do círculo informado. */
    public static int contarQuadradinhosNoCirculo(
            List<QuadradinhoVenn> quadradinhosVenn, CirculoVenn circulo) {
        int total = 0;
        for (int i = 0; i < quadradinhosVenn.size(); i++) {
            QuadradinhoVenn q = quadradinhosVenn.get(i);
            if (circulo.contem(q.centroX(), q.centroY())) {
                total++;
            }
        }
        return total;
    }

    /**
     * Retorna o quadradinho sob o ponto (x, y), varrendo de cima para baixo
     * (do último para o primeiro da lista, preservando a ordem de
     * empilhamento visual original), ou null se nenhum contiver o ponto.
     */
    public static QuadradinhoVenn encontrarQuadradinhoVenn(
            List<QuadradinhoVenn> quadradinhosVenn, int x, int y) {
        for (int i = quadradinhosVenn.size() - 1; i >= 0; i--) {
            QuadradinhoVenn q = quadradinhosVenn.get(i);
            if (q.contem(x, y)) {
                return q;
            }
        }
        return null;
    }

    /**
     * Retorna o círculo sob o ponto (x, y), varrendo de cima para baixo
     * (do último para o primeiro da lista), ou null se nenhum contiver o ponto.
     */
    public static CirculoVenn encontrarCirculoVenn(
            List<CirculoVenn> circulosVenn, int x, int y) {
        for (int i = circulosVenn.size() - 1; i >= 0; i--) {
            CirculoVenn c = circulosVenn.get(i);
            if (c.contem(x, y)) {
                return c;
            }
        }
        return null;
    }

    /**
     * Retorna, em nova lista, os quadradinhos cujo centro está contido no
     * agrupamento informado. Lista vazia se agrupamento for null.
     */
    public static ArrayList<QuadradinhoVenn> obterQuadradinhosDoAgrupamento(
            List<QuadradinhoVenn> quadradinhosVenn, CirculoVenn agrupamento) {
        ArrayList<QuadradinhoVenn> encontrados = new ArrayList<QuadradinhoVenn>();
        if (agrupamento == null) {
            return encontrados;
        }
        for (QuadradinhoVenn quadradinho : quadradinhosVenn) {
            if (agrupamento.contem(quadradinho.centroX(), quadradinho.centroY())) {
                encontrados.add(quadradinho);
            }
        }
        return encontrados;
    }

    /**
     * Retorna os quadradinhos de uma barra (círculo usado como barra de
     * comparação), ordenados de baixo para cima (linha mais próxima da base
     * primeiro; dentro da mesma linha, da esquerda para a direita).
     * Lista vazia se barra for null.
     */
    public static ArrayList<QuadradinhoVenn> quadradinhosDaBarraOrdenadosDeBaixoParaCima(
            List<QuadradinhoVenn> quadradinhosVenn, CirculoVenn barra) {
        ArrayList<QuadradinhoVenn> unidades = new ArrayList<QuadradinhoVenn>();
        if (barra == null) {
            return unidades;
        }
        for (QuadradinhoVenn quadradinho : quadradinhosVenn) {
            if (barra.contem(quadradinho.centroX(), quadradinho.centroY())) {
                unidades.add(quadradinho);
            }
        }
        Collections.sort(unidades, new Comparator<QuadradinhoVenn>() {
            @Override
            public int compare(QuadradinhoVenn a, QuadradinhoVenn b) {
                int porLinha = Integer.compare(b.y, a.y);
                if (porLinha != 0) {
                    return porLinha;
                }
                return Integer.compare(a.x, b.x);
            }
        });
        return unidades;
    }

    /**
     * Retorna a coordenada Y do topo do conteúdo (quadradinho mais alto)
     * dentro da barra informada, ou o topo da própria barra (barra.y +
     * barra.altura) se ela não contiver nenhum quadradinho.
     */
    public static int topoConteudoBarraComparacao(
            List<QuadradinhoVenn> quadradinhosVenn, CirculoVenn barra) {
        int topo = barra.y + barra.altura;
        boolean encontrou = false;
        for (QuadradinhoVenn q : quadradinhosVenn) {
            if (barra.contem(q.centroX(), q.centroY())) {
                topo = Math.min(topo, q.y);
                encontrou = true;
            }
        }
        return encontrou ? topo : barra.y + barra.altura;
    }

    /**
     * Monta uma string de assinatura representando o estado relevante do
     * diagrama Venn (tipo de situação, área e valores), usada para detectar
     * se uma reconstrução do diagrama é necessária.
     */
    public static String criarAssinaturaDiagramaVenn(
            gerard.campoaditivo.modelo.TipoSituacaoAditiva tipoVenn,
            java.awt.Rectangle area, int[] valores) {
        StringBuilder assinatura = new StringBuilder();
        assinatura.append(tipoVenn != null ? tipoVenn.name() : "");
        assinatura.append('|').append(area.x).append(',').append(area.y)
                .append(',').append(area.width).append(',').append(area.height);
        if (valores != null) {
            for (int i = 0; i < valores.length; i++) {
                assinatura.append('|').append(valores[i]);
            }
        }
        return assinatura.toString();
    }
}
