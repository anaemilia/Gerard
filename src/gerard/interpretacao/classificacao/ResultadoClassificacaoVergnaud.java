package gerard.interpretacao.classificacao;

import gerard.campoaditivo.modelo.TipoSituacaoAditiva;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Resultado explicável da classificação de uma situação-problema.
 */
public class ResultadoClassificacaoVergnaud {
    private final TipoSituacaoAditiva categoriaPrevista;
    private final TipoSituacaoAditiva categoriaModelo;
    private final TipoSituacaoAditiva categoriaRegra;
    private final TipoSituacaoAditiva categoriaManual;
    private final double confianca;
    private final double confiancaModelo;
    private final double confiancaRegra;
    private final String origem;
    private final List<String> pistas;

    public ResultadoClassificacaoVergnaud(TipoSituacaoAditiva categoriaPrevista,
                                          TipoSituacaoAditiva categoriaModelo,
                                          TipoSituacaoAditiva categoriaRegra,
                                          TipoSituacaoAditiva categoriaManual,
                                          double confianca,
                                          double confiancaModelo,
                                          double confiancaRegra,
                                          String origem,
                                          List<String> pistas) {
        this.categoriaPrevista = categoriaPrevista;
        this.categoriaModelo = categoriaModelo;
        this.categoriaRegra = categoriaRegra;
        this.categoriaManual = categoriaManual;
        this.confianca = limitar(confianca);
        this.confiancaModelo = limitar(confiancaModelo);
        this.confiancaRegra = limitar(confiancaRegra);
        this.origem = origem == null ? "" : origem;
        this.pistas = pistas == null ? new ArrayList<String>() : new ArrayList<String>(pistas);
    }

    public static ResultadoClassificacaoVergnaud criar(TipoSituacaoAditiva categoriaPrevista,
                                                        double confianca,
                                                        String origem,
                                                        List<String> pistas) {
        return new ResultadoClassificacaoVergnaud(categoriaPrevista, categoriaPrevista, null, null,
                confianca, confianca, 0.0, origem, pistas);
    }

    private static double limitar(double valor) {
        if (Double.isNaN(valor) || Double.isInfinite(valor)) {
            return 0.0;
        }
        if (valor < 0.0) {
            return 0.0;
        }
        if (valor > 1.0) {
            return 1.0;
        }
        return valor;
    }

    public TipoSituacaoAditiva getCategoriaPrevista() {
        return categoriaPrevista;
    }

    public TipoSituacaoAditiva getCategoriaModelo() {
        return categoriaModelo;
    }

    public TipoSituacaoAditiva getCategoriaRegra() {
        return categoriaRegra;
    }

    public TipoSituacaoAditiva getCategoriaManual() {
        return categoriaManual;
    }

    public double getConfianca() {
        return confianca;
    }

    public double getConfiancaModelo() {
        return confiancaModelo;
    }

    public double getConfiancaRegra() {
        return confiancaRegra;
    }

    public String getOrigem() {
        return origem;
    }

    public List<String> getPistas() {
        return Collections.unmodifiableList(pistas);
    }

    public boolean isBaixaConfianca() {
        return confianca < 0.55;
    }

    public String explicar() {
        StringBuilder sb = new StringBuilder();
        sb.append("Categoria prevista: ").append(categoriaPrevista);
        sb.append(" | confiança: ").append(String.format(java.util.Locale.US, "%.2f", confianca));
        if (origem.length() > 0) {
            sb.append(" | origem: ").append(origem);
        }
        if (!pistas.isEmpty()) {
            sb.append(" | pistas: ").append(pistas);
        }
        return sb.toString();
    }
}
