package gerard.adaptacao;

import java.time.Instant;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Regra explicável produzida pelo Modelador.
 *
 * É um artefato computacional de adaptação, não um invariante operatório e
 * não uma afirmação sobre o que o participante sabe.
 */
public final class RegraAdaptativaPublicada {

    private final String id;
    private final String versao;
    private final String algoritmoOrigem;
    private final Instant publicadaEm;
    private final String provenienciaCasos;
    private final String proprietarioSemantico;
    private final EscopoProprietarioSemantico escopo;
    private final Map<String, String> condicoes;
    private final String codigoAjudaRecomendada;
    private final Double suporte;
    private final Double confianca;
    private final Double lift;
    private final EstadoPublicacaoRegra estado;

    public RegraAdaptativaPublicada(
            String id,
            String versao,
            String algoritmoOrigem,
            Instant publicadaEm,
            String provenienciaCasos,
            String proprietarioSemantico,
            EscopoProprietarioSemantico escopo,
            Map<String, String> condicoes,
            String codigoAjudaRecomendada,
            Double suporte,
            Double confianca,
            Double lift,
            EstadoPublicacaoRegra estado) {
        this.id = textoObrigatorio(id, "id");
        this.versao = textoObrigatorio(versao, "versão");
        this.algoritmoOrigem = textoObrigatorio(algoritmoOrigem, "algoritmo de origem");
        this.publicadaEm = Objects.requireNonNull(publicadaEm, "data de publicação não pode ser nula");
        this.provenienciaCasos = textoObrigatorio(provenienciaCasos, "proveniência dos casos");
        this.proprietarioSemantico = textoObrigatorio(proprietarioSemantico, "proprietário semântico");
        this.escopo = Objects.requireNonNull(escopo, "escopo não pode ser nulo");
        this.condicoes = copiarCondicoes(condicoes);
        this.codigoAjudaRecomendada = CodigosAjudaAdaptativa.validar(
                textoObrigatorio(codigoAjudaRecomendada, "código da ajuda"));
        this.suporte = metricaValida(suporte, "suporte");
        this.confianca = metricaValida(confianca, "confiança");
        this.lift = metricaNaoNegativa(lift, "lift");
        this.estado = Objects.requireNonNull(estado, "estado não pode ser nulo");
    }

    public String getId() { return id; }
    public String getVersao() { return versao; }
    public String getAlgoritmoOrigem() { return algoritmoOrigem; }
    public Instant getPublicadaEm() { return publicadaEm; }
    public String getProvenienciaCasos() { return provenienciaCasos; }
    public String getProprietarioSemantico() { return proprietarioSemantico; }
    public EscopoProprietarioSemantico getEscopo() { return escopo; }
    public Map<String, String> getCondicoes() { return condicoes; }
    public String getCodigoAjudaRecomendada() { return codigoAjudaRecomendada; }
    public Double getSuporte() { return suporte; }
    public Double getConfianca() { return confianca; }
    public Double getLift() { return lift; }
    public EstadoPublicacaoRegra getEstado() { return estado; }

    public boolean estaPublicada() {
        return estado == EstadoPublicacaoRegra.PUBLICADA;
    }

    private static Map<String, String> copiarCondicoes(Map<String, String> origem) {
        Objects.requireNonNull(origem, "condições não podem ser nulas");
        Map<String, String> copia = new LinkedHashMap<String, String>();
        for (Map.Entry<String, String> entrada : origem.entrySet()) {
            copia.put(textoObrigatorio(entrada.getKey(), "chave de condição"),
                    textoObrigatorio(entrada.getValue(), "valor de condição"));
        }
        return Collections.unmodifiableMap(copia);
    }

    private static String textoObrigatorio(String valor, String nome) {
        String normalizado = valor == null ? "" : valor.trim();
        if (normalizado.isEmpty()) {
            throw new IllegalArgumentException(nome + " não pode ser vazio");
        }
        return normalizado;
    }

    private static Double metricaValida(Double valor, String nome) {
        if (valor != null && (valor.isNaN() || valor.isInfinite()
                || valor.doubleValue() < 0.0 || valor.doubleValue() > 1.0)) {
            throw new IllegalArgumentException(nome + " deve estar entre 0 e 1");
        }
        return valor;
    }

    private static Double metricaNaoNegativa(Double valor, String nome) {
        if (valor != null && (valor.isNaN() || valor.isInfinite() || valor.doubleValue() < 0.0)) {
            throw new IllegalArgumentException(nome + " não pode ser negativo");
        }
        return valor;
    }
}
