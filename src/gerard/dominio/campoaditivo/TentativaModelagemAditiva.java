package gerard.dominio.campoaditivo;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * Agregado semântico de escopo da tentativa/modelagem aditiva.
 *
 * A restauração coordena potencialmente vários papéis, portanto não pertence
 * a um papel isolado. Este agregado constitui uma única ação, referencia os
 * participantes e encerra suas sequências de rejeições sem conhecer Swing,
 * geometria ou persistência.
 */
public final class TentativaModelagemAditiva {

    private final String tentativaId;

    public TentativaModelagemAditiva(String tentativaId) {
        this.tentativaId = tentativaId == null ? "" : tentativaId.trim();
    }

    public RegistroAcaoRestauracaoModelagem restaurar(
            TipoRestauracaoModelagem tipo, OrigemAcao origem,
            PapelQuantitativo... papeisParticipantes) {
        if (tipo == null) {
            throw new IllegalArgumentException("tipo de restauração não pode ser nulo");
        }
        OrigemAcao origemEfetiva = origem == null
                ? OrigemAcao.ORIGEM_USUARIO : origem;
        Set<String> papeis = new LinkedHashSet<String>();
        Set<String> sequencias = new LinkedHashSet<String>();
        if (papeisParticipantes != null) {
            for (PapelQuantitativo papel : papeisParticipantes) {
                if (papel == null) {
                    continue;
                }
                papeis.add(papel.getChave());
                String sequencia = papel.getRejectionSequenceIdAtual();
                if (sequencia != null && sequencia.trim().length() > 0) {
                    sequencias.add(sequencia.trim());
                }
                papel.restaurar();
            }
        }
        List<String> papeisOrdenados = new ArrayList<String>(papeis);
        List<String> sequenciasOrdenadas = new ArrayList<String>(sequencias);
        return new RegistroAcaoRestauracaoModelagem(
                UUID.randomUUID().toString(), tentativaId, tipo, origemEfetiva,
                papeisOrdenados, sequenciasOrdenadas);
    }
}
