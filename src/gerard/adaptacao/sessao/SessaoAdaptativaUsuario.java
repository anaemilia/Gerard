package gerard.adaptacao.sessao;

import gerard.adaptacao.ContextoAdaptativoUsuario;
import gerard.adaptacao.ProprietarioRepertorioAjuda;
import gerard.adaptacao.RegraAdaptativaPublicada;
import gerard.adaptacao.modelousuario.FotografiaModeloUsuario;
import gerard.agente.modelousuario.ModeloUsuario;
import gerard.agente.modelousuario.RepositorioModeloUsuario;
import java.util.List;
import java.util.Optional;

/**
 * Ciclo de vida da fotografia do Modelo do Usuário na sessão da aplicação.
 *
 * <p>Carrega uma única vez no login, conserva a mesma instância durante a
 * sessão e a descarta somente no logout. Não seleciona ajuda e não conhece
 * componentes Swing.</p>
 */
public final class SessaoAdaptativaUsuario {

    private final RepositorioModeloUsuario repositorioModeloUsuario;
    private final FonteRegrasAdaptativasCandidatas fonteRegras;
    private FotografiaModeloUsuario fotografiaAtual;

    public SessaoAdaptativaUsuario(
            RepositorioModeloUsuario repositorioModeloUsuario,
            FonteRegrasAdaptativasCandidatas fonteRegras) {
        if (repositorioModeloUsuario == null || fonteRegras == null) {
            throw new IllegalArgumentException(
                    "repositório do modelo e fonte de regras são obrigatórios");
        }
        this.repositorioModeloUsuario = repositorioModeloUsuario;
        this.fonteRegras = fonteRegras;
    }

    public synchronized FotografiaModeloUsuario iniciarNoLogin(String usuarioId) {
        String id = textoObrigatorio(usuarioId, "id do usuário");
        if (fotografiaAtual != null) {
            if (fotografiaAtual.getUsuarioId().equals(id)) {
                return fotografiaAtual;
            }
            throw new IllegalStateException(
                    "encerre a sessão atual antes de autenticar outro usuário");
        }
        ModeloUsuario modelo = repositorioModeloUsuario.obter(id);
        if (modelo == null || modelo.getPerfilAluno().getNome() == null) {
            throw new IllegalArgumentException(
                    "o login exige um Modelo do Usuário cadastrado: " + id);
        }
        List<RegraAdaptativaPublicada> candidatas = fonteRegras.obterPara(id);
        if (candidatas == null) {
            throw new IllegalStateException("a fonte de regras devolveu valor nulo");
        }
        fotografiaAtual = FotografiaModeloUsuario.carregarNoLogin(modelo, candidatas);
        return fotografiaAtual;
    }

    public synchronized Optional<FotografiaModeloUsuario> fotografiaAtual() {
        return Optional.ofNullable(fotografiaAtual);
    }

    /**
     * Entrega ao proprietário somente o recorte que ele próprio declarou.
     * A sessão conhece o ciclo de vida da fotografia, mas não conhece as
     * condições das regras nem escolhe ajuda.
     */
    public synchronized Optional<ContextoAdaptativoUsuario> projetarContextoPara(
            ProprietarioRepertorioAjuda<?> proprietario) {
        if (proprietario == null) {
            throw new IllegalArgumentException("proprietário semântico é obrigatório");
        }
        if (fotografiaAtual == null) {
            return Optional.empty();
        }
        return Optional.of(fotografiaAtual.projetarContextoAdaptativo(
                proprietario.chaveProprietarioSemantico(),
                proprietario.escopoAdaptativo(),
                proprietario.dimensoesModeloUsuarioRelevantes()));
    }

    public synchronized void encerrarNoLogout() {
        fotografiaAtual = null;
    }

    private static String textoObrigatorio(String valor, String nome) {
        String normalizado = valor == null ? "" : valor.trim();
        if (normalizado.isEmpty()) {
            throw new IllegalArgumentException(nome + " não pode ser vazio");
        }
        return normalizado;
    }
}
