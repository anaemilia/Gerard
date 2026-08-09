package gerard.semantica.quantidade;

import gerard.campoaditivo.modelo.SituacaoProblemaAditiva;
import gerard.campoaditivo.modelo.TipoSituacaoAditiva;
import gerard.idioma.IdiomaInterface;

public final class TesteFormatacaoValoresVergnaud {

    public static void main(String[] args) {
        ServicoQuantidadeContextual servico =
                new ServicoQuantidadeContextual();

        exigir("12".equals(servico.formatarMedidaParaDiagrama(12, null)),
                "medida positiva não deve explicitar sinal");
        exigir("+12".equals(servico.formatarNumeroRelativoParaDiagrama(12, null)),
                "número relativo positivo deve explicitar sinal");
        exigir("-12".equals(servico.formatarNumeroRelativoParaDiagrama(-12, null)),
                "número relativo negativo deve preservar sinal");

        SituacaoProblemaAditiva monetariaEmPortugues =
                new SituacaoProblemaAditiva(
                        TipoSituacaoAditiva.TRANSFORMACAO_MEDIDAS,
                        IdiomaInterface.PORTUGUES,
                        "Maria tinha R$ 1.250,00.");
        exigir("1.250".equals(servico.formatarMedidaParaDiagrama(
                        1250, monetariaEmPortugues)),
                "medida deve preservar a formatação contextual brasileira");
        exigir("+1.250".equals(servico.formatarNumeroRelativoParaDiagrama(
                        1250, monetariaEmPortugues)),
                "relação deve combinar sinal e formatação contextual");

        System.out.println("Teste aprovado: valores do Vergnaud usam formatação semântica.");
    }

    private static void exigir(boolean condicao, String mensagem) {
        if (!condicao) {
            throw new AssertionError(mensagem);
        }
    }
}
