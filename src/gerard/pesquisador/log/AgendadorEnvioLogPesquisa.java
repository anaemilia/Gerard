package gerard.pesquisador.log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Envio periódico do log local de interação (gerard_interacao_*.tsv, ver
 * LoggerInteracaoGerard) para o banco de pesquisa (RepositorioEventosLogPesquisa)
 * — ver POST /api/pesquisa/log em ServidorPrototipoWeb, que é o mesmo caminho
 * usado por outras instâncias externas (desktop de outro pesquisador, outro
 * servidor web); esta classe só automatiza esse mesmo envio para a instância
 * local, sem round-trip HTTP desnecessário (backend e log-fonte são o mesmo
 * processo aqui).
 *
 * Não existe hoje um arquivo de log por sessão de navegador individual — o
 * log web é um único arquivo por processo do servidor (LoggerInteracaoGerard
 * é singleton). Por isso o gatilho é periódico (a cada N minutos), não por
 * fim de sessão de um visitante específico — decisão explícita da
 * pesquisadora ao ser apresentada com essa limitação real.
 */
public final class AgendadorEnvioLogPesquisa {
    private final RepositorioEventosLogPesquisa repositorio;
    private final String origemInstancia;
    private final Timer timer = new Timer("envio-log-pesquisa", true);
    private int proximoIndiceLinha = 0;

    public AgendadorEnvioLogPesquisa(RepositorioEventosLogPesquisa repositorio, String origemInstancia) {
        this.repositorio = repositorio;
        this.origemInstancia = origemInstancia;
    }

    public void iniciar(long intervaloMinutos) {
        long intervaloMs = Math.max(1, intervaloMinutos) * 60_000L;
        timer.scheduleAtFixedRate(new TimerTask() {
            public void run() {
                try {
                    executarCiclo();
                } catch (Exception ex) {
                    System.err.println("Falha no envio periódico do log de pesquisa: " + ex.getMessage());
                }
            }
        }, intervaloMs, intervaloMs);
    }

    public synchronized int executarCiclo() {
        File arquivo = LoggerInteracaoGerard.getInstancia().getArquivoSessao();
        if (!arquivo.exists()) {
            return 0;
        }
        List<EventoLogGerard> eventos = new ArrayList<EventoLogGerard>();
        int totalLinhas = lerEventosApartirDe(arquivo, proximoIndiceLinha, eventos);
        proximoIndiceLinha = totalLinhas;
        if (eventos.isEmpty()) {
            return 0;
        }
        return repositorio.inserirTodos(origemInstancia, eventos);
    }

    /** Lê o arquivo inteiro (é só um .tsv de log, nunca gigante), pulando as
     * linhas já processadas em ciclos anteriores; retorna o total de linhas
     * do arquivo (novo cursor) e preenche {@code destino} com os eventos
     * novos válidos. */
    private static int lerEventosApartirDe(File arquivo, int indiceParaPular, List<EventoLogGerard> destino) {
        int indice = 0;
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(new FileInputStream(arquivo), StandardCharsets.UTF_8))) {
            String linha;
            while ((linha = br.readLine()) != null) {
                if (indice >= indiceParaPular) {
                    EventoLogGerard evento = EventoLogGerard.deTsv(linha);
                    if (evento != null) {
                        destino.add(evento);
                    }
                }
                indice++;
            }
        } catch (IOException ex) {
            System.err.println("Não foi possível ler o log local para envio: " + ex.getMessage());
        }
        return indice;
    }
}
