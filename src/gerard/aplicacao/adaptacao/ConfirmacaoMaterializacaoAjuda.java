package gerard.aplicacao.adaptacao;

import gerard.dominio.campoaditivo.ModalidadeEntregaScaffolding;

/**
 * Fato devolvido pela representacao depois que uma modalidade foi realmente
 * materializada. Nao afirma percepcao nem compreensao pelo participante.
 */
public final class ConfirmacaoMaterializacaoAjuda {

    private final String codigoAjuda;
    private final ModalidadeEntregaScaffolding modalidade;
    private final String criterioConfirmacao;
    private final String detalhe;

    public ConfirmacaoMaterializacaoAjuda(
            String codigoAjuda,
            ModalidadeEntregaScaffolding modalidade,
            String criterioConfirmacao,
            String detalhe) {
        this.codigoAjuda = textoObrigatorio(codigoAjuda, "codigo da ajuda");
        if (modalidade == null) {
            throw new IllegalArgumentException("modalidade de entrega e obrigatoria");
        }
        this.modalidade = modalidade;
        this.criterioConfirmacao = textoObrigatorio(
                criterioConfirmacao, "criterio de confirmacao");
        this.detalhe = detalhe == null ? "" : detalhe.trim();
    }

    public String getCodigoAjuda() { return codigoAjuda; }
    public ModalidadeEntregaScaffolding getModalidade() { return modalidade; }
    public String getCriterioConfirmacao() { return criterioConfirmacao; }
    public String getDetalhe() { return detalhe; }

    private static String textoObrigatorio(String valor, String nome) {
        String normalizado = valor == null ? "" : valor.trim();
        if (normalizado.length() == 0) {
            throw new IllegalArgumentException(nome + " nao pode ser vazio");
        }
        return normalizado;
    }
}
