package gerard.campoaditivo.semantica;

import gerard.campoaditivo.diagrama.elementos.CirculoVenn;
import gerard.campoaditivo.diagrama.elementos.ElementoVergnaud;
import gerard.campoaditivo.diagrama.modelo.CenaDiagramaAditivo;
import gerard.campoaditivo.diagrama.modelo.FiguraDiagrama;
import gerard.campoaditivo.diagrama.servico.GeradorCenaDiagramaAditivo;
import gerard.campoaditivo.modelo.DefinicaoDiagramaAditivo;
import gerard.campoaditivo.modelo.TipoSituacaoAditiva;
import gerard.campoaditivo.sincronizacao.EstadoSemanticoCompartilhado;
import gerard.campoaditivo.transformacao.processo.EstadoProcessoTransformacao;
import gerard.campoaditivo.transformacao.processo.LayoutProcessoTransformacao;
import gerard.campoaditivo.transformacao.processo.RenderizadorProcessoTransformacao;
import gerard.campoaditivo.transformacao.processo.RotulosProcessoTransformacao;
import gerard.campoaditivo.venn.modelo.CenaDiagramaVenn;
import gerard.campoaditivo.venn.modelo.NoDiagramaVenn;
import gerard.campoaditivo.venn.servico.GeradorCenaDiagramaVenn;
import gerard.idioma.IdiomaInterface;
import gerard.i18n.ServicoLocalizacao;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import javax.imageio.ImageIO;

public final class TesteRotulosSemanticosI18n {
    private static int verificacoes;

    public static void main(String[] args) throws Exception {
        ServicoLocalizacao loc = ServicoLocalizacao.getInstancia();
        try {
            testarParte2Obrigatoria(loc);
            testarLocalizacaoDoProcesso(loc);
            gerarPreviaComposicao(loc);
            gerarPreviaProcessoIngles(loc);
        } finally {
            loc.definirIdioma(IdiomaInterface.PORTUGUES);
        }
        System.out.println("Teste de rótulos semânticos/i18n aprovado: "
                + verificacoes + " verificações.");
    }

    private static void testarParte2Obrigatoria(ServicoLocalizacao loc) {
        NormalizadorRotulosSemanticosDiagrama normalizador =
                new NormalizadorRotulosSemanticosDiagrama();
        DefinicaoDiagramaAditivo incompleta = new DefinicaoDiagramaAditivo(
                "Composição de medidas", "Parte 1", "", "Todo");

        loc.definirIdioma(IdiomaInterface.PORTUGUES);
        DefinicaoDiagramaAditivo pt = normalizador.garantir(
                TipoSituacaoAditiva.COMPOSICAO_MEDIDAS, incompleta, loc);
        confirmar("Parte 2".equals(pt.getRotulo2()),
                "segunda parte deve recuperar o papel Parte 2 em português");

        CenaDiagramaAditivo formal = new GeradorCenaDiagramaAditivo().gerar(
                TipoSituacaoAditiva.COMPOSICAO_MEDIDAS,
                new Rectangle(0, 0, 360, 360), incompleta,
                new int[] {57, 31, 88});
        confirmar(formal.getFiguras().size() == 3,
                "composição formal deve possuir três figuras");
        confirmar("Parte 2".equals(formal.getFiguras().get(1).getRotulo()),
                "a segunda figura formal não pode ficar sem papel semântico");

        CenaDiagramaVenn complementar = new GeradorCenaDiagramaVenn().gerar(
                TipoSituacaoAditiva.COMPOSICAO_MEDIDAS,
                new Rectangle(0, 0, 560, 430), incompleta,
                new int[] {57, 31, 88});
        confirmar("Parte 2".equals(complementar.getNos().get(1).getRotulo()),
                "a segunda coleção complementar deve manter Parte 2");

        loc.definirIdioma(IdiomaInterface.INGLES);
        DefinicaoDiagramaAditivo en = normalizador.garantir(
                TipoSituacaoAditiva.COMPOSICAO_MEDIDAS,
                new DefinicaoDiagramaAditivo("Composition", "", "", ""), loc);
        confirmar("Part 1".equals(en.getRotulo1()),
                "papel 1 deve acompanhar o idioma inglês");
        confirmar("Part 2".equals(en.getRotulo2()),
                "papel 2 deve acompanhar o idioma inglês");
        confirmar("Whole".equals(en.getRotulo3()),
                "todo deve acompanhar o idioma inglês");

        loc.definirIdioma(IdiomaInterface.FRANCES);
        DefinicaoDiagramaAditivo fr = normalizador.garantir(
                TipoSituacaoAditiva.COMPOSICAO_MEDIDAS,
                new DefinicaoDiagramaAditivo("Composition", "", "", ""), loc);
        confirmar("Partie 2".equals(fr.getRotulo2()),
                "papel 2 deve acompanhar o idioma francês");
    }

    private static void testarLocalizacaoDoProcesso(ServicoLocalizacao loc) {
        RotulosProcessoTransformacao rotulos =
                new RotulosProcessoTransformacao();
        EstadoProcessoTransformacao insercao = estado(16, 1, 17);
        EstadoProcessoTransformacao retirada = estado(16, -1, 15);

        loc.definirIdioma(IdiomaInterface.PORTUGUES);
        confirmar("Entraram".equals(rotulos.obterEtapa(insercao, loc)),
                "inserção deve usar rótulo português");
        confirmar("Saíram".equals(rotulos.obterEtapa(retirada, loc)),
                "retirada deve usar rótulo português");

        loc.definirIdioma(IdiomaInterface.INGLES);
        confirmar("Entered".equals(rotulos.obterEtapa(insercao, loc)),
                "inserção deve usar rótulo inglês");
        confirmar("Left".equals(rotulos.obterEtapa(retirada, loc)),
                "retirada deve usar rótulo inglês");

        loc.definirIdioma(IdiomaInterface.FRANCES);
        confirmar("Sont entrés".equals(rotulos.obterEtapa(insercao, loc)),
                "inserção deve usar rótulo francês");
        confirmar("Sont sortis".equals(rotulos.obterEtapa(retirada, loc)),
                "retirada deve usar rótulo francês");
    }

    private static EstadoProcessoTransformacao estado(int inicial,
            int transformacao, int finalEstado) {
        EstadoSemanticoCompartilhado compartilhado =
                new EstadoSemanticoCompartilhado();
        return EstadoProcessoTransformacao.aPartir(compartilhado.atualizar(
                TipoSituacaoAditiva.TRANSFORMACAO_MEDIDAS,
                new Integer[] {inicial, transformacao, finalEstado},
                new boolean[] {true, true, true}, 1,
                EstadoSemanticoCompartilhado.Origem.VERGNAUD));
    }

    private static void gerarPreviaComposicao(ServicoLocalizacao loc)
            throws Exception {
        loc.definirIdioma(IdiomaInterface.PORTUGUES);
        CenaDiagramaAditivo cena = new GeradorCenaDiagramaAditivo().gerar(
                TipoSituacaoAditiva.COMPOSICAO_MEDIDAS,
                new Rectangle(20, 20, 360, 360),
                new DefinicaoDiagramaAditivo(
                        "Composição de medidas", "Parte 1", "", "Todo"),
                new int[] {57, 31, 88});
        BufferedImage imagem = new BufferedImage(420, 420,
                BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = imagem.createGraphics();
        try {
            g2.setColor(Color.WHITE);
            g2.fillRect(0, 0, imagem.getWidth(), imagem.getHeight());
            for (FiguraDiagrama figura : cena.getFiguras()) {
                ElementoVergnaud elemento = new ElementoVergnaud(
                        figura.getX(), figura.getY(), figura.getLargura(),
                        figura.getAltura(), figura.getTipo(),
                        figura.getRotulo(), new Rectangle(0, 0, 420, 420), false);
                elemento.textoEditavel = Integer.toString(figura.getValorReferencia());
                elemento.desenhar(g2);
            }
        } finally {
            g2.dispose();
        }
        File arquivo = new File("build/PREVIA_COMPOSICAO_PAPEIS_C152.png");
        arquivo.getParentFile().mkdirs();
        ImageIO.write(imagem, "png", arquivo);
        confirmar(arquivo.isFile() && arquivo.length() > 1000,
                "prévia de composição deve ser criada");
    }


    private static void gerarPreviaProcessoIngles(ServicoLocalizacao loc)
            throws Exception {
        loc.definirIdioma(IdiomaInterface.INGLES);
        CenaDiagramaVenn cena = new LayoutProcessoTransformacao().criarCena(
                new Rectangle(10, 10, 560, 390),
                new DefinicaoDiagramaAditivo(
                        "Transformation process", "Initial state",
                        "Transformation", "Final state"),
                new int[] {16, 1, 17});
        List<CirculoVenn> zonas = new ArrayList<CirculoVenn>();
        for (NoDiagramaVenn no : cena.getNos()) {
            CirculoVenn zona = new CirculoVenn(no.getX(), no.getY(),
                    no.getLargura(), no.getAltura(), no.getRotulo(),
                    no.getValorReferencia(), no.isExibirQuadradinhos());
            zona.formaRetangular = true;
            zonas.add(zona);
        }
        EstadoProcessoTransformacao estado = estado(16, 1, 17);
        BufferedImage imagem = new BufferedImage(600, 420,
                BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = imagem.createGraphics();
        try {
            g2.setColor(Color.WHITE);
            g2.fillRect(0, 0, imagem.getWidth(), imagem.getHeight());
            RenderizadorProcessoTransformacao render =
                    new RenderizadorProcessoTransformacao();
            render.desenharCabecalho(g2, new Rectangle(10, 10, 560, 390), loc);
            for (int i = 0; i < zonas.size(); i++) {
                render.desenharZona(g2, zonas.get(i), i, estado, loc);
            }
            render.desenharEstrutura(g2, zonas, estado);
        } finally {
            g2.dispose();
        }
        File arquivo = new File("build/PREVIA_PROCESSO_I18N_EN_C152.png");
        arquivo.getParentFile().mkdirs();
        ImageIO.write(imagem, "png", arquivo);
        confirmar(arquivo.isFile() && arquivo.length() > 1000,
                "prévia inglesa do processo deve ser criada");
    }

    private static void confirmar(boolean condicao, String mensagem) {
        verificacoes++;
        if (!condicao) {
            throw new AssertionError(mensagem);
        }
    }
}
