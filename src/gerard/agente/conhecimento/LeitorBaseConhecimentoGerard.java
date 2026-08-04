package gerard.agente.conhecimento;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Lê a base de conhecimento integrada do Gérard (JSONL) do classpath —
 * mesmo padrão de RepositorioProtocolosReaisReplay (getResourceAsStream,
 * UTF-8), sem mover nem reorganizar os dados de pesquisa da usuária. Os
 * arquivos ficam em
 * dados/base_conhecimento_gerard_jsons/base_conhecimento_gerard/ (ver
 * manifest_base_conhecimento_gerard.json para a lista completa e as
 * contagens esperadas).
 */
public final class LeitorBaseConhecimentoGerard {
    private static final String DIRETORIO_BASE =
            "/gerard/pesquisador/replay/dados/base_conhecimento_gerard_jsons/base_conhecimento_gerard/";

    public List<RegraConhecimento> lerDominio() {
        return lerArquivo("regras_dominio.jsonl");
    }

    public List<RegraConhecimento> lerPedagogicas() {
        return lerArquivo("regras_pedagogicas.jsonl");
    }

    public List<RegraConhecimento> lerEntrevistas() {
        return lerArquivo("regras_entrevistas.jsonl");
    }

    public List<RegraConhecimento> lerIntegradas() {
        return lerArquivo("regras_integradas.jsonl");
    }

    public List<RegraConhecimento> lerJ48Part() {
        return lerArquivo("regras_j48_part.jsonl");
    }

    public List<RegraConhecimento> lerApriori() {
        return lerArquivo("regras_apriori.jsonl");
    }

    public List<RegraConhecimento> lerBaseIntegrada() {
        return lerArquivo("base_integrada_regras_gerard.jsonl");
    }

    public List<RegraConhecimento> lerArquivo(String nomeArquivo) {
        String caminho = DIRETORIO_BASE + nomeArquivo;
        InputStream fluxo = LeitorBaseConhecimentoGerard.class.getResourceAsStream(caminho);
        if (fluxo == null) {
            throw new IllegalStateException("Arquivo de regras não encontrado no classpath: " + caminho);
        }
        List<RegraConhecimento> regras = new ArrayList<RegraConhecimento>();
        try (BufferedReader leitor = new BufferedReader(new InputStreamReader(fluxo, StandardCharsets.UTF_8))) {
            String linha;
            while ((linha = leitor.readLine()) != null) {
                String linhaLimpa = linha.trim();
                if (linhaLimpa.length() == 0) {
                    continue;
                }
                regras.add(converter(objetoRaiz(linhaLimpa)));
            }
        } catch (IOException e) {
            throw new IllegalStateException("Falha ao ler arquivo de regras: " + caminho, e);
        }
        return regras;
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> objetoRaiz(String linha) {
        return (Map<String, Object>) AnalisadorJsonSimples.analisar(linha);
    }

    @SuppressWarnings("unchecked")
    private RegraConhecimento converter(Map<String, Object> objeto) {
        String ruleId = (String) objeto.get("rule_id");
        String nome = (String) objeto.get("nome");
        String tipo = (String) objeto.get("tipo");
        String versao = (String) objeto.get("versao");
        String status = (String) objeto.get("status");
        Object prioridadeObj = objeto.get("prioridade");
        int prioridade = prioridadeObj instanceof Number ? ((Number) prioridadeObj).intValue() : 0;
        String explicacao = (String) objeto.get("explicacao");

        List<Condicao> antecedentes = new ArrayList<Condicao>();
        Object antecedentesObj = objeto.get("antecedentes");
        if (antecedentesObj instanceof List) {
            for (Object item : (List<Object>) antecedentesObj) {
                Map<String, Object> condicaoMapa = (Map<String, Object>) item;
                antecedentes.add(new Condicao(
                        (String) condicaoMapa.get("campo"),
                        (String) condicaoMapa.get("operador"),
                        condicaoMapa.get("valor")));
            }
        }

        List<Conclusao> conclusoes = new ArrayList<Conclusao>();
        Object conclusoesObj = objeto.get("conclusoes");
        if (conclusoesObj instanceof List) {
            for (Object item : (List<Object>) conclusoesObj) {
                Map<String, Object> conclusaoMapa = (Map<String, Object>) item;
                conclusoes.add(new Conclusao((String) conclusaoMapa.get("campo"), conclusaoMapa.get("valor")));
            }
        }

        List<String> fontes = new ArrayList<String>();
        Object fontesObj = objeto.get("fontes");
        if (fontesObj instanceof List) {
            for (Object item : (List<Object>) fontesObj) {
                fontes.add(String.valueOf(item));
            }
        }

        return new RegraConhecimento(
                ruleId, nome, tipo, versao, status, prioridade, antecedentes, conclusoes, fontes, explicacao);
    }
}
