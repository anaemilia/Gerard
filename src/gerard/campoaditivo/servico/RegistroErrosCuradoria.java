package gerard.campoaditivo.servico;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Registro técnico separado do log de interação do aprendiz.
 * Guarda somente inconsistências de curadoria e decisões do pesquisador.
 */
public final class RegistroErrosCuradoria {
    private RegistroErrosCuradoria() { }

    public static synchronized void registrar(String categoria, String situacaoId,
            String grupoId, String detalhes, String decisao) {
        try {
            File diretorio = RepositorioSituacoesAditivas.obterDiretorioCuradoriaUsuario();
            if (!diretorio.exists()) diretorio.mkdirs();
            File arquivo = new File(diretorio, "erros_curadoria.tsv");
            boolean novo = !arquivo.exists() || arquivo.length() == 0;
            try (BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(
                    new FileOutputStream(arquivo, true), StandardCharsets.UTF_8))) {
                if (novo) {
                    bw.write("data_hora\tcategoria\tsituacao_id\tsituacao_grupo_id\tdetalhes\tdecisao");
                    bw.newLine();
                }
                bw.write(campo(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date())) + "\t"
                        + campo(categoria) + "\t" + campo(situacaoId) + "\t" + campo(grupoId) + "\t"
                        + campo(detalhes) + "\t" + campo(decisao));
                bw.newLine();
            }
        } catch (Exception ignorada) {
            // Uma falha no registro técnico não deve impedir a correção da curadoria.
        }
    }

    private static String campo(String valor) {
        return valor == null ? "" : valor.replace('\t', ' ').replace('\n', ' ').replace('\r', ' ').trim();
    }
}
