package gerard.Scaffolding.conclusao;

import gerard.campoaditivo.diagrama.elementos.ConectorVergnaud;
import gerard.campoaditivo.diagrama.elementos.ElementoVergnaud;
import gerard.campoaditivo.diagrama.elementos.ItemTextoArrastavel;
import gerard.campoaditivo.diagrama.elementos.QuadradinhoVenn;
import java.util.List;

/** Aplica ou remove o estado visual azul da conclusão no diagrama de Vergnaud
 *  e, quando presente, no diagrama de coleções (quadradinhos Venn). */
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
        if (conectores != null) {
            for (ConectorVergnaud conector : conectores) {
                if (conector != null) conector.definirConclusaoDestacada(concluido);
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
