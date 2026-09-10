import gerard.campoaditivo.curadoria.ConstrutorResultadoCurado;
import gerard.campoaditivo.modelo.SituacaoProblemaAditiva;
import gerard.campoaditivo.servico.RepositorioSituacoesAditivas;
import gerard.interpretacao.modelo.NumeroEncontrado;
import gerard.interpretacao.modelo.ResultadoInterpretacao;
import java.util.List;

/**
 * Regressão para o bug descoberto via protocolo de mouse real ao testar a
 * decomposição do estado inicial em Composição de Transformações: a
 * narrativa da vovó ("Vovó possui 2 rosas brancas e 3 rosas amarelas...")
 * tem estadoInicialParte2=2 e transformacaoFinal=2 — mesmo valor numérico.
 * Antes da correção, ambos os papéis reivindicavam a MESMA ocorrência de
 * "2" no enunciado (localizar() não sabia que a posição já tinha sido usada
 * por outro papel), e o papel processado depois (estadoInicialParte2) nunca
 * recebia uma faixa de texto válida — a caixa "Parte 2 do Estado Inicial"
 * ficava permanentemente sem número arrastável (rejeitada com
 * "a situação atual não possui posicionamento de papel conhecido
 * implementado", verificado ao vivo no navegador).
 */
public final class TesteConstrutorResultadoCuradoColisaoDeNumeros {

    public static void main(String[] args) {
        List<SituacaoProblemaAditiva> validadas =
                new RepositorioSituacoesAditivas().listarValidadas();
        SituacaoProblemaAditiva situacao = localizar(
                validadas, "PO_COMPOSICAO_TRANSFORMACAO_MEDIDAS_flores_422431114");
        exigir("2".equals(situacao.getEstadoInicialParte2()),
                "pré-condição do teste: estadoInicialParte2 deveria ser 2.");
        exigir("2".equals(situacao.getResultado()),
                "pré-condição do teste: resultado (transformacaoFinal) deveria ser 2 "
                        + "— mesma magnitude, para reproduzir a colisão.");

        String enunciado = new gerard.campoaditivo.curadoria.MaterializadorEnunciadoCurado()
                .materializar(situacao);
        ResultadoInterpretacao interpretacao = new ConstrutorResultadoCurado()
                .construir(situacao, enunciado);
        List<NumeroEncontrado> numeros = interpretacao.getNumeros();

        NumeroEncontrado parte2 = buscarPorPapel(numeros, "papel.estadoInicialParte2");
        exigir(parte2 != null, "papel.estadoInicialParte2 deveria aparecer na lista de números.");
        exigir(parte2.getPosicaoInicial() >= 0,
                "papel.estadoInicialParte2 deveria ter uma posição válida no enunciado "
                        + "(a colisão com transformacaoFinal não pode deixá-lo sem posição).");
        exigir("2".equals(parte2.getValorCanonico()),
                "papel.estadoInicialParte2 deveria valer 2.");

        NumeroEncontrado parte1 = buscarPorPapel(numeros, "papel.estadoInicialParte1");
        exigir(parte1 != null && parte1.getPosicaoInicial() >= 0
                        && "3".equals(parte1.getValorCanonico()),
                "papel.estadoInicialParte1 deveria valer 3 com posição válida.");

        // As duas faixas não podem se sobrepor — são números fisicamente
        // diferentes no enunciado ("2" de brancas vs "3" de amarelas), então
        // isto já era esperado, mas serve de checagem de sanidade extra.
        exigir(!sobrepoe(parte1, parte2),
                "as faixas de parte1 e parte2 não deveriam se sobrepor.");

        System.out.println("APROVADO: papel.estadoInicialParte1/2 recebem posições válidas e "
                + "não sobrepostas no enunciado, mesmo colidindo em VALOR com "
                + "papel.transformacaoFinal.");
    }

    private static boolean sobrepoe(NumeroEncontrado a, NumeroEncontrado b) {
        return a.getPosicaoInicial() < b.getPosicaoFinal()
                && b.getPosicaoInicial() < a.getPosicaoFinal();
    }

    private static NumeroEncontrado buscarPorPapel(List<NumeroEncontrado> numeros, String chavePapel) {
        for (NumeroEncontrado numero : numeros) {
            if (chavePapel.equals(numero.getChavePapelSemantico())) {
                return numero;
            }
        }
        return null;
    }

    private static SituacaoProblemaAditiva localizar(
            List<SituacaoProblemaAditiva> situacoes, String id) {
        for (SituacaoProblemaAditiva situacao : situacoes) {
            if (id.equals(situacao.getId())) {
                return situacao;
            }
        }
        throw new AssertionError("Situação curada não encontrada: " + id);
    }

    private static void exigir(boolean condicao, String mensagem) {
        if (!condicao) {
            throw new AssertionError(mensagem);
        }
    }
}
