package gerard.dominio.campoaditivo.situacao;

import gerard.dominio.campoaditivo.OperacaoAditiva;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Critério curado para avaliar a operação escolhida pelo participante.
 *
 * A operação é um procedimento de resolução solicitado pela atividade. Ela
 * não substitui a relação estrutural entre números dirigidos nem autoriza o
 * sistema a recalcular um valor que o pesquisador declarou na curadoria.
 */
public final class CriterioOperacaoModelagem {
    private final String chave;
    private final OperacaoAditiva operacaoEsperada;
    private final List<String> chavesPapeisEnvolvidos;

    public CriterioOperacaoModelagem(
            String chave,
            OperacaoAditiva operacaoEsperada,
            List<String> chavesPapeisEnvolvidos) {
        this.chave = FamiliaObjeto.obrigatorio(
                chave, "chave do critério de operação não pode ser vazia");
        if (operacaoEsperada == null || chavesPapeisEnvolvidos == null
                || chavesPapeisEnvolvidos.isEmpty()) {
            throw new IllegalArgumentException(
                    "operação esperada e papéis envolvidos são obrigatórios");
        }
        ArrayList<String> papeis = new ArrayList<>();
        for (String chavePapel : chavesPapeisEnvolvidos) {
            papeis.add(FamiliaObjeto.obrigatorio(
                    chavePapel, "chave de papel do critério não pode ser vazia"));
        }
        this.operacaoEsperada = operacaoEsperada;
        this.chavesPapeisEnvolvidos = Collections.unmodifiableList(papeis);
    }

    public String getChave() { return chave; }
    public OperacaoAditiva getOperacaoEsperada() { return operacaoEsperada; }
    public List<String> getChavesPapeisEnvolvidos() {
        return chavesPapeisEnvolvidos;
    }

    public boolean correspondeA(OperacaoAditiva tentativa) {
        return tentativa != null && tentativa == operacaoEsperada;
    }
}
