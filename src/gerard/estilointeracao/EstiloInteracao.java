package gerard.estilointeracao;

/**
 * Pilar independente que define como o sujeito interage com qualquer
 * representação. O estilo não pertence ao texto, ao diagrama ou ao idioma;
 * essas representações apenas o utilizam.
 */
public enum EstiloInteracao {
    PROXIMIDADE("ui.test.mode.proximity"),
    DROP_TARGET_HIGHLIGHTING("ui.test.mode.dropTarget"),
    DRAG_OVER_FEEDBACK("ui.test.mode.dragOver"),
    AFFORDANCE("ui.test.mode.affordance"),
    SNAP_TO_TARGET("ui.test.mode.snap");

    private final String chave;

    EstiloInteracao(String chave) {
        this.chave = chave;
    }

    public String getChave() {
        return chave;
    }
}
