package gerard.i18n;

import gerard.campoaditivo.modelo.TipoSituacaoAditiva;
import gerard.idioma.IdiomaInterface;
import gerard.interpretacao.modelo.CategoriaProblema;
import gerard.interpretacao.modelo.IdiomaProblema;
import java.text.MessageFormat;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

public class ServicoLocalizacao {
    private static final ServicoLocalizacao INSTANCIA = new ServicoLocalizacao();

    private final Map<IdiomaInterface, Map<String, String>> mensagens;
    private IdiomaInterface idiomaAtual;

    private ServicoLocalizacao() {
        this.mensagens = new EnumMap<IdiomaInterface, Map<String, String>>(IdiomaInterface.class);
        this.idiomaAtual = IdiomaInterface.PORTUGUES;
        carregarMensagens();
    }

    public static ServicoLocalizacao getInstancia() {
        return INSTANCIA;
    }

    public void definirIdioma(IdiomaInterface idioma) {
        if (idioma != null) {
            this.idiomaAtual = idioma;
        }
    }

    public IdiomaInterface getIdiomaAtual() {
        return idiomaAtual;
    }

    public String texto(String chave) {
        Map<String, String> mapa = mensagens.get(idiomaAtual);
        if (mapa != null && mapa.containsKey(chave)) {
            return mapa.get(chave);
        }
        Map<String, String> fallback = mensagens.get(IdiomaInterface.PORTUGUES);
        if (fallback != null && fallback.containsKey(chave)) {
            return fallback.get(chave);
        }
        return chave;
    }

    public String formatar(String chave, Object... argumentos) {
        return MessageFormat.format(texto(chave), argumentos);
    }

    public String nomeIdioma(IdiomaInterface idioma) {
        if (idioma == null) {
            return texto("idioma.portugues");
        }
        switch (idioma) {
            case PORTUGUES: return texto("idioma.portugues");
            case INGLES: return texto("idioma.ingles");
            case FRANCES: return texto("idioma.frances");
            case ESPANHOL: return texto("idioma.espanhol");
            default: return texto("idioma.portugues");
        }
    }

    public String nomeIdiomaDetectado(IdiomaProblema idioma) {
        if (idioma == null) {
            return texto("idioma.indefinido");
        }
        switch (idioma) {
            case PORTUGUES: return texto("idioma.portugues");
            case INGLES: return texto("idioma.ingles");
            case FRANCES: return texto("idioma.frances");
            case ESPANHOL: return texto("idioma.espanhol");
            case INDEFINIDO: default: return texto("idioma.indefinido");
        }
    }

    public String descricaoCategoria(CategoriaProblema categoria) {
        if (categoria == null) {
            return texto("categoria.indefinida");
        }
        switch (categoria) {
            case COMPOSICAO_MEDIDAS: return texto("categoria.composicao_medidas");
            case TRANSFORMACAO_MEDIDAS: return texto("categoria.transformacao_medidas");
            case COMPOSICAO_TRANSFORMACAO_MEDIDAS: return texto("categoria.composicao_transformacao_medidas");
            case COMPARACAO_MEDIDAS: return texto("categoria.comparacao_medidas");
            case COMPOSICAO_TRANSFORMACOES: return texto("categoria.composicao_transformacoes");
            case TRANSFORMACAO_COMPOSTA_DOIS_PASSOS: return texto("categoria.transformacao_composta_dois_passos");
            case TRANSFORMACAO_RELACAO: return texto("categoria.transformacao_relacao");
            case COMPOSICAO_RELACOES: return texto("categoria.composicao_relacoes");
            case MULTIPLICACAO: return texto("categoria.multiplicacao");
            case DIVISAO_PARTES: return texto("categoria.divisao_partes");
            case DIVISAO_COTAS: return texto("categoria.divisao_cotas");
            case INDEFINIDA: default: return texto("categoria.indefinida");
        }
    }

    public String descricaoTipo(TipoSituacaoAditiva tipo) {
        if (tipo == null) {
            return texto("tipo.transformacao_medidas");
        }
        switch (tipo) {
            case COMPOSICAO_MEDIDAS: return texto("tipo.composicao_medidas");
            case TRANSFORMACAO_MEDIDAS: return texto("tipo.transformacao_medidas");
            case COMPOSICAO_TRANSFORMACAO_MEDIDAS: return texto("tipo.composicao_transformacao_medidas");
            case COMPARACAO_MEDIDAS: return texto("tipo.comparacao_medidas");
            case COMPOSICAO_TRANSFORMACOES: return texto("tipo.composicao_transformacoes");
            case TRANSFORMACAO_COMPOSTA_DOIS_PASSOS: return texto("tipo.transformacao_composta_dois_passos");
            case TRANSFORMACAO_RELACAO: return texto("tipo.transformacao_relacao");
            case COMPOSICAO_RELACOES: return texto("tipo.composicao_relacoes");
            default: return texto("tipo.transformacao_medidas");
        }
    }

    public String relacao(String codigo) {
        if (codigo == null) {
            return texto("relacao.nao_inferida");
        }
        if ("positiva".equals(codigo)) {
            return texto("relacao.positiva");
        }
        if ("negativa".equals(codigo)) {
            return texto("relacao.negativa");
        }
        return texto("relacao.nao_inferida");
    }

    /**
     * Carrega as mensagens de cada idioma a partir de arquivos de recurso
     * (src/gerard/i18n/mensagens_xx.properties), em vez de texto embutido no
     * código Java.
     *
     * Extraído como parte da Fase 6 do plano de refatoração — ver
     * PLANO_REFATORACAO_ARQUITETURA_GERARD.md. Comportamento idêntico ao
     * original: cada par chave/valor foi migrado sem alteração de conteúdo.
     *
     * O espanhol mantém o mesmo design original: começa como uma cópia
     * completa do português (fallback para chaves ainda não traduzidas) e
     * depois recebe por cima as traduções específicas já feitas
     * (mensagens_es.properties, hoje com um subconjunto das chaves).
     */
    private void carregarMensagens() {
        Map<String, String> pt = carregarArquivoIdioma("pt");
        Map<String, String> en = carregarArquivoIdioma("en");
        Map<String, String> fr = carregarArquivoIdioma("fr");

        Map<String, String> es = new HashMap<String, String>(pt);
        es.putAll(carregarArquivoIdioma("es"));

        mensagens.put(IdiomaInterface.PORTUGUES, pt);
        mensagens.put(IdiomaInterface.INGLES, en);
        mensagens.put(IdiomaInterface.FRANCES, fr);
        mensagens.put(IdiomaInterface.ESPANHOL, es);
    }

    /**
     * Lê o arquivo de mensagens do idioma informado (ex.: "pt" para
     * mensagens_pt.properties), sempre em UTF-8.
     */
    private Map<String, String> carregarArquivoIdioma(String codigoIdioma) {
        String caminho = "/gerard/i18n/mensagens_" + codigoIdioma + ".properties";
        java.util.Properties props = new java.util.Properties();
        try (java.io.InputStream fluxo = ServicoLocalizacao.class.getResourceAsStream(caminho)) {
            if (fluxo == null) {
                throw new IllegalStateException(
                        "Arquivo de mensagens não encontrado no classpath: " + caminho);
            }
            try (java.io.Reader leitor = new java.io.InputStreamReader(
                    fluxo, java.nio.charset.StandardCharsets.UTF_8)) {
                props.load(leitor);
            }
        } catch (java.io.IOException e) {
            throw new IllegalStateException(
                    "Falha ao carregar arquivo de mensagens: " + caminho, e);
        }
        Map<String, String> mapa = new HashMap<String, String>();
        for (String chave : props.stringPropertyNames()) {
            mapa.put(chave, props.getProperty(chave));
        }
        return mapa;
    }
}
