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
    VALOR_RELATIVO("curadoria.sinal.papel.valorRelativo");

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
