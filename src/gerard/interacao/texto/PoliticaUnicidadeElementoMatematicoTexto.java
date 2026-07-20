package gerard.interacao.texto;

import gerard.campoaditivo.diagrama.elementos.ItemTextoArrastavel;
import gerard.campoaditivo.diagrama.elementos.MarcadorTexto;
import java.util.List;

/**
 * Impede a duplicação da mesma ocorrência textual no diagrama.
 *
 * A identidade é o token da ocorrência, e não apenas o valor ou o papel. Isso
 * evita que um número já posicionado bloqueie indevidamente a incógnita ou
 * outra ocorrência com o mesmo valor.
 */
public final class PoliticaUnicidadeElementoMatematicoTexto {

    public boolean jaEstaNoDiagrama(MarcadorTexto marcador,
            List<ItemTextoArrastavel> itens) {
        ElementoMatematicoImersoTexto elemento =
                ElementoMatematicoImersoTexto.aPartirDe(marcador);
        if (elemento == null || itens == null) {
            return false;
        }
        String token = elemento.getTokenId();
        if (token.length() == 0) {
            return false;
        }
        for (ItemTextoArrastavel item : itens) {
            if (item != null && item.estaNoDiagrama()
                    && token.equals(item.tokenSemanticoId)) {
                return true;
            }
        }
        return false;
    }
}
