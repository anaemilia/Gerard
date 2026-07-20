package gerard.interacao.arraste;

import gerard.campoaditivo.diagrama.elementos.ItemTextoArrastavel;
import gerard.campoaditivo.diagrama.elementos.MarcadorTexto;
import gerard.interacao.texto.ElementoMatematicoImersoTexto;
import java.awt.Rectangle;

/**
 * Controla o arraste por cópia visual dos elementos matemáticos imersos no
 * texto. A projeção textual nunca é movida. Apenas o proxy temporário percorre
 * a tela e pode ser promovido a item persistente do diagrama após validação.
 */
public class SessaoArrasteTextoParaDiagrama {

    private ItemTextoArrastavel proxyAtivo;
    private ElementoMatematicoImersoTexto elementoOrigem;
    private boolean feedbackIncorretoAtivo;

    public ItemTextoArrastavel iniciarPorMarcador(MarcadorTexto marcador) {
        return iniciar(ElementoMatematicoImersoTexto.aPartirDe(marcador));
    }

    public ItemTextoArrastavel iniciar(ElementoMatematicoImersoTexto elemento) {
        if (elemento == null) {
            limpar();
            return null;
        }
        Rectangle area = elemento.getAreaOrigem();
        proxyAtivo = new ItemTextoArrastavel(
                area.x,
                area.y,
                Math.max(1, area.width),
                Math.max(1, area.height),
                elemento.getValorExibido(),
                elemento.isEditavel(),
                elemento.getValorSemanticoOriginal(),
                elemento.getChavePapelSemantico(),
                elemento.getTokenId()
        );
        elementoOrigem = elemento;
        feedbackIncorretoAtivo = false;
        return proxyAtivo;
    }

    public boolean ehProxyAtivo(ItemTextoArrastavel item) {
        return elementoOrigem != null && item != null && item == proxyAtivo;
    }

    /**
     * A persistência exige simultaneamente alvo visual e compatibilidade
     * semântica. Isso impede que um proxy incorreto seja incorporado ao modelo.
     */
    public boolean devePersistirAoSoltar(
            ItemTextoArrastavel item,
            boolean solturaSobreDiagrama,
            boolean posicionamentoSemanticamenteCorreto) {
        return ehProxyAtivo(item)
                && solturaSobreDiagrama
                && posicionamentoSemanticamenteCorreto;
    }

    /** Compatibilidade: uma soltura visualmente válida era tratada como semanticamente válida. */
    public boolean devePersistirAoSoltar(ItemTextoArrastavel item, boolean solturaSobreDiagrama) {
        return devePersistirAoSoltar(item, solturaSobreDiagrama, true);
    }

    /**
     * Um elemento matemático solto sobre o diagrama, ainda que em papel
     * semanticamente incorreto, deve permanecer como item manipulável. O erro
     * é tratado por feedback; ele não autoriza remover o item da tela.
     */
    public boolean deveManterNoDiagramaAposErro(
            ItemTextoArrastavel item,
            boolean solturaSobreDiagrama,
            boolean posicionamentoSemanticamenteCorreto) {
        return ehProxyAtivo(item)
                && solturaSobreDiagrama
                && !posicionamentoSemanticamenteCorreto;
    }

    public boolean deveDescartarAoSoltar(
            ItemTextoArrastavel item,
            boolean solturaSobreDiagrama,
            boolean posicionamentoSemanticamenteCorreto) {
        return ehProxyAtivo(item) && !solturaSobreDiagrama;
    }

    /** Compatibilidade com o fluxo anterior de descarte por área. */
    public boolean deveDescartarAoSoltar(ItemTextoArrastavel item, boolean solturaSobreDiagrama) {
        return deveDescartarAoSoltar(item, solturaSobreDiagrama, true);
    }

    public ElementoMatematicoImersoTexto getElementoOrigem() {
        return elementoOrigem;
    }

    /**
     * Um erro semântico sobre o diagrama deve aguardar o feedback visual antes
     * do descarte. Solturas fora do diagrama continuam sendo descartadas de
     * imediato, pois não representam associação a um papel incorreto.
     */
    public boolean deveAguardarFeedbackIncorreto(
            ItemTextoArrastavel item,
            boolean solturaSobreDiagrama,
            boolean posicionamentoSemanticamenteCorreto) {
        return false;
    }

    public boolean iniciarFeedbackIncorreto(ItemTextoArrastavel item) {
        if (!ehProxyAtivo(item)) {
            return false;
        }
        feedbackIncorretoAtivo = true;
        return true;
    }

    public ItemTextoArrastavel obterProxyEmFeedback() {
        return feedbackIncorretoAtivo ? proxyAtivo : null;
    }

    public void concluirFeedbackIncorreto(ItemTextoArrastavel item) {
        if (ehProxyAtivo(item) && feedbackIncorretoAtivo) {
            limpar();
        }
    }

    public void confirmarPersistencia(ItemTextoArrastavel item) {
        if (ehProxyAtivo(item)) {
            limpar();
        }
    }

    public void descartarProxy(ItemTextoArrastavel item) {
        if (ehProxyAtivo(item)) {
            limpar();
        }
    }

    public void limpar() {
        proxyAtivo = null;
        elementoOrigem = null;
        feedbackIncorretoAtivo = false;
    }
}
