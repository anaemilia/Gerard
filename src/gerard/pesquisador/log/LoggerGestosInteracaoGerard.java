package gerard.pesquisador.log;

import gerard.interacao.PublicadorGestoInteracao;
import gerard.interacao.RegistroGestoInteracao;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Adaptador de persistência do log factual de gestos. Recebe o registro
 * pronto e não decide se houve ação, acerto, erro ou rejeição semântica.
 */
public final class LoggerGestosInteracaoGerard
        implements PublicadorGestoInteracao {
    private final File arquivo;

    public LoggerGestosInteracaoGerard(File arquivo) {
        if (arquivo == null) {
            throw new IllegalArgumentException("O arquivo do log de gestos não pode ser nulo.");
        }
        this.arquivo = arquivo;
        inicializarArquivo();
    }

    public static LoggerGestosInteracaoGerard paraSessao(
            LoggerInteracaoGerard loggerAcoes) {
        if (loggerAcoes == null) {
            throw new IllegalArgumentException("O logger de ações é obrigatório para correlacionar a sessão.");
        }
        return new LoggerGestosInteracaoGerard(new File(
                loggerAcoes.getDiretorioLogs(),
                "gerard_gestos_" + loggerAcoes.getSessaoId() + ".tsv"));
    }

    @Override
    public synchronized void publicar(RegistroGestoInteracao registro) {
        if (registro == null) {
            return;
        }
        escreverLinha(formatar(registro), true);
    }

    public File getArquivo() { return arquivo; }

    public static String cabecalhoTsv() {
        return "timestamp\tsessao\tusuario\tproblema\ttentativa\tgesture_id"
                + "\ttipo_gesto\tobjeto_interface\tartefato_interface"
                + "\tinicio_x\tinicio_y\tfim_x\tfim_y\tamostras"
                + "\tmudancas_orientacao\tdistancia_percorrida\tdestino_geometrico";
    }

    private String formatar(RegistroGestoInteracao registro) {
        return formatarInstante(registro.getInstanteMillis())
                + "\t" + campo(registro.getContexto().getSessaoId())
                + "\t" + campo(registro.getContexto().getUsuarioId())
                + "\t" + campo(registro.getContexto().getProblemaId())
                + "\t" + campo(registro.getContexto().getTentativaId())
                + "\t" + campo(registro.getGestoId())
                + "\t" + campo(registro.getTipoGesto())
                + "\t" + campo(registro.getObjetoInterfaceId())
                + "\t" + campo(registro.getArtefatoInterface())
                + "\t" + registro.getInicioX()
                + "\t" + registro.getInicioY()
                + "\t" + registro.getFimX()
                + "\t" + registro.getFimY()
                + "\t" + registro.getAmostras()
                + "\t" + registro.getMudancasOrientacao()
                + "\t" + registro.getDistanciaPercorrida()
                + "\t" + registro.getDestinoGeometrico().name();
    }

    private void inicializarArquivo() {
        File diretorio = arquivo.getParentFile();
        if (diretorio != null && !diretorio.exists()) {
            diretorio.mkdirs();
        }
        if (!arquivo.exists() || arquivo.length() == 0L) {
            escreverLinha(cabecalhoTsv(), false);
        }
    }

    private void escreverLinha(String linha, boolean anexar) {
        BufferedWriter writer = null;
        try {
            writer = new BufferedWriter(new OutputStreamWriter(
                    new FileOutputStream(arquivo, anexar), "UTF-8"));
            writer.write(linha);
            writer.newLine();
        } catch (Exception ex) {
            System.err.println("Erro ao registrar gesto de interação: " + ex.getMessage());
        } finally {
            try { if (writer != null) writer.close(); } catch (Exception ignored) { }
        }
    }

    private static String formatarInstante(long instanteMillis) {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS")
                .format(new Date(instanteMillis));
    }

    private static String campo(String valor) {
        if (valor == null) {
            return "";
        }
        return valor.replace('\t', ' ').replace('\r', ' ').replace('\n', ' ');
    }
}
