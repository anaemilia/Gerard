package gerard.campoaditivo.sincronizacao;

import gerard.campoaditivo.modelo.TipoSituacaoAditiva;
import gerard.semantica.numero.DominioNumerico;
import gerard.semantica.numero.ValorNumerico;

/** Protege a conversão dos valores brutos para os domínios dos seis esquemas. */
public final class TesteConversorValoresEstadoAditivo {
    private static int verificacoes;

    public static void main(String[] args) {
        ConversorValoresEstadoAditivo conversor =
                new ConversorValoresEstadoAditivo();

        verificarDominios(conversor, TipoSituacaoAditiva.COMPOSICAO_MEDIDAS,
                DominioNumerico.NATURAIS, DominioNumerico.NATURAIS,
                DominioNumerico.NATURAIS);
        verificarDominios(conversor, TipoSituacaoAditiva.TRANSFORMACAO_MEDIDAS,
                DominioNumerico.NATURAIS, DominioNumerico.INTEIROS,
                DominioNumerico.NATURAIS);
        verificarDominios(conversor, TipoSituacaoAditiva.COMPARACAO_MEDIDAS,
                DominioNumerico.NATURAIS, DominioNumerico.INTEIROS,
                DominioNumerico.NATURAIS);
        verificarDominios(conversor, TipoSituacaoAditiva.COMPOSICAO_TRANSFORMACOES,
                DominioNumerico.INTEIROS, DominioNumerico.INTEIROS,
                DominioNumerico.INTEIROS);
        verificarDominios(conversor, TipoSituacaoAditiva.TRANSFORMACAO_RELACAO,
                DominioNumerico.INTEIROS, DominioNumerico.INTEIROS,
                DominioNumerico.INTEIROS);
        verificarDominios(conversor, TipoSituacaoAditiva.COMPOSICAO_RELACOES,
                DominioNumerico.INTEIROS, DominioNumerico.INTEIROS,
                DominioNumerico.INTEIROS);

        ValorNumerico naturalValido = conversor.normalizarEntrada(
                TipoSituacaoAditiva.COMPOSICAO_MEDIDAS, 0,
                Integer.valueOf(7), true);
        confirmar(naturalValido.ehConhecido()
                        && naturalValido.valorOuNull().intValue() == 7,
                "entrada natural válida deve permanecer conhecida");

        ValorNumerico naturalNegativo = conversor.normalizarEntrada(
                TipoSituacaoAditiva.COMPOSICAO_MEDIDAS, 0,
                Integer.valueOf(-3), true);
        confirmar(!naturalNegativo.ehConhecido(),
                "medida negativa deve virar valor desconhecido");

        ValorNumerico relacaoNegativa = conversor.normalizarEntrada(
                TipoSituacaoAditiva.TRANSFORMACAO_MEDIDAS, 1,
                Integer.valueOf(-3), true);
        confirmar(relacaoNegativa.ehConhecido()
                        && relacaoNegativa.valorOuNull().intValue() == -3,
                "relação negativa deve permanecer conhecida");

        ValorNumerico entradaDesconhecida = conversor.normalizarEntrada(
                TipoSituacaoAditiva.COMPOSICAO_RELACOES, 0,
                Integer.valueOf(9), false);
        confirmar(!entradaDesconhecida.ehConhecido(),
                "flag desconhecida deve prevalecer sobre o valor bruto");

        confirmar(conversor.criarCalculadoOuNull(
                        TipoSituacaoAditiva.COMPOSICAO_MEDIDAS, 0, -1) == null,
                "cálculo natural inválido deve permitir preservar o valor anterior");
        confirmar(conversor.criarCalculadoOuNull(
                        TipoSituacaoAditiva.TRANSFORMACAO_MEDIDAS, 1, -1) != null,
                "cálculo inteiro negativo deve ser aceito");

        ValorNumerico fallback = conversor.desconhecido(null, 1);
        confirmar(!fallback.ehConhecido()
                        && fallback.getDominio() == DominioNumerico.NATURAIS,
                "categoria nula deve preservar o fallback natural existente");

        System.out.println("Teste do conversor de valores do estado aprovado: "
                + verificacoes + " verificações.");
    }

    private static void verificarDominios(ConversorValoresEstadoAditivo conversor,
            TipoSituacaoAditiva tipo, DominioNumerico d0,
            DominioNumerico d1, DominioNumerico d2) {
        confirmar(conversor.desconhecido(tipo, 0).getDominio() == d0,
                tipo + " deve preservar o domínio do índice 0");
        confirmar(conversor.desconhecido(tipo, 1).getDominio() == d1,
                tipo + " deve preservar o domínio do índice 1");
        confirmar(conversor.desconhecido(tipo, 2).getDominio() == d2,
                tipo + " deve preservar o domínio do índice 2");
    }

    private static void confirmar(boolean condicao, String mensagem) {
        verificacoes++;
        if (!condicao) {
            throw new AssertionError(mensagem);
        }
    }
}
