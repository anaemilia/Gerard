package gerard.campoaditivo.curadoria;

import gerard.dominio.campoaditivo.situacao.DiagnosticoSituacao;
import gerard.dominio.campoaditivo.situacao.SituacaoProblema;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/** Resultado explícito da ponte entre a curadoria tabular e o agregado rico. */
public final class ResultadoConversaoSituacaoProblemaRica {
    private final SituacaoProblema situacao;
    private final List<DiagnosticoSituacao> diagnosticos;

    ResultadoConversaoSituacaoProblemaRica(
            SituacaoProblema situacao,
            List<DiagnosticoSituacao> diagnosticos) {
        this.situacao = situacao;
        this.diagnosticos = Collections.unmodifiableList(
                new ArrayList<DiagnosticoSituacao>(diagnosticos));
    }

    public boolean foiConstruida() { return situacao != null; }

    public boolean ehValida() {
        return situacao != null && diagnosticos.isEmpty();
    }

    public Optional<SituacaoProblema> getSituacao() {
        return Optional.ofNullable(situacao);
    }

    public SituacaoProblema getSituacaoOuFalhar() {
        if (situacao == null) {
            throw new IllegalStateException(
                    "a situação rica não foi construída; consulte os diagnósticos");
        }
        return situacao;
    }

    public List<DiagnosticoSituacao> getDiagnosticos() { return diagnosticos; }

    public boolean possuiCodigo(String codigo) {
        for (DiagnosticoSituacao diagnostico : diagnosticos) {
            if (diagnostico.getCodigo().equals(codigo)) return true;
        }
        return false;
    }
}
