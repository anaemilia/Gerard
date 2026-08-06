package gerard.agente.modelador;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;

/**
 * Contador global de novos casos armazenados desde a última execução
 * bem-sucedida da mineração automática (PART + Apriori). Incrementado por
 * AgenteModelador.armazenarCaso a cada caso novo; zerado depois de uma
 * mineração automática concluída com sucesso (ver Main — gatilhos de abrir
 * e fechar).
 *
 * Escrita atômica (arquivo temporário + renomear), mesmo padrão já usado em
 * LoggerInteracaoGerard.associarInvarianteATentativaAtual — evita um valor
 * corrompido se o processo for encerrado no meio da escrita.
 */
public final class ContadorMineracao {

    private final File arquivo;

    public ContadorMineracao() {
        File diretorio = new File(new File(System.getProperty("user.home"), "Gerard"), "analises");
        if (!diretorio.exists()) diretorio.mkdirs();
        arquivo = new File(diretorio, "contador_mineracao.txt");
    }

    /** Construtor usado por testes: permite apontar para um arquivo isolado. */
    public ContadorMineracao(File arquivo) {
        this.arquivo = arquivo;
    }

    public synchronized int obter() {
        if (!arquivo.exists()) {
            return 0;
        }
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(arquivo), "UTF-8"));
            try {
                String linha = reader.readLine();
                return linha == null ? 0 : Integer.parseInt(linha.trim());
            } finally {
                reader.close();
            }
        } catch (Exception ex) {
            return 0;
        }
    }

    public synchronized void incrementar() {
        gravar(obter() + 1);
    }

    public synchronized void zerar() {
        gravar(0);
    }

    private void gravar(int valor) {
        File temporario = new File(arquivo.getParentFile(), arquivo.getName() + ".tmp");
        try {
            Writer writer = new OutputStreamWriter(new FileOutputStream(temporario), "UTF-8");
            try {
                writer.write(String.valueOf(valor));
            } finally {
                writer.close();
            }
            if (arquivo.exists() && !arquivo.delete()) {
                return;
            }
            temporario.renameTo(arquivo);
        } catch (Exception ex) {
            if (temporario.exists()) temporario.delete();
        }
    }
}
