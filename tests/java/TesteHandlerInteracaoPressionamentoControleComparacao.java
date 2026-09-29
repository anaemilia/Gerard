import gerard.interacao.arraste.AlvoControleComparacao;
import gerard.interacao.arraste.HandlerInteracaoControleComparacao;
import gerard.interacao.arraste.ResultadoPressionamentoControleComparacao;

/**
 * Fase 7.12 de gerard-handlers-de-interacao: sequência do pressionamento da
 * barra de Comparação de Medidas extraída de Main.mousePressed. Verifica os
 * três desfechos, a ordem (alvo antes da liberação, liberação antes do
 * início) e que só o início muda o estado do gesto.
 */
public final class TesteHandlerInteracaoPressionamentoControleComparacao {

    private static final class AlvoFalso implements AlvoControleComparacao {
        private final boolean contem;
        int consultas;
        int ultimoX = -1;
        int ultimoY = -1;

        AlvoFalso(boolean contem) {
            this.contem = contem;
        }

        @Override
        public boolean contemControleOuEscala(int posicaoX, int posicaoY) {
            consultas++;
            ultimoX = posicaoX;
            ultimoY = posicaoY;
            return contem;
        }
    }

    public static void main(String[] args) {
        HandlerInteracaoControleComparacao handler = new HandlerInteracaoControleComparacao();

        exigir(handler.pressionar(null, 10, 20, true)
                        == ResultadoPressionamentoControleComparacao.NAO_CONSUMIDO
                        && !handler.estaAtivo(),
                "Sem alvo o clique não é consumido e o gesto não inicia.");

        AlvoFalso fora = new AlvoFalso(false);
        exigir(handler.pressionar(fora, 10, 20, true)
                        == ResultadoPressionamentoControleComparacao.NAO_CONSUMIDO
                        && !handler.estaAtivo(),
                "Fora do controle e da escala o clique segue para os demais protocolos.");
        exigir(fora.consultas == 1 && fora.ultimoX == 10 && fora.ultimoY == 20,
                "O hit-test é consultado uma vez, com as coordenadas recebidas.");

        AlvoFalso foraBloqueado = new AlvoFalso(false);
        exigir(handler.pressionar(foraBloqueado, 1, 2, false)
                        == ResultadoPressionamentoControleComparacao.NAO_CONSUMIDO,
                "O alvo é avaliado antes da liberação: fora do alvo não há bloqueio.");

        AlvoFalso dentro = new AlvoFalso(true);
        exigir(handler.pressionar(dentro, 30, 40, false)
                        == ResultadoPressionamentoControleComparacao.BLOQUEADO
                        && !handler.estaAtivo(),
                "Sobre o controle, sem liberação, o gesto é bloqueado e não inicia.");
        exigir(dentro.consultas == 1, "Bloqueio não repete o hit-test.");

        AlvoFalso liberado = new AlvoFalso(true);
        exigir(handler.pressionar(liberado, 30, 40, true)
                        == ResultadoPressionamentoControleComparacao.INICIADO
                        && handler.estaAtivo(),
                "Sobre o controle, liberado, o gesto inicia.");
        exigir(liberado.consultas == 1, "Início não repete o hit-test.");

        handler.concluir();
        exigir(!handler.estaAtivo(), "A soltura continua encerrando o gesto pelo mesmo handler.");

        System.out.println("Teste aprovado: pressionamento da barra de Comparação — não consumido, "
                + "bloqueado sem iniciar, iniciado; alvo antes da liberação.");
    }

    private static void exigir(boolean condicao, String mensagem) {
        if (!condicao) {
            throw new AssertionError(mensagem);
        }
    }
}
