package gerard.desktop.composicaomedidas;

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
 * Fonte dos dados: o log curado situacoes_vergnaud.tsv, filtrado por
 * tipo = COMPOSICAO_MEDIDAS e idioma = PORTUGUES, conforme instruído.
 * As colunas quantidade_1/quantidade_2/resultado ainda estão vazias
 * (dados não anotados); os valores são extraídos do próprio enunciado.
 */
public final class CarregadorSituacoesComposicaoMedidas {

    private static final String[] CAMINHOS_CANDIDATOS = {
        "src/gerard/campoaditivo/dados/situacoes_vergnaud.tsv",
        "dados/situacoes_vergnaud.tsv",
        "../src/gerard/campoaditivo/dados/situacoes_vergnaud.tsv",
    };
    private static final String RECURSO_CLASSPATH =
            "/gerard/campoaditivo/dados/situacoes_vergnaud.tsv";

    private CarregadorSituacoesComposicaoMedidas() {
    }

    public static List<SituacaoComposicaoMedidas> carregar() {
        List<String[]> linhas = lerTsv();
        List<SituacaoComposicaoMedidas> resultado = new ArrayList<SituacaoComposicaoMedidas>();
        if (linhas.isEmpty()) {
            return resultado;
        }
        String[] cabecalho = linhas.get(0);
        int idxId = indiceColuna(cabecalho, "# id", "id");
        int idxIdioma = indiceColuna(cabecalho, "idioma");
        int idxTipo = indiceColuna(cabecalho, "tipo");
        int idxEnunciado = indiceColuna(cabecalho, "enunciado");

        for (int i = 1; i < linhas.size(); i++) {
            String[] campos = linhas.get(i);
            if (campos.length <= Math.max(idxEnunciado, Math.max(idxTipo, idxIdioma))) {
                continue;
            }
            if (!"PORTUGUES".equals(campos[idxIdioma].trim())
                    || !"COMPOSICAO_MEDIDAS".equals(campos[idxTipo].trim())) {
                continue;
            }
            String enunciado = campos[idxEnunciado].trim();
            SituacaoComposicaoMedidas situacao = classificar(
                    idxId >= 0 && campos.length > idxId ? campos[idxId].trim() : ("linha_" + i),
                    enunciado);
            if (situacao != null) {
                resultado.add(situacao);
            }
        }
        return resultado;
    }

    /**
     * Classifica os números encontrados no enunciado em parte1/parte2/todo.
     * Heurística: se a pergunta pede o total ("ao todo", "em conjunto",
     * "no total", "juntos"), os dois números encontrados são as partes e o
     * todo é a incógnita. Caso contrário (pergunta sobre uma parte
     * específica), o primeiro número é o todo (conhecido), o segundo é a
     * parte 1 (conhecida) e a parte 2 é a incógnita.
     */
    private static SituacaoComposicaoMedidas classificar(String id, String enunciado) {
        List<NumeroTextoExtraido> encontrados = ExtratorNumerosPortugues.extrair(enunciado);
        List<NumeroTextoExtraido> numeros = new ArrayList<NumeroTextoExtraido>();
        for (NumeroTextoExtraido n : encontrados) {
            if (!n.ehIncognita()) {
                numeros.add(n);
            }
        }
        if (numeros.size() < 2) {
            return null;
        }
        NumeroTextoExtraido incognitaTextual = null;
        for (NumeroTextoExtraido n : encontrados) {
            if (n.ehIncognita()) {
                incognitaTextual = n;
            }
        }
        if (incognitaTextual == null) {
            return null;
        }

        String minusculo = enunciado.toLowerCase();
        boolean pedeTotal = minusculo.contains("ao todo")
                || minusculo.contains("no total")
                || minusculo.contains("em conjunto")
                || minusculo.contains("juntos")
                || minusculo.contains("ao total");

        NumeroTextoExtraido parte1;
        NumeroTextoExtraido parte2;
        NumeroTextoExtraido todo;
        if (pedeTotal || numeros.size() == 2) {
            parte1 = numeros.get(0);
            parte2 = numeros.get(1);
            todo = new NumeroTextoExtraido("?", null, incognitaTextual.inicio, incognitaTextual.fim);
        } else {
            todo = numeros.get(0);
            parte1 = numeros.get(1);
            parte2 = new NumeroTextoExtraido("?", null, incognitaTextual.inicio, incognitaTextual.fim);
        }
        return new SituacaoComposicaoMedidas(id, enunciado, parte1, parte2, todo);
    }

    private static int indiceColuna(String[] cabecalho, String... nomesPossiveis) {
        for (int i = 0; i < cabecalho.length; i++) {
            String coluna = cabecalho[i].trim();
            for (String nome : nomesPossiveis) {
                if (coluna.equalsIgnoreCase(nome)) {
                    return i;
                }
            }
        }
        return -1;
    }

    private static List<String[]> lerTsv() {
        List<String[]> linhas = new ArrayList<String[]>();
        InputStream fluxo = obterFluxoTsv();
        if (fluxo == null) {
            return linhas;
        }
        BufferedReader leitor = null;
        try {
            leitor = new BufferedReader(new InputStreamReader(fluxo, StandardCharsets.UTF_8));
            String linha;
            while ((linha = leitor.readLine()) != null) {
                linhas.add(linha.split("\t", -1));
            }
        } catch (IOException ex) {
            // Sem dados disponíveis; a tela mostra estado vazio.
        } finally {
            if (leitor != null) {
                try {
                    leitor.close();
                } catch (IOException ignorada) {
                    // nada a fazer
                }
            }
        }
        return linhas;
    }

    private static InputStream obterFluxoTsv() {
        InputStream doClasspath = CarregadorSituacoesComposicaoMedidas.class
                .getResourceAsStream(RECURSO_CLASSPATH);
        if (doClasspath != null) {
            return doClasspath;
        }
        for (String caminho : CAMINHOS_CANDIDATOS) {
            File arquivo = new File(caminho);
            if (arquivo.isFile()) {
                try {
                    return new FileInputStream(arquivo);
                } catch (IOException ex) {
                    // tenta o próximo candidato
                }
            }
        }
        return null;
    }
}
