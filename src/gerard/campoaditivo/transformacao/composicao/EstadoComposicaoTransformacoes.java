package gerard.campoaditivo.transformacao.composicao;

import gerard.campoaditivo.sincronizacao.EstadoSemanticoCompartilhado;
import gerard.campoaditivo.transformacao.processo.PoliticaVisualProcessoTransformacao;
import gerard.campoaditivo.transformacao.processo.TipoProcessoTransformacao;
import gerard.semantica.numero.DominioNumerico;
import gerard.semantica.numero.FabricaValoresNumericos;
import gerard.semantica.numero.ValorNumerico;

/**
 * Retrato semântico imutável dos três papéis de Composição de Transformações
 * (TipoSituacaoAditiva.COMPOSICAO_TRANSFORMACOES): Transformacao1,
 * Transformacao2, TransformacaoFinal — índices 0/1/2, na mesma ordem de
 * CatalogoEsquemasCategoriasAditivas e de
 * RelacaoEstruturalComposicaoDeTransformacoes.verificarConsistencia(t1, t2,
 * tFinal). Análogo a
 * {@link gerard.campoaditivo.transformacao.processo.EstadoProcessoTransformacao},
 * mas os três papéis são números relativos (DominioNumerico.INTEIROS) — não
 * há "estado"/medida aqui, diferente da Transformação de Medidas simples
 * (onde os índices 0 e 2 são NATURAIS). Ver
 * RELATORIO_PROCESSO_COMPOSICAO_TRANSFORMACOES_2026-08-07.md.
 */
public final class EstadoComposicaoTransformacoes {
    private final ValorNumerico[] valores;
    private final PoliticaVisualProcessoTransformacao politicaVisual;

    private EstadoComposicaoTransformacoes(ValorNumerico[] valores) {
        this.valores = valores;
        this.politicaVisual = new PoliticaVisualProcessoTransformacao();
    }

    public static EstadoComposicaoTransformacoes aPartir(
            EstadoSemanticoCompartilhado.Snapshot snapshot) {
        FabricaValoresNumericos fabrica = new FabricaValoresNumericos();
        ValorNumerico[] valores = new ValorNumerico[3];
        for (int i = 0; i < 3; i++) {
            boolean conhecido = snapshot != null && snapshot.isConhecido(i);
            Integer valor = conhecido
                    ? Integer.valueOf(snapshot.valorOuZero(i)) : null;
            try {
                valores[i] = fabrica.criar(DominioNumerico.INTEIROS, valor, conhecido);
            } catch (IllegalArgumentException invalido) {
                valores[i] = fabrica.desconhecido(DominioNumerico.INTEIROS);
            }
        }
        return new EstadoComposicaoTransformacoes(valores);
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
        return valor == null || !valor.ehConhecido() ? "?" : valor.formatar(true);
    }

    /** Cada um dos três papéis é assinado — cada zona decide sua própria forma (inserção/retirada/neutra). */
    public TipoProcessoTransformacao getTipoProcesso(int indice) {
        return politicaVisual.classificar(getValor(indice));
    }
}
