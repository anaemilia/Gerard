import gerard.Scaffolding.ajudacontextual.ScaffoldingAjudaContextual;
import gerard.idioma.IdiomaInterface;
import gerard.i18n.ServicoLocalizacao;

public class TesteAjudaContextual {
    public static void main(String[] args) {
        ScaffoldingAjudaContextual ajuda = new ScaffoldingAjudaContextual();
        int combinacoes = 0;

        for (IdiomaInterface idioma : new IdiomaInterface[] {
                IdiomaInterface.PORTUGUES, IdiomaInterface.INGLES, IdiomaInterface.FRANCES}) {
            ServicoLocalizacao.getInstancia().definirIdioma(idioma);
            for (ScaffoldingAjudaContextual.Area area : ScaffoldingAjudaContextual.Area.values()) {
                String nomeArea = ServicoLocalizacao.getInstancia().texto(ajuda.obterChaveArea(area));
                exigir(nomeArea != null && nomeArea.trim().length() > 0, "área sem tradução");
                for (ScaffoldingAjudaContextual.Intencao intencao : ScaffoldingAjudaContextual.Intencao.values()) {
                    String opcao = ServicoLocalizacao.getInstancia().texto(ajuda.obterChaveOpcao(intencao));
                    String mensagem = ServicoLocalizacao.getInstancia().texto(ajuda.obterChaveMensagem(area, intencao));
                    exigir(opcao != null && !opcao.startsWith("ui.help."), "opção sem tradução");
                    exigir(mensagem != null && !mensagem.startsWith("ui.help."), "mensagem sem tradução");
                    combinacoes++;
                }
            }
        }
        exigir(combinacoes == 27, "combinações de ajuda incompletas: " + combinacoes);
        System.out.println("OK: ajuda contextual cobre 3 áreas, 3 intenções e 3 idiomas.");
    }

    private static void exigir(boolean condicao, String mensagem) {
        if (!condicao) throw new AssertionError(mensagem);
    }
}
