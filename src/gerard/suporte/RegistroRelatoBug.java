package gerard.suporte;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Persiste relatos voluntários de problemas em arquivo separado dos logs de
 * interação e dos dados de curadoria. Nenhum envio pela rede é realizado.
 */
public final class RegistroRelatoBug {
    private static final String CABECALHO_ANTIGO =
            "data_hora\tdescricao\tsituacao_id\tcategoria\tidioma_interface\tidioma_situacao\tenunciado";
    private static final String CABECALHO_ATUAL =
            "data_hora\tdescricao\tsituacao_id\tcategoria\trepresentacoes\tidioma_interface\tidioma_situacao\tenunciado";

    private RegistroRelatoBug() { }

    public static synchronized File registrar(String descricao, String situacaoId,
            String categoria, String representacoes, String idiomaInterface,
            String idiomaSituacao, String enunciado) throws IOException {
        String relato = campo(descricao);
        if (relato.length() == 0) {
            throw new IllegalArgumentException("Descrição vazia.");
        }

        File diretorio = obterDiretorioRelatos();
        if (!diretorio.exists() && !diretorio.mkdirs()) {
            throw new IOException("Não foi possível criar a pasta de relatos.");
        }

        File arquivo = new File(diretorio, "relatos_bug.tsv");
        garantirCabecalhoAtual(arquivo);
        boolean novo = !arquivo.exists() || arquivo.length() == 0;
        try (BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(
                new FileOutputStream(arquivo, true), StandardCharsets.UTF_8))) {
            if (novo) {
                bw.write(CABECALHO_ATUAL);
                bw.newLine();
            }
            bw.write(campo(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date())));
            bw.write('\t');
            bw.write(relato);
            bw.write('\t');
            bw.write(campo(situacaoId));
            bw.write('\t');
            bw.write(campo(categoria));
            bw.write('\t');
            bw.write(campo(representacoes));
            bw.write('\t');
            bw.write(campo(idiomaInterface));
            bw.write('\t');
            bw.write(campo(idiomaSituacao));
            bw.write('\t');
            bw.write(campo(enunciado));
            bw.newLine();
        }
        return arquivo;
    }

    /**
     * Migra silenciosamente o arquivo criado pelas versões C73/C74, inserindo
     * a nova coluna de representações sem perder relatos anteriores.
     */
    private static void garantirCabecalhoAtual(File arquivo) throws IOException {
        if (arquivo == null || !arquivo.isFile() || arquivo.length() == 0) {
            return;
        }
        List<String> linhas = Files.readAllLines(arquivo.toPath(), StandardCharsets.UTF_8);
        if (linhas.isEmpty() || CABECALHO_ATUAL.equals(linhas.get(0))) {
            return;
        }
        if (!CABECALHO_ANTIGO.equals(linhas.get(0))) {
            return;
        }

        List<String> migradas = new ArrayList<String>();
        migradas.add(CABECALHO_ATUAL);
        for (int i = 1; i < linhas.size(); i++) {
            String linha = linhas.get(i);
            String[] campos = linha.split("\t", -1);
            if (campos.length == 7) {
                StringBuilder atualizada = new StringBuilder();
                for (int c = 0; c < campos.length; c++) {
                    if (c > 0) {
                        atualizada.append('\t');
                    }
                    atualizada.append(campos[c]);
                    if (c == 3) {
                        atualizada.append('\t');
                    }
                }
                migradas.add(atualizada.toString());
            } else {
                migradas.add(linha);
            }
        }
        Files.write(arquivo.toPath(), migradas, StandardCharsets.UTF_8);
    }

    public static File obterDiretorioRelatos() {
        File base = new File(System.getProperty("user.home"), "Gerard");
        return new File(base, "relatos_bug");
    }

    private static String campo(String valor) {
        return valor == null ? "" : valor.replace('\t', ' ')
                .replace('\n', ' ').replace('\r', ' ').trim();
    }
}
