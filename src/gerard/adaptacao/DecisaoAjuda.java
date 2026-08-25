package gerard.adaptacao;

/**
 * Resultado rastreável de uma seleção local. Não desenha, localiza texto nem
 * afirma que o participante compreendeu o apoio.
 */
public final class DecisaoAjuda {

    private final boolean aplicarAjuda;
    private final String versaoModelo;
    private final String proprietarioSemantico;
    private final String diagnosticoFactual;
    private final String regraId;
    private final String regraVersao;
    private final String regraAlgoritmoOrigem;
    private final String regraProvenienciaCasos;
    private final ItemRepertorioAjuda ajuda;

    private DecisaoAjuda(
            boolean aplicarAjuda,
            String versaoModelo,
            String proprietarioSemantico,
            String diagnosticoFactual,
            String regraId,
            String regraVersao,
            String regraAlgoritmoOrigem,
            String regraProvenienciaCasos,
            ItemRepertorioAjuda ajuda) {
        this.aplicarAjuda = aplicarAjuda;
        this.versaoModelo = versaoModelo;
        this.proprietarioSemantico = proprietarioSemantico;
        this.diagnosticoFactual = diagnosticoFactual;
        this.regraId = regraId;
        this.regraVersao = regraVersao;
        this.regraAlgoritmoOrigem = regraAlgoritmoOrigem;
        this.regraProvenienciaCasos = regraProvenienciaCasos;
        this.ajuda = ajuda;
    }

    public static DecisaoAjuda aplicar(
            ContextoAdaptativoUsuario contexto,
            String diagnosticoFactual,
            RegraAdaptativaPublicada regra,
            RepertorioAjuda repertorio) {
        validarContextoERepertorio(contexto, repertorio);
        if (regra == null || !contexto.contem(regra)) {
            throw new IllegalArgumentException("regra não pertence à fotografia da sessão");
        }
        ItemRepertorioAjuda item = repertorio.obter(regra.getCodigoAjudaRecomendada())
                .orElseThrow(() -> new IllegalArgumentException("regra recomenda ajuda ausente do repertório local"));
        return new DecisaoAjuda(true, contexto.getVersaoModelo(), contexto.getProprietarioSemantico(),
                textoObrigatorio(diagnosticoFactual, "diagnóstico factual"),
                regra.getId(), regra.getVersao(), regra.getAlgoritmoOrigem(),
                regra.getProvenienciaCasos(), item);
    }

    public static DecisaoAjuda semRegraAplicavel(
            ContextoAdaptativoUsuario contexto,
            String diagnosticoFactual,
            RepertorioAjuda repertorio) {
        validarContextoERepertorio(contexto, repertorio);
        return new DecisaoAjuda(false, contexto.getVersaoModelo(), contexto.getProprietarioSemantico(),
                textoObrigatorio(diagnosticoFactual, "diagnóstico factual"),
                null, null, null, null, null);
    }

    public boolean deveAplicarAjuda() { return aplicarAjuda; }
    public String getVersaoModelo() { return versaoModelo; }
    public String getProprietarioSemantico() { return proprietarioSemantico; }
    public String getDiagnosticoFactual() { return diagnosticoFactual; }
    public String getRegraId() { return regraId; }
    public String getRegraVersao() { return regraVersao; }
    public String getRegraAlgoritmoOrigem() { return regraAlgoritmoOrigem; }
    public String getRegraProvenienciaCasos() { return regraProvenienciaCasos; }
    public ItemRepertorioAjuda getAjuda() { return ajuda; }

    private static void validarContextoERepertorio(
            ContextoAdaptativoUsuario contexto,
            RepertorioAjuda repertorio) {
        if (contexto == null || repertorio == null) {
            throw new IllegalArgumentException("contexto e repertório são obrigatórios");
        }
        if (!contexto.getProprietarioSemantico().equals(repertorio.getProprietarioSemantico())) {
            throw new IllegalArgumentException("contexto e repertório pertencem a objetos diferentes");
        }
    }

    private static String textoObrigatorio(String valor, String nome) {
        String normalizado = valor == null ? "" : valor.trim();
        if (normalizado.isEmpty()) {
            throw new IllegalArgumentException(nome + " não pode ser vazio");
        }
        return normalizado;
    }
}
