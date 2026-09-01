import gerard.aplicacao.PoliticaSorteioSituacoesAditivas;
import gerard.aplicacao.PoliticaSorteioSituacoesAditivas.Grupo;
import gerard.campoaditivo.modelo.TipoSituacaoAditiva;
import java.util.Arrays;
import java.util.Random;

public class TestePoliticaSorteioSituacoesAditivas {
    public static void main(String[] args) {
        PoliticaSorteioSituacoesAditivas politica = new PoliticaSorteioSituacoesAditivas();
        TipoSituacaoAditiva[] medidas = politica.categoriasDoGrupo(Grupo.MEDIDAS);
        TipoSituacaoAditiva[] relacoes = politica.categoriasDoGrupo(Grupo.RELACOES);
        exigir(medidas.length == 3 && relacoes.length == 3, "cada grupo possui três categorias");
        exigir(Arrays.asList(medidas).contains(TipoSituacaoAditiva.COMPOSICAO_MEDIDAS), "medidas contém composição de medidas");
        exigir(Arrays.asList(relacoes).contains(TipoSituacaoAditiva.COMPOSICAO_RELACOES), "relações contém composição de relações");
        exigir(politica.categoriasDoGrupo(Grupo.TODAS).length == 6, "união contém as seis categorias");
        exigir(Arrays.asList(medidas).contains(politica.sortearCategoria(Grupo.MEDIDAS, new Random(7))), "sorteio de medidas não escapa do grupo");
        exigir(Arrays.asList(relacoes).contains(politica.sortearCategoria(Grupo.RELACOES, new Random(7))), "sorteio de relações não escapa do grupo");
        System.out.println("APROVADO: política de sorteio independe de Swing.");
    }

    private static void exigir(boolean condicao, String mensagem) {
        if (!condicao) throw new AssertionError(mensagem);
    }
}
