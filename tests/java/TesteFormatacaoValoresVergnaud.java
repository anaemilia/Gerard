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
        exigir("12".equals(servico.formatarMagnitudeNumeroRelativo(-12, null)),
                "controle deve receber somente a magnitude");
        exigir("-".equals(servico.sinalNumeroRelativo(-12)),
                "controle deve receber o sinal negativo");
        exigir(servico.converterNumeroRelativoLegado("12", "-", null) == -12,
                "magnitude e sinal devem recompor o número relativo");
        exigir(servico.converterNumeroRelativoLegado("inválido", "+", null) == 0,
                "entrada legada inválida deve preservar o fallback zero");

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
        exigir("1.250".equals(servico.formatarMagnitudeNumeroRelativo(
                        -1250, monetariaEmPortugues)),
                "magnitude deve preservar o locale da situação");

        System.out.println("Teste aprovado: valores do Vergnaud usam formatação semântica.");
    }

    private static void exigir(boolean condicao, String mensagem) {
        if (!condicao) {
            throw new AssertionError(mensagem);
        }
    }
}
