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
