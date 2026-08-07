package gerard.dominio.campoaditivo;

/**
 * Um dos dois eixos do repertório de Scaffolding de cada objeto semântico
 * (REFERENCE.md §4.8) — o outro é o tipo funcional (conceitual,
 * metacognitivo, procedimental, estratégico), ainda sem enum próprio.
 * Determina o critério de confirmação do evento {@code FEEDBACK_EXIBIDO}:
 *
 * <ul>
 * <li>Passivas ({@link #VISUAL}, {@link #SONORA}): critério "renderizado"
 * — dispara quando o componente de interface foi de fato construído e
 * exibido, ou o som foi de fato reproduzido.</li>
 * <li>Interativas ({@link #HAPTICA}, {@link #GUIADA_POR_MOVIMENTO},
 * {@link #MANIPULATIVA}): critério "affordance ativada" — dispara quando
 * o mecanismo de interação foi disponibilizado para o participante operar,
 * não quando ele de fato opera sobre ele.</li>
 * </ul>
 *
 * Aberto e extensível — não é um enum fechado na literatura de origem
 * (Hannafin, Land &amp; Oliver, 1999), mas hoje só estas cinco modalidades
 * têm instância concreta no Gérard.
 */
public enum ModalidadeEntregaScaffolding {
    VISUAL,
    SONORA,
    HAPTICA,
    MANIPULATIVA,
    GUIADA_POR_MOVIMENTO;

    /** Passivas: critério de confirmação é "renderizado". */
    public boolean ehPassiva() {
        return this == VISUAL || this == SONORA;
    }

    /** Interativas: critério de confirmação é "affordance ativada". */
    public boolean ehInterativa() {
        return !ehPassiva();
    }
}
