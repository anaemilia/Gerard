package gerard.campoaditivo.curadoria.sinal;

import gerard.i18n.ServicoLocalizacao;
import java.text.Normalizer;

/**
 * Operação declarada pelo pesquisador para combinar dois papéis conhecidos
 * (relação inicial + transformação, relação 1 + relação 2, transformação 1 +
 * transformação 2). Em Transformação de Relação, a relação final continua
 * sendo informada diretamente na curadoria e não é calculada por esta opção.
 *
 * A operação registra como o pesquisador decidiu que a escolha do aluno deve
 * ser avaliada. O software não a infere do enunciado, da posição dos elementos
 * nem dos personagens; os valores, os sinais, a operação e os personagens são
 * declarações independentes da curadoria humana.
 */
public enum OpcaoOperacaoCuradoria {
    NAO_SELECIONADO("", "curadoria.operacao.selecione", false),
    SOMA("soma", "curadoria.operacao.soma", true),
    SUBTRACAO("subtracao", "curadoria.operacao.subtracao", true);

    private final String valorCanonico;
    private final String chaveRotulo;
    private final boolean escolhaValida;

    OpcaoOperacaoCuradoria(String valorCanonico, String chaveRotulo, boolean escolhaValida) {
        this.valorCanonico = valorCanonico;
        this.chaveRotulo = chaveRotulo;
        this.escolhaValida = escolhaValida;
    }

    public String getValorCanonico() {
        return valorCanonico;
    }

    public boolean isEscolhaValida() {
        return escolhaValida;
    }

    public String rotulo(ServicoLocalizacao localizacao) {
        ServicoLocalizacao servico = localizacao == null
                ? ServicoLocalizacao.getInstancia() : localizacao;
        return servico.texto(chaveRotulo);
    }

    /**
     * @return o resultado de aplicar esta operação a (a, b) — soma: a + b;
     *         subtração: a - b. Chamado só quando isEscolhaValida() é
     *         verdadeiro; caso contrário devolve null (nada para calcular).
     */
    public Integer aplicar(int a, int b) {
        if (this == SOMA) return Integer.valueOf(a + b);
        if (this == SUBTRACAO) return Integer.valueOf(a - b);
        return null;
    }

    public static OpcaoOperacaoCuradoria aPartirDoEstado(String operacaoCurada) {
        String s = normalizar(operacaoCurada);
        if ("soma".equals(s) || "adicao".equals(s) || "+".equals(limpar(operacaoCurada))) {
            return SOMA;
        }
        if ("subtracao".equals(s) || "diminuicao".equals(s) || "-".equals(limpar(operacaoCurada))) {
            return SUBTRACAO;
        }
        return NAO_SELECIONADO;
    }

    private static String limpar(String valor) {
        return valor == null ? "" : valor.trim();
    }

    private static String normalizar(String texto) {
        String v = limpar(texto);
        return Normalizer.normalize(v, Normalizer.Form.NFD)
                .replaceAll("\\p{M}+", "")
                .toLowerCase()
                .replaceAll("[^a-z0-9+\\-]+", "");
    }
}
