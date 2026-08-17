import gerard.campoaditivo.diagrama.elementos.ElementoTextoMovel;
import gerard.interacao.arraste.HandlerInteracaoElementoTextoMovel;
import gerard.ui.enunciado.GeometriaAreaEnunciado;
import gerard.ui.geometria.NoGeometriaRepresentacao;
import java.awt.Rectangle;

public final class TesteHandlerInteracaoElementoTextoMovel {
    public static void main(String[] args) {
        HandlerInteracaoElementoTextoMovel handler =
                new HandlerInteracaoElementoTextoMovel();
        ElementoTextoMovel elemento = elemento(100, 240, 50, 20);

        exigir(handler.iniciar(elemento, 112, 249),
                "O pickup deveria aceitar um elemento válido.");
        exigir(handler.estaAtivo()
                        && handler.obterElementoAtivo() == elemento,
                "O handler deveria preservar a identidade do elemento.");

        handler.moverLivrePara(180, 320);
        exigir(elemento.x == 168 && elemento.y == 311,
                "O movimento livre deveria preservar o offset do pickup.");

        GeometriaAreaEnunciado geometria =
                new GeometriaAreaEnunciado(130);
        Rectangle limites = geometria.obterLimitesMovimento(elemento, 295);
        exigir(limites.equals(new Rectangle(20, 205, 200, 115)),
                "A árvore deveria derivar os limites do card e do elemento.");
        handler.moverDentroDosLimites(500, 100, limites);
        exigir(elemento.x == 220 && elemento.y == 205,
                "O movimento limitado deveria respeitar a geometria recebida.");
        exigir(geometria.contem(15, 185, 295)
                        && geometria.contem(280, 320, 295)
                        && !geometria.contem(14, 185, 295),
                "A área deveria preservar os limites inclusivos do card.");

        NoGeometriaRepresentacao raiz = new NoGeometriaRepresentacao(
                null, new Rectangle(100, 50, 300, 200));
        NoGeometriaRepresentacao filho = new NoGeometriaRepresentacao(
                raiz, new Rectangle(15, 10, 80, 40));
        exigir(filho.getPai() == raiz
                        && filho.obterLimitesAbsolutos().equals(
                                new Rectangle(115, 60, 80, 40)),
                "O nó deveria resolver sua geometria a partir do pai.");

        exigir(handler.concluir() == elemento && !handler.estaAtivo(),
                "A conclusão deveria devolver o elemento e limpar o gesto.");

        handler.iniciar(elemento, elemento.x, elemento.y);
        handler.cancelar();
        exigir(handler.moverLivrePara(400, 400) == null,
                "Um gesto cancelado não pode continuar movendo o elemento.");

        exigir(handler.identificarFoco(elemento, true) == elemento,
                "Elemento dentro do enunciado deveria aceitar foco.");
        exigir(handler.identificarFoco(elemento, false) == null,
                "Elemento fora do enunciado não deveria aceitar foco.");

        System.out.println("Teste aprovado: handler preserva pickup, movimento livre, limites, soltura e foco do texto.");
    }

    private static ElementoTextoMovel elemento(int x, int y,
            int largura, int altura) {
        ElementoTextoMovel elemento = new ElementoTextoMovel("texto");
        elemento.x = x;
        elemento.y = y;
        elemento.largura = largura;
        elemento.altura = altura;
        return elemento;
    }

    private static void exigir(boolean condicao, String mensagem) {
        if (!condicao) {
            throw new AssertionError(mensagem);
        }
    }
}
