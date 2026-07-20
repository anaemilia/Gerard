package gerard.campoaditivo.sincronizacao.texto;

/**
 * Converte a chave do papel exibido no enunciado para o índice canônico do
 * estado semântico compartilhado.
 */
public interface MapeadorPapelSemanticoTexto {
    int paraIndiceSemantico(String chavePapel);
}
