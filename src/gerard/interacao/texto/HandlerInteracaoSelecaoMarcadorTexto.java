package gerard.interacao.texto;

import gerard.campoaditivo.diagrama.elementos.ItemTextoArrastavel;
import gerard.campoaditivo.diagrama.elementos.MarcadorTexto;
import gerard.interacao.arraste.SessaoArrasteTextoParaDiagrama;
import java.util.List;

/**
 * Preserva a sequência portátil da seleção de um elemento matemático imerso
 * no enunciado.
 *
 * O hit-test, a apresentação, os logs e o início do gesto físico permanecem
 * no adaptador da plataforma. Este handler apenas impede duplicação e solicita
 * à sessão a cópia representacional que poderá ser arrastada ao diagrama.
 */
public final class HandlerInteracaoSelecaoMarcadorTexto {
    private final PoliticaUnicidadeElementoMatematicoTexto politicaUnicidade;
    private final SessaoArrasteTextoParaDiagrama sessaoArraste;

    public HandlerInteracaoSelecaoMarcadorTexto(
            PoliticaUnicidadeElementoMatematicoTexto politicaUnicidade,
            SessaoArrasteTextoParaDiagrama sessaoArraste) {
        if (politicaUnicidade == null || sessaoArraste == null) {
            throw new IllegalArgumentException(
                    "politica de unicidade e sessao de arraste sao obrigatorias");
        }
        this.politicaUnicidade = politicaUnicidade;
        this.sessaoArraste = sessaoArraste;
    }

    public ResultadoSelecaoMarcadorTexto processar(
            MarcadorTexto marcador, List<ItemTextoArrastavel> itens) {
        if (marcador == null) {
            return null;
        }
        if (politicaUnicidade.jaEstaNoDiagrama(marcador, itens)) {
            return new ResultadoSelecaoMarcadorTexto(
                    ResultadoSelecaoMarcadorTexto.Desfecho.JA_POSICIONADO,
                    marcador, null);
        }
        ItemTextoArrastavel item = sessaoArraste.iniciarPorMarcador(marcador);
        return new ResultadoSelecaoMarcadorTexto(
                item == null
                        ? ResultadoSelecaoMarcadorTexto.Desfecho.SEM_PROXY
                        : ResultadoSelecaoMarcadorTexto.Desfecho.INICIADO,
                marcador, item);
    }
}
