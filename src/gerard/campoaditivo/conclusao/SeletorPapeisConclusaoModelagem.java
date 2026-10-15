package gerard.campoaditivo.conclusao;

import gerard.campoaditivo.curadoria.SemanticaCuradaSituacao;
import gerard.campoaditivo.modelo.SituacaoProblemaAditiva;
import gerard.i18n.ServicoLocalizacao;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Seleciona os papeis exigidos para a conclusao da atividade: os que a curadoria definiu E que têm elemento arrastável
 * no enunciado. Um papel derivado, sem elemento no enunciado (o estado intermediário de uma situação que não o cita),
 * não há o que o aluno posicione; cobrá-lo tornaria a situação impossível de concluir — mesma regra da web.
 */
public final class SeletorPapeisConclusaoModelagem {
    public List<String> selecionar(Collection<String> papeisDaCena, Collection<String> papeisComElementoNoEnunciado,
            SituacaoProblemaAditiva situacao, ServicoLocalizacao localizacao) {
        List<String> selecionados = new ArrayList<String>();
        if (papeisDaCena == null) return selecionados;
        for (String papel : papeisDaCena) {
            String chave = papel == null ? "" : papel.trim();
            if (chave.length() == 0 || "papel.valor".equals(chave)) continue;
            if (papeisComElementoNoEnunciado != null && !papeisComElementoNoEnunciado.contains(chave)) continue;
            if (situacao == null || SemanticaCuradaSituacao
                    .papelExigidoNaModelagem(situacao, localizacao, chave)) {
                selecionados.add(chave);
            }
        }
        return selecionados;
    }
}
