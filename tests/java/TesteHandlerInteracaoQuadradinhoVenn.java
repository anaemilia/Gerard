import gerard.campoaditivo.diagrama.elementos.CirculoVenn;
import gerard.campoaditivo.diagrama.elementos.QuadradinhoVenn;
import gerard.interacao.arraste.HandlerInteracaoQuadradinhoVenn;

import java.util.Arrays;
import java.util.List;

/**
 * Harness executável de HandlerInteracaoQuadradinhoVenn — Fase 7.5 do
 * roteiro de gerard-handlers-de-interacao (mesmo padrão sem JUnit de
 * TesteHandlerInteracaoArrasteIncremental/TesteHandlerInteracaoElementoTextoMovel).
 *
 * Círculos de teste são retangulares (formaRetangular = true) para que a
 * matemática de contenção seja verificável de cabeça, sem depender da
 * fórmula de elipse (que já não é responsabilidade deste handler).
 */
public final class TesteHandlerInteracaoQuadradinhoVenn {

    public static void main(String[] args) {
        CirculoVenn circuloA = new CirculoVenn(0, 0, 100, 100, "A", 0, true);
        circuloA.formaRetangular = true;
        CirculoVenn circuloB = new CirculoVenn(200, 0, 100, 100, "B", 0, true);
        circuloB.formaRetangular = true;
        List<CirculoVenn> circulos = Arrays.asList(circuloA, circuloB);

        HandlerInteracaoQuadradinhoVenn handler = new HandlerInteracaoQuadradinhoVenn();

        // --- Nada ativo: mover/concluir não lançam exceção, não fazem nada ---
        exigir(!handler.mover(10, 10), "mover() sem gesto ativo deveria devolver false.");
        HandlerInteracaoQuadradinhoVenn.ResultadoSoltura resultadoVazio = handler.concluir(circulos);
        exigir(!resultadoVazio.houveMovimento(), "concluir() sem gesto ativo não deveria reportar movimento.");
        exigir(resultadoVazio.obterIndiceParaSincronizacao() == -1,
                "Sem gesto ativo, o índice de sincronização deveria ser -1.");

        // --- Pickup dentro do Círculo A (0,0 a 100,100) ---
        QuadradinhoVenn quadradinho = new QuadradinhoVenn(20, 20, 10, "parte1");
        // centro em (25,25) — dentro de A.

        exigir(!handler.iniciar(null, 25, 25, circulos),
                "iniciar(null, ...) deveria devolver false.");
        exigir(!handler.estaAtivo(), "Handler não deveria ficar ativo após iniciar(null, ...).");

        exigir(handler.iniciar(quadradinho, 25, 25, circulos),
                "O pickup deveria aceitar um quadradinho válido.");
        exigir(handler.estaAtivo() && handler.obterQuadradinhoAtivo() == quadradinho,
                "O handler deveria preservar a identidade do quadradinho.");

        // --- Movimento livre, sem limiar nem clamp (mesmo comportamento de antes da extração) ---
        boolean moveu = handler.mover(30, 30);
        exigir(moveu && quadradinho.x == 25 && quadradinho.y == 25,
                "O movimento deveria aplicar o offset do pickup sem limiar nem clamp "
                        + "(pickup em (25,25) sobre quadradinho (20,20), deslocamento (5,5); "
                        + "mover para (30,30) => quadradinho em (25,25)).");

        // Movimento para fora de qualquer área nomeada é permitido livremente.
        handler.mover(1000, 1000);
        exigir(quadradinho.x == 995 && quadradinho.y == 995,
                "O movimento deveria continuar livre, sem clamp por zona.");

        // --- Soltura fora de qualquer círculo: sem destino, cai no fallback de origem ---
        HandlerInteracaoQuadradinhoVenn.ResultadoSoltura resultadoForaDeCirculo = handler.concluir(circulos);
        exigir(resultadoForaDeCirculo.houveMovimento(), "Deveria reportar que houve movimento.");
        exigir(resultadoForaDeCirculo.getIndiceOrigem() == 0,
                "O quadradinho começou dentro do Círculo A (índice 0).");
        exigir(resultadoForaDeCirculo.getIndiceDestino() == -1,
                "O quadradinho terminou fora de qualquer círculo.");
        exigir(resultadoForaDeCirculo.obterIndiceParaSincronizacao() == 0,
                "Sem destino, o índice de sincronização deveria cair no fallback da origem (0).");
        exigir(!handler.estaAtivo(), "concluir() deveria encerrar o gesto.");

        // --- Soltura dentro do Círculo B: destino tem prioridade sobre a origem ---
        QuadradinhoVenn quadradinho2 = new QuadradinhoVenn(20, 20, 10, "parte2");
        handler.iniciar(quadradinho2, 25, 25, circulos); // pickup dentro de A (índice 0)
        handler.mover(225, 25); // desloca (5,5); novo canto (220,20), centro (225,25) — dentro de B
        HandlerInteracaoQuadradinhoVenn.ResultadoSoltura resultadoParaB = handler.concluir(circulos);
        exigir(resultadoParaB.houveMovimento(), "Deveria reportar que houve movimento.");
        exigir(resultadoParaB.getIndiceOrigem() == 0, "A origem deveria continuar sendo o Círculo A (0).");
        exigir(resultadoParaB.getIndiceDestino() == 1, "O destino deveria ser o Círculo B (1).");
        exigir(resultadoParaB.obterIndiceParaSincronizacao() == 1,
                "Com destino encontrado, o índice de sincronização deveria ser o destino (1), não a origem.");

        // --- cancelar() interrompe o gesto sem produzir resultado ---
        QuadradinhoVenn quadradinho3 = new QuadradinhoVenn(20, 20, 10, "todo");
        handler.iniciar(quadradinho3, 25, 25, circulos);
        handler.cancelar();
        exigir(!handler.estaAtivo() && handler.obterQuadradinhoAtivo() == null,
                "cancelar() deveria liberar o quadradinho ativo.");
        boolean moveuDepoisDeCancelar = handler.mover(500, 500);
        exigir(!moveuDepoisDeCancelar && quadradinho3.x == 20 && quadradinho3.y == 20,
                "Um gesto cancelado não pode continuar movendo o quadradinho.");

        // --- circulos == null não lança exceção (defensivo) ---
        QuadradinhoVenn quadradinho4 = new QuadradinhoVenn(20, 20, 10, "referido");
        exigir(handler.iniciar(quadradinho4, 25, 25, null),
                "iniciar() com lista de círculos nula ainda deveria aceitar o pickup.");
        HandlerInteracaoQuadradinhoVenn.ResultadoSoltura resultadoSemCirculos = handler.concluir(null);
        exigir(resultadoSemCirculos.houveMovimento() && resultadoSemCirculos.getIndiceDestino() == -1
                        && resultadoSemCirculos.getIndiceOrigem() == -1,
                "Sem lista de círculos, origem e destino ficam -1, sem exceção.");

        System.out.println("Teste aprovado: handler preserva pickup, movimento livre sem limiar/clamp, "
                + "detecção de círculo de origem/destino e fallback de sincronização para o quadradinho "
                + "do diagrama de Venn.");
    }

    private static void exigir(boolean condicao, String mensagem) {
        if (!condicao) {
            throw new AssertionError(mensagem);
        }
    }
}
