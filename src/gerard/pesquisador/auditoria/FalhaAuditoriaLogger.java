package gerard.pesquisador.auditoria;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Log de falhas do serviço de auditoria, em arquivo separado — a interação
 * principal do Gérard nunca pode parar por causa de um erro aqui (ver
 * AgentAuditService: toda chamada pública dele é protegida por try/catch
 * que só grava aqui e segue).
 */
public final class FalhaAuditoriaLogger {
    private final File arquivo;

    public FalhaAuditoriaLogger(File diretorioLogs) {
        this.arquivo = new File(diretorioLogs,
                "falhas_auditoria_" + new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date()) + ".log");
    }

    public void registrar(String contexto, Throwable erro) {
        try {
            PrintWriter escritor = new PrintWriter(new FileWriter(arquivo, true));
            try {
                escritor.println("[" + new Date() + "] " + contexto);
                if (erro != null) {
                    erro.printStackTrace(escritor);
                }
            } finally {
                escritor.close();
            }
        } catch (IOException ignorada) {
            // Falhar ao gravar a falha não pode, por sua vez, quebrar nada —
            // não há mais nenhum lugar seguro pra registrar isso.
        }
    }

    public File getArquivo() {
        return arquivo;
    }
}
