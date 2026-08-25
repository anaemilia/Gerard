package gerard.adaptacao.modelousuario;

import gerard.agente.modelousuario.DiagnosticoTarefa;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/** Cópia profunda e imutável dos diagnósticos factuais armazenados. */
public final class HistoricoDiagnosticosProjetado {

    private final List<DiagnosticoTarefaProjetado> diagnosticos;

    public HistoricoDiagnosticosProjetado(List<DiagnosticoTarefa> origem) {
        if (origem == null || origem.isEmpty()) {
            throw new IllegalArgumentException("histórico de diagnósticos não pode ser vazio");
        }
        List<DiagnosticoTarefaProjetado> copia =
                new ArrayList<DiagnosticoTarefaProjetado>();
        for (DiagnosticoTarefa diagnostico : origem) {
            copia.add(new DiagnosticoTarefaProjetado(diagnostico));
        }
        this.diagnosticos = Collections.unmodifiableList(copia);
    }

    public List<DiagnosticoTarefaProjetado> getDiagnosticos() {
        return diagnosticos;
    }
}
