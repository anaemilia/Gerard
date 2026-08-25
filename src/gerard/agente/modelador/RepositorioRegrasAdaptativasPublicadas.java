package gerard.agente.modelador;

import gerard.adaptacao.EscopoProprietarioSemantico;
import gerard.adaptacao.EstadoPublicacaoRegra;
import gerard.adaptacao.RegraAdaptativaPublicada;
import gerard.adaptacao.sessao.FonteRegrasAdaptativasCandidatas;
import gerard.agente.conhecimento.AnalisadorJsonSimples;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.time.Instant;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

/**
 * Catálogo operacional das regras que o Agente Modelador publicou.
 *
 * <p>É uma fronteira diferente de {@link RepositorioRegrasInferidas}: saídas
 * brutas de PART/Apriori permanecem experimentais e nunca são lidas por este
 * repositório. Somente registros do esquema explícito abaixo podem entrar na
 * fotografia criada no login.</p>
 */
public final class RepositorioRegrasAdaptativasPublicadas
        implements FonteRegrasAdaptativasCandidatas {

    public static final String VERSAO_ESQUEMA =
            "gerard.regra_adaptativa_publicada.v1";

    private static final Set<String> ALGORITMOS_PUBLICAVEIS =
            new LinkedHashSet<String>();

    static {
        ALGORITMOS_PUBLICAVEIS.add("PART");
        ALGORITMOS_PUBLICAVEIS.add("J48");
        ALGORITMOS_PUBLICAVEIS.add("J48.PART");
        ALGORITMOS_PUBLICAVEIS.add("APRIORI");
    }

    private final File arquivo;

    public RepositorioRegrasAdaptativasPublicadas() {
        this(new File(
                new File(new File(System.getProperty("user.home"), "Gerard"),
                        "analises"),
                "regras_adaptativas_publicadas.jsonl"));
    }

    public RepositorioRegrasAdaptativasPublicadas(File arquivo) {
        if (arquivo == null) {
            throw new IllegalArgumentException("arquivo de regras é obrigatório");
        }
        this.arquivo = arquivo;
    }

    @Override
    public synchronized List<RegraAdaptativaPublicada> obterPara(
            String usuarioId) {
        String id = textoObrigatorio(usuarioId, "id do usuário");
        List<RegraAdaptativaPublicada> regras =
                new ArrayList<RegraAdaptativaPublicada>();
        for (RegistroPersistido registro : lerTodos()) {
            if (id.equals(registro.usuarioId)) {
                regras.add(registro.regra);
            }
        }
        return regras;
    }

    /**
     * Substitui atomicamente a versão publicada para um usuário.
     *
     * <p>A operação é deliberada: não aceita regra experimental, não interpreta
     * o texto produzido pelo Weka e não escolhe código de ajuda, proprietário ou
     * condições. Esses dados já precisam chegar explicitamente validados.</p>
     */
    synchronized void publicarPara(
            String usuarioId,
            List<RegraAdaptativaPublicada> regras) throws IOException {
        String id = textoObrigatorio(usuarioId, "id do usuário");
        if (regras == null) {
            throw new IllegalArgumentException("regras publicadas não podem ser nulas");
        }

        List<RegistroPersistido> substitutas =
                new ArrayList<RegistroPersistido>();
        Set<String> identidades = new LinkedHashSet<String>();
        for (RegraAdaptativaPublicada regra : regras) {
            validarPublicavel(regra);
            String identidade = identidade(regra);
            if (!identidades.add(identidade)) {
                throw new IllegalArgumentException(
                        "regra publicada duplicada: " + identidade);
            }
            substitutas.add(new RegistroPersistido(id, regra));
        }

        List<RegistroPersistido> todos = lerTodos();
        List<RegistroPersistido> atualizados =
                new ArrayList<RegistroPersistido>();
        for (RegistroPersistido registro : todos) {
            if (!id.equals(registro.usuarioId)) {
                atualizados.add(registro);
            }
        }
        atualizados.addAll(substitutas);
        escreverAtomicamente(atualizados);
    }

    private List<RegistroPersistido> lerTodos() {
        List<RegistroPersistido> registros =
                new ArrayList<RegistroPersistido>();
        if (!arquivo.exists()) {
            return registros;
        }
        int numeroLinha = 0;
        try (BufferedReader leitor = new BufferedReader(new InputStreamReader(
                new FileInputStream(arquivo), "UTF-8"))) {
            String linha;
            while ((linha = leitor.readLine()) != null) {
                numeroLinha++;
                String limpa = linha.trim();
                if (limpa.isEmpty()) continue;
                try {
                    registros.add(converter(objetoRaiz(limpa)));
                } catch (RuntimeException erro) {
                    throw new IllegalStateException(
                            "regra adaptativa publicada inválida em "
                                    + arquivo.getAbsolutePath() + ", linha "
                                    + numeroLinha,
                            erro);
                }
            }
        } catch (IOException erro) {
            throw new IllegalStateException(
                    "falha ao ler regras adaptativas publicadas em "
                            + arquivo.getAbsolutePath(),
                    erro);
        }
        return registros;
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> objetoRaiz(String linha) {
        Object raiz = AnalisadorJsonSimples.analisar(linha);
        if (!(raiz instanceof Map)) {
            throw new IllegalArgumentException("a linha JSONL deve ser um objeto");
        }
        return (Map<String, Object>) raiz;
    }

    private RegistroPersistido converter(Map<String, Object> objeto) {
        String esquema = stringObrigatoria(objeto, "schema_version");
        if (!VERSAO_ESQUEMA.equals(esquema)) {
            throw new IllegalArgumentException(
                    "schema_version não suportada: " + esquema);
        }
        String usuarioId = stringObrigatoria(objeto, "usuario_id");
        String algoritmo = stringObrigatoria(objeto, "algoritmo_origem");
        validarAlgoritmo(algoritmo);
        EstadoPublicacaoRegra estado = EstadoPublicacaoRegra.valueOf(
                stringObrigatoria(objeto, "estado"));
        if (estado != EstadoPublicacaoRegra.PUBLICADA) {
            throw new IllegalArgumentException(
                    "o catálogo operacional aceita somente estado PUBLICADA");
        }

        RegraAdaptativaPublicada regra = new RegraAdaptativaPublicada(
                stringObrigatoria(objeto, "id"),
                stringObrigatoria(objeto, "versao"),
                algoritmo,
                Instant.parse(stringObrigatoria(objeto, "publicada_em")),
                stringObrigatoria(objeto, "proveniencia_casos"),
                stringObrigatoria(objeto, "proprietario_semantico"),
                EscopoProprietarioSemantico.de(
                        stringObrigatoria(objeto, "escopo")),
                condicoes(objeto.get("condicoes")),
                stringObrigatoria(objeto, "codigo_ajuda"),
                numeroOpcional(objeto, "suporte"),
                numeroOpcional(objeto, "confianca"),
                numeroOpcional(objeto, "lift"),
                estado);
        return new RegistroPersistido(usuarioId, regra);
    }

    @SuppressWarnings("unchecked")
    private Map<String, String> condicoes(Object valor) {
        if (!(valor instanceof Map)) {
            throw new IllegalArgumentException("condicoes deve ser um objeto JSON");
        }
        Map<String, String> resultado = new LinkedHashMap<String, String>();
        for (Map.Entry<Object, Object> entrada
                : ((Map<Object, Object>) valor).entrySet()) {
            if (!(entrada.getKey() instanceof String)
                    || !(entrada.getValue() instanceof String)) {
                throw new IllegalArgumentException(
                        "condições devem mapear texto para texto");
            }
            resultado.put((String) entrada.getKey(), (String) entrada.getValue());
        }
        return resultado;
    }

    private Double numeroOpcional(Map<String, Object> objeto, String campo) {
        Object valor = objeto.get(campo);
        if (valor == null) return null;
        if (!(valor instanceof Number)) {
            throw new IllegalArgumentException(campo + " deve ser numérico ou null");
        }
        return Double.valueOf(((Number) valor).doubleValue());
    }

    private String stringObrigatoria(Map<String, Object> objeto, String campo) {
        Object valor = objeto.get(campo);
        if (!(valor instanceof String)) {
            throw new IllegalArgumentException(campo + " deve ser texto");
        }
        return textoObrigatorio((String) valor, campo);
    }

    private void validarPublicavel(RegraAdaptativaPublicada regra) {
        if (regra == null) {
            throw new IllegalArgumentException("regra publicada não pode ser nula");
        }
        if (!regra.estaPublicada()) {
            throw new IllegalArgumentException(
                    "somente regra com estado PUBLICADA pode ser publicada");
        }
        validarAlgoritmo(regra.getAlgoritmoOrigem());
    }

    private void validarAlgoritmo(String algoritmo) {
        String normalizado = textoObrigatorio(
                algoritmo, "algoritmo de origem").toUpperCase(Locale.ROOT);
        if (!ALGORITMOS_PUBLICAVEIS.contains(normalizado)) {
            throw new IllegalArgumentException(
                    "algoritmo não publicável: " + algoritmo);
        }
    }

    private void escreverAtomicamente(
            List<RegistroPersistido> registros) throws IOException {
        File absoluto = arquivo.getAbsoluteFile();
        File pai = absoluto.getParentFile();
        if (pai != null && !pai.exists() && !pai.mkdirs()) {
            throw new IOException("não foi possível criar " + pai.getAbsolutePath());
        }
        File temporario = new File(pai, absoluto.getName() + ".tmp");
        try (BufferedWriter escritor = new BufferedWriter(new OutputStreamWriter(
                new FileOutputStream(temporario), "UTF-8"))) {
            for (RegistroPersistido registro : registros) {
                escritor.write(serializar(registro));
                escritor.newLine();
            }
        }
        try {
            Files.move(temporario.toPath(), absoluto.toPath(),
                    StandardCopyOption.REPLACE_EXISTING,
                    StandardCopyOption.ATOMIC_MOVE);
        } catch (AtomicMoveNotSupportedException semMovimentoAtomico) {
            Files.move(temporario.toPath(), absoluto.toPath(),
                    StandardCopyOption.REPLACE_EXISTING);
        }
    }

    private String serializar(RegistroPersistido registro) {
        RegraAdaptativaPublicada regra = registro.regra;
        StringBuilder json = new StringBuilder();
        json.append('{');
        campo(json, "schema_version", VERSAO_ESQUEMA, true);
        campo(json, "usuario_id", registro.usuarioId, false);
        campo(json, "id", regra.getId(), false);
        campo(json, "versao", regra.getVersao(), false);
        campo(json, "algoritmo_origem", regra.getAlgoritmoOrigem(), false);
        campo(json, "publicada_em", regra.getPublicadaEm().toString(), false);
        campo(json, "proveniencia_casos", regra.getProvenienciaCasos(), false);
        campo(json, "proprietario_semantico", regra.getProprietarioSemantico(), false);
        campo(json, "escopo", regra.getEscopo().getChave(), false);
        json.append(",\"condicoes\":{");
        boolean primeira = true;
        for (Map.Entry<String, String> condicao : regra.getCondicoes().entrySet()) {
            if (!primeira) json.append(',');
            json.append('"').append(escaparJson(condicao.getKey())).append("\":\"")
                    .append(escaparJson(condicao.getValue())).append('"');
            primeira = false;
        }
        json.append('}');
        campo(json, "codigo_ajuda", regra.getCodigoAjudaRecomendada(), false);
        numero(json, "suporte", regra.getSuporte());
        numero(json, "confianca", regra.getConfianca());
        numero(json, "lift", regra.getLift());
        campo(json, "estado", regra.getEstado().name(), false);
        json.append('}');
        return json.toString();
    }

    private void campo(
            StringBuilder json,
            String nome,
            String valor,
            boolean primeiro) {
        if (!primeiro) json.append(',');
        json.append('"').append(nome).append("\":\"")
                .append(escaparJson(valor)).append('"');
    }

    private void numero(StringBuilder json, String nome, Double valor) {
        json.append(",\"").append(nome).append("\":");
        json.append(valor == null ? "null" : valor.toString());
    }

    private String escaparJson(String valor) {
        StringBuilder resultado = new StringBuilder();
        for (int i = 0; i < valor.length(); i++) {
            char atual = valor.charAt(i);
            switch (atual) {
                case '"': resultado.append("\\\""); break;
                case '\\': resultado.append("\\\\"); break;
                case '\b': resultado.append("\\b"); break;
                case '\f': resultado.append("\\f"); break;
                case '\n': resultado.append("\\n"); break;
                case '\r': resultado.append("\\r"); break;
                case '\t': resultado.append("\\t"); break;
                default:
                    if (atual < 0x20) {
                        resultado.append(String.format("\\u%04x", Integer.valueOf(atual)));
                    } else {
                        resultado.append(atual);
                    }
            }
        }
        return resultado.toString();
    }

    private static String identidade(RegraAdaptativaPublicada regra) {
        return regra.getProprietarioSemantico() + "|" + regra.getEscopo()
                + "|" + regra.getId() + "@" + regra.getVersao();
    }

    private static String textoObrigatorio(String valor, String nome) {
        String normalizado = valor == null ? "" : valor.trim();
        if (normalizado.isEmpty()) {
            throw new IllegalArgumentException(nome + " não pode ser vazio");
        }
        return normalizado;
    }

    private static final class RegistroPersistido {
        private final String usuarioId;
        private final RegraAdaptativaPublicada regra;

        private RegistroPersistido(
                String usuarioId,
                RegraAdaptativaPublicada regra) {
            this.usuarioId = usuarioId;
            this.regra = regra;
        }
    }
}
