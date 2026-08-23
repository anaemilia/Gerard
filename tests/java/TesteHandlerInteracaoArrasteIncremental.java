import gerard.campoaditivo.diagrama.elementos.ConectorVergnaud;
import gerard.campoaditivo.diagrama.modelo.TipoConectorDiagrama;
import gerard.interacao.arraste.AlvoMovelIncremental;
import gerard.interacao.arraste.HandlerInteracaoArrasteIncremental;
import gerard.interacao.geometria.LimitesMovimento;
import gerard.ui.vergnaud.AdaptadorMovimentoConectorVergnaud;
import java.awt.Rectangle;

public final class TesteHandlerInteracaoArrasteIncremental {
    public static void main(String[] args) {
        testarProtocoloPortatil();
        testarAdaptadorDesktopDoConector();
        System.out.println("Teste aprovado: arraste incremental portátil preserva limiar, "
                + "deltas, cancelamento e adaptação geométrica do conector desktop.");
    }

    private static void testarProtocoloPortatil() {
        HandlerInteracaoArrasteIncremental<AlvoFalso> handler =
                new HandlerInteracaoArrasteIncremental<AlvoFalso>();
        AlvoFalso alvo = new AlvoFalso();
        LimitesMovimento limites = new LimitesMovimento(0, 700, 0, 400);

        exigir(handler.iniciar(alvo, 100, 50),
                "O pickup deveria aceitar um alvo portátil válido.");
        exigir(handler.estaAtivo() && handler.obterAlvoAtivo() == alvo,
                "O handler deveria preservar a identidade do alvo.");

        boolean moveuAbaixoDoLimiar = handler.mover(102, 50, limites);
        exigir(!moveuAbaixoDoLimiar && alvo.movimentos == 0,
                "Oscilação abaixo do limiar não deveria movimentar o alvo.");

        boolean moveu = handler.mover(130, 90, limites);
        exigir(moveu && alvo.deltaX == 30 && alvo.deltaY == 40
                        && alvo.limites == limites,
                "O protocolo deveria encaminhar delta e geometria neutra.");

        handler.cancelar();
        exigir(!handler.estaAtivo() && handler.obterAlvoAtivo() == null,
                "cancelar() deveria liberar o alvo ativo.");
        exigir(!handler.mover(180, 120, limites),
                "Um gesto cancelado não pode continuar movendo o alvo.");
        exigir(!handler.iniciar(null, 0, 0) && !handler.estaAtivo(),
                "iniciar(null) deveria manter o protocolo cancelado.");
    }

    private static void testarAdaptadorDesktopDoConector() {
        ConectorVergnaud conector = new ConectorVergnaud(
                TipoConectorDiagrama.LINHA, 50, 50, 150, 50, "",
                new Rectangle(0, 0, 200, 100));
        AdaptadorMovimentoConectorVergnaud adaptador =
                new AdaptadorMovimentoConectorVergnaud(conector);
        LimitesMovimento limitesDaTela =
                AdaptadorMovimentoConectorVergnaud.traduzir(
                        new Rectangle(0, 0, 700, 400));

        adaptador.moverPor(100, 100, limitesDaTela);
        exigir(conector.x1 == 100 && conector.x2 == 200
                        && conector.y1 == 100 && conector.y2 == 100,
                "O adaptador deveria preservar a zona própria do conector.");

        ConectorVergnaud semZonaPropria = new ConectorVergnaud(
                TipoConectorDiagrama.LINHA, 10, 10, 30, 10, "", null);
        new AdaptadorMovimentoConectorVergnaud(semZonaPropria)
                .moverPor(-50, -50, new LimitesMovimento(0, 100, 0, 100));
        exigir(semZonaPropria.x1 == 0 && semZonaPropria.y1 == 0,
                "Sem zona própria, o conector deveria usar os limites portáteis recebidos.");
    }

    private static final class AlvoFalso implements AlvoMovelIncremental {
        private int movimentos;
        private int deltaX;
        private int deltaY;
        private LimitesMovimento limites;

        @Override
        public void moverPor(int deltaX, int deltaY, LimitesMovimento limites) {
            movimentos++;
            this.deltaX = deltaX;
            this.deltaY = deltaY;
            this.limites = limites;
        }
    }

    private static void exigir(boolean condicao, String mensagem) {
        if (!condicao) {
            throw new AssertionError(mensagem);
        }
    }
}
