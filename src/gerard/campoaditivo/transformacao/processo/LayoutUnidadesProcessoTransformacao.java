package gerard.campoaditivo.transformacao.processo;

import gerard.campoaditivo.diagrama.elementos.CirculoVenn;
import gerard.semantica.numero.NumeroInteiro;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/** Posiciona os quadradinhos nos estados e no funil correspondente ao sinal. */
public final class LayoutUnidadesProcessoTransformacao {
    private final PoliticaVisualProcessoTransformacao politica =
            new PoliticaVisualProcessoTransformacao();

    public List<Rectangle> calcular(CirculoVenn zona, int indice,
            int quantidade) {
        if (zona == null || quantidade <= 0) {
            return new ArrayList<Rectangle>();
        }
        if (indice != 1) {
            Rectangle area = new Rectangle(zona.x + 12, zona.y + 48,
                    Math.max(20, zona.largura - 24),
                    Math.max(20, zona.altura - 82));
            return distribuir(area, quantidade, 13);
        }

        TipoProcessoTransformacao tipo = politica.classificar(
                new NumeroInteiro(zona.valorReferencia));
        GeometriaProcessoTransformacao geometria =
                GeometriaProcessoTransformacao.calcular(
                        Arrays.asList(zonaEstadoVirtual(zona, true), zona,
                                zonaEstadoVirtual(zona, false)));
        Polygon funil = tipo == TipoProcessoTransformacao.RETIRADA
                ? geometria.getFunilRetirada()
                : geometria.getFunilInsercao();
        return distribuirNoFunil(funil, quantidade, 9);
    }

    /**
     * Recria apenas as referências laterais necessárias ao cálculo geométrico
     * quando o layout é solicitado para a zona central isoladamente.
     */
    private CirculoVenn zonaEstadoVirtual(CirculoVenn processo,
            boolean inicial) {
        int largura = Math.max(100, processo.largura - 12);
        int distancia = Math.max(22, processo.largura / 4);
        int x = inicial
                ? processo.x - distancia - largura
                : processo.x + processo.largura + distancia;
        int altura = Math.max(160, processo.altura - 54);
        int y = processo.y + (processo.altura - altura) / 2;
        CirculoVenn zona = new CirculoVenn(x, y, largura, altura,
                inicial ? "Estado inicial" : "Estado final", 0, true);
        zona.formaRetangular = true;
        return zona;
    }

    public List<Rectangle> calcular(List<CirculoVenn> zonas, int indice,
            int quantidade) {
        if (zonas == null || indice < 0 || indice >= zonas.size()
                || quantidade <= 0) {
            return new ArrayList<Rectangle>();
        }
        CirculoVenn zona = zonas.get(indice);
        if (indice != 1) {
            Rectangle area = new Rectangle(zona.x + 12, zona.y + 48,
                    Math.max(20, zona.largura - 24),
                    Math.max(20, zona.altura - 82));
            return distribuir(area, quantidade, 13);
        }
        TipoProcessoTransformacao tipo = politica.classificar(
                new NumeroInteiro(zona.valorReferencia));
        GeometriaProcessoTransformacao geometria =
                GeometriaProcessoTransformacao.calcular(zonas);
        Polygon funil = tipo == TipoProcessoTransformacao.RETIRADA
                ? geometria.getFunilRetirada()
                : geometria.getFunilInsercao();
        return distribuirNoFunil(funil, quantidade, 9);
    }


    /**
     * Distribui as unidades respeitando a geometria trapezoidal do funil.
     *
     * Diferentemente de uma grade retangular, cada linha calcula a largura
     * realmente disponível no polígono. As linhas superiores recebem mais
     * unidades e as inferiores ficam progressivamente menores, evitando que
     * quadradinhos atravessem as bordas inclinadas ou ocupem o gargalo.
     */
    private List<Rectangle> distribuirNoFunil(Polygon funil, int quantidade,
            int tamanhoMaximo) {
        List<Rectangle> vazio = new ArrayList<Rectangle>();
        if (funil == null || funil.npoints < 4 || quantidade <= 0) {
            return vazio;
        }

        Rectangle limites = funil.getBounds();
        for (int tamanho = tamanhoMaximo; tamanho >= 4; tamanho--) {
            int espacamentoHorizontal = Math.max(4, tamanho - 2);
            int espacamentoVertical = Math.max(5, tamanho - 1);
            int margemVertical = Math.max(7, tamanho / 2 + 3);
            int alturaUtil = limites.height - 2 * margemVertical;
            if (alturaUtil < tamanho) {
                continue;
            }

            int maxLinhas = Math.max(1,
                    (alturaUtil + espacamentoVertical)
                    / (tamanho + espacamentoVertical));
            int linhasIniciais = Math.max(1,
                    (int) Math.ceil(Math.sqrt(quantidade * 1.35d)));
            linhasIniciais = Math.min(linhasIniciais, maxLinhas);

            for (int linhas = linhasIniciais; linhas <= maxLinhas; linhas++) {
                List<LinhaFunil> linhasFunil = criarLinhasDoFunil(funil,
                        limites, linhas, tamanho, espacamentoHorizontal,
                        margemVertical);
                int capacidadeTotal = 0;
                for (LinhaFunil linha : linhasFunil) {
                    capacidadeTotal += linha.capacidade;
                }
                if (capacidadeTotal < quantidade) {
                    continue;
                }

                int[] porLinha = distribuirQuantidadePorLinhas(
                        linhasFunil, quantidade);
                List<Rectangle> resultado = new ArrayList<Rectangle>();
                for (int i = 0; i < linhasFunil.size(); i++) {
                    LinhaFunil linha = linhasFunil.get(i);
                    int quantidadeNaLinha = porLinha[i];
                    if (quantidadeNaLinha <= 0) {
                        continue;
                    }
                    int larguraGrade = quantidadeNaLinha * tamanho
                            + Math.max(0, quantidadeNaLinha - 1)
                            * espacamentoHorizontal;
                    int inicioX = linha.centroX - larguraGrade / 2;
                    for (int coluna = 0; coluna < quantidadeNaLinha; coluna++) {
                        Rectangle unidade = new Rectangle(
                                inicioX + coluna * (tamanho
                                        + espacamentoHorizontal),
                                linha.y, tamanho, tamanho);
                        if (estaDentroDoFunil(funil, unidade)) {
                            resultado.add(unidade);
                        }
                    }
                }
                if (resultado.size() == quantidade) {
                    return resultado;
                }
            }
        }

        // Fallback defensivo para geometrias extremamente pequenas. Mantém a
        // quantidade completa, mas ainda usa a área interna calculada.
        Rectangle area = new Rectangle(limites.x + 8, limites.y + 8,
                Math.max(20, limites.width - 16),
                Math.max(20, limites.height - 16));
        return distribuir(area, quantidade, 4);
    }

    private List<LinhaFunil> criarLinhasDoFunil(Polygon funil,
            Rectangle limites, int quantidadeLinhas, int tamanho,
            int espacamentoHorizontal, int margemVertical) {
        List<LinhaFunil> linhas = new ArrayList<LinhaFunil>();
        int topo = limites.y + margemVertical;
        int base = limites.y + limites.height - margemVertical - tamanho;
        double passo = quantidadeLinhas <= 1 ? 0.0d
                : (base - topo) / (double) (quantidadeLinhas - 1);

        for (int i = 0; i < quantidadeLinhas; i++) {
            int y = (int) Math.round(topo + i * passo);
            int[] faixa = calcularFaixaHorizontal(funil,
                    y + tamanho / 2.0d);
            int margemLateral = Math.max(6, tamanho / 2 + 2);
            int esquerda = faixa[0] + margemLateral;
            int direita = faixa[1] - margemLateral;
            int largura = Math.max(0, direita - esquerda);
            int capacidade = largura < tamanho ? 0
                    : 1 + (largura - tamanho)
                    / (tamanho + espacamentoHorizontal);
            linhas.add(new LinhaFunil(y, (esquerda + direita) / 2,
                    capacidade));
        }
        return linhas;
    }

    private int[] distribuirQuantidadePorLinhas(List<LinhaFunil> linhas,
            int quantidade) {
        int[] resultado = new int[linhas.size()];
        int restantes = quantidade;

        // Garante a forma visual 2–2–1, 3–3–2–1 etc.: primeiro ocupa todas as
        // linhas possíveis e depois acrescenta unidades da abertura para o
        // gargalo, respeitando a capacidade progressivamente menor.
        for (int i = 0; i < linhas.size() && restantes > 0; i++) {
            if (linhas.get(i).capacidade > 0) {
                resultado[i] = 1;
                restantes--;
            }
        }
        while (restantes > 0) {
            boolean adicionou = false;
            for (int i = 0; i < linhas.size() && restantes > 0; i++) {
                if (resultado[i] < linhas.get(i).capacidade) {
                    resultado[i]++;
                    restantes--;
                    adicionou = true;
                }
            }
            if (!adicionou) {
                break;
            }
        }
        return resultado;
    }

    private int[] calcularFaixaHorizontal(Polygon poligono, double y) {
        List<Double> intersecoes = new ArrayList<Double>();
        for (int i = 0; i < poligono.npoints; i++) {
            int seguinte = (i + 1) % poligono.npoints;
            double x1 = poligono.xpoints[i];
            double y1 = poligono.ypoints[i];
            double x2 = poligono.xpoints[seguinte];
            double y2 = poligono.ypoints[seguinte];
            if (Math.abs(y2 - y1) < 0.0001d) {
                continue;
            }
            double minimoY = Math.min(y1, y2);
            double maximoY = Math.max(y1, y2);
            if (y < minimoY || y > maximoY) {
                continue;
            }
            double proporcao = (y - y1) / (y2 - y1);
            intersecoes.add(x1 + proporcao * (x2 - x1));
        }
        if (intersecoes.size() < 2) {
            Rectangle limites = poligono.getBounds();
            return new int[] {limites.x, limites.x + limites.width};
        }
        double esquerda = intersecoes.get(0);
        double direita = intersecoes.get(0);
        for (Double x : intersecoes) {
            esquerda = Math.min(esquerda, x.doubleValue());
            direita = Math.max(direita, x.doubleValue());
        }
        return new int[] {(int) Math.ceil(esquerda),
            (int) Math.floor(direita)};
    }

    private boolean estaDentroDoFunil(Polygon funil, Rectangle unidade) {
        int margem = 1;
        return funil.contains(unidade.x + margem, unidade.y + margem)
                && funil.contains(unidade.x + unidade.width - margem,
                        unidade.y + margem)
                && funil.contains(unidade.x + margem,
                        unidade.y + unidade.height - margem)
                && funil.contains(unidade.x + unidade.width - margem,
                        unidade.y + unidade.height - margem);
    }

    private static final class LinhaFunil {
        private final int y;
        private final int centroX;
        private final int capacidade;

        private LinhaFunil(int y, int centroX, int capacidade) {
            this.y = y;
            this.centroX = centroX;
            this.capacidade = Math.max(0, capacidade);
        }
    }

    private List<Rectangle> distribuir(Rectangle area, int quantidade,
            int tamanhoMaximo) {
        List<Rectangle> posicoes = new ArrayList<Rectangle>();
        if (area == null || quantidade <= 0 || area.width <= 0 || area.height <= 0) {
            return posicoes;
        }
        double proporcao = area.width / (double) Math.max(1, area.height);
        int colunas = Math.max(1,
                (int) Math.ceil(Math.sqrt(quantidade * proporcao)));
        colunas = Math.min(colunas, quantidade);
        int linhas = (int) Math.ceil(quantidade / (double) colunas);
        int passoX = Math.max(5, area.width / Math.max(1, colunas));
        int passoY = Math.max(5, area.height / Math.max(1, linhas));
        int tamanho = Math.max(4, Math.min(tamanhoMaximo,
                Math.min(passoX - 3, passoY - 3)));
        int larguraGrade = (colunas - 1) * passoX + tamanho;
        int alturaGrade = (linhas - 1) * passoY + tamanho;
        int inicioX = area.x + Math.max(0, (area.width - larguraGrade) / 2);
        int inicioY = area.y + Math.max(0, (area.height - alturaGrade) / 2);
        for (int i = 0; i < quantidade; i++) {
            int coluna = i % colunas;
            int linha = i / colunas;
            posicoes.add(new Rectangle(
                    inicioX + coluna * passoX,
                    inicioY + linha * passoY,
                    tamanho, tamanho));
        }
        return posicoes;
    }
}
