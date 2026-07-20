package gerard.ui.enunciado;

import gerard.campoaditivo.diagrama.elementos.ItemTextoArrastavel;
import gerard.campoaditivo.sincronizacao.texto.ElementoSemanticoTexto;
import java.util.ArrayList;
import java.util.List;

/**
 * Seleciona, dentre os itens de texto arrastáveis, apenas os que ainda estão
 * no enunciado (isto é, que o usuário ainda não moveu para dentro do
 * diagrama).
 *
 * Extraído de Main.TelaGerard (trecho original dentro de
 * sincronizarElementosSemanticosDoTexto) como parte da Fase 1 do plano de
 * refatoração — ver PLANO_REFATORACAO_ARQUITETURA_GERARD.md.
 *
 * É uma função pura sobre a lista recebida: não lê nem escreve nenhum campo
 * de TelaGerard.
 */
public final class SeletorItensTexto {

    private SeletorItensTexto() {
    }

    /**
     * @param itensArrastaveis lista completa de itens de texto arrastáveis
     *                         (pode conter itens já movidos para o diagrama)
     * @return nova lista, só com os itens que ainda estão no enunciado
     *         (item != null e !item.estaNoDiagrama()), na mesma ordem original
     */
    public static List<ElementoSemanticoTexto> itensAindaNoEnunciado(
            List<ItemTextoArrastavel> itensArrastaveis) {
        List<ElementoSemanticoTexto> itensDoEnunciado = new ArrayList<ElementoSemanticoTexto>();
        if (itensArrastaveis == null) {
            return itensDoEnunciado;
        }
        for (int i = 0; i < itensArrastaveis.size(); i++) {
            ItemTextoArrastavel item = itensArrastaveis.get(i);
            if (item != null && !item.estaNoDiagrama()) {
                itensDoEnunciado.add(item);
            }
        }
        return itensDoEnunciado;
    }
}
