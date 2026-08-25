package gerard.aplicacao.adaptacao;

import gerard.adaptacao.DecisaoAjuda;
import java.util.List;

/** Porta da representacao: concretiza uma decisao sem reinterpretar a regra. */
public interface MaterializadorDecisaoAjuda {

    List<ConfirmacaoMaterializacaoAjuda> materializar(
            DecisaoAjuda decisao,
            String chavePapelAlvo);
}
