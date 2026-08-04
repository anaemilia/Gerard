import gerard.agente.conhecimento.AnalisadorJsonSimples;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Map;

/** Inspeção pontual descartável: imprime step/acao/avaliacao de um usuário do JSONL, em ordem. */
public class InspecionarEpisodio {
    public static void main(String[] args) throws Exception {
        String caminho = args[0];
        String userId = args[1];
        BufferedReader leitor = new BufferedReader(new FileReader(caminho));
        String linha;
        while ((linha = leitor.readLine()) != null) {
            if (linha.trim().length() == 0) {
                continue;
            }
            @SuppressWarnings("unchecked")
            Map<String, Object> mapa = (Map<String, Object>) AnalisadorJsonSimples.analisar(linha);
            if (!"evento".equals(String.valueOf(mapa.get("tipo_registro")))) {
                continue;
            }
            @SuppressWarnings("unchecked")
            Map<String, Object> identificacao = (Map<String, Object>) mapa.get("identificacao");
            if (identificacao == null || !userId.equals(identificacao.get("user_id"))) {
                continue;
            }
            @SuppressWarnings("unchecked")
            Map<String, Object> acao = (Map<String, Object>) mapa.get("acao_usuario");
            @SuppressWarnings("unchecked")
            Map<String, Object> agentes = (Map<String, Object>) mapa.get("agents");
            @SuppressWarnings("unchecked")
            Map<String, Object> monitor = (Map<String, Object>) agentes.get("MONITOR");
            @SuppressWarnings("unchecked")
            Map<String, Object> decisaoMonitor = (Map<String, Object>) monitor.get("decision");
            @SuppressWarnings("unchecked")
            Map<String, Object> zdp = (Map<String, Object>) agentes.get("ZDP");
            @SuppressWarnings("unchecked")
            Map<String, Object> decisaoZdp = (Map<String, Object>) zdp.get("decision");
            System.out.println("step=" + identificacao.get("step")
                    + " tipo=" + acao.get("type")
                    + " target_role=" + acao.get("target_role")
                    + " value=" + acao.get("value")
                    + " avaliacao=" + decisaoMonitor.get("evaluation")
                    + " camada_zdp=" + decisaoZdp.get("layer"));
        }
        leitor.close();
    }
}
