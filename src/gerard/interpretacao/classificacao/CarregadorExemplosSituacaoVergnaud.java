package gerard.interpretacao.classificacao;

import gerard.campoaditivo.modelo.TipoSituacaoAditiva;
import gerard.idioma.IdiomaInterface;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * Carrega exemplos rotulados para treinamento local do classificador.
 */
public class CarregadorExemplosSituacaoVergnaud {
    private static final String RECURSO_PRINCIPAL = "/gerard/interpretacao/classificacao/exemplos_treino_vergnaud.tsv";
    private static final String RECURSO_FALLBACK = "/gerard/campoaditivo/dados/situacoes_vergnaud.tsv";

    public List<ExemploSituacaoVergnaud> carregar() {
        InputStream input = abrirRecurso(RECURSO_PRINCIPAL);
        if (input == null) {
            input = abrirRecurso(RECURSO_FALLBACK);
        }
        if (input == null) {
            input = abrirArquivoFisico("src/gerard/interpretacao/classificacao/exemplos_treino_vergnaud.tsv");
        }
        if (input == null) {
            input = abrirArquivoFisico("src/gerard/campoaditivo/dados/situacoes_vergnaud.tsv");
        }
        if (input == null) {
            input = abrirArquivoFisico("dados/situacoes_vergnaud.tsv");
        }
        return ler(input);
    }

    private InputStream abrirRecurso(String recurso) {
        try {
            return CarregadorExemplosSituacaoVergnaud.class.getResourceAsStream(recurso);
        } catch (Exception ex) {
            return null;
        }
    }

    private InputStream abrirArquivoFisico(String caminho) {
        try {
            File arquivo = new File(caminho);
            if (arquivo.exists() && arquivo.isFile()) {
                return new FileInputStream(arquivo);
            }
        } catch (IOException ex) {
            return null;
        }
        return null;
    }

    private List<ExemploSituacaoVergnaud> ler(InputStream input) {
        List<ExemploSituacaoVergnaud> exemplos = new ArrayList<ExemploSituacaoVergnaud>();
        if (input == null) {
            return exemplos;
        }

        try (BufferedReader br = new BufferedReader(new InputStreamReader(input, StandardCharsets.UTF_8))) {
            String linha;
            while ((linha = br.readLine()) != null) {
                linha = linha.trim();
                if (linha.length() == 0 || linha.startsWith("#")) {
                    continue;
                }
                String[] partes = linha.split("\\t", 5);
                if (partes.length >= 4) {
                    adicionarExemplo(exemplos, partes[0], partes[1], partes[2], partes[3]);
                } else {
                    // Formato reduzido: texto;categoria
                    String[] simples = linha.split(";", 2);
                    if (simples.length == 2) {
                        adicionarExemplo(exemplos, "PORTUGUES", simples[1], "", simples[0]);
                    }
                }
            }
        } catch (IOException ex) {
            return exemplos;
        }
        return exemplos;
    }

    private void adicionarExemplo(List<ExemploSituacaoVergnaud> exemplos, String idiomaTexto, String categoriaTexto, String contexto, String enunciado) {
        try {
            IdiomaInterface idioma = IdiomaInterface.valueOf(idiomaTexto.trim());
            TipoSituacaoAditiva categoria = TipoSituacaoAditiva.valueOf(categoriaTexto.trim());
            String texto = enunciado == null ? "" : enunciado.trim();
            if (texto.length() > 0) {
                exemplos.add(new ExemploSituacaoVergnaud(texto, categoria, idioma, contexto));
            }
        } catch (IllegalArgumentException ex) {
            // Exemplo ignorado quando o rótulo ainda não pertence ao domínio atual.
        }
    }
}
