package gerard.campoaditivo.sincronizacao;

import gerard.campoaditivo.modelo.TipoSituacaoAditiva;
import gerard.semantica.numero.DominioNumerico;
import gerard.semantica.numero.NumeroInteiro;
import gerard.semantica.numero.NumeroNatural;
import gerard.semantica.numero.ValorDesconhecido;
import gerard.semantica.numero.ValorNumerico;

/** Protege a coordenação automática sem duplicar as fórmulas das relações. */
public final class TesteResolvedorRelacoesEstruturaisAditivas {
    private static int verificacoes;

    public static void main(String[] args) {
        ResolvedorRelacoesEstruturaisAditivas resolvedor =
                new ResolvedorRelacoesEstruturaisAditivas();

        for (TipoSituacaoAditiva tipo : TipoSituacaoAditiva.values()) {
            testarValorAusente(resolvedor, tipo);
            testarRecalculo(resolvedor, tipo);
            testarEstadoIncompleto(resolvedor, tipo);
            testarIndiceInvalido(resolvedor, tipo);
        }
        confirmar(!resolvedor.resolver(null, valoresInteiros(8, 6, null), 0)
                        .foiResolvida(),
                "categoria nula não deve produzir resolução");

        System.out.println("Teste do resolvedor de relações estruturais aprovado: "
                + verificacoes + " verificações.");
    }

    private static void testarValorAusente(
            ResolvedorRelacoesEstruturaisAditivas resolvedor,
            TipoSituacaoAditiva tipo) {
        ResolvedorRelacoesEstruturaisAditivas.ResolucaoAutomatica resolucao =
                resolvedor.resolver(tipo, valores(tipo, 8, 6, null), 0);
        confirmar(resolucao.foiResolvida(),
                tipo + " deve resolver o único valor ausente");
        confirmar(resolucao.getIndice() == 2 && resolucao.getValor() == 14,
                tipo + " deve devolver índice 2 e valor 14");
    }

    private static void testarRecalculo(
            ResolvedorRelacoesEstruturaisAditivas resolvedor,
            TipoSituacaoAditiva tipo) {
        ResolvedorRelacoesEstruturaisAditivas.ResolucaoAutomatica resolucao =
                resolvedor.resolver(tipo, valores(tipo, 10, 6, 14), 0);
        confirmar(resolucao.foiResolvida(),
                tipo + " deve recalcular um estado completo");
        confirmar(resolucao.getIndice() == 2 && resolucao.getValor() == 16,
                tipo + " deve devolver índice 2 e valor 16");
    }

    private static void testarEstadoIncompleto(
            ResolvedorRelacoesEstruturaisAditivas resolvedor,
            TipoSituacaoAditiva tipo) {
        ValorNumerico[] valores = valores(tipo, 8, 6, null);
        valores[1] = new ValorDesconhecido(valores[1].getDominio());
        confirmar(!resolvedor.resolver(tipo, valores, 0).foiResolvida(),
                tipo + " não deve resolver estado com duas incógnitas");
    }

    private static void testarIndiceInvalido(
            ResolvedorRelacoesEstruturaisAditivas resolvedor,
            TipoSituacaoAditiva tipo) {
        confirmar(!resolvedor.resolver(tipo, valores(tipo, 8, 6, 14), -1)
                        .foiResolvida(),
                tipo + " não deve recalcular sem índice alterado válido");
    }

    private static ValorNumerico[] valores(TipoSituacaoAditiva tipo,
            int primeiro, int segundo, Integer terceiro) {
        boolean medidas = tipo == TipoSituacaoAditiva.COMPOSICAO_MEDIDAS
                || tipo == TipoSituacaoAditiva.TRANSFORMACAO_MEDIDAS
                || tipo == TipoSituacaoAditiva.COMPARACAO_MEDIDAS;
        DominioNumerico extremidades = medidas
                ? DominioNumerico.NATURAIS : DominioNumerico.INTEIROS;
        DominioNumerico centro = tipo == TipoSituacaoAditiva.COMPOSICAO_MEDIDAS
                ? DominioNumerico.NATURAIS : DominioNumerico.INTEIROS;
        return new ValorNumerico[] {
            conhecido(extremidades, primeiro),
            conhecido(centro, segundo),
            terceiro == null
                    ? new ValorDesconhecido(extremidades)
                    : conhecido(extremidades, terceiro.intValue())
        };
    }

    private static ValorNumerico[] valoresInteiros(int primeiro, int segundo,
            Integer terceiro) {
        return new ValorNumerico[] {
            new NumeroInteiro(primeiro),
            new NumeroInteiro(segundo),
            terceiro == null
                    ? new ValorDesconhecido(DominioNumerico.INTEIROS)
                    : new NumeroInteiro(terceiro.intValue())
        };
    }

    private static ValorNumerico conhecido(DominioNumerico dominio, int valor) {
        return dominio == DominioNumerico.NATURAIS
                ? new NumeroNatural(valor) : new NumeroInteiro(valor);
    }

    private static void confirmar(boolean condicao, String mensagem) {
        verificacoes++;
        if (!condicao) {
            throw new AssertionError(mensagem);
        }
    }
}
