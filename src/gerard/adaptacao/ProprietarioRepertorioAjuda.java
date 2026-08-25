package gerard.adaptacao;

import gerard.adaptacao.modelousuario.DimensaoModeloUsuario;
import java.util.Set;

/**
 * Contrato implementado pelo próprio objeto semântico que possui um
 * repertório. Não há implementação central nem dependência de interface.
 *
 * @param <D> fatos da decisão; cada fato deve chegar já produzido pelo seu
 *            proprietário de conhecimento, sem ser recalculado neste contrato
 */
public interface ProprietarioRepertorioAjuda<D> {

    String chaveProprietarioSemantico();

    EscopoProprietarioSemantico escopoAdaptativo();

    /**
     * Declara o menor recorte do Modelo do Usuário necessário para as regras
     * deste proprietário. A fronteira de sessão usa essa declaração para
     * projetar a fotografia sem conhecer o vocabulário interno do objeto.
     */
    Set<DimensaoModeloUsuario> dimensoesModeloUsuarioRelevantes();

    RepertorioAjuda repertorioAjuda();

    DecisaoAjuda selecionarAjuda(D diagnosticoFactual, ContextoAdaptativoUsuario contexto);
}
