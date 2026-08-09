import gerard.campoaditivo.modelo.TipoSituacaoAditiva;
import gerard.i18n.ServicoLocalizacao;
import gerard.idioma.IdiomaInterface;

public class TesteSeparacaoDominioLocalizacao {
    public static void main(String[] args) {
        ServicoLocalizacao localizacao = ServicoLocalizacao.getInstancia();

        for (TipoSituacaoAditiva tipo : TipoSituacaoAditiva.values()) {
            if (!tipo.getChaveDescricao().startsWith("tipo.")) {
                throw new AssertionError("Chave de descrição inválida: " + tipo);
            }
            if (tipo.getSigla() == null || tipo.getSigla().isEmpty()) {
                throw new AssertionError("Sigla ausente: " + tipo);
            }
        }

        localizacao.definirIdioma(IdiomaInterface.PORTUGUES);
        assertEquals("Composição de medidas",
                localizacao.descricaoTipo(TipoSituacaoAditiva.COMPOSICAO_MEDIDAS));
        assertEquals("CM - Composição de medidas",
                localizacao.rotuloBotaoTipo(TipoSituacaoAditiva.COMPOSICAO_MEDIDAS));

        localizacao.definirIdioma(IdiomaInterface.INGLES);
        assertEquals("Composition of measures",
                localizacao.descricaoTipo(TipoSituacaoAditiva.COMPOSICAO_MEDIDAS));
        assertEquals("CM - Composition of measures",
                localizacao.rotuloBotaoTipo(TipoSituacaoAditiva.COMPOSICAO_MEDIDAS));

        localizacao.definirIdioma(IdiomaInterface.PORTUGUES);
        System.out.println("OK: domínio não resolve tradução e os rótulos foram preservados.");
    }

    private static void assertEquals(String esperado, String atual) {
        if (!esperado.equals(atual)) {
            throw new AssertionError("Esperado <" + esperado + ">, obtido <" + atual + ">");
        }
    }
}
