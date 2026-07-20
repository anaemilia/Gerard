package gerard.campoaditivo.transformacao.processo;

import gerard.campoaditivo.diagrama.elementos.CirculoVenn;
import gerard.campoaditivo.diagrama.elementos.QuadradinhoVenn;
import gerard.campoaditivo.modelo.DefinicaoDiagramaAditivo;
import gerard.campoaditivo.modelo.SituacaoProblemaAditiva;
import gerard.campoaditivo.modelo.TipoSituacaoAditiva;
import gerard.campoaditivo.sincronizacao.EstadoSemanticoCompartilhado;
import gerard.campoaditivo.venn.modelo.CenaDiagramaVenn;
import gerard.campoaditivo.venn.modelo.NoDiagramaVenn;
import gerard.i18n.ServicoLocalizacao;
import gerard.idioma.IdiomaInterface;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import javax.imageio.ImageIO;

public final class GerarPreviaGrandezaMonetariaC157 {
    public static void main(String[] args) throws Exception {
        String caminho = args.length > 0 ? args[0]
                : "build/PREVIA_GRANDEZA_MONETARIA_C157.png";
        int largura = 720;
        int altura = 520;
        BufferedImage imagem = new BufferedImage(largura, altura,
                BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = imagem.createGraphics();
        try {
            g2.setColor(Color.WHITE);
            g2.fillRect(0, 0, largura, altura);
            Rectangle area = new Rectangle(10, 10, 700, 500);
            int[] valores = new int[] {250, -180, 70};
            CenaDiagramaVenn cena = new LayoutProcessoTransformacao().criarCena(
                    area, new DefinicaoDiagramaAditivo(
                            "Transformação de medidas", "Estado inicial",
                            "Transformação", "Estado final"), valores);
            List<CirculoVenn> zonas = new ArrayList<CirculoVenn>();
            for (NoDiagramaVenn no : cena.getNos()) {
                CirculoVenn zona = new CirculoVenn(no.getX(), no.getY(),
                        no.getLargura(), no.getAltura(), no.getRotulo(),
                        no.getValorReferencia(), no.isExibirQuadradinhos());
                zona.formaRetangular = true;
                zonas.add(zona);
            }
            EstadoSemanticoCompartilhado compartilhado =
                    new EstadoSemanticoCompartilhado();
            EstadoSemanticoCompartilhado.Snapshot snapshot = compartilhado.atualizar(
                    TipoSituacaoAditiva.TRANSFORMACAO_MEDIDAS,
                    new Integer[] {250, -180, 70},
                    new boolean[] {true, true, true}, 1,
                    EstadoSemanticoCompartilhado.Origem.VERGNAUD);
            EstadoProcessoTransformacao estado =
                    EstadoProcessoTransformacao.aPartir(snapshot);
            SituacaoProblemaAditiva situacao = new SituacaoProblemaAditiva(
                    "PREVIA", true, TipoSituacaoAditiva.TRANSFORMACAO_MEDIDAS,
                    IdiomaInterface.PORTUGUES,
                    "Maria tinha R$ 250,00, gastou R$ 180,00 e ficou com R$ 70,00.",
                    "Dinheiro", "teste", "", "250,00", "180,00", "70,00",
                    "", "", "", "", "PROCESSO_TRANSFORMACAO", "");
            PlanoUnidadesProcessoTransformacao plano =
                    new SincronizadorUnidadesProcessoTransformacao()
                    .criarPlano(snapshot, situacao);
            RenderizadorProcessoTransformacao render =
                    new RenderizadorProcessoTransformacao();
            render.desenharCabecalho(g2, area, ServicoLocalizacao.getInstancia());
            for (int i = 0; i < zonas.size(); i++) {
                render.desenharZona(g2, zonas.get(i), i, estado,
                        ServicoLocalizacao.getInstancia(), plano);
            }
            render.desenharEstrutura(g2, zonas, estado, plano);
            LayoutUnidadesProcessoTransformacao layout =
                    new LayoutUnidadesProcessoTransformacao();
            for (int i = 0; i < 3; i++) {
                for (Rectangle r : layout.calcular(zonas, i,
                        plano.getQuantidade(i))) {
                    new QuadradinhoVenn(r.x, r.y, r.width,
                            i == 1 ? "transformacao_negativa"
                                    : "situacao_problema").desenhar(g2);
                }
            }
        } finally {
            g2.dispose();
        }
        File arquivo = new File(caminho);
        arquivo.getParentFile().mkdirs();
        ImageIO.write(imagem, "png", arquivo);
        System.out.println(arquivo.getAbsolutePath());
    }
}
