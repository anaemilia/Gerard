import gerard.campoaditivo.modelo.DefinicaoDiagramaAditivo;
import gerard.campoaditivo.modelo.TipoSituacaoAditiva;
import gerard.campoaditivo.servico.CatalogoDefinicoesAditivas;
import gerard.campoaditivo.venn.modelo.CenaDiagramaVenn;
import gerard.campoaditivo.venn.modelo.NoDiagramaVenn;
import gerard.campoaditivo.venn.servico.GeradorCenaDiagramaVenn;
import gerard.idioma.IdiomaInterface;
import gerard.i18n.ServicoLocalizacao;
import java.awt.Rectangle;

public final class TesteComparacaoBarrasCuradoria {
    public static void main(String[] args) {
        ServicoLocalizacao.getInstancia().definirIdioma(IdiomaInterface.PORTUGUES);
        DefinicaoDiagramaAditivo definicao = new CatalogoDefinicoesAditivas()
                .obter(TipoSituacaoAditiva.COMPARACAO_MEDIDAS);
        Rectangle area = new Rectangle(0, 0, 500, 630);
        CenaDiagramaVenn cena = new GeradorCenaDiagramaVenn().gerar(
                TipoSituacaoAditiva.COMPARACAO_MEDIDAS,
                area,
                definicao,
                new int[] {6, 8, 14});

        exigir(cena != null, "Cena ausente");
        exigir(cena.getNos().size() == 3, "Quantidade de nós incorreta");
        NoDiagramaVenn referido = cena.getNos().get(0);
        NoDiagramaVenn referendo = cena.getNos().get(1);
        NoDiagramaVenn relativo = cena.getNos().get(2);

        exigir("Referido".equals(referido.getRotulo()), "Rótulo do referido incorreto");
        exigir(referido.getValorReferencia() == 6, "Valor do referido incorreto");
        exigir("Referendo".equals(referendo.getRotulo()), "Rótulo do referendo incorreto");
        exigir(referendo.getValorReferencia() == 14, "Extensão visual do referendo incorreta");
        exigir("Valor relativo".equals(relativo.getRotulo()), "Rótulo do valor relativo incorreto");
        exigir(relativo.getValorReferencia() == 8, "Valor relativo incorreto");

        int topoEsperado = area.y + area.height / 2 - 70;
        exigir(referido.getY() == topoEsperado,
                "Barra do referido fora da altura do diagrama de Vergnaud");
        exigir(referendo.getY() == topoEsperado,
                "Barra do referendo fora da altura do diagrama de Vergnaud");
        exigir(relativo.getY() >= topoEsperado,
                "Cartão do valor relativo acima do conjunto de barras");
        exigir(referido.getY() + referido.getAltura() < area.y + area.height,
                "Barra do referido ultrapassa a área complementar");

        Rectangle areaCompacta = new Rectangle(0, 0, 500, 300);
        CenaDiagramaVenn cenaCompacta = new GeradorCenaDiagramaVenn().gerar(
                TipoSituacaoAditiva.COMPARACAO_MEDIDAS,
                areaCompacta,
                definicao,
                new int[] {6, 8, 14});
        NoDiagramaVenn barraCompacta = cenaCompacta.getNos().get(0);
        exigir(barraCompacta.getY() >= areaCompacta.y,
                "Barra compacta ultrapassa o topo da área");
        exigir(barraCompacta.getY() + barraCompacta.getAltura() + 44
                        <= areaCompacta.y + areaCompacta.height,
                "Barra compacta não preserva espaço para os rótulos");

        System.out.println("Teste aprovado: barras alinhadas verticalmente ao diagrama de Vergnaud.");
    }

    private static void exigir(boolean condicao, String mensagem) {
        if (!condicao) throw new AssertionError(mensagem);
    }
}
