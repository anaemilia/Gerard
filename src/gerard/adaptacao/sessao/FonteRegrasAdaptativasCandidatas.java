package gerard.adaptacao.sessao;

import gerard.adaptacao.RegraAdaptativaPublicada;
import java.util.Collections;
import java.util.List;

/**
 * Fronteira de leitura das regras disponíveis no instante do login.
 * A fotografia ainda filtra pelo estado PUBLICADA; a fonte não promove regras.
 */
public interface FonteRegrasAdaptativasCandidatas {

    FonteRegrasAdaptativasCandidatas NENHUMA =
            new FonteRegrasAdaptativasCandidatas() {
                public List<RegraAdaptativaPublicada> obterPara(String usuarioId) {
                    return Collections.emptyList();
                }
            };

    List<RegraAdaptativaPublicada> obterPara(String usuarioId);
}
