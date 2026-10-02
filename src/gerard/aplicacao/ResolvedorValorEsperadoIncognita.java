package gerard.aplicacao;

import gerard.campoaditivo.curadoria.SemanticaCuradaSituacao;
import gerard.campoaditivo.modelo.SituacaoProblemaAditiva;
import gerard.campoaditivo.sincronizacao.EstadoSemanticoCompartilhado;
import gerard.i18n.ServicoLocalizacao;

/**
 * Resolve o valor normativo usado internamente para avaliar uma incógnita.
 * Prioriza o estado vivo recalculado quando o papel solicitado é a incógnita;
 * a curadoria é apenas o fallback. Não expõe o valor para representação.
 */
public final class ResolvedorValorEsperadoIncognita {

    public Integer resolver(SituacaoProblemaAditiva situacao,
            ServicoLocalizacao localizacao, String papelSolicitado,
            String papelIncognita, EstadoSemanticoCompartilhado.Snapshot estado,
            int indiceIncognita) {
        if (papelSolicitado == null) {
            return null;
        }
        if (papelSolicitado.equals(papelIncognita) && estado != null
                && indiceIncognita >= 0 && indiceIncognita < 3) {
            Integer[] valores = new Integer[3];
            boolean[] conhecidos = new boolean[3];
            for (int i = 0; i < 3; i++) {
                if (i != indiceIncognita) {
                    valores[i] = estado.getValor(i);
                    conhecidos[i] = estado.isConhecido(i);
                }
            }
            gerard.campoaditivo.sincronizacao.ResolvedorRelacoesEstruturaisAditivas.ResolucaoAutomatica calculo =
                    new gerard.campoaditivo.sincronizacao.ResolvedorRelacoesEstruturaisAditivas()
                            .resolverValores(estado.getTipo(), valores, conhecidos, -1);
            if (calculo.foiResolvida() && calculo.getIndice() == indiceIncognita) {
                return Integer.valueOf(calculo.getValor());
            }
        }
        if (papelSolicitado.equals(papelIncognita)
                && estado != null && indiceIncognita >= 0
                && estado.isConhecido(indiceIncognita)) {
            return estado.getValor(indiceIncognita);
        }
        SemanticaCuradaSituacao.PapelCurado curado =
                SemanticaCuradaSituacao.buscar(
                        situacao, localizacao, papelSolicitado);
        return curado == null ? null : curado.getValorInteiro();
    }
}
