package gerard.campoaditivo.curadoria.sinal;

import gerard.i18n.ServicoLocalizacao;
import java.text.Normalizer;

/**
 * Opções canônicas de sinal apresentadas ao curador.
 * NAO_SELECIONADO é apenas o marcador inicial e nunca é persistido.
 */
public enum OpcaoSinalCuradoria {
    NAO_SELECIONADO("", "curadoria.sinal.selecione", false, ""),
    POSITIVO("positivo", "curadoria.sinal.positivo", true, "+"),
    NEGATIVO("negativo", "curadoria.sinal.negativo", true, "-"),
    NEUTRO("neutro", "curadoria.sinal.neutro", true, "");

    private final String valorCanonico;
    private final String chaveRotulo;
    private final boolean escolhaValida;
    private final String prefixo;

    OpcaoSinalCuradoria(String valorCanonico, String chaveRotulo,
            boolean escolhaValida, String prefixo) {
        this.valorCanonico = valorCanonico;
        this.chaveRotulo = chaveRotulo;
        this.escolhaValida = escolhaValida;
        this.prefixo = prefixo;
    }

    public String getValorCanonico() {
        return valorCanonico;
    }

    public boolean isEscolhaValida() {
        return escolhaValida;
    }

    public String getPrefixo() {
        return prefixo;
    }

    public String rotulo(ServicoLocalizacao localizacao) {
        ServicoLocalizacao servico = localizacao == null
                ? ServicoLocalizacao.getInstancia() : localizacao;
        return servico.texto(chaveRotulo);
    }

    public static OpcaoSinalCuradoria aPartirDoEstado(String sinalCurado,
            String valorAtual) {
        String sinal = normalizar(sinalCurado);
        if ("positivo".equals(sinal) || "mais".equals(sinal) || "+".equals(limpar(sinalCurado))) {
            return POSITIVO;
        }
        if ("negativo".equals(sinal) || "menos".equals(sinal) || "-".equals(limpar(sinalCurado))) {
            return NEGATIVO;
        }
        if ("neutro".equals(sinal) || "zero".equals(sinal)) {
            return NEUTRO;
        }

        String valor = limpar(valorAtual);
        if (valor.startsWith("+")) {
            return POSITIVO;
        }
        if (valor.startsWith("-")) {
            return NEGATIVO;
        }
        if (PoliticaSinalCuradoria.ehZero(valor)) {
            return NEUTRO;
        }
        // Um valor sem sinal explícito não é interpretado como positivo:
        // o objetivo da curadoria é exigir uma escolha humana consciente.
        return NAO_SELECIONADO;
    }

    private static String limpar(String valor) {
        return valor == null ? "" : valor.trim();
    }

    private static String normalizar(String valor) {
        String texto = limpar(valor);
        return Normalizer.normalize(texto, Normalizer.Form.NFD)
                .replaceAll("\\p{M}+", "")
                .toLowerCase()
                .replaceAll("[^a-z0-9+\\-]+", "");
    }
}
