import gerard.interacao.arraste.HandlerInteracaoControleComparacao;

/**
 * Harness executável de HandlerInteracaoControleComparacao — Fase 7.8 do
 * roteiro de gerard-handlers-de-interacao (mesmo padrão sem JUnit de
 * TesteHandlerInteracaoQuadradinhoVenn/TesteHandlerInteracaoPaineisEixosRelacoes).
 *
 * O handler só guarda um booleano de gesto em curso — não há offset,
 * geometria nem lista de alvos a testar, diferente dos handlers de arraste
 * livre. O harness Robot (TesteMonkeySemiGuiado) não dirige este controle
 * específico (só item textual, digitação de incógnita, troca de categoria e
 * "nova situação"), então este teste unitário é a cobertura automatizada
 * real desta extração.
 */
public final class TesteHandlerInteracaoControleComparacao {

    public static void main(String[] args) {
        HandlerInteracaoControleComparacao handler = new HandlerInteracaoControleComparacao();

        exigir(!handler.estaAtivo(), "Um handler recém-criado não deveria estar ativo.");

        handler.iniciar();
        exigir(handler.estaAtivo(), "Após iniciar(), o handler deveria estar ativo.");

        handler.concluir();
        exigir(!handler.estaAtivo(), "Após concluir(), o handler não deveria mais estar ativo.");

        // concluir() sem gesto ativo é o reset defensivo usado no início de
        // mousePressed (arraste anterior interrompido sem passar por
        // mouseReleased) — não deve lançar exceção nem mudar o estado.
        handler.concluir();
        exigir(!handler.estaAtivo(), "concluir() sem gesto ativo deveria permanecer inofensivo.");

        // Reiniciar após concluir() funciona normalmente (novo gesto).
        handler.iniciar();
        exigir(handler.estaAtivo(), "Um novo iniciar() após concluir() deveria reativar o handler.");
        handler.concluir();
        exigir(!handler.estaAtivo(), "O segundo gesto também deveria encerrar corretamente.");

        System.out.println("Teste aprovado: handler concentra o único estado mecânico do arraste "
                + "do controle de Comparação (em curso ou não), com reset defensivo idempotente.");
    }

    private static void exigir(boolean condicao, String mensagem) {
        if (!condicao) {
            throw new AssertionError(mensagem);
        }
    }
}
