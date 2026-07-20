package gerard.pesquisador.tentativa;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

/** Persistência local, separada do log automático de interação. */
public final class RepositorioArtefatosExplicativos {
    private final File arquivo;

    public RepositorioArtefatosExplicativos() {
        File diretorio = new File(new File(System.getProperty("user.home"), "Gerard"), "analises");
        if (!diretorio.exists()) diretorio.mkdirs();
        arquivo = new File(diretorio, "artefatos_explicativos.tsv");
    }

    public synchronized void salvar(ArtefatoExplicativo artefato) throws Exception {
        boolean novo = !arquivo.exists() || arquivo.length() == 0;
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(arquivo, true), "UTF-8"));
        try {
            if (novo) {
                writer.write("timestamp\ttentativa_id\tusuario_id\tproblema_id\tsituacao_id\tsituacao_grupo_id\tidioma\tcategoria\ttipo_registro\telemento\tpapel_semantico\tdificuldade\texplicacao\tinvariante_operatorio\tfotografia_modelagem");
                writer.newLine();
            }
            String timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(new Date());
            if (artefato.getRespostas().isEmpty()) {
                gravarLinha(writer, timestamp, artefato, "GERAL", "", "", "", artefato.getExplicacaoGeral());
            } else {
                for (RespostaElementoModelagem resposta : artefato.getRespostas()) {
                    gravarLinha(writer, timestamp, artefato, "ELEMENTO", resposta.getElemento(),
                            resposta.getPapelSemantico(), resposta.getDificuldade(), resposta.getExplicacao());
                }
                gravarLinha(writer, timestamp, artefato, "GERAL", "", "", "", artefato.getExplicacaoGeral());
            }
        } finally {
            writer.close();
        }
    }

    private void gravarLinha(BufferedWriter writer, String timestamp, ArtefatoExplicativo a,
            String tipo, String elemento, String papel, String dificuldade, String explicacao) throws Exception {
        String[] campos = new String[]{timestamp, a.getTentativaId(), a.getUsuarioId(), a.getProblemaId(),
            a.getSituacaoId(), a.getSituacaoGrupoId(), a.getIdioma(), a.getCategoria(), tipo,
            elemento, papel, dificuldade, explicacao, a.getInvarianteOperatorio(), a.getFotografiaModelagem()};
        for (int i = 0; i < campos.length; i++) {
            if (i > 0) writer.write('\t');
            writer.write(escapar(campos[i]));
        }
        writer.newLine();
    }

    private String escapar(String valor) {
        if (valor == null) return "";
        return valor.replace("\\", "\\\\").replace("\t", "\\t").replace("\r", "\\r").replace("\n", "\\n");
    }
}
