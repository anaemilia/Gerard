package gerard.agente.modelousuario;

/**
 * Nível de conceitualização linguística estimado a partir do texto livre de
 * explicacaoElemento (ver DiagnosticoTarefa), inspirado na escada descrita
 * por Vergnaud em "The role of language and symbols in representation"
 * (1998, seção 3): adjetivo (predicado de um lugar, descreve um elemento) →
 * relacional (liga dois ou mais elementos/papéis) → substantivado (o nome
 * do papel vira sujeito com propriedade própria) → classificatório
 * (generalização ou classificação numa categoria maior).
 *
 * AUSENTE cobre tanto texto vazio quanto texto curto demais para classificar.
 *
 * Sinal fraco/exploratório (ver AnalisadorNivelConceitual, que é casamento de
 * padrão sobre vocabulário fechado, não análise sintática de verdade) — só
 * nivelConceitualCurado, preenchido por um pesquisador, deve alimentar
 * inferência de regras (ver InferenciaRegrasModelador). Decisão do usuário
 * em 2026-07-23.
 */
public enum NivelConceitualExplicacao {
    AUSENTE,
    ADJETIVO,
    RELACIONAL,
    SUBSTANTIVADO,
    CLASSIFICATORIO
}
