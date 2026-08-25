package gerard.adaptacao.modelousuario;

import gerard.agente.modelousuario.MidiaPreferida;
import gerard.agente.modelousuario.NivelEscolaridade;
import gerard.agente.modelousuario.PerfilAprendizagem;
import java.util.Optional;

/** Fotografia das preferências que integram, mas não esgotam, o modelo. */
public final class PerfilAprendizagemProjetado {

    private final MidiaPreferida midiaPreferida;
    private final NivelEscolaridade nivelEscolaridade;

    public PerfilAprendizagemProjetado(PerfilAprendizagem origem) {
        if (origem == null
                || (origem.getMidiaPreferida() == null
                && origem.getNivelEscolaridade() == null)) {
            throw new IllegalArgumentException("perfil de aprendizagem não possui dados conhecidos");
        }
        this.midiaPreferida = origem.getMidiaPreferida();
        this.nivelEscolaridade = origem.getNivelEscolaridade();
    }

    public Optional<MidiaPreferida> getMidiaPreferida() {
        return Optional.ofNullable(midiaPreferida);
    }

    public Optional<NivelEscolaridade> getNivelEscolaridade() {
        return Optional.ofNullable(nivelEscolaridade);
    }
}
