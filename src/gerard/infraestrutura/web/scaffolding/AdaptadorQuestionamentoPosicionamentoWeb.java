package gerard.infraestrutura.web.scaffolding;

import gerard.Scaffolding.questionamento.ResultadoQuestionamento;
import gerard.Scaffolding.questionamento.ScaffoldingQuestionamento;
import gerard.aplicacao.portabilidade.PortaQuestionamentoPosicionamento;
import gerard.aplicacao.portabilidade.ResultadoQuestionamentoPosicionamento;

/**
 * Único ponto que instancia {@link ScaffoldingQuestionamento} para a web,
 * traduzindo o resultado para o tipo neutro da porta — assim a camada de
 * aplicação (gerard.aplicacao.portabilidade) não precisa importar o pacote
 * concreto do scaffolding (ver AvaliadorOrigemDestinoWeb). Vive na
 * infraestrutura web, não em gerard.aplicacao nem em gerard.Scaffolding,
 * porque a fronteira que o verificador de localidade protege é justamente
 * "camada de aplicação não importa gerard.Scaffolding.*" — um adaptador
 * dentro do próprio pacote Scaffolding continuaria do lado errado dela.
 */
public final class AdaptadorQuestionamentoPosicionamentoWeb
        implements PortaQuestionamentoPosicionamento {
    private final ScaffoldingQuestionamento scaffolding = new ScaffoldingQuestionamento();

    public ResultadoQuestionamentoPosicionamento avaliar(
            String chavePapelNumeral, String chavePapelAlvo,
            String papelDoElementoNoDiagrama, String categoriaEscolhida) {
        ResultadoQuestionamento resultado = scaffolding.avaliarPosicionamento(
                chavePapelNumeral, chavePapelAlvo, papelDoElementoNoDiagrama, categoriaEscolhida);
        if (!resultado.isAplicavel()) {
            return ResultadoQuestionamentoPosicionamento.naoAplicavel();
        }
        if (resultado.isCorreto()) {
            return ResultadoQuestionamentoPosicionamento.correto(
                    resultado.getChavePapelNumeral(), resultado.getChavePapelAlvo());
        }
        return ResultadoQuestionamentoPosicionamento.incorreto(
                resultado.getChavePapelNumeral(), resultado.getChavePapelAlvo(), resultado.getMensagem());
    }
}
