package gerard.agente.zdp;

import java.util.HashMap;
import java.util.Map;

/**
 * Circuit-breaker do quiz de adivinhação de categoria (decisão da usuária,
 * 2026-07-30): acumula erros consecutivos combinando dois sinais distintos
 * avaliados pelo AgenteMonitor — clique no ícone de categoria errado e
 * concordar ("Sim") com a definição errada no diálogo de confirmação — e
 * sinaliza quando a interação deve parar para reexplicação, em vez de
 * deixar a pessoa clicando infinitamente. Reflete a intervenção que a
 * própria usuária fazia manualmente como pesquisadora nos experimentos em
 * papel: parar a ação e explicar novamente cada categoria.
 *
 * Não é uma CamadaEstrategiaZDP: ainda não está decidido se isto é
 * "estratégia pedagógica" (usuária explicitamente em dúvida, 2026-07-30) —
 * por isso fica como mecanismo de segurança separado, com contagem própria
 * por usuário, em vez de reaproveitar errosConsecutivosPorTarefa do
 * AgenteZDP (que já é usado para outra decisão, a camada N0-N2).
 */
public final class LimiteErrosConsecutivosCategoria {
    private final int limite;
    private final Map<String, Integer> errosPorUsuario = new HashMap<String, Integer>();

    public LimiteErrosConsecutivosCategoria(int limite) {
        this.limite = limite;
    }

    /** Categoria resolvida corretamente: zera a contagem desse usuário. */
    public void registrarAcerto(String idUsuario) {
        errosPorUsuario.remove(chave(idUsuario));
    }

    /**
     * Um erro (clique errado no ícone OU concordância com a definição
     * errada no diálogo de confirmação). Retorna true quando o limite foi
     * atingido nesta chamada — a contagem já volta a zero automaticamente;
     * quem chama deve interromper a interação e reexplicar as categorias.
     */
    public boolean registrarErro(String idUsuario) {
        String chave = chave(idUsuario);
        Integer anterior = errosPorUsuario.get(chave);
        int erros = (anterior == null ? 0 : anterior.intValue()) + 1;
        if (erros >= limite) {
            errosPorUsuario.remove(chave);
            return true;
        }
        errosPorUsuario.put(chave, Integer.valueOf(erros));
        return false;
    }

    private String chave(String idUsuario) {
        return idUsuario == null ? "" : idUsuario;
    }
}
