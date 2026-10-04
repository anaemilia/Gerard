package gerard.dominio.campoaditivo;

/**
 * As duas fases da atividade. MODELAGEM: o participante constrói o modelo; os dados curados não
 * mudam e tudo o que faz é registrado. EXPLORATORIA: começa na conclusão correta (diagrama azul);
 * os valores podem ser recalculados para manter as representações consistentes e nada é
 * registrado. Reiniciar ou sortear volta à MODELAGEM, com uma nova tentativa.
 */
public enum FaseDaTentativa {
    MODELAGEM,
    EXPLORATORIA
}
