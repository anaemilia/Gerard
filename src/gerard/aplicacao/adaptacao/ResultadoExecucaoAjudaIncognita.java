package gerard.aplicacao.adaptacao;

import gerard.adaptacao.DecisaoAjuda;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/** Resultado explicito da integracao da decisao local com a representacao. */
public final class ResultadoExecucaoAjudaIncognita {

    public enum Estado {
        CONTEXTO_INDISPONIVEL,
        SEM_REGRA_APLICAVEL,
        MATERIALIZADA
    }

    private final Estado estado;
    private final DecisaoAjuda decisao;
    private final List<ConfirmacaoMaterializacaoAjuda> confirmacoes;

    private ResultadoExecucaoAjudaIncognita(
            Estado estado,
            DecisaoAjuda decisao,
            List<ConfirmacaoMaterializacaoAjuda> confirmacoes) {
        this.estado = estado;
        this.decisao = decisao;
        this.confirmacoes = Collections.unmodifiableList(
                new ArrayList<ConfirmacaoMaterializacaoAjuda>(confirmacoes));
    }

    public static ResultadoExecucaoAjudaIncognita contextoIndisponivel() {
        return new ResultadoExecucaoAjudaIncognita(
                Estado.CONTEXTO_INDISPONIVEL, null,
                Collections.<ConfirmacaoMaterializacaoAjuda>emptyList());
    }

    public static ResultadoExecucaoAjudaIncognita semRegra(DecisaoAjuda decisao) {
        return new ResultadoExecucaoAjudaIncognita(
                Estado.SEM_REGRA_APLICAVEL, decisao,
                Collections.<ConfirmacaoMaterializacaoAjuda>emptyList());
    }

    public static ResultadoExecucaoAjudaIncognita materializada(
            DecisaoAjuda decisao,
            List<ConfirmacaoMaterializacaoAjuda> confirmacoes) {
        if (confirmacoes == null || confirmacoes.isEmpty()) {
            throw new IllegalArgumentException(
                    "uma ajuda aplicada precisa confirmar ao menos uma materializacao");
        }
        return new ResultadoExecucaoAjudaIncognita(
                Estado.MATERIALIZADA, decisao, confirmacoes);
    }

    public Estado getEstado() { return estado; }
    public boolean foiMaterializada() { return estado == Estado.MATERIALIZADA; }
    public Optional<DecisaoAjuda> getDecisao() { return Optional.ofNullable(decisao); }
    public List<ConfirmacaoMaterializacaoAjuda> getConfirmacoes() { return confirmacoes; }
}
