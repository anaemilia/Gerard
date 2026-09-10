import gerard.interpretacao.modelo.CategoriaProblema;
import gerard.interpretacao.modelo.NumeroEncontrado;
import gerard.interpretacao.modelo.PapelElementoInterpretado;
import gerard.interpretacao.servico.InferidorPapeisNumericos;
import java.util.ArrayList;
import java.util.List;

/**
 * Spike isolado (ver plano "Composição de Transformações: decompor o estado
 * inicial em Parte1+Parte2=Todo"): confirma que
 * InferidorPapeisNumericos.inferirComposicaoTransformacoes, com os novos
 * parâmetros valorEstadoInicialParte1/2, casa POR VALOR (não por posição) os
 * números da narrativa da vovó ("Vovó possui 2 rosas brancas e 3 rosas
 * amarelas. Deu 1 rosa branca e 1 rosa amarela...") — numeros=[2,3,1,1] —
 * mesmo com o dado curado definindo parte1=amarelas=3 (que aparece DEPOIS de
 * brancas=2 no texto) e parte2=brancas=2, exatamente como a imagem de
 * referência do usuário (Parte1="Rosas Amarelas" em cima, Parte2="Rosas
 * brancas" embaixo) — e que null/vazio preserva o comportamento anterior
 * (regressão zero).
 */
public final class TesteInferidorPapeisNumericosComposicaoTransformacoes {

    public static void main(String[] args) {
        InferidorPapeisNumericos inferidor = new InferidorPapeisNumericos();
        List<NumeroEncontrado> numerosVovo = numeros("2", "3", "1", "1");

        // --- valores curados presentes: parte1=3 (amarelas, 2º no texto),
        // parte2=2 (brancas, 1º no texto) — casamento por valor, não posição ---
        List<PapelElementoInterpretado> comDecomposicao = inferidor.inferir(
                "texto irrelevante para este teste (números já extraídos)",
                CategoriaProblema.COMPOSICAO_TRANSFORMACOES, numerosVovo, "3", "2");
        exigirPapel(comDecomposicao, "papel.estadoInicialParte1", "3", true);
        exigirPapel(comDecomposicao, "papel.estadoInicialParte2", "2", true);
        exigirPapel(comDecomposicao, "papel.transformacao1", "1", true);
        exigirPapel(comDecomposicao, "papel.transformacao2", "1", true);
        exigirPapelDesconhecido(comDecomposicao, "papel.transformacaoFinal");

        // --- valores ausentes (default, comportamento hoje): sem
        // decomposição, as 2 primeiras posições (2 e 3) viram transformação1/2
        // (mesmo resultado do código antes desta mudança) ---
        List<PapelElementoInterpretado> semDecomposicao = inferidor.inferir(
                "texto irrelevante", CategoriaProblema.COMPOSICAO_TRANSFORMACOES, numerosVovo);
        exigirPapel(semDecomposicao, "papel.transformacao1", "2", true);
        exigirPapel(semDecomposicao, "papel.transformacao2", "3", true);
        exigirPapel(semDecomposicao, "papel.transformacaoFinal", "1", true);
        exigirAusente(semDecomposicao, "papel.estadoInicialParte1");
        exigirAusente(semDecomposicao, "papel.estadoInicialParte2");

        // --- valor curado que não bate com nenhum número do texto: fica
        // sem tag (não aplicável), sem lançar exceção nem consumir posições ---
        List<PapelElementoInterpretado> valorInexistente = inferidor.inferir(
                "texto irrelevante", CategoriaProblema.COMPOSICAO_TRANSFORMACOES,
                numerosVovo, "99", "2");
        exigirAusente(valorInexistente, "papel.estadoInicialParte1");
        exigirPapel(valorInexistente, "papel.estadoInicialParte2", "2", true);

        // --- valores curados presentes, mas só 2 números disponíveis no
        // texto (parte1/parte2 sem nenhuma transformação) — transformacaoFinal
        // cai no "desconhecido" de sempre, sem lançar exceção ---
        List<PapelElementoInterpretado> poucosNumeros = inferidor.inferir(
                "texto irrelevante", CategoriaProblema.COMPOSICAO_TRANSFORMACOES,
                numeros("3", "2"), "3", "2");
        exigirPapel(poucosNumeros, "papel.estadoInicialParte1", "3", true);
        exigirPapel(poucosNumeros, "papel.estadoInicialParte2", "2", true);
        exigirAusente(poucosNumeros, "papel.transformacao1");
        exigirAusente(poucosNumeros, "papel.transformacao2");
        exigirPapelDesconhecido(poucosNumeros, "papel.transformacaoFinal");

        System.out.println("APROVADO: inferirComposicaoTransformacoes casa Parte1/Parte2 por "
                + "VALOR curado (não por posição no texto), preserva Transformação1/2/Final, "
                + "e valores ausentes/sem correspondência preservam o comportamento anterior.");
    }

    private static List<NumeroEncontrado> numeros(String... valores) {
        List<NumeroEncontrado> resultado = new ArrayList<NumeroEncontrado>();
        int posicao = 0;
        for (String valor : valores) {
            resultado.add(new NumeroEncontrado(valor, posicao, posicao + valor.length()));
            posicao += valor.length() + 1;
        }
        return resultado;
    }

    private static PapelElementoInterpretado buscar(
            List<PapelElementoInterpretado> papeis, String chavePapel) {
        for (PapelElementoInterpretado papel : papeis) {
            if (chavePapel.equals(papel.getChavePapel())) {
                return papel;
            }
        }
        return null;
    }

    private static void exigirPapel(List<PapelElementoInterpretado> papeis,
            String chavePapel, String valorEsperado, boolean conhecidoEsperado) {
        PapelElementoInterpretado papel = buscar(papeis, chavePapel);
        exigir(papel != null, chavePapel + " deveria estar presente na lista de papéis.");
        exigir(valorEsperado.equals(papel.getElemento()),
                chavePapel + " deveria valer " + valorEsperado + ", veio " + papel.getElemento());
        exigir(papel.isConhecido() == conhecidoEsperado,
                chavePapel + " conhecido deveria ser " + conhecidoEsperado);
    }

    private static void exigirPapelDesconhecido(
            List<PapelElementoInterpretado> papeis, String chavePapel) {
        PapelElementoInterpretado papel = buscar(papeis, chavePapel);
        exigir(papel != null, chavePapel + " deveria estar presente (como desconhecido).");
        exigir(!papel.isConhecido(), chavePapel + " deveria estar marcado como não conhecido.");
    }

    private static void exigirAusente(List<PapelElementoInterpretado> papeis, String chavePapel) {
        exigir(buscar(papeis, chavePapel) == null,
                chavePapel + " não deveria aparecer na lista de papéis.");
    }

    private static void exigir(boolean condicao, String mensagem) {
        if (!condicao) {
            throw new AssertionError(mensagem);
        }
    }
}
