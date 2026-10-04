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
     * Regra de 2026-10-02: toda primeira modelagem tem persistência. Depois
     * do diagrama ficar azul (conclusão correta com os dados originais), a
     * exploração ocorre sem persistência. Restaurar ou sortear reabre a
     * primeira modelagem. O encerramento e a reabertura pertencem a este
     * agregado de escopo da tentativa.
     */
    private boolean encerradaPorConclusao;
    private final java.util.Map<String, Integer> rejeicoesOperacao =
            new java.util.LinkedHashMap<String, Integer>();
    private final Set<String> operacoesAcompanhadas = new LinkedHashSet<String>();
    private boolean ajudaVisualOperacaoNoLimite;
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
     * Ponto único em que a conclusão observada por uma representação encerra a tentativa. Só a
     * primeira conclusão encerra; observar a conclusão de novo (exploração) não faz nada.
     *
     * @return verdadeiro se esta chamada encerrou a tentativa
     */
    public boolean encerrarSeConcluida(FonteDeConclusao fonte, PapelQuantitativo... papeisParticipantes) {
        if (encerradaPorConclusao || fonte == null || !fonte.isConcluida()) {
            return false;
        }
        encerrarPorConclusao(papeisParticipantes);
        return true;
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
     * A tentativa é a dona da regra de persistência (2026-10-02): só a
     * primeira modelagem gera registro factual. Quem persiste (logger,
     * publicador de gestos, porta web) apenas consulta este fato.
     */
    public boolean admiteRegistroFactual() {
        return getFase() == FaseDaTentativa.MODELAGEM;
    }

    /** Fase em que a atividade está: modelagem até o azul, exploratória depois dele. */
    public FaseDaTentativa getFase() {
        return encerradaPorConclusao ? FaseDaTentativa.EXPLORATORIA : FaseDaTentativa.MODELAGEM;
    }

    /**
     * A fase de exploração só existe depois da conclusão correta (diagrama azul): é quando os
     * valores e os números do enunciado podem ser recalculados para manter as representações
     * consistentes. Antes disso, os dados curados nunca mudam.
     */
    public boolean admiteExploracao() {
        return getFase() == FaseDaTentativa.EXPLORATORIA;
    }

    /**
     * Constitui um fato como ação instrumental desta tentativa. Após o
     * encerramento pela conclusão, nenhuma ação é constituída: a manipulação
     * é exploratória e não gera registro.
     */
    public <R> java.util.Optional<R> constituir(R registro) {
        if (registro == null || !admiteRegistroFactual()) {
            return java.util.Optional.empty();
        }
        if (registro instanceof gerard.dominio.campoaditivo.situacao.ResultadoEscolhaOperacaoModelagem) {
            acompanharEscolhaOperacao(
                    (gerard.dominio.campoaditivo.situacao.ResultadoEscolhaOperacaoModelagem) registro);
        }
        return java.util.Optional.of(registro);
    }

    private void acompanharEscolhaOperacao(
            gerard.dominio.campoaditivo.situacao.ResultadoEscolhaOperacaoModelagem registro) {
        if (!operacoesAcompanhadas.add(registro.getActionId())) {
            return;
        }
        String alvo = registro.getAlvoSemantico();
        Integer anterior = rejeicoesOperacao.get(alvo);
        int quantidade = registro.foiCorreta() ? 0 : (anterior == null ? 1 : anterior + 1);
        rejeicoesOperacao.put(alvo, quantidade);
        if (quantidade >= PapelQuantitativo.LIMITE_TENTATIVAS_REJEITADAS_CONSECUTIVAS) {
            ajudaVisualOperacaoNoLimite = true;
        }
    }

    /** Fato da tentativa, sem alterar bloqueio, avaliação ou registros dos participantes. */
    public boolean estaNoLimiteAjudaVisual() {
        if (ajudaVisualOperacaoNoLimite) {
            return true;
        }
        for (PapelQuantitativo papel : participantes) {
            if (papel.estaBloqueadoPorLimiteTentativas()) {
                return true;
            }
        }
        return false;
    }

    public String getTentativaId() {
        return tentativaId;
    }

    /**
     * Restaurar reabre a primeira modelagem: a tentativa deixa de estar
     * encerrada pela conclusão e todos os papéis participantes voltam a
     * avaliar, contar rejeições e constituir ações. Acontece antes de o
     * registro da própria restauração ser constituído, que portanto é
     * persistido.
     */
    private void reabrirPrimeiraModelagem(PapelQuantitativo[] papeisParticipantes) {
        encerradaPorConclusao = false;
        if (papeisParticipantes != null) {
            for (PapelQuantitativo papel : papeisParticipantes) {
                incorporar(papel);
            }
        }
        for (PapelQuantitativo papel : participantes) {
            papel.reabrirPrimeiraModelagem();
        }
    }

    public RegistroAcaoRestauracaoModelagem restaurar(
            TipoRestauracaoModelagem tipo, OrigemAcao origem,
            PapelQuantitativo... papeisParticipantes) {
        if (tipo == null) {
            throw new IllegalArgumentException("tipo de restauração não pode ser nulo");
        }
        reabrirPrimeiraModelagem(papeisParticipantes);
        rejeicoesOperacao.clear();
        operacoesAcompanhadas.clear();
        ajudaVisualOperacaoNoLimite = false;
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
