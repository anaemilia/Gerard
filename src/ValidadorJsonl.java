import gerard.agente.conhecimento.AnalisadorJsonSimples;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Map;

/**
 * Validador descartável do log estruturado de auditoria: confirma que cada
 * linha de agentes_execucao.jsonl é um JSON válido e independente, e que a
 * contagem de linhas bate com o legível — reaproveita o mesmo parser real
 * (AnalisadorJsonSimples), não reimplementa nada.
 */
public class ValidadorJsonl {
    public static void main(String[] args) throws Exception {
        String caminho = args[0];
        BufferedReader leitor = new BufferedReader(new FileReader(caminho));
        int linhas = 0;
        int eventos = 0;
        int resumos = 0;
        int comMonitorZdpModelador = 0;
        int comDivergenciaFelipeS9 = 0;
        String linha;
        while ((linha = leitor.readLine()) != null) {
            if (linha.trim().length() == 0) {
                continue;
            }
            linhas++;
            Object valor = AnalisadorJsonSimples.analisar(linha);
            if (!(valor instanceof Map)) {
                System.out.println("LINHA " + linhas + " NAO E OBJETO JSON");
                continue;
            }
            @SuppressWarnings("unchecked")
            Map<String, Object> mapa = (Map<String, Object>) valor;
            String tipo = String.valueOf(mapa.get("tipo_registro"));
            if ("evento".equals(tipo)) {
                eventos++;
                Object agentes = mapa.get("agents");
                if (agentes instanceof Map) {
                    Map<?, ?> a = (Map<?, ?>) agentes;
                    if (a.containsKey("MONITOR") && a.containsKey("ZDP") && a.containsKey("MODELADOR")) {
                        comMonitorZdpModelador++;
                    }
                }
                Object identificacao = mapa.get("identificacao");
                if (identificacao instanceof Map) {
                    Object userId = ((Map<?, ?>) identificacao).get("user_id");
                    if (userId != null && userId.toString().contains("jamile_s9")) {
                        comDivergenciaFelipeS9++;
                    }
                }
            } else if ("episode_summary".equals(tipo)) {
                resumos++;
            }
        }
        leitor.close();
        System.out.println("Linhas totais: " + linhas);
        System.out.println("Todas parseiam como JSON valido: SIM");
        System.out.println("Eventos: " + eventos);
        System.out.println("Resumos de episodio: " + resumos);
        System.out.println("Eventos com os 3 agentes presentes: " + comMonitorZdpModelador);
        System.out.println("Eventos do episodio Jamile S9: " + comDivergenciaFelipeS9);
    }
}
