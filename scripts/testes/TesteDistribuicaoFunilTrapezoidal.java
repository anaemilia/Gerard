package gerard.campoaditivo.transformacao.processo;

import gerard.campoaditivo.diagrama.elementos.CirculoVenn;
import gerard.campoaditivo.diagrama.elementos.QuadradinhoVenn;
import gerard.campoaditivo.modelo.DefinicaoDiagramaAditivo;
import gerard.campoaditivo.modelo.TipoSituacaoAditiva;
import gerard.campoaditivo.sincronizacao.EstadoSemanticoCompartilhado;
import gerard.campoaditivo.venn.modelo.CenaDiagramaVenn;
import gerard.campoaditivo.venn.modelo.NoDiagramaVenn;
import gerard.i18n.ServicoLocalizacao;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.imageio.ImageIO;

public final class TesteDistribuicaoFunilTrapezoidal {
    private static int verificacoes;

    public static void main(String[] args) throws Exception {
        testarInsercaoCinco();
        testarRetiradaCinco();
        testarQuantidadeMaior();
        gerarPrevia("build/PREVIA_FUNIL_TRAPEZOIDAL_C162_MAIS_5.png");
        System.out.println("Distribuição trapezoidal do funil aprovada: "
                + verificacoes + " verificações.");
    }

    private static void testarInsercaoCinco() {
        List<CirculoVenn> zonas = criarZonas(5);
        Polygon funil = GeometriaProcessoTransformacao.calcular(zonas)
                .getFunilInsercao();
        List<Rectangle> unidades = new LayoutUnidadesProcessoTransformacao()
                .calcular(zonas, 1, 5);
        confirmar(unidades.size() == 5,
                "inserção +5 deve manter cinco quadradinhos");
        confirmar(todasDentro(funil, unidades),
                "todos os quadradinhos de inserção devem ficar dentro do cone");
        Map<Integer, Integer> porLinha = contarPorLinha(unidades);
        confirmar(porLinha.size() == 3,
                "+5 deve usar três linhas progressivas");
        int[] esperado = new int[] {2, 2, 1};
        int i = 0;
        for (Integer quantidade : porLinha.values()) {
            confirmar(quantidade.intValue() == esperado[i++],
                    "+5 deve formar linhas 2-2-1");
        }
    }

    private static void testarRetiradaCinco() {
        List<CirculoVenn> zonas = criarZonas(-5);
        Polygon funil = GeometriaProcessoTransformacao.calcular(zonas)
                .getFunilRetirada();
        List<Rectangle> unidades = new LayoutUnidadesProcessoTransformacao()
                .calcular(zonas, 1, 5);
        confirmar(unidades.size() == 5,
                "retirada -5 deve manter cinco quadradinhos");
        confirmar(todasDentro(funil, unidades),
                "todos os quadradinhos de retirada devem ficar dentro do cone");
    }

    private static void testarQuantidadeMaior() {
        List<CirculoVenn> zonas = criarZonas(18);
        Polygon funil = GeometriaProcessoTransformacao.calcular(zonas)
                .getFunilInsercao();
        List<Rectangle> unidades = new LayoutUnidadesProcessoTransformacao()
                .calcular(zonas, 1, 18);
        confirmar(unidades.size() == 18,
                "o layout deve preservar magnitudes maiores");
        confirmar(todasDentro(funil, unidades),
                "magnitudes maiores não podem atravessar a borda inclinada");
    }

    private static List<CirculoVenn> criarZonas(int transformacao) {
        CenaDiagramaVenn cena = new LayoutProcessoTransformacao().criarCena(
                new Rectangle(0, 0, 760, 510),
                new DefinicaoDiagramaAditivo("Transformação de medidas",
                        "Estado inicial", "Transformação", "Estado final"),
                new int[] {10, transformacao, 10 + transformacao});
        List<CirculoVenn> zonas = new ArrayList<CirculoVenn>();
        for (NoDiagramaVenn no : cena.getNos()) {
            CirculoVenn zona = new CirculoVenn(no.getX(), no.getY(),
                    no.getLargura(), no.getAltura(), no.getRotulo(),
                    no.getValorReferencia(), no.isExibirQuadradinhos());
            zona.formaRetangular = true;
            zonas.add(zona);
        }
        return zonas;
    }

    private static boolean todasDentro(Polygon funil,
            List<Rectangle> unidades) {
        for (Rectangle unidade : unidades) {
            int margem = 1;
            if (!funil.contains(unidade.x + margem, unidade.y + margem)
                    || !funil.contains(unidade.x + unidade.width - margem,
                            unidade.y + margem)
                    || !funil.contains(unidade.x + margem,
                            unidade.y + unidade.height - margem)
                    || !funil.contains(unidade.x + unidade.width - margem,
                            unidade.y + unidade.height - margem)) {
                return false;
            }
        }
        return true;
    }

    private static Map<Integer, Integer> contarPorLinha(
            List<Rectangle> unidades) {
        Map<Integer, Integer> resultado = new LinkedHashMap<Integer, Integer>();
        for (Rectangle unidade : unidades) {
            Integer quantidade = resultado.get(Integer.valueOf(unidade.y));
            resultado.put(Integer.valueOf(unidade.y), Integer.valueOf(
                    quantidade == null ? 1 : quantidade.intValue() + 1));
        }
        return resultado;
    }

    private static void gerarPrevia(String caminho) throws Exception {
        int largura = 760;
        int altura = 520;
        BufferedImage imagem = new BufferedImage(largura, altura,
                BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = imagem.createGraphics();
        try {
            g2.setColor(java.awt.Color.WHITE);
            g2.fillRect(0, 0, largura, altura);
            List<CirculoVenn> zonas = criarZonas(5);
            EstadoSemanticoCompartilhado compartilhado =
                    new EstadoSemanticoCompartilhado();
            EstadoProcessoTransformacao estado = EstadoProcessoTransformacao.aPartir(
                    compartilhado.atualizar(
                            TipoSituacaoAditiva.TRANSFORMACAO_MEDIDAS,
                            new Integer[] {10, 5, 15},
                            new boolean[] {true, true, true}, 1,
                            EstadoSemanticoCompartilhado.Origem.VERGNAUD));
            RenderizadorProcessoTransformacao render =
                    new RenderizadorProcessoTransformacao();
            for (int i = 0; i < zonas.size(); i++) {
                render.desenharZona(g2, zonas.get(i), i, estado,
                        ServicoLocalizacao.getInstancia());
            }
            render.desenharEstrutura(g2, zonas, estado);
            LayoutUnidadesProcessoTransformacao layout =
                    new LayoutUnidadesProcessoTransformacao();
            int[] quantidades = new int[] {10, 5, 15};
            for (int i = 0; i < 3; i++) {
                for (Rectangle r : layout.calcular(zonas, i, quantidades[i])) {
                    new QuadradinhoVenn(r.x, r.y, r.width,
                            "situacao_problema").desenhar(g2);
                }
            }
        } finally {
            g2.dispose();
        }
        File arquivo = new File(caminho);
        arquivo.getParentFile().mkdirs();
        ImageIO.write(imagem, "png", arquivo);
        confirmar(arquivo.isFile() && arquivo.length() > 1000,
                "prévia deve ser gerada");
    }

    private static void confirmar(boolean condicao, String mensagem) {
        verificacoes++;
        if (!condicao) {
            throw new AssertionError(mensagem);
        }
    }
}
