package gerard.campoaditivo.montagem;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Avalia, passo a passo, se cada bloco já inserido na montagem está coerente
 * com a sequência textual curada. A análise é posicional: um bloco correto,
 * porém colocado na posição errada, deve ser marcado como incorreto.
 */
public final class AvaliadorMontagemPassoAPasso {

    public List<StatusVerificacaoBlocoMontagem> avaliar(List<BlocoTextoMontagem> blocosMontados,
                                                        List<String> idsCorretosOrdenados) {
        if (blocosMontados == null || blocosMontados.isEmpty()) {
            return Collections.emptyList();
        }
        List<StatusVerificacaoBlocoMontagem> estados =
                new ArrayList<StatusVerificacaoBlocoMontagem>(blocosMontados.size());
        for (int i = 0; i < blocosMontados.size(); i++) {
            BlocoTextoMontagem bloco = blocosMontados.get(i);
            String esperado = idsCorretosOrdenados != null && i < idsCorretosOrdenados.size()
                    ? idsCorretosOrdenados.get(i) : "";
            if (bloco != null && bloco.isCorreto() && bloco.getId().equals(esperado)) {
                estados.add(StatusVerificacaoBlocoMontagem.CORRETO);
            } else {
                estados.add(StatusVerificacaoBlocoMontagem.INCORRETO);
            }
        }
        return estados;
    }
}
