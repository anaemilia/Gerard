package gerard.pesquisador.analiseunidade;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * Uma linha de {@code cardinalidade_unidades_analise.tsv} (rodada 5,
 * 2026-07-31) — só soma o que {@link AnalysisUnitAuditService#finalizarEpisodio()}
 * já calculou (analysis_units_total = units_A_B + units_A_B_C + units_A_B_C_D;
 * protocol_instances_total = correct_actions + error_actions), nenhuma
 * decisão nova acontece aqui.
 */
public final class AnalysisUnitCardinalityReport {
    private static final String[] COLUNAS = {
        "analysis_units_total", "units_A_B", "units_A_B_C", "units_A_B_C_D", "protocol_instances_total",
        "correct_actions", "error_actions", "explanation_screens_opened", "opened_not_answered",
        "partially_answered", "answered", "technical_events", "divergences"
    };

    private AnalysisUnitCardinalityReport() {
    }

    public static void escreverCabecalho(File arquivo) throws IOException {
        if (arquivo.exists()) {
            return;
        }
        PrintWriter escritor = new PrintWriter(
                new OutputStreamWriter(new FileOutputStream(arquivo, true), StandardCharsets.UTF_8));
        try {
            StringBuilder linha = new StringBuilder("episode_id");
            for (String coluna : COLUNAS) {
                linha.append('\t').append(coluna);
            }
            linha.append("\tequalities_hold");
            escritor.println(linha.toString());
        } finally {
            escritor.close();
        }
    }

    public static void escreverLinha(File arquivo, String episodeId, Map<String, Object> resumo) throws IOException {
        PrintWriter escritor = new PrintWriter(
                new OutputStreamWriter(new FileOutputStream(arquivo, true), StandardCharsets.UTF_8));
        try {
            StringBuilder linha = new StringBuilder(episodeId);
            for (String coluna : COLUNAS) {
                linha.append('\t').append(inteiro(resumo, coluna));
            }
            boolean primeiraIgualdade = inteiro(resumo, "analysis_units_total")
                    == inteiro(resumo, "units_A_B") + inteiro(resumo, "units_A_B_C") + inteiro(resumo, "units_A_B_C_D");
            boolean segundaIgualdade = inteiro(resumo, "protocol_instances_total")
                    == inteiro(resumo, "correct_actions") + inteiro(resumo, "error_actions");
            linha.append('\t').append(primeiraIgualdade && segundaIgualdade);
            escritor.println(linha.toString());
        } finally {
            escritor.close();
        }
    }

    private static int inteiro(Map<String, Object> resumo, String chave) {
        Object valor = resumo.get(chave);
        return valor instanceof Number ? ((Number) valor).intValue() : 0;
    }
}
