package gerard.pesquisador.auditoria;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * Escreve agentes_execucao.jsonl — uma linha JSON válida e independente por
 * AgentAuditEvent (ou por EpisodeSummaryAudit, marcado
 * tipo_registro=episode_summary), UTF-8, append-only durante a sessão.
 */
public final class JsonlAgentAuditWriter {
    private final Writer destino;

    public JsonlAgentAuditWriter(java.io.File arquivo) throws IOException {
        this.destino = new BufferedWriter(
                new OutputStreamWriter(new FileOutputStream(arquivo, true), StandardCharsets.UTF_8));
    }

    public synchronized void escrever(AgentAuditEvent evento) throws IOException {
        escreverMapa(evento.paraMapa());
    }

    public synchronized void escreverResumoEpisodio(String episodeId, Map<String, Object> mapaResumo) throws IOException {
        mapaResumo.put("tipo_registro", "episode_summary");
        mapaResumo.put("episode_id", episodeId);
        escreverMapa(mapaResumo);
    }

    private void escreverMapa(Map<String, Object> mapa) throws IOException {
        destino.write(EscritorJsonSimples.escrever(mapa));
        destino.write("\n");
        destino.flush();
    }

    public void fechar() throws IOException {
        destino.close();
    }
}
