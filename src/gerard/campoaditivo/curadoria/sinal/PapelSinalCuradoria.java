package gerard.campoaditivo.curadoria.sinal;

import gerard.i18n.ServicoLocalizacao;

/**
 * Identifica o papel quantitativo cujo sinal deve ser curado explicitamente.
 */
public enum PapelSinalCuradoria {
    TRANSFORMACAO("curadoria.sinal.papel.transformacao"),
    TRANSFORMACAO_1("curadoria.sinal.papel.transformacao1"),
    TRANSFORMACAO_2("curadoria.sinal.papel.transformacao2"),
    TRANSFORMACAO_RESULTANTE("curadoria.sinal.papel.transformacaoResultante"),
    VALOR_RELATIVO("curadoria.sinal.papel.valorRelativo"),
    RELACAO_INICIAL("curadoria.sinal.papel.relacaoInicial"),
    RELACAO_FINAL("curadoria.sinal.papel.relacaoFinal"),
    RELACAO_1("curadoria.sinal.papel.relacao1"),
    RELACAO_2("curadoria.sinal.papel.relacao2"),
    RELACAO_RESULTANTE("curadoria.sinal.papel.relacaoResultante");

    private final String chaveDescricao;

    PapelSinalCuradoria(String chaveDescricao) {
        this.chaveDescricao = chaveDescricao;
    }

    public String descricao(ServicoLocalizacao localizacao) {
        ServicoLocalizacao servico = localizacao == null
                ? ServicoLocalizacao.getInstancia() : localizacao;
        return servico.texto(chaveDescricao);
    }
}
