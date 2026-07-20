package gerard.campoaditivo.sincronizacao.representacoes;

/**
 * Regra comum de habilitação para operações que alteram valores semânticos em
 * eixo, barras, coleções e demais representações. Ela não se aplica a ações
 * puramente visuais do componente, como mover, reposicionar ou ocultar um
 * painel flutuante. Nenhuma representação pode se tornar a origem do primeiro
 * valor semântico: esse preenchimento deve ocorrer no diagrama de Vergnaud.
 */
public final class PoliticaInteracaoRepresentacoes {

    private final EstadoPrimeiroPosicionamento estado;
    private final String chaveMensagemBloqueio;

    public PoliticaInteracaoRepresentacoes(
            EstadoPrimeiroPosicionamento estado,
            String chaveMensagemBloqueio) {
        if (estado == null) {
            throw new IllegalArgumentException("estado não pode ser nulo");
        }
        this.estado = estado;
        this.chaveMensagemBloqueio = chaveMensagemBloqueio == null
                ? "" : chaveMensagemBloqueio;
    }

    public boolean estaLiberada() {
        return estado.possuiConteudoSemanticoNoVergnaud();
    }

    public String obterChaveMensagemBloqueio() {
        return chaveMensagemBloqueio;
    }
}
