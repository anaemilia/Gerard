package gerard.interpretacao.modelo;

import gerard.i18n.ServicoLocalizacao;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ResultadoInterpretacao {
    private final IdiomaProblema idiomaDetectado;
    private final CategoriaProblema categoriaProvavel;
    private final double confianca;
    private final List<PistaLinguistica> pistas;
    private final List<NumeroEncontrado> numeros;
    private final List<String> avisos;
    private final List<PapelElementoInterpretado> papeis;
    private final String relacaoProvavel;
    private final SubtipoVergnaud subtipoVergnaud;

    public ResultadoInterpretacao(
            IdiomaProblema idiomaDetectado,
            CategoriaProblema categoriaProvavel,
            double confianca,
            List<PistaLinguistica> pistas,
            List<NumeroEncontrado> numeros,
            List<String> avisos,
            List<PapelElementoInterpretado> papeis,
            String relacaoProvavel,
            SubtipoVergnaud subtipoVergnaud
    ) {
        this.idiomaDetectado = idiomaDetectado;
        this.categoriaProvavel = categoriaProvavel;
        this.confianca = confianca;
        this.pistas = new ArrayList<PistaLinguistica>(pistas);
        this.numeros = new ArrayList<NumeroEncontrado>(numeros);
        this.avisos = new ArrayList<String>(avisos);
        this.papeis = new ArrayList<PapelElementoInterpretado>(papeis);
        this.relacaoProvavel = relacaoProvavel;
        this.subtipoVergnaud = subtipoVergnaud;
    }

    public IdiomaProblema getIdiomaDetectado() {
        return idiomaDetectado;
    }

    public CategoriaProblema getCategoriaProvavel() {
        return categoriaProvavel;
    }

    public double getConfianca() {
        return confianca;
    }

    public int getConfiancaPercentual() {
        return (int) Math.round(confianca * 100.0);
    }

    public List<PistaLinguistica> getPistas() {
        return Collections.unmodifiableList(pistas);
    }

    public List<NumeroEncontrado> getNumeros() {
        return Collections.unmodifiableList(numeros);
    }

    public List<String> getAvisos() {
        return Collections.unmodifiableList(avisos);
    }

    public String getRelacaoProvavel() {
        return ServicoLocalizacao.getInstancia().relacao(relacaoProvavel);
    }

    public List<PapelElementoInterpretado> getPapeis() {
        return Collections.unmodifiableList(papeis);
    }

    public SubtipoVergnaud getSubtipoVergnaud() {
        return subtipoVergnaud;
    }

    public String getPistasFormatadas() {
        if (pistas.isEmpty()) {
            return ServicoLocalizacao.getInstancia().texto("interpret.none.clues");
        }

        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < pistas.size(); i++) {
            if (i > 0) {
                sb.append(", ");
            }
            sb.append('"').append(pistas.get(i).getExpressao()).append('"');
        }

        return sb.toString();
    }

    public String getNumerosFormatados() {
        if (numeros.isEmpty()) {
            return ServicoLocalizacao.getInstancia().texto("interpret.none.numbers");
        }

        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < numeros.size(); i++) {
            if (i > 0) {
                sb.append(", ");
            }
            sb.append(numeros.get(i).getTextoOriginal());
        }

        return sb.toString();
    }


    public String getPapeisFormatados() {
        if (papeis.isEmpty()) {
            return ServicoLocalizacao.getInstancia().texto("interpret.none.roles");
        }

        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < papeis.size(); i++) {
            if (i > 0) {
                sb.append("; ");
            }

            PapelElementoInterpretado papel = papeis.get(i);
            sb.append(papel.getElemento())
              .append(" → ")
              .append(ServicoLocalizacao.getInstancia().texto(papel.getChavePapel()));

            if (!papel.isConhecido()) {
                sb.append(" (")
                  .append(ServicoLocalizacao.getInstancia().texto("papel.desconhecido"))
                  .append(")");
            }
        }

        return sb.toString();
    }

    public String getSubtipoFormatado() {
        if (subtipoVergnaud == null) {
            return ServicoLocalizacao.getInstancia().texto("subtipo.indefinido");
        }
        if (subtipoVergnaud.temIncognitaFigura()) {
            return ServicoLocalizacao.getInstancia().formatar(
                    "ui.subtype.summary",
                    subtipoVergnaud.getDescricaoLocalizada(),
                    subtipoVergnaud.getIncognitaLocalizada()
            );
        }
        return subtipoVergnaud.getDescricaoLocalizada();
    }

    public String getAvisosFormatados() {
        if (avisos.isEmpty()) {
            return ServicoLocalizacao.getInstancia().texto("interpret.none.warnings");
        }

        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < avisos.size(); i++) {
            if (i > 0) {
                sb.append(" | ");
            }
            sb.append(avisos.get(i));
        }

        return sb.toString();
    }
}
