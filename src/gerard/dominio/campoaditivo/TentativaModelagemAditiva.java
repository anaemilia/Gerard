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
    /**
     * Decisão da usuária (2026-09-29): depois do diagrama ficar azul, toda
     * modificação é exploratória, até outra situação ser sorteada. O
     * encerramento pertence a este agregado de escopo da tentativa; a
     * restauração não o desfaz (só uma nova tentativa/situação o faz).
     */
    private boolean encerradaPorConclusao;
    private final Set<PapelQuantitativo> participantes =
            java.util.Collections.newSetFromMap(
                    new java.util.IdentityHashMap<PapelQuantitativo, Boolean>());

    public TentativaModelagemAditiva(String tentativaId) {
        this.tentativaId = tentativaId == null ? "" : tentativaId.trim();
    }

    /**
     * Encerra a tentativa pela conclusão e informa os papéis participantes,
     * que passam a tratar submissões como exploratórias.
     */
    public void encerrarPorConclusao(PapelQuantitativo... papeisParticipantes) {
        encerradaPorConclusao = true;
        if (papeisParticipantes != null) {
            for (PapelQuantitativo papel : papeisParticipantes) {
                incorporar(papel);
            }
        }
        for (PapelQuantitativo papel : participantes) {
            papel.encerrarPorConclusao();
        }
    }

    /**
     * Registra um papel como participante desta tentativa. Quem cria os
     * papéis (desktop ou web) só os incorpora; o encerramento pela conclusão
     * alcança todos os participantes, inclusive os incorporados depois dele.
     */
    public void incorporar(PapelQuantitativo papel) {
        if (papel == null) {
            return;
        }
        participantes.add(papel);
        if (encerradaPorConclusao) {
            papel.encerrarPorConclusao();
        }
    }

    public boolean estaEncerradaPorConclusao() {
        return encerradaPorConclusao;
    }

    /**
     * Constitui um fato como ação instrumental desta tentativa. Após o
     * encerramento pela conclusão, nenhuma ação é constituída: a manipulação
     * é exploratória e não gera registro.
     */
    public <R> java.util.Optional<R> constituir(R registro) {
        if (registro == null || encerradaPorConclusao) {
            return java.util.Optional.empty();
        }
        return java.util.Optional.of(registro);
    }

    public String getTentativaId() {
        return tentativaId;
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
