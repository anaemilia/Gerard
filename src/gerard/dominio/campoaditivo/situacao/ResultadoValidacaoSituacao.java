package gerard.dominio.campoaditivo.situacao;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/** Resultado imutável da validação estrutural, narrativa e cruzada. */
public final class ResultadoValidacaoSituacao {
    private final List<DiagnosticoSituacao> diagnosticos;

    public ResultadoValidacaoSituacao(List<DiagnosticoSituacao> diagnosticos) {
        this.diagnosticos = Collections.unmodifiableList(new ArrayList<>(diagnosticos));
    }

    public boolean ehValida() { return diagnosticos.isEmpty(); }
    public List<DiagnosticoSituacao> getDiagnosticos() { return diagnosticos; }

    public boolean possuiCodigo(String codigo) {
        for (DiagnosticoSituacao diagnostico : diagnosticos) {
            if (diagnostico.getCodigo().equals(codigo)) {
                return true;
            }
        }
        return false;
    }
}
