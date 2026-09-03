package gerard.campoaditivo.conclusao;

import gerard.campoaditivo.curadoria.SemanticaCuradaSituacao;
import gerard.campoaditivo.modelo.SituacaoProblemaAditiva;
import gerard.i18n.ServicoLocalizacao;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/** Seleciona os papeis exigidos para a conclusao da atividade. */
public final class SeletorPapeisConclusaoModelagem {
    public List<String> selecionar(Collection<String> papeisDaCena,
            SituacaoProblemaAditiva situacao, ServicoLocalizacao localizacao) {
        List<String> selecionados = new ArrayList<String>();
        if (papeisDaCena == null) return selecionados;
        for (String papel : papeisDaCena) {
            String chave = papel == null ? "" : papel.trim();
            if (chave.length() == 0 || "papel.valor".equals(chave)) continue;
            if (situacao == null || SemanticaCuradaSituacao
                    .papelExigidoNaModelagem(situacao, localizacao, chave)) {
                selecionados.add(chave);
            }
        }
        return selecionados;
    }
}
