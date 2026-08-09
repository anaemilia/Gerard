import gerard.campoaditivo.diagrama.elementos.ItemTextoArrastavel;
import gerard.interacao.arraste.HandlerInteracaoItemTextoArrastavel;

public final class TesteHandlerInteracaoItemTextoArrastavel {
    public static void main(String[] args) {
        HandlerInteracaoItemTextoArrastavel handler =
                new HandlerInteracaoItemTextoArrastavel();
        ItemTextoArrastavel item = item(100, 240);

        exigir(handler.iniciar(item, 112, 249),
                "O pickup deveria aceitar um item válido.");
        exigir(handler.estaAtivo() && handler.obterItemAtivo() == item,
                "O handler deveria manter a identidade do item ativo.");

        handler.moverPara(180, 320);
        exigir(item.x == 168 && item.y == 311,
                "O movimento deveria preservar o offset real do pickup.");

        HandlerInteracaoItemTextoArrastavel.ResultadoSoltura soltura =
                handler.concluir();
        exigir(soltura.getItem() == item && soltura.houveMovimento(),
                "A soltura deveria identificar o item e o movimento real.");
        exigir(!handler.estaAtivo(),
                "A conclusão deveria limpar o estado transitório do gesto.");

        handler.iniciar(item, item.x + 3, item.y + 4);
        soltura = handler.concluir();
        exigir(!soltura.houveMovimento(),
                "Pressionar e soltar sem mover deve ser reavaliação.");

        handler.iniciar(item, item.x, item.y);
        handler.cancelar();
        exigir(!handler.estaAtivo() && handler.moverPara(400, 400) == null,
                "Um gesto cancelado não pode continuar movimentando o item.");

        exigir(handler.identificarFoco(item) == item,
                "Item no diagrama deveria aceitar foco de hover.");
        ItemTextoArrastavel itemNoTexto = item(20, 100);
        exigir(handler.identificarFoco(itemNoTexto) == null,
                "Item fora do diagrama não deveria aceitar foco de hover.");

        System.out.println("Teste aprovado: handler preserva pickup, movimento, soltura e foco do item textual.");
    }

    private static ItemTextoArrastavel item(int x, int y) {
        return new ItemTextoArrastavel(
                x, y, 40, 28, "7", true, "7", "papel.parte1");
    }

    private static void exigir(boolean condicao, String mensagem) {
        if (!condicao) {
            throw new AssertionError(mensagem);
        }
    }
}
