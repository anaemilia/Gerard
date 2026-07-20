package gerard.campoaditivo.transformacao.processo;

import gerard.campoaditivo.diagrama.elementos.CirculoVenn;
import gerard.campoaditivo.diagrama.elementos.QuadradinhoVenn;
import gerard.campoaditivo.modelo.DefinicaoDiagramaAditivo;
import gerard.campoaditivo.modelo.TipoSituacaoAditiva;
import gerard.campoaditivo.representacao.SeletorRepresentacaoComplementar;
import gerard.campoaditivo.representacao.TipoRepresentacaoComplementar;
import gerard.campoaditivo.sincronizacao.EstadoSemanticoCompartilhado;
import gerard.campoaditivo.venn.modelo.CenaDiagramaVenn;
import gerard.campoaditivo.venn.modelo.NoDiagramaVenn;
import gerard.i18n.ServicoLocalizacao;
import gerard.semantica.numero.NumeroInteiro;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import javax.imageio.ImageIO;

public final class TesteProcessoTransformacao {
    private static int verificacoes;

    public static void main(String[] args) throws Exception {
        testarSelecao();
        testarPoliticaDerivadaDoNumero();
        testarLayoutDoProcesso();
        testarAusenciaDoContainerCentralLegado();
        testarFunilEstruturalAntesDoValor();
        testarControlesJuntoAoFunil();
        testarQuadradinhosPorSinal();
        testarSincronizacaoSemantica();
        gerarPrevia(-4, "build/PREVIA_PROCESSO_TRANSFORMACAO_C150_RETIRADA.png");
        gerarPrevia(4, "build/PREVIA_PROCESSO_TRANSFORMACAO_C150_INSERCAO.png");
        gerarPrevia(0, "build/PREVIA_PROCESSO_TRANSFORMACAO_C150_NEUTRA.png");
        gerarPreviaDesconhecida(
                "build/PREVIA_PROCESSO_TRANSFORMACAO_C150_DESCONHECIDA.png");
        System.out.println("Teste do processo de transformação aprovado: "
                + verificacoes + " verificações.");
    }

    private static void testarSelecao() {
        SeletorRepresentacaoComplementar seletor =
                new SeletorRepresentacaoComplementar();
        confirmar(seletor.selecionar(
                TipoSituacaoAditiva.TRANSFORMACAO_MEDIDAS, false)
                == TipoRepresentacaoComplementar.PROCESSO_TRANSFORMACAO,
                "transformação de medidas deve selecionar processo próprio");
    }

    private static void testarPoliticaDerivadaDoNumero() {
        PoliticaVisualProcessoTransformacao politica =
                new PoliticaVisualProcessoTransformacao();
        confirmar(politica.classificar(new NumeroInteiro(-4))
                == TipoProcessoTransformacao.RETIRADA,
                "inteiro negativo deve produzir retirada");
        confirmar(politica.classificar(new NumeroInteiro(0))
                == TipoProcessoTransformacao.NEUTRA,
                "zero deve manter somente o canal");
        confirmar(politica.classificar(new NumeroInteiro(4))
                == TipoProcessoTransformacao.INSERCAO,
                "inteiro positivo deve produzir inserção");
    }

    private static CenaDiagramaVenn criarCena(int transformacao) {
        LayoutProcessoTransformacao layout = new LayoutProcessoTransformacao();
        DefinicaoDiagramaAditivo definicao = new DefinicaoDiagramaAditivo(
                "Transformação de medidas", "Estado inicial",
                "Transformação", "Estado final");
        return layout.criarCena(new Rectangle(10, 10, 560, 430),
                definicao, new int[] {16, transformacao, 16 + transformacao});
    }

    private static List<CirculoVenn> converter(CenaDiagramaVenn cena) {
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

    private static void testarLayoutDoProcesso() {
        CenaDiagramaVenn cena = criarCena(-4);
        confirmar(cena.getNos().size() == 3,
                "processo deve manter três papéis semânticos");
        confirmar(cena.getConectores().isEmpty(),
                "canal deve ser responsabilidade do renderizador de processo");
        List<CirculoVenn> zonas = converter(cena);
        GeometriaProcessoTransformacao geo =
                GeometriaProcessoTransformacao.calcular(zonas);
        confirmar(geo.getCanal().width > 100,
                "canal deve ligar estado inicial e final");
        confirmar(geo.getAreaUnidadesInsercao().y < geo.getCanal().y,
                "quadradinhos de inserção devem ficar acima do canal");
        confirmar(geo.getAreaUnidadesRetirada().y > geo.getCanal().y,
                "quadradinhos retirados devem ficar abaixo do canal");
    }

    private static void testarAusenciaDoContainerCentralLegado() throws Exception {
        int largura = 700;
        int altura = 500;
        BufferedImage imagem = new BufferedImage(largura, altura,
                BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = imagem.createGraphics();
        List<CirculoVenn> zonas;
        try {
            g2.setColor(java.awt.Color.WHITE);
            g2.fillRect(0, 0, largura, altura);
            CenaDiagramaVenn cena = criarCena(-4);
            zonas = converter(cena);
            EstadoSemanticoCompartilhado compartilhado =
                    new EstadoSemanticoCompartilhado();
            EstadoProcessoTransformacao estado = EstadoProcessoTransformacao.aPartir(
                    compartilhado.atualizar(TipoSituacaoAditiva.TRANSFORMACAO_MEDIDAS,
                            new Integer[] {16, -4, 12},
                            new boolean[] {true, true, true}, 1,
                            EstadoSemanticoCompartilhado.Origem.VERGNAUD));
            RenderizadorProcessoTransformacao render =
                    new RenderizadorProcessoTransformacao();
            for (int i = 0; i < zonas.size(); i++) {
                render.desenharZona(g2, zonas.get(i), i, estado,
                        ServicoLocalizacao.getInstancia());
            }
            render.desenharEstrutura(g2, zonas, estado);
        } finally {
            g2.dispose();
        }
        CirculoVenn central = zonas.get(1);
        int x = central.x + 2;
        int y = central.y + central.altura / 2;
        int rgb = imagem.getRGB(x, y) & 0x00FFFFFF;
        confirmar(rgb == 0x00FFFFFF,
                "zona semântica central não pode desenhar o contêiner legado");
    }

    private static void testarFunilEstruturalAntesDoValor()
            throws Exception {
        int largura = 700;
        int altura = 500;
        BufferedImage imagem = new BufferedImage(largura, altura,
                BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = imagem.createGraphics();
        List<CirculoVenn> zonas = converter(criarCena(0));
        GeometriaProcessoTransformacao geo =
                GeometriaProcessoTransformacao.calcular(zonas);
        try {
            g2.setColor(java.awt.Color.WHITE);
            g2.fillRect(0, 0, largura, altura);
            EstadoProcessoTransformacao estado =
                    EstadoProcessoTransformacao.aPartir(null);
            new RenderizadorProcessoTransformacao().desenharEstrutura(
                    g2, zonas, estado);
        } finally {
            g2.dispose();
        }
        Rectangle areaFunil = geo.getFunilInsercao().getBounds();
        areaFunil.add(geo.getHasteInsercao());
        int pixelsNaoBrancos = 0;
        for (int y = Math.max(0, areaFunil.y);
                y < Math.min(altura, areaFunil.y + areaFunil.height); y++) {
            for (int x = Math.max(0, areaFunil.x);
                    x < Math.min(largura, areaFunil.x + areaFunil.width); x++) {
                if ((imagem.getRGB(x, y) & 0x00FFFFFF) != 0x00FFFFFF) {
                    pixelsNaoBrancos++;
                }
            }
        }
        confirmar(pixelsNaoBrancos > 80,
                "funil estrutural deve aparecer antes de a transformação ser conhecida");
    }

    private static void testarControlesJuntoAoFunil() {
        List<CirculoVenn> zonas = converter(criarCena(0));
        EstadoProcessoTransformacao desconhecido =
                EstadoProcessoTransformacao.aPartir(null);
        ControleSinalProcessoTransformacao controle =
                new ControleSinalProcessoTransformacao();
        Rectangle mais = controle.obterAreaAdicionar(zonas, desconhecido);
        Rectangle menos = controle.obterAreaRemover(zonas, desconhecido);
        Rectangle funil = GeometriaProcessoTransformacao.calcular(zonas)
                .getFunilInsercao().getBounds();
        confirmar(mais.x > funil.x + funil.width - 2,
                "controle + deve ficar junto ao funil, não solto no topo");
        confirmar(menos.y > mais.y,
                "controle - deve ficar alinhado abaixo do controle +");
        confirmar(!mais.intersects(
                GeometriaProcessoTransformacao.calcular(zonas).getCanal()),
                "controles do funil não devem cobrir o canal");
    }

    private static void testarQuadradinhosPorSinal() {
        LayoutUnidadesProcessoTransformacao layout =
                new LayoutUnidadesProcessoTransformacao();
        List<CirculoVenn> positivas = converter(criarCena(4));
        GeometriaProcessoTransformacao geoPos =
                GeometriaProcessoTransformacao.calcular(positivas);
        List<Rectangle> inseridas = layout.calcular(positivas, 1, 4);
        confirmar(inseridas.size() == 4,
                "inserção deve concretizar toda a magnitude com quadradinhos");
        for (Rectangle r : inseridas) {
            confirmar(r.y + r.height <= geoPos.getCanal().y,
                    "quadradinho inserido deve ficar no funil superior");
        }

        List<CirculoVenn> negativas = converter(criarCena(-4));
        GeometriaProcessoTransformacao geoNeg =
                GeometriaProcessoTransformacao.calcular(negativas);
        List<Rectangle> retiradas = layout.calcular(negativas, 1, 4);
        confirmar(retiradas.size() == 4,
                "retirada deve concretizar toda a magnitude com quadradinhos");
        for (Rectangle r : retiradas) {
            confirmar(r.y >= geoNeg.getCanal().y,
                    "quadradinho retirado deve ficar no funil inferior");
        }
    }

    private static void testarSincronizacaoSemantica() {
        EstadoSemanticoCompartilhado compartilhado =
                new EstadoSemanticoCompartilhado();
        EstadoSemanticoCompartilhado.Snapshot snapshot = compartilhado.atualizar(
                TipoSituacaoAditiva.TRANSFORMACAO_MEDIDAS,
                new Integer[] {16, -4, 12},
                new boolean[] {true, true, true}, 1,
                EstadoSemanticoCompartilhado.Origem.VERGNAUD);
        EstadoProcessoTransformacao estado =
                EstadoProcessoTransformacao.aPartir(snapshot);
        confirmar(estado.getTipoProcesso()
                == TipoProcessoTransformacao.RETIRADA,
                "snapshot negativo deve ativar retirada");
        confirmar("-4".equals(estado.formatar(1)),
                "valor inteiro deve manter seu sinal");
        PlanoUnidadesProcessoTransformacao plano =
                new SincronizadorUnidadesProcessoTransformacao()
                        .criarPlano(snapshot);
        confirmar(plano.getQuantidade(0) == 16
                && plano.getQuantidade(1) == 4
                && plano.getQuantidade(2) == 12,
                "plano concreto deve usar estados naturais e magnitude inteira");
        confirmar("transformacao_negativa".equals(plano.getOrigem(1)),
                "quadradinhos retirados devem preservar a origem negativa");
    }

    private static void gerarPrevia(int transformacao, String caminho)
            throws Exception {
        int largura = 700;
        int altura = 500;
        BufferedImage imagem = new BufferedImage(largura, altura,
                BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = imagem.createGraphics();
        try {
            g2.setColor(java.awt.Color.WHITE);
            g2.fillRect(0, 0, largura, altura);
            int estadoInicial = 16;
            int estadoFinal = estadoInicial + transformacao;
            CenaDiagramaVenn cena = new LayoutProcessoTransformacao().criarCena(
                    new Rectangle(10, 10, 680, 470),
                    new DefinicaoDiagramaAditivo("Transformação de medidas",
                            "Estado inicial", "Transformação", "Estado final"),
                    new int[] {estadoInicial, transformacao, estadoFinal});
            List<CirculoVenn> zonas = converter(cena);
            EstadoSemanticoCompartilhado compartilhado =
                    new EstadoSemanticoCompartilhado();
            EstadoProcessoTransformacao estado = EstadoProcessoTransformacao.aPartir(
                    compartilhado.atualizar(TipoSituacaoAditiva.TRANSFORMACAO_MEDIDAS,
                            new Integer[] {estadoInicial, transformacao, estadoFinal},
                            new boolean[] {true, true, true}, 1,
                            EstadoSemanticoCompartilhado.Origem.VERGNAUD));
            RenderizadorProcessoTransformacao render =
                    new RenderizadorProcessoTransformacao();
            render.desenharCabecalho(g2, new Rectangle(10, 10, 680, 470),
                    ServicoLocalizacao.getInstancia());
            for (int i = 0; i < zonas.size(); i++) {
                render.desenharZona(g2, zonas.get(i), i, estado,
                        ServicoLocalizacao.getInstancia());
            }
            render.desenharEstrutura(g2, zonas, estado);
            ControleSinalProcessoTransformacao controle =
                    new ControleSinalProcessoTransformacao();
            controle.desenharAdicionar(g2, zonas, estado, false, true);
            controle.desenharRemover(g2, zonas, estado, false, true);
            LayoutUnidadesProcessoTransformacao layout =
                    new LayoutUnidadesProcessoTransformacao();
            int[] quantidades = new int[] {
                estadoInicial, Math.abs(transformacao), estadoFinal
            };
            for (int i = 0; i < 3; i++) {
                for (Rectangle r : layout.calcular(zonas, i, quantidades[i])) {
                    new QuadradinhoVenn(r.x, r.y, r.width,
                            i == 1 && transformacao < 0
                                    ? "transformacao_negativa"
                                    : "situacao_problema")
                            .desenhar(g2);
                }
            }
        } finally {
            g2.dispose();
        }
        File arquivo = new File(caminho);
        arquivo.getParentFile().mkdirs();
        ImageIO.write(imagem, "png", arquivo);
        confirmar(arquivo.isFile() && arquivo.length() > 1000,
                "prévia visual deve ser gerada: " + caminho);
    }

    private static void gerarPreviaDesconhecida(String caminho)
            throws Exception {
        int largura = 700;
        int altura = 500;
        BufferedImage imagem = new BufferedImage(largura, altura,
                BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = imagem.createGraphics();
        try {
            g2.setColor(java.awt.Color.WHITE);
            g2.fillRect(0, 0, largura, altura);
            CenaDiagramaVenn cena = new LayoutProcessoTransformacao().criarCena(
                    new Rectangle(10, 10, 680, 470),
                    new DefinicaoDiagramaAditivo("Transformação de medidas",
                            "Estado inicial", "Transformação", "Estado final"),
                    new int[] {0, 0, 0});
            List<CirculoVenn> zonas = converter(cena);
            EstadoProcessoTransformacao estado =
                    EstadoProcessoTransformacao.aPartir(null);
            RenderizadorProcessoTransformacao render =
                    new RenderizadorProcessoTransformacao();
            render.desenharCabecalho(g2, new Rectangle(10, 10, 680, 470),
                    ServicoLocalizacao.getInstancia());
            for (int i = 0; i < zonas.size(); i++) {
                render.desenharZona(g2, zonas.get(i), i, estado,
                        ServicoLocalizacao.getInstancia());
            }
            render.desenharEstrutura(g2, zonas, estado);
            ControleSinalProcessoTransformacao controle =
                    new ControleSinalProcessoTransformacao();
            controle.desenharAdicionar(g2, zonas, estado, false, false);
            controle.desenharRemover(g2, zonas, estado, false, false);
        } finally {
            g2.dispose();
        }
        File arquivo = new File(caminho);
        arquivo.getParentFile().mkdirs();
        ImageIO.write(imagem, "png", arquivo);
        confirmar(arquivo.isFile() && arquivo.length() > 1000,
                "prévia desconhecida deve mostrar funil estrutural: " + caminho);
    }

    private static void confirmar(boolean condicao, String mensagem) {
        verificacoes++;
        if (!condicao) {
            throw new AssertionError(mensagem);
        }
    }
}
