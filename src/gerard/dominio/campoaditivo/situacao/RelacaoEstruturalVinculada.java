package gerard.dominio.campoaditivo.situacao;

import gerard.dominio.campoaditivo.EstadoConsistencia;
import gerard.dominio.campoaditivo.PapelQuantitativo;
import gerard.dominio.campoaditivo.RelacaoEstruturalAditiva;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Vincula uma relação estrutural aos três papéis que ela coordena nesta
 * situação. A relação continua proprietária da regra matemática.
 */
public final class RelacaoEstruturalVinculada {
    private final String chave;
    private final RelacaoEstruturalAditiva relacao;
    private final PapelQuantitativo papel0;
    private final PapelQuantitativo papel1;
    private final PapelQuantitativo papel2;

    public RelacaoEstruturalVinculada(
            String chave,
            RelacaoEstruturalAditiva relacao,
            PapelQuantitativo papel0,
            PapelQuantitativo papel1,
            PapelQuantitativo papel2) {
        this.chave = FamiliaObjeto.obrigatorio(
                chave, "chave da relação vinculada não pode ser vazia");
        if (relacao == null || papel0 == null || papel1 == null || papel2 == null) {
            throw new IllegalArgumentException("relação e seus três papéis são obrigatórios");
        }
        this.relacao = relacao;
        this.papel0 = papel0;
        this.papel1 = papel1;
        this.papel2 = papel2;
    }

    public String getChave() { return chave; }
    public String descreverRelacao() { return relacao.descreverRelacao(); }
    public RelacaoEstruturalAditiva getRelacao() { return relacao; }

    public List<PapelQuantitativo> getPapeis() {
        return Collections.unmodifiableList(Arrays.asList(papel0, papel1, papel2));
    }

    public EstadoConsistencia verificarConsistencia() {
        return relacao.verificarConsistencia(papel0, papel1, papel2);
    }
}
