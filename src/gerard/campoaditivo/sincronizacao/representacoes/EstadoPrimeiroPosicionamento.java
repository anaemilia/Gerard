package gerard.campoaditivo.sincronizacao.representacoes;

/**
 * Leitura mínima do estado necessário para liberar a manipulação das
 * representações complementares após o primeiro preenchimento semântico do
 * diagrama de Vergnaud.
 */
public interface EstadoPrimeiroPosicionamento {
    boolean possuiConteudoSemanticoNoVergnaud();
}
