package gerard.pesquisador.auditoria;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * Escreve agentes_execucao_legivel.log — mesmos dados do JSONL (recebe o
 * MESMO AgentAuditEvent, chama o mesmo paraMapa(), nunca relê o arquivo
 * JSONL nem reconstrói o evento por conta própria), com os marcadores
 * [MONITOR]/[ZDP]/[MODELADOR] explícitos. Ordem dos blocos = ordem real de
 * execução (MONITOR → ZDP → MODELADOR, ver AgentAuditEvent/Main.java), não
 * a ordem do documento de referência — a usuária pediu explicitamente pra
 * não inventar ordem diferente da implementada.
 */
public final class HumanReadableAgentAuditWriter {
    private final Writer destino;

    public HumanReadableAgentAuditWriter(File arquivo) throws IOException {
        this.destino = new BufferedWriter(
                new OutputStreamWriter(new FileOutputStream(arquivo, true), StandardCharsets.UTF_8));
    }

    @SuppressWarnings("unchecked")
    public synchronized void escrever(AgentAuditEvent evento) throws IOException {
        Map<String, Object> mapa = evento.paraMapa();
        Map<String, Object> identificacao = (Map<String, Object>) mapa.get("identificacao");
        Map<String, Object> agentes = (Map<String, Object>) mapa.get("agents");

        linha("=== evento " + valor(identificacao, "event_id") + " — passo " + valor(identificacao, "step")
                + " — usuario " + valor(identificacao, "user_id") + " — "
                + valor(identificacao, "timestamp") + " ===");
        linha("acao_usuario: " + mapa.get("acao_usuario"));
        linha("profile_before: " + mapa.get("profile_before"));

        linha("[MONITOR]");
        linha(String.valueOf(agentes.get("MONITOR")));

        linha("[ZDP]");
        linha(String.valueOf(agentes.get("ZDP")));

        linha("[MODELADOR]");
        linha(String.valueOf(agentes.get("MODELADOR")));

        linha("profile_after: " + mapa.get("profile_after"));
        linha("interaction_result: " + mapa.get("interaction_result"));
        linha("");
        destino.flush();
    }

    public synchronized void escreverResumoEpisodio(String episodeId, Map<String, Object> resumo) throws IOException {
        linha("### resumo do episodio " + episodeId + " ###");
        linha(String.valueOf(resumo));
        linha("");
        destino.flush();
    }

    private String valor(Map<String, Object> mapa, String chave) {
        return mapa == null ? "?" : String.valueOf(mapa.get(chave));
    }

    private void linha(String texto) throws IOException {
        destino.write(texto);
        destino.write("\n");
    }

    public void fechar() throws IOException {
        destino.close();
    }
}
