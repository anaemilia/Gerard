package gerard.aplicacao.portabilidade;

/**
 * Porta da avaliação de compatibilidade origem/destino usada pela web
 * (ver {@code AvaliadorOrigemDestino}). A implementação concreta, que de
 * fato instancia o scaffolding de questionamento, vive fora da camada de
 * aplicação (ver {@code gerard.infraestrutura.web.scaffolding.
 * AdaptadorQuestionamentoPosicionamento}), entregue pela raiz de composição.
 */
public interface PortaQuestionamentoPosicionamento {

    /**
     * Valor neutro da aplicação (mesmo papel de {@code PortaRegistroAtividade.NENHUMA}):
     * sem adaptador fornecido pela raiz de composição, nenhuma soltura é
     * questionada. A aplicação não conhece nem instancia o adaptador concreto.
     */
    PortaQuestionamentoPosicionamento NAO_APLICAVEL = new PortaQuestionamentoPosicionamento() {
        public ResultadoQuestionamentoPosicionamento avaliar(String chavePapelNumeral,
                String chavePapelAlvo, String papelDoElementoNoDiagrama, String categoriaEscolhida) {
            return ResultadoQuestionamentoPosicionamento.naoAplicavel();
        }
    };

    ResultadoQuestionamentoPosicionamento avaliar(
            String chavePapelNumeral, String chavePapelAlvo,
            String papelDoElementoNoDiagrama, String categoriaEscolhida);
}
