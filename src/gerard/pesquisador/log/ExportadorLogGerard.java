package gerard.pesquisador.log;

import java.io.File;

public class ExportadorLogGerard {
    public static File diretorioLogs() {
        return LoggerInteracaoGerard.getInstancia().getDiretorioLogs();
    }

    public static File arquivoSessaoAtual() {
        return LoggerInteracaoGerard.getInstancia().getArquivoSessao();
    }
}
