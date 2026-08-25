package gerard.dominio.campoaditivo.evento;

/**
 * Tipos de evento semântico que um PapelQuantitativo pode produzir.
 *
 * {@code FEEDBACK_EXIBIDO} (REFERENCE.md §4.8, adicionado 2026-08-07):
 * registra que um elemento do repertório de Scaffolding foi de fato
 * apresentado — nunca que o participante o percebeu, entendeu ou prestou
 * atenção (isso continua não registrado, Seção 4.10). O critério de
 * confirmação depende da modalidade de entrega
 * ({@link ModalidadeEntregaScaffolding}): "renderizado" para modalidades
 * passivas (visual, sonora); "affordance ativada" para interativas
 * (háptica, guiada por movimento, manipulativa) — quem publica o evento
 * decide qual critério já foi satisfeito no momento da publicação, este
 * enum só nomeia o tipo.
 */
public enum TipoEventoPapel {
    VALOR_POSICIONADO,
    VALOR_REJEITADO,
    TENTATIVA_AVALIADA,
    FEEDBACK_EXIBIDO;

    /**
     * Tipo semântico com sufixo de versão embutido (ex.:
     * {@code "papel_quantitativo.valor_posicionado.v1"}), para
     * {@link EventoEnvelope#getTipoVersionado()} — convenção "type-based
     * versioning" do CloudEvents, já referenciada em REFERENCE.md §4.8.
     */
    public String chaveVersionada() {
        return "papel_quantitativo." + name().toLowerCase(java.util.Locale.ROOT) + ".v1";
    }
}
