import gerard.campoaditivo.diagrama.modelo.AreaDiagrama;
import gerard.campoaditivo.diagrama.modelo.CenaDiagramaAditivo;
import gerard.campoaditivo.diagrama.modelo.ConectorDiagrama;
import gerard.campoaditivo.diagrama.modelo.FiguraDiagrama;
import gerard.campoaditivo.diagrama.servico.GeradorCenaDiagramaAditivo;
import gerard.campoaditivo.modelo.TipoSituacaoAditiva;
import gerard.campoaditivo.servico.CatalogoDefinicoesAditivas;
import gerard.campoaditivo.venn.modelo.CenaDiagramaVenn;
import java.awt.Rectangle;

public class TesteAreaDiagramaPortatil {
    public static void main(String[] args) {
        GeradorCenaDiagramaAditivo gerador = new GeradorCenaDiagramaAditivo();
        CatalogoDefinicoesAditivas catalogo = new CatalogoDefinicoesAditivas();
        for (TipoSituacaoAditiva tipo : TipoSituacaoAditiva.values()) {
            CenaDiagramaVenn.Natureza natureza = CenaDiagramaVenn.naturezaPara(tipo);
            if (tipo == TipoSituacaoAditiva.COMPOSICAO_MEDIDAS) {
                exigir(natureza == CenaDiagramaVenn.Natureza.COLECOES,
                        "composição deve declarar cena de coleções");
            } else if (tipo == TipoSituacaoAditiva.COMPARACAO_MEDIDAS) {
                exigir(natureza == CenaDiagramaVenn.Natureza.BARRAS_COMPARACAO,
                        "comparação deve declarar cena de barras");
            } else {
                exigir(natureza == CenaDiagramaVenn.Natureza.VENN,
                        tipo + " deve declarar cena Venn");
            }
            AreaDiagrama limite = new AreaDiagrama(17, 23, 840, 480);
            CenaDiagramaAditivo swing = gerador.gerar(tipo,
                    new Rectangle(17, 23, 840, 480), catalogo.obter(tipo),
                    new int[] {3, 5, 8});
            CenaDiagramaAditivo portatil = gerador.gerar(tipo,
                    limite, catalogo.obter(tipo),
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
                AreaDiagrama zona = gerador.projetarZonaFigura(
                        portatil, limite, i);
                exigir(contem(limite, zona), tipo + " zona da figura fora da área");
                exigir(zona.x <= b.getX() && zona.y <= b.getY()
                        && zona.x + zona.largura >= b.getX() + b.getLargura()
                        && zona.y + zona.altura >= b.getY() + b.getAltura(),
                        tipo + " zona não contém a figura " + i);
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
                AreaDiagrama zona = gerador.projetarZonaConector(
                        portatil, limite, i);
                exigir(contem(limite, zona), tipo + " zona do conector fora da área");
                exigir(contem(zona, b.getX1(), b.getY1())
                        && contem(zona, b.getX2(), b.getY2()),
                        tipo + " zona não contém o conector " + i);
            }
        }
        System.out.println("APROVADO: área portátil preserva as seis cenas Swing.");
    }

    private static void exigir(boolean condicao, String mensagem) {
        if (!condicao) throw new AssertionError(mensagem);
    }

    private static boolean contem(AreaDiagrama externa, AreaDiagrama interna) {
        return interna != null && externa.x <= interna.x && externa.y <= interna.y
                && externa.x + externa.largura >= interna.x + interna.largura
                && externa.y + externa.altura >= interna.y + interna.altura;
    }

    private static boolean contem(AreaDiagrama area, int x, int y) {
        return area != null && x >= area.x && y >= area.y
                && x <= area.x + area.largura && y <= area.y + area.altura;
    }
}
