import gerard.campoaditivo.diagrama.modelo.AreaDiagrama;
import gerard.campoaditivo.diagrama.modelo.CenaDiagramaAditivo;
import gerard.campoaditivo.diagrama.modelo.ConectorDiagrama;
import gerard.campoaditivo.diagrama.modelo.FiguraDiagrama;
import gerard.campoaditivo.diagrama.servico.GeradorCenaDiagramaAditivo;
import gerard.campoaditivo.modelo.TipoSituacaoAditiva;
import gerard.campoaditivo.servico.CatalogoDefinicoesAditivas;
import java.awt.Rectangle;

public class TesteAreaDiagramaPortatil {
    public static void main(String[] args) {
        GeradorCenaDiagramaAditivo gerador = new GeradorCenaDiagramaAditivo();
        CatalogoDefinicoesAditivas catalogo = new CatalogoDefinicoesAditivas();
        for (TipoSituacaoAditiva tipo : TipoSituacaoAditiva.values()) {
            CenaDiagramaAditivo swing = gerador.gerar(tipo,
                    new Rectangle(17, 23, 840, 480), catalogo.obter(tipo),
                    new int[] {3, 5, 8});
            CenaDiagramaAditivo portatil = gerador.gerar(tipo,
                    new AreaDiagrama(17, 23, 840, 480), catalogo.obter(tipo),
                    new int[] {3, 5, 8});
            exigir(swing.getFiguras().size() == portatil.getFiguras().size(), tipo + " figuras");
            exigir(swing.getConectores().size() == portatil.getConectores().size(), tipo + " conectores");
            for (int i = 0; i < swing.getFiguras().size(); i++) {
                FiguraDiagrama a = swing.getFiguras().get(i), b = portatil.getFiguras().get(i);
                exigir(a.getTipo() == b.getTipo() && a.getX() == b.getX()
                        && a.getY() == b.getY() && a.getLargura() == b.getLargura()
                        && a.getAltura() == b.getAltura()
                        && a.getPosicaoRotulo() == b.getPosicaoRotulo()
                        && a.isExibirLupa() == b.isExibirLupa(), tipo + " figura " + i);
            }
            if (tipo == TipoSituacaoAditiva.COMPARACAO_MEDIDAS) {
                exigir(portatil.getFiguras().get(0).getPosicaoRotulo().name().equals("ABAIXO"),
                        "referido abaixo");
                exigir(portatil.getFiguras().get(1).isExibirLupa(),
                        "valor relativo publica lupa");
                exigir(portatil.getFiguras().get(2).getPosicaoRotulo().name().equals("ACIMA"),
                        "referendo acima");
            }
            for (int i = 0; i < swing.getConectores().size(); i++) {
                ConectorDiagrama a = swing.getConectores().get(i), b = portatil.getConectores().get(i);
                exigir(a.getTipo() == b.getTipo() && a.getX1() == b.getX1()
                        && a.getY1() == b.getY1() && a.getX2() == b.getX2()
                        && a.getY2() == b.getY2(), tipo + " conector " + i);
            }
        }
        System.out.println("APROVADO: área portátil preserva as seis cenas Swing.");
    }

    private static void exigir(boolean condicao, String mensagem) {
        if (!condicao) throw new AssertionError(mensagem);
    }
}
