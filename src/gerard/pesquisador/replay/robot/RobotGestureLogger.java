package gerard.pesquisador.replay.robot;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;

/**
 * Grava {@code robot_gestos.log} — uma linha por tentativa de gesto, mais
 * uma linha de resumo por gesto completo. Escreve de imediato (sem buffer
 * que sobreviva a um crash) porque o propósito deste arquivo é justamente
 * provar que nenhuma tentativa desapareceu, mesmo que o processo trave logo
 * depois.
 */
public final class RobotGestureLogger {
    private final File arquivo;

    public RobotGestureLogger(File arquivo) {
        this.arquivo = arquivo;
    }

    public void registrarTentativa(RobotGestureTrace gesto, RobotGestureAttempt tentativa) {
        escrever("[TENTATIVA] episodio=" + gesto.getEpisodeId() + " ordem=" + gesto.getOrdemProtocolo()
                + " " + tentativa.resumoLinhaUnica());
    }

    public void registrarGestoFinalizado(RobotGestureTrace gesto) {
        escrever("[GESTO] " + gesto.resumoLinhaUnica());
    }

    public void registrarEvento(String mensagem) {
        escrever("[INFO] " + mensagem);
    }

    private void escrever(String linha) {
        try {
            PrintWriter escritor = new PrintWriter(
                    new OutputStreamWriter(new FileOutputStream(arquivo, true), StandardCharsets.UTF_8));
            try {
                escritor.println(linha);
            } finally {
                escritor.close();
            }
        } catch (IOException e) {
            // Este arquivo é diagnóstico do próprio Robot — se ele falhar,
            // não há mais nenhum lugar seguro para registrar isso além de
            // stderr (que o shell já captura no redirecionamento do teste).
            System.err.println("[RobotGestureLogger] falha ao gravar em " + arquivo + ": " + e);
        }
    }

    public File getArquivo() {
        return arquivo;
    }
}
