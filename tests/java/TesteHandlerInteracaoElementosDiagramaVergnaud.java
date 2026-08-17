import gerard.campoaditivo.diagrama.elementos.ConectorVergnaud;
import gerard.campoaditivo.diagrama.elementos.ElementoVergnaud;
import gerard.campoaditivo.diagrama.modelo.TipoConectorDiagrama;
import gerard.campoaditivo.diagrama.modelo.TipoFiguraDiagrama;
import gerard.interacao.arraste.HandlerInteracaoElementosDiagramaVergnaud;
import java.awt.Rectangle;

public final class TesteHandlerInteracaoElementosDiagramaVergnaud {
    public static void main(String[] args) {
        Rectangle limiteDiagrama = new Rectangle(0, 0, 700, 400);

        // --- Elemento: pickup, limiar de arraste e clamp pela zona permitida ---
        HandlerInteracaoElementosDiagramaVergnaud handler =
                new HandlerInteracaoElementosDiagramaVergnaud();
        ElementoVergnaud elemento = new ElementoVergnaud(
                100, 100, 50, 20, TipoFiguraDiagrama.RETANGULO, "7",
                new Rectangle(0, 0, 300, 300), false);

        exigir(handler.iniciarElemento(elemento, 112, 105),
                "O pickup deveria aceitar um elemento válido.");
        exigir(handler.estaAtivo() && handler.obterElementoAtivo() == elemento,
                "O handler deveria preservar a identidade do elemento.");
        exigir(handler.obterConectorAtivo() == null,
                "Nenhum conector deveria estar ativo enquanto um elemento é arrastado.");

        // Pequena oscilação (abaixo do limiar de 6px) não deve mover nada.
        boolean moveuAbaixoDoLimiar = handler.mover(114, 107, limiteDiagrama);
        exigir(!moveuAbaixoDoLimiar && elemento.x == 100 && elemento.y == 100,
                "Oscilação abaixo do limiar de arraste estrutural não deveria mover o elemento.");

        // Movimento além do limiar aplica o deslocamento do pickup.
        boolean moveu = handler.mover(180, 160, limiteDiagrama);
        exigir(moveu && elemento.x == 168 && elemento.y == 155,
                "O movimento além do limiar deveria preservar o offset do pickup.");

        // Clamp pela zona permitida do próprio elemento (300x300), não pelo limite geral.
        handler.mover(600, 600, limiteDiagrama);
        exigir(elemento.x == 250 && elemento.y == 280,
                "O movimento deveria respeitar a zona permitida do elemento (300x300, elemento 50x20).");

        handler.cancelar();
        exigir(!handler.estaAtivo() && handler.obterElementoAtivo() == null,
                "cancelar() deveria liberar o elemento ativo.");
        boolean moveuDepoisDeCancelar = handler.mover(500, 500, limiteDiagrama);
        exigir(!moveuDepoisDeCancelar,
                "Um gesto cancelado não pode continuar movendo o elemento.");

        // --- Conector: pickup por delta, limiar e clamp pela zona ---
        ConectorVergnaud conector = new ConectorVergnaud(
                TipoConectorDiagrama.LINHA, 50, 50, 150, 50, "",
                new Rectangle(0, 0, 700, 400));

        exigir(handler.iniciarConector(conector, 100, 50),
                "O pickup deveria aceitar um conector válido.");
        exigir(handler.estaAtivo() && handler.obterConectorAtivo() == conector,
                "O handler deveria preservar a identidade do conector.");
        exigir(handler.obterElementoAtivo() == null,
                "Nenhum elemento deveria estar ativo enquanto um conector é arrastado.");

        boolean moveuConectorAbaixoDoLimiar = handler.mover(102, 50, limiteDiagrama);
        exigir(!moveuConectorAbaixoDoLimiar && conector.x1 == 50 && conector.x2 == 150,
                "Oscilação abaixo do limiar não deveria mover o conector.");

        boolean moveuConector = handler.mover(130, 90, limiteDiagrama);
        exigir(moveuConector && conector.x1 == 80 && conector.y1 == 90
                        && conector.x2 == 180 && conector.y2 == 90,
                "O movimento do conector deveria aplicar o delta desde o último ponto.");

        // --- Mutuamente exclusivos: iniciar um cancela o outro ---
        ElementoVergnaud outroElemento = new ElementoVergnaud(
                10, 10, 30, 30, TipoFiguraDiagrama.RETANGULO, "?", null, true);
        handler.iniciarElemento(outroElemento, 20, 20);
        exigir(handler.obterConectorAtivo() == null
                        && handler.obterElementoAtivo() == outroElemento,
                "Iniciar um elemento deveria cancelar um conector previamente ativo.");

        handler.iniciarConector(conector, 80, 90);
        exigir(handler.obterElementoAtivo() == null
                        && handler.obterConectorAtivo() == conector,
                "Iniciar um conector deveria cancelar um elemento previamente ativo.");

        exigir(!handler.iniciarElemento(null, 0, 0) && !handler.estaAtivo(),
                "iniciarElemento(null) deveria cancelar qualquer gesto em andamento.");

        System.out.println("Teste aprovado: handler preserva pickup, limiar de arraste, "
                + "clamp pela zona permitida e exclusividade mútua entre elemento e conector "
                + "do diagrama de Vergnaud.");
    }

    private static void exigir(boolean condicao, String mensagem) {
        if (!condicao) {
            throw new AssertionError(mensagem);
        }
    }
}
