package gerard.estilointeracao;

/**
 * Pilar independente que define como o sujeito interage com qualquer
 * representação. O estilo não pertence ao texto, ao diagrama ou ao idioma;
 * essas representações apenas o utilizam.
 */
public enum EstiloInteracao {
    PROXIMIDADE,
    DROP_TARGET_HIGHLIGHTING,
    DRAG_OVER_FEEDBACK,
    AFFORDANCE,
    SNAP_TO_TARGET
}
