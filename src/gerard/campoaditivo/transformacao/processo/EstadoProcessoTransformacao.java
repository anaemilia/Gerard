package gerard.campoaditivo.transformacao.processo;

import gerard.campoaditivo.sincronizacao.EstadoSemanticoCompartilhado;
import gerard.semantica.numero.DominioNumerico;
import gerard.semantica.numero.FabricaValoresNumericos;
import gerard.semantica.numero.ValorNumerico;

/** Retrato semântico imutável do estado inicial, transformação e estado final. */
public final class EstadoProcessoTransformacao {
    private final ValorNumerico[] valores;
    private final PoliticaVisualProcessoTransformacao politicaVisual;

    private EstadoProcessoTransformacao(ValorNumerico[] valores) {
        this.valores = valores;
        this.politicaVisual = new PoliticaVisualProcessoTransformacao();
    }

    public static EstadoProcessoTransformacao aPartir(
            EstadoSemanticoCompartilhado.Snapshot snapshot) {
        FabricaValoresNumericos fabrica = new FabricaValoresNumericos();
        ValorNumerico[] valores = new ValorNumerico[3];
        for (int i = 0; i < 3; i++) {
            DominioNumerico dominio = i == 1
                    ? DominioNumerico.INTEIROS
                    : DominioNumerico.NATURAIS;
            boolean conhecido = snapshot != null && snapshot.isConhecido(i);
            Integer valor = conhecido
                    ? Integer.valueOf(snapshot.valorOuZero(i)) : null;
            try {
                valores[i] = fabrica.criar(dominio, valor, conhecido);
            } catch (IllegalArgumentException invalido) {
                valores[i] = fabrica.desconhecido(dominio);
            }
        }
        return new EstadoProcessoTransformacao(valores);
    }

    public ValorNumerico getValor(int indice) {
        return indice >= 0 && indice < valores.length ? valores[indice] : null;
    }

    public boolean isConhecido(int indice) {
        ValorNumerico valor = getValor(indice);
        return valor != null && valor.ehConhecido();
    }

    public int valorOuZero(int indice) {
        ValorNumerico valor = getValor(indice);
        Integer numero = valor == null ? null : valor.valorOuNull();
        return numero == null ? 0 : numero.intValue();
    }

    public String formatar(int indice) {
        ValorNumerico valor = getValor(indice);
        return valor == null || !valor.ehConhecido()
                ? "?" : valor.formatar(indice == 1);
    }

    public TipoProcessoTransformacao getTipoProcesso() {
        return politicaVisual.classificar(getValor(1));
    }
}
