package gerard.campoaditivo.sincronizacao;

import gerard.campoaditivo.modelo.TipoSituacaoAditiva;
import gerard.dominio.campoaditivo.ResultadoCalculo;
import gerard.semantica.numero.ValorNumerico;

/**
 * Coordena a resolução automática das relações aditivas sem armazenar estado
 * e sem conter fórmulas matemáticas.
 */
public final class ResolvedorRelacoesEstruturaisAditivas {
    private final CatalogoRelacoesEstruturaisAditivas catalogo;
    private final ConversorValoresEstadoAditivo conversor =
            new ConversorValoresEstadoAditivo();

    public ResolvedorRelacoesEstruturaisAditivas() {
        this(new CatalogoRelacoesEstruturaisAditivas());
    }

    ResolvedorRelacoesEstruturaisAditivas(
            CatalogoRelacoesEstruturaisAditivas catalogo) {
        this.catalogo = catalogo;
    }

    public ResolucaoAutomatica resolver(TipoSituacaoAditiva tipo,
            ValorNumerico[] valores, int indiceAlterado) {
        CatalogoRelacoesEstruturaisAditivas.RelacaoContextualizada contexto =
                catalogo.criar(tipo, valores);
        if (contexto == null) {
            return ResolucaoAutomatica.naoResolvida();
        }

        int quantidadeIncognitas = 0;
        int indiceIncognita = -1;
        for (int i = 0; i < 3; i++) {
            if (!conhecido(valores, i)) {
                quantidadeIncognitas++;
                indiceIncognita = i;
            }
        }

        if (quantidadeIncognitas == 1) {
            return converter(contexto, contexto.calcularValorAusente(),
                    indiceIncognita);
        }
        if (quantidadeIncognitas == 0
                && indiceAlterado >= 0 && indiceAlterado <= 2) {
            return converter(contexto,
                    contexto.recalcularParaConsistencia(indiceAlterado), -1);
        }
        return ResolucaoAutomatica.naoResolvida();
    }

    /**
     * Simula uma alteração antes de publicá-la no estado compartilhado e
     * responde se tanto o papel alterado quanto o eventual papel recalculado
     * permanecem em seus domínios numéricos.
     */
    public boolean tentativaPreservaDominios(TipoSituacaoAditiva tipo,
            ValorNumerico[] valoresAtuais, int indiceAlterado,
            Integer valorProposto) {
        if (tipo == null || indiceAlterado < 0 || indiceAlterado > 2
                || valorProposto == null) {
            return true;
        }
        ValorNumerico[] tentativa = new ValorNumerico[3];
        for (int i = 0; i < tentativa.length; i++) {
            if (i == indiceAlterado) {
                tentativa[i] = conversor.normalizarEntrada(
                        tipo, i, valorProposto, true);
                if (!tentativa[i].ehConhecido()) {
                    return false;
                }
            } else {
                tentativa[i] = valoresAtuais != null && i < valoresAtuais.length
                        ? valoresAtuais[i] : conversor.desconhecido(tipo, i);
            }
        }
        ResolucaoAutomatica resolucao = resolver(
                tipo, tentativa, indiceAlterado);
        return !resolucao.foiResolvida()
                || conversor.criarCalculadoOuNull(tipo, resolucao.getIndice(),
                        resolucao.getValor()) != null;
    }

    /** Resolve uma relação a partir de valores brutos sem expor conversores. */
    public ResolucaoAutomatica resolverValores(TipoSituacaoAditiva tipo,
            Integer[] valoresBrutos, boolean[] conhecidos,
            int indiceAlterado) {
        ValorNumerico[] valores = new ValorNumerico[3];
        for (int i = 0; i < valores.length; i++) {
            Integer bruto = valoresBrutos != null && i < valoresBrutos.length
                    ? valoresBrutos[i] : null;
            boolean conhecido = conhecidos != null && i < conhecidos.length
                    && conhecidos[i];
            valores[i] = conversor.normalizarEntrada(
                    tipo, i, bruto, conhecido);
        }
        return resolver(tipo, valores, indiceAlterado);
    }

    private static ResolucaoAutomatica converter(
            CatalogoRelacoesEstruturaisAditivas.RelacaoContextualizada contexto,
            ResultadoCalculo resultado, int indiceEsperado) {
        if (resultado == null || !resultado.temValorCalculavel()) {
            return ResolucaoAutomatica.naoResolvida();
        }
        int indice = contexto.indiceDoPapel(resultado.getPapelCalculado());
        if (indice < 0 || (indiceEsperado >= 0 && indice != indiceEsperado)) {
            return ResolucaoAutomatica.naoResolvida();
        }
        return ResolucaoAutomatica.resolvida(indice,
                resultado.getValorCalculado().valorOuNull().intValue());
    }

    private static boolean conhecido(ValorNumerico[] valores, int indice) {
        return valores != null && indice >= 0 && indice < valores.length
                && valores[indice] != null && valores[indice].ehConhecido();
    }

    public static final class ResolucaoAutomatica {
        private static final ResolucaoAutomatica NAO_RESOLVIDA =
                new ResolucaoAutomatica(false, -1, 0);

        private final boolean resolvida;
        private final int indice;
        private final int valor;

        private ResolucaoAutomatica(boolean resolvida, int indice, int valor) {
            this.resolvida = resolvida;
            this.indice = indice;
            this.valor = valor;
        }

        public static ResolucaoAutomatica naoResolvida() {
            return NAO_RESOLVIDA;
        }

        public static ResolucaoAutomatica resolvida(int indice, int valor) {
            return new ResolucaoAutomatica(true, indice, valor);
        }

        public boolean foiResolvida() {
            return resolvida;
        }

        public int getIndice() {
            return indice;
        }

        public int getValor() {
            return valor;
        }
    }
}
