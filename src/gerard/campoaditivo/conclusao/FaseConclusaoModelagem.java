package gerard.campoaditivo.conclusao;

/**
 * Fases semânticas do encerramento da modelagem.
 *
 * A tarefa só chega a CONCLUIDA quando a incógnita originalmente representada
 * por "?" foi posicionada no papel correto e substituída por um número pelo
 * protocolo de mouse/texto.
 */
public enum FaseConclusaoModelagem {
    INCOMPLETA,
    AGUARDANDO_PREENCHIMENTO_INCOGNITA,
    CONCLUIDA
}
