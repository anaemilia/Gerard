package gerard.campoaditivo.transformacao.processo;

import gerard.semantica.numero.NumeroInteiro;
import gerard.semantica.numero.ValorNumerico;

/**
 * Traduz um número inteiro semântico em uma forma de processo. A política não
 * consulta categoria, posição visual ou texto: o comportamento decorre do
 * próprio valor matemático.
 */
public final class PoliticaVisualProcessoTransformacao {

    public TipoProcessoTransformacao classificar(ValorNumerico valor) {
        if (!(valor instanceof NumeroInteiro) || !valor.ehConhecido()) {
            return TipoProcessoTransformacao.NEUTRA;
        }
        int inteiro = ((NumeroInteiro) valor).intValue();
        if (inteiro < 0) {
            return TipoProcessoTransformacao.RETIRADA;
        }
        if (inteiro > 0) {
            return TipoProcessoTransformacao.INSERCAO;
        }
        return TipoProcessoTransformacao.NEUTRA;
    }
}
