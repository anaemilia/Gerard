package gerard.Scaffolding.conclusao;

import gerard.campoaditivo.diagrama.elementos.ConectorVergnaud;
import gerard.campoaditivo.diagrama.elementos.ElementoVergnaud;
import gerard.campoaditivo.diagrama.elementos.ItemTextoArrastavel;
import gerard.campoaditivo.diagrama.elementos.QuadradinhoVenn;
import java.util.List;

/**
 * Aplica ou remove o estado visual azul da conclusão no diagrama de Vergnaud
 * e, quando presente, no diagrama de coleções (quadradinhos Venn).
 *
 * O destaque azul (COR_SUCESSO) fica restrito aos elementos que exibem um
 * número (ElementoVergnaud, ItemTextoArrastavel) — conectores (setas, chaves)
 * nunca recebem o destaque, pois não representam um valor numérico e o azul
 * de sucesso deve sinalizar especificamente os números corretos, não a
 * estrutura do diagrama em torno deles.
 */
public final class AplicadorDestaqueConclusaoDiagrama {
    public void aplicar(boolean concluido,
                        List<ElementoVergnaud> elementos,
                        List<ConectorVergnaud> conectores,
                        List<ItemTextoArrastavel> itens) {
        aplicar(concluido, elementos, conectores, itens, null);
    }

    public void aplicar(boolean concluido,
                        List<ElementoVergnaud> elementos,
                        List<ConectorVergnaud> conectores,
                        List<ItemTextoArrastavel> itens,
                        List<QuadradinhoVenn> quadradinhos) {
        if (elementos != null) {
            for (ElementoVergnaud elemento : elementos) {
                if (elemento != null) elemento.definirConclusaoDestacada(concluido);
            }
        }
        if (itens != null) {
            for (ItemTextoArrastavel item : itens) {
                if (item != null) item.definirConclusaoDestacada(concluido);
            }
        }
        if (quadradinhos != null) {
            for (QuadradinhoVenn quadradinho : quadradinhos) {
                if (quadradinho != null) quadradinho.definirConclusaoDestacada(concluido);
            }
        }
    }
}
